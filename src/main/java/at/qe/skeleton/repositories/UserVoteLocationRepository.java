package at.qe.skeleton.repositories;

import at.qe.skeleton.model.*;

import java.util.Collection;

public interface UserVoteLocationRepository extends AbstractRepository<UserVoteLocation, Long> {
    UserVoteLocation findUserVoteLocationByUserAndId(User currentUser, long userVoteId);

    UserVoteLocation findUserVoteLocationByUserAndEventAndLocation(User currentUser, Event event, Location location);

    Collection<UserVoteLocation> findUserVoteLocationsByEvent(Event event);

    Collection<UserVoteLocation> findUserVoteLocationsByEventAndLocation(Event event, Location location);

}
