package com.baykalsoft.postmail.web;

import com.baykalsoft.postmail.persistence.model.Account;
import com.baykalsoft.postmail.persistence.repo.AccountRepository;
import com.baykalsoft.postmail.service.Mailer;
import com.google.common.hash.Hashing;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Controller
public class SimpleController {

    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private Mailer mailer;

    @Value("${spring.application.name}")
    String appName;

    @Value("${app.root.url}")
    String appUrl;


    @Value("${validity.expiry}")
    int expiry;

    @RequestMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping("/postmail")
    public String postMail(Model model) {
        model.addAttribute("appUrl", appUrl+"/send");
        return "postmail";
    }

    @GetMapping("/test")
    public String test(Model model) {
        return "test";
    }


    @PostMapping(value = "/submit")
    public String submitMail(@RequestParam(value = "email") String email, Model model) {
        Account account;
        logger.info("email is " + email);
        String content ="You can start using the html mail service. \n" +
                "From below link you can access copy-and-paste example and read documentation :\n" +
                appUrl+"/account/";

        try {

            List<Account> accountList = accountRepository.findByEmail(email);
            if (accountList.size() == 1) {
                content =  content.concat(accountList.get(0).getVerifyToken());
                model.addAttribute("email", email);
                mailer.sendmail(accountList.get(0),content,"Re-sending your access token");

            }
            else {

                String accessToken = Hashing.sha1().hashString(email, StandardCharsets.UTF_8).toString();
                String verifyToken = Hashing.sha1().hashString(email.concat(String.valueOf(System.currentTimeMillis())), StandardCharsets.UTF_8).toString();
                account = new Account(accessToken, new Date(), email, 0, verifyToken);
                accountRepository.save(account);
                content = content.concat(verifyToken);
                model.addAttribute("email", email);
                mailer.sendmail(account,content,"Your acccess token");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("error",e.getMessage());
            model.addAttribute("message","Problem occurred while sending activation mail");
            return "error";
        }
        return "result";
    }

    @GetMapping("/account/{verifyToken}")
    public String verifyMail(@PathVariable String verifyToken, Model model) {
        List<Account> accountList = accountRepository.findByVerifyToken(verifyToken);

        if (accountList.size() == 0) {
            model.addAttribute("error", "Verification failed");
            model.addAttribute("message", "Your mail verification token was not found");
            return "error";
        }
        logger.info(accountList.toString());


        if (accountList.get(0).getStatus() == 0) {
            DateTime created = new DateTime(accountList.get(0).getCreated());
            if (Minutes.minutesBetween(created, new DateTime(DateTimeZone.UTC)).getMinutes() >= expiry) {
                accountRepository.delete(accountList.get(0));
                model.addAttribute("error", "Verification failed");
                model.addAttribute("message", "Your token expired, register again. ");
                return "error";
            }
            accountRepository.setStatus(1, accountList.get(0).getId());
        }

        model.addAttribute("email", accountList.get(0).getEmail());
        model.addAttribute("token", accountList.get(0).getAccessToken());
        model.addAttribute("appUrl", appUrl+"/send");
        return "account";

    }


    @PostMapping(value = "/send")
    public String send(@RequestParam(value = "subject") String subject,
                       @RequestParam(value = "text") String text,
                       @RequestParam(value = "access_token") String accessToken,
                       @RequestParam(value = "success_url") String successUrl,
                       @RequestParam(value = "error_url") String errorUrl,
                       Model model, RedirectAttributes redirectAttributes) {

        logger.info(subject);
        logger.info(accessToken);
        logger.info(text);

        List<Account> accountList = accountRepository.findByAccessToken(accessToken);

        if (accountList.size() == 0) {
            model.addAttribute("error", "Mail sending failed");
            model.addAttribute("message", "Access Token not found");
            return "error";
        }
        logger.info(accountList.toString());

        try {
            mailer.sendmail(accountList.get(0),text,subject);
            model.addAttribute("message", "Message Sent");
            model.addAttribute("error", "0");
            model.addAttribute("email", accountList.get(0).getEmail());
            model.addAttribute("token", accountList.get(0).getAccessToken());
            model.addAttribute("appUrl", appUrl+"/send");
            return "account";

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("error", "Mail sending failed");
            model.addAttribute("message", e.getMessage());
            return "error";

        }



    }

}