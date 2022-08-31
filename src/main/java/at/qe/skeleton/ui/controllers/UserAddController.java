package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserRole;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.IOException;
import java.util.*;

@Component
@Scope("view")
public class UserAddController{


    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**Set Services for the JUnit tests
     *
     * @param userService
     */

    public void setService(UserService userService) {
        this.userService = userService;
    }

    private String password;

    private User user = new User();

    private boolean isValidUsername;

    private boolean isValidEmail;

    private String messageInfo;
    private String facesMessage;
    

    public String getFacesMessage() {
        return facesMessage;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public boolean isValidUsername() {
        return isValidUsername;
    }

    public void setValidUsername(boolean validUsername) {
        isValidUsername = validUsername;
    }
    

    public boolean isValidEmail() {
        return isValidEmail;
    }
    
    public void setValidEmail(boolean validEmail) {
        isValidEmail = validEmail;
    }

    public void doSaveUser() throws IOException {

        checkValidUsername();
        checkValidEmail();

        if(isValidUsername && isValidEmail) {
            user.setPassword(passwordEncoder.encode(password));

            this.userService.saveUser(user);

            FacesContext.getCurrentInstance().getExternalContext().redirect("/admin/users.xhtml");
        }
    }

    /**Setter & getters to add a new User*/
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**Setter & getter to get and add all Roles selected in array, calling addUserRoles*/
    public Set<String> getSelectedRoles() {
        Set<String> selectedRoles = new HashSet<>();
       selectedRoles.add("USER"); //pre-selected

       return selectedRoles;
    }

    /**Method to add all selected roles*/
    public void setSelectedRoles(Set<String> selectedRoles) {
        Set<UserRole> userRoles = new HashSet<>();
        for(String s : selectedRoles){
            userRoles.add(UserRole.valueOf(s));
        }
        userRoles.add(UserRole.USER); //if user is un-selected, add the new user always as normal user
        this.user.setRoles(userRoles);
    }


    public void checkValidUsername() throws ValidatorException {

        String newUsername = getUser().getUsername();
        User newUser = userService.loadUser(newUsername);

        //if the user exists in the database and is not removed
        if (userService.getAllUsers().contains(newUser)) {

            messageInfo="Username Exists";
            facesMessage="The Username: " + newUsername + " already exists! Please choose another one.";

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                    facesMessage));
            setValidUsername(false);
            
        }
        else{
          setValidUsername(true);
        }
        }

    public void checkValidEmail() throws ValidatorException {

        String newEmail = getUser().getEmail();

        List<String> allEmails = new ArrayList<>();

        //search each user in all users (including the users with status Removed) and add each email into the array
        for(User existingUser : userService.getAllUsers()){
            if(existingUser.getEmail() != null
                    && !Objects.equals(existingUser.getUserStatus(), "Removed") //if user removed, we can use that email
                    && !getUser().equals(existingUser) //exclude the current logged in user, editing its own email
                    && !Objects.equals(existingUser.getEmail(), "")
            ){
                allEmails.add(existingUser.getEmail());
            }
        }

            if(allEmails.contains(newEmail)){
                messageInfo="Email Exists";
                facesMessage="The E-mail: " + newEmail + " is already in use! Please choose another one.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(messageInfo,
                        facesMessage));
                setValidEmail(false);
            }
            else{
                setValidEmail(true);
            }
        }


}
