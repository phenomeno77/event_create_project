package at.qe.skeleton.repositories;

import at.qe.skeleton.model.*;

import java.util.Collection;

public interface UserVoteTimeslotRepository extends AbstractRepository<UserVoteTimeslot, Long> {
    UserVoteTimeslot findUserVoteTimeslotByUserAndId(User currentUser, long userVoteId);

    UserVoteTimeslot findUserVoteTimeslotByUserAndEventAndTimeslot(User currentUser, Event event, Timeslot timeslot);

    Collection<UserVoteTimeslot> findUserVoteTimeslotByEvent(Event event);

    Collection<UserVoteTimeslot> findUserVoteTimeslotsByEventAndTimeslot(Event event, Timeslot timeslot);
}
