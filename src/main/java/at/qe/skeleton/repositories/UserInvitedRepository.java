package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.User;
import at.qe.skeleton.model.UserInvited;

import java.util.Collection;

public interface UserInvitedRepository extends AbstractRepository <UserInvited, Long>{
    UserInvited findByToken(String token);
    UserInvited findByUser(User user);

    UserInvited findByUserAndEvent(User user, Event event);
    Collection<UserInvited> findUserInvitedsByInvitationAcceptedTrueAndEvent(Event event);

}