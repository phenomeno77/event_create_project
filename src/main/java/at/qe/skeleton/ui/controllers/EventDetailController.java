package at.qe.skeleton.ui.controllers;


import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.UserInvited;
import at.qe.skeleton.services.EmailService;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.UserInvitedService;
import at.qe.skeleton.services.UserService;
import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Date;


@Component
@Scope("view")
public class EventDetailController {

    @Autowired
    EventService eventService;

    @Autowired
    UserInvitedService userInvitedService;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    SessionInfoBean sessionInfoBean;

    private String facesMessage;
    private String messageInfo;

    /**Set services for the JUnit tests
     *
     * @param eventService
     * @param userInvitedService
     * @param userService
     * @param emailService
     * @param taskScheduler
     */
    public void setServices(EventService eventService, UserInvitedService userInvitedService,
                            UserService userService, EmailService emailService, TaskScheduler taskScheduler, SessionInfoBean sessionInfoBean) {
        this.eventService = eventService;
        this.userInvitedService = userInvitedService;
        this.userService = userService;
        this.emailService = emailService;
        this.taskScheduler = taskScheduler;
        this.sessionInfoBean = sessionInfoBean;
    }
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }



    public String getFacesMessage() {
        return facesMessage;
    }

    public String getMessageInfo() {
        return messageInfo;
    }


    /**
     * Attribute to cache the currently displayed event
     */
    private Event event;

    /**
     * Method to cancel the Event, setting the Event to disable
     * will not anymore be visible.
     */



    public void doCancelEvent() {

        if(userService.getAuthenticatedUser().equals(event.getUser()) || sessionInfoBean.hasRole("ADMIN")) {
            this.eventService.cancelEvent(event);
            messageInfo = "Canceled";
            facesMessage = "The Event has been successfully canceled! All the invited user are now informed by E-mail.";

            sendCancelNotification();

            doSaveEvent();
        }else{
            messageInfo = "Not Event Creator";
            facesMessage = "Dear User, only Admins or the Event Creator of that event can cancel that event.";
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo, facesMessage));
    }

    public void doEndEvent() {
        if(userService.getAuthenticatedUser().equals(event.getUser()) || sessionInfoBean.hasRole("ADMIN")) {
            this.event.setVotingExpiry(new Date());

           event = this.eventService.patchVotingExpiry(event, false);
           event = this.eventService.patchEventStatus(event, "Expired");
           String results = this.eventService.getVotingResults(event);
            facesMessage = "The Event has been successfully ended! All the invited user are now informed by E-mail.";
            messageInfo ="Event Ended" ;

            doSaveEvent();

            if(!results.equals("No results found.")) {
                sendResults();
            }


        }else{
            messageInfo = "Not Event Creator";
            facesMessage = "Dear User, only Admins or the Event Creator of that event can end that event.";
        }

        FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(messageInfo,
                facesMessage));


    }

    public String getVotingResults(Event event) {

        return eventService.getVotingResults(event);
    }

    /**Method to use the Location for the Location Details dialog in history page
     *
     * @return
     */
    public Location getWinnerLocation(){

        return eventService.getWinnerLocation(event);
    }

    /**Overloaded to disable or enable the Location Details button depending on the location status.
     * If the Location has been closed or removed, the corresponding button will be shown to warn the Users in case.
     * @param event
     * @return
     */
    public Location getWinnerLocation(Event event){

        return eventService.getWinnerLocation(event);
    }

    private void sendCancelNotification() {
        for (UserInvited user : userInvitedService.findUserInvitedByInvitationAcceptedTrueAndEvent(event)) {
            emailService.sendEventCanceledNotification(event, user.getUser());
        }
    }

    private void sendResults() {
        String locationWinner = this.event.getEventLocationWinner();
        String timeslotWinner = this.event.getEventTimeslotWinner();

        for (UserInvited user : userInvitedService.findUserInvitedByInvitationAcceptedTrueAndEvent(event)) {
            emailService.sendEventResult(event, locationWinner, timeslotWinner,user.getUser());
        }
    }


    /**
     * Sets the currently displayed event and reloads it form db. This event is
     * targeted by any further calls of
     * {@link #doReloadEvent()}, {@link #doSaveEvent()} and
     * .
     */
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        doReloadEvent();
    }

    /**
     * Action to force a reload of the currently displayed event.
     */
    public void doReloadEvent() {
        event = eventService.loadEvent(event.getEventId());
    }

    /**
     * Action to save the currently displayed event.
     */
    public void doSaveEvent() {
        event = this.eventService.saveEvent(event);
    }

}
