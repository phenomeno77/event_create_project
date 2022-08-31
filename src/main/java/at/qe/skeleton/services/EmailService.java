package at.qe.skeleton.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserInvited;

@Service
public class EmailService {
    @Autowired
    private final UserInvitedService userInvitedService;

    @Autowired
    private final EventService eventService;

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    public EmailService(UserInvitedService userInvitedService, TemplateEngine templateEngine,
                        JavaMailSender javaMailSender, EventService eventService) {
        this.userInvitedService = userInvitedService;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
        this.eventService = eventService;
    }
    /**
     * sends an invitation link to the chosen users
     *
     */
    public void sendMail(User user, Event event) {

        Event invitedEvent = eventService.findEventByEventId(event.getEventId());
        UserInvited userInvited = userInvitedService.findByUserAndEvent(user, invitedEvent);
        userInvitedService.save(userInvited);
        if (userInvited != null) {
            String token = userInvited.getToken();
            Context context = new Context();
            context.setVariable("title", "Confirm your invitation");
            context.setVariable("link", "http://localhost:8080/confirmation?token=" + token);

            String body = templateEngine.process("verification", context);


            MimeMessage message = javaMailSender.createMimeMessage();

            if (user.getEmail() != null) {
                try {
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setTo(user.getEmail());
                    helper.setSubject("event invitation");
                    helper.setText(body, true);
                    javaMailSender.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(user.getUsername() + " does not have an email");
            }
        }

    }

    /**
     * sends an Email about the event canceled to the chosen users
     *
     */
    public void sendEventCanceledNotification(Event event, User user) {
        Context context = new Context();
        context.setVariable("title", "Event canceled");
        context.setVariable("eventName", event.getEventName());

        String body = templateEngine.process("notification", context);


        MimeMessage message = javaMailSender.createMimeMessage();

        if (user.getEmail() != null) {
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(user.getEmail());
                helper.setSubject("Event " + event.getEventName() + " canceled");
                helper.setText(body, true);
                javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(user.getUsername() + " does not have an email");
        }


    }
    /**
     * sends an Email about the event results to the chosen users
     *
     */
    public void sendEventResult(Event event, String locationName, String timeslotName, User user) {
        Context context = new Context();
        context.setVariable("title", "Event canceled");
        context.setVariable("eventName", event.getEventName());
        context.setVariable("locationWinner", locationName);
        context.setVariable("timeslotWinner", timeslotName);

        String body = templateEngine.process("eventResult", context);


        MimeMessage message = javaMailSender.createMimeMessage();

        if (user.getEmail() != null) {
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(user.getEmail());
                helper.setSubject("Results for " + event.getEventName());
                helper.setText(body, true);
                javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(user.getUsername() + " does not have an email");
        }


    }
}
