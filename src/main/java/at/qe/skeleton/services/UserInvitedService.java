package at.qe.skeleton.services;

import java.util.Collection;
import java.util.UUID;

import at.qe.skeleton.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserInvited;
import at.qe.skeleton.repositories.UserInvitedRepository;

@Service
public class UserInvitedService {
    @Autowired
    private final UserInvitedRepository userInvitedRepository;


    public UserInvitedService(UserInvitedRepository userInvitedRepository) {
        this.userInvitedRepository = userInvitedRepository;
    }

    public UserInvited findByToken(String token) {
        return userInvitedRepository.findByToken(token);
    }

    public UserInvited findByUser(User user) {
        return userInvitedRepository.findByUser(user);
    }

    public UserInvited findByUserAndEvent(User user, Event event) {
        return userInvitedRepository.findByUserAndEvent(user, event);
    }

    public UserInvited save(UserInvited userInvited) {
        userInvited.setToken(generateToken());//TODO: Move this logic to EmailService
       return userInvitedRepository.save(userInvited);

    }

    public Collection<UserInvited> findUserInvitedByInvitationAcceptedTrueAndEvent(Event event) {
        return userInvitedRepository.findUserInvitedsByInvitationAcceptedTrueAndEvent(event);
    }


    private String generateToken() {
        return UUID.randomUUID().toString();


    }
}
