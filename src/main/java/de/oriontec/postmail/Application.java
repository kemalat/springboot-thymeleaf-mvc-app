package de.oriontec.postmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//@ServletComponentScan
//@SpringBootApplication(scanBasePackages = {"com.baeldung","com.baeldung.service"})
//@EnableJpaRepositories("com.baeldung.persistence.repo")
//@EntityScan("com.baeldung.persistence.model")
@SpringBootApplication

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

    }

}
