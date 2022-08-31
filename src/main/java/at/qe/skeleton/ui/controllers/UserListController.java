package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.User;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Controller for the user list view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class UserListController {

    @Autowired
    private UserService userService;

    /**Set Services for the JUnit tests
     *
     * @param userService
     */
    public void setServices(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a list of all users.
     *
     * @return
     */
    public Collection<User> getUsers() {
        return userService.getAllUsers();
    }

}
