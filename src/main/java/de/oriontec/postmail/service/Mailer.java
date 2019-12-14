package de.oriontec.postmail.service;

import de.oriontec.postmail.persistence.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by kemalatik on 3/29/19.
 */
@Component
public class Mailer {

    Logger logger = LoggerFactory.getLogger(Mailer.class);

    public Mailer(Logger logger) {
        this.logger = logger;
    }

    @Value("${smtp.auth}")
    private String smtpAuth;

    @Value("${smtp.starttls.enable}")
    private String starttlsEnabled;

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.port}")
    private String smtpPort;

    @Value("${smtp.user}")
    private String smtpUser;

    @Value("${smtp.password}")
    private String smtpPassword;


    public Mailer() {
    }

    public void sendmail(Account account, String content, String subject) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnabled);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(smtpUser, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(account.getEmail()));
        msg.setSubject(subject);
        msg.setContent(subject, "text/html");
        msg.setSentDate(new Date());
        msg.setText(content);

        Transport.send(msg);
    }

}
