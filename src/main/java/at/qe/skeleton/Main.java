package at.qe.skeleton;

import java.util.Properties;

import javax.faces.webapp.FacesServlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Spring boot application. Execute maven with <code>mvn spring-boot:run</code>
 * to start this web application.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
/*
 * Prevent spring from trying to autowire the websocket-infrastructure: Exclude
 * the at.qe.skeleton.ui.websockets package from component scan.
 *
 * NOTE: Do not add any components to this package which should be managed by
 * spring. It is reserved for the CDI-injection-mechanisms (Weld). Only add
 * CDI-managed components.
 */
@ComponentScan(basePackages = "at.qe.skeleton", excludeFilters = @Filter(type = FilterType.REGEX, pattern = "at.qe.skeleton.ui.websockets.*"))
public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
            SpringApplication.run(Main.class, args);
    }

    @Bean
    public ServletRegistrationBean<FacesServlet> servletRegistrationBean() {
            FacesServlet servlet = new FacesServlet();
            ServletRegistrationBean<FacesServlet> servletRegistrationBean = new ServletRegistrationBean<>(servlet,
                            "*.xhtml");
            servletRegistrationBean.setName("Faces Servlet");
            servletRegistrationBean.setAsyncSupported(true);
            servletRegistrationBean.setLoadOnStartup(1);
            return servletRegistrationBean;
    }
    
    

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        
        mailSender.setUsername("confirmg5t2@gmail.com");
        mailSender.setPassword("!Password1");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }


}
