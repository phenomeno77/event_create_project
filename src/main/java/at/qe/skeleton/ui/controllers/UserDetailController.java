package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserInvited;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.EmailService;
import at.qe.skeleton.services.EventService;
import at.qe.skeleton.services.UserInvitedService;
import at.qe.skeleton.services.UserService;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * Controller for the user detail view.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class UserDetailController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInvitedService userInvitedService;

    @Autowired
    private EventService eventService;

    @Autowired
    EmailService emailService;

    private String facesMessage ;
    private String messageInfo = "Warning";
    private final String removedUser = "Removed";

    /**Set Services for the JUnit tests
     *
     * @param userService
     * @param userInvitedService
     * @param eventService
     * @param emailService
     */

    public void setServices(UserService userService, UserInvitedService userInvitedService, EventService eventService, EmailService emailService) {
        this.userService = userService;
        this.userInvitedService = userInvitedService;
        this.eventService = eventService;
        this.emailService = emailService;
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
     * Attribute to cache the currently displayed user
     */
    private User user;

    private boolean userInvitedToEvent;

    public boolean isUserInvitedToEvent() {
        return userInvitedToEvent;
    }

    public void setUserInvitedToEvent(boolean userInvitedToEvent) {
        this.userInvitedToEvent = userInvitedToEvent;
    }

    /**
     * Getter with foreach loop to first display all current roles.
     * With each click or unclick on check box, setter will update the roles
     */
    public Set<String> getSelectedRoles() {
        Set<String> selectedRoles = new HashSet<>();

        for (UserRole ur : this.user.getRoles()) {
            selectedRoles.add(ur.name());
        }

        return selectedRoles;
    }

    /**
     * Set Roles for User after clicking on buttons
     */
    public void setSelectedRoles(Set<String> selectedRoles) {
        Set<UserRole> userRoles = new HashSet<>();
        for (String s : selectedRoles) {
            userRoles.add(UserRole.valueOf(s));
        }
        this.user.setRoles(userRoles);

    }

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     *
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getUsername());
    }

    /** Returns the currently logged user.
     *
     * @return user
     */

    public User getCurrentUser() {
        return this.userService.getAuthenticatedUser();
    }




    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        if (checkValidEmail()) {
            user = this.userService.saveUser(user);
            PrimeFaces.current().executeScript("PF('userEditDialog').hide()");
        }
    }

    /**
     * Ban User method, that user will be disabled and could not anymore login either
     * take part of an Event until it set to enabled. If that user has already voted,
     * the voting count wont be part of the result.
     */
    public void doBanUser() {
        if (userService.getAuthenticatedUser().equals(user)) {

            facesMessage = "Dear " + getUser().getUsername() + " you cannot ban yourself.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                    facesMessage));
        } else {
            if (this.user.isEnabled()) {
                checkUserInvitedToEvent();
                this.user.setEnabled(false);
                this.user.setUserStatus("Banned");
                doSaveUser();
            }
        }
    }

    /**
     * Un-Ban user if banned, setting that User back to enabled, but that user
     * wont be able to vote on the already running events.
     */
    public void doUnBanUser() {
        if (!this.user.isEnabled() && this.user.getUserStatus().equals("Banned")) {
            this.user.setEnabled(true);
            this.user.setUserStatus("Active");
            doSaveUser();
        }
    }


    /**
     * Remove User from everywhere. By removing User the entity will exist in the Database
     * untill the doDeleteUser method will be called.
     */
    public void doRemoveUser() {
        setUserInvitedToEvent(false);
        if (userService.getAuthenticatedUser().equals(user)) {

            facesMessage = "Dear " + getUser().getUsername() + " you cannot remove yourself.";

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                    facesMessage));
        } else {

            checkUserInvitedToEvent();
            this.user.setEnabled(false);
            this.user.setUserStatus(removedUser);

            if(userInvitedToEvent) {
                doRemoveUserFromEvents();
            }
            doSaveUser();
        }

    }

    public void doDeleteUser() {
        if(!this.user.isEnabled() && this.user.getUserStatus().equals(removedUser)) {
            this.userService.deleteUser(user);
            user = null;
        }
        else{

            facesMessage ="Dear" + getUser().getUsername()+ " to completely delete that user, you have first to remove the user.";

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                    facesMessage));
        }
    }

    /**Method to remove that user as userInvited from all events before removing it*/
    private void doRemoveUserFromEvents(){

        for(Event event : eventService.getAllEvents()) {
                for (UserInvited userInvited : event.getUserInvited()) {
                    if(userInvited.getUser() == null || userInvited.getEvent() == null){
                        continue;
                    }
                    if(Objects.equals(userInvited.getUser().getUsername(), this.user.getUsername()))
                        userInvited.setUser(null);
                    }
        }


    }

    /**
     * Method to proof if the User is part of an Event.
     * By deleting, banning the user, the corresponding booleans will be set to true or false.
     * The invitation is set to false, so the users vote wont be count in the final results
     */
    public void checkUserInvitedToEvent() {
        for (Event event : eventService.getAllEvents()) {
                if (event.isActive() && user.getUsername().equals(event.getUser().getUsername())) { //if the user is the event creator
                    eventService.cancelEvent(event);
                    sendCancelNotification(event);
                }
                for (UserInvited userInvited : userInvitedService.findUserInvitedByInvitationAcceptedTrueAndEvent(event)) {
                    if (userInvited != null && Objects.equals(userInvited.getUser().getUsername(), this.user.getUsername())) {
                            userInvited.setInvitationAccepted(false); //banning or removing the user, set accepted invitation to false
                            setUserInvitedToEvent(true);    //set that variable to true
                    }
                }
        }
    }

    private void sendCancelNotification(Event event) {
        for (UserInvited userToNotify : userInvitedService.findUserInvitedByInvitationAcceptedTrueAndEvent(event)) {
            emailService.sendEventCanceledNotification(event, userToNotify.getUser());
        }
    }


    /**
     * Method to proof if the email exists in the database on editing of the user
     */
    public boolean checkValidEmail() {

    List<String> allEmails = new ArrayList<>();

    //iterate over all users and add each email in the array
        for (User userToCheckEmail : userService.getAllUsers()) {
        if (userToCheckEmail.getEmail() != null
                && !Objects.equals(userToCheckEmail.getUserStatus(), removedUser) //if user removed, we can use that email
                && !getUser().equals(userToCheckEmail) //exclude the current logged in user, editing its own email
                && !Objects.equals(userToCheckEmail.getEmail(), "")
        ) {
            allEmails.add(userToCheckEmail.getEmail());
        }
    }

        if (allEmails.contains(this.user.getEmail())) {
        messageInfo="Email Exists";
        facesMessage="The E-mail: " + this.user.getEmail() + " is already in use! Please choose another one.";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                facesMessage));
        return false;
    } else {
        return true;
    }
}

}
