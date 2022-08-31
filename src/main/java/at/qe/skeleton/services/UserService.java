package at.qe.skeleton.services;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.repositories.UserRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for accessing and manipulating user data.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  EventService eventService;

    /**
     * Returns a collection of all users.
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Loads a single user identified by its username.
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #username")
    public User loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
     * Saves the user. This method will also set {@link User#createDate} for new
     * entities or {@link User#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link User#createDate}
     * or {@link User#updateUser} respectively.
     *
     * @param user the user to save
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public User saveUser(User user) {
        if (user.isNew()) {
            user.setCreateDate(new Date());
            user.setEnabled(true);
            user.setUserStatus("Active");
            user.setCreateUser(getAuthenticatedUser());
        } else {
            user.setUpdateDate(new Date());
            user.setUpdateUser(getAuthenticatedUser());
        }
        return userRepository.save(user);
    }


    /**
     * Deletes the user.
     *
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(User user) {

        for (Event e : user.getEvents()){
            e.setUser(null);
            eventService.saveEvent(e);

        }
        user.setEvents(null);
        saveUser(user);
        userRepository.delete(user);
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }


    /**
     * Adds a new User to H2
     * Takes data from the POST request, sets the values and sends it to the DB.
     **/

    @PreAuthorize("hasAuthority('ADMIN')")
    public void addNewUser(User request) {
        User user = new User();
        user.setNewUser(request);
        saveUser(user);
    }

    /**
     * GET LSIT OF USERS, BUT NOT THE ACTUAL ONE.
     * LOGIC TO ONLY INVITE OTHER USERS AND NOT HISSELF TOO.
     * HISSELF AUTO-INVITE FROM CONTROLLER
     */

    public Collection<User> getUsersExceptCurrent(User user) {

        return userRepository.findAll().stream()
                .filter(actualUser -> !Objects.equals(actualUser, user))
                .collect(Collectors.toList());
    }
    public User findFirstByUsername(String username){
        return userRepository.findFirstByUsername(username);
    }

}
