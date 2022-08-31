package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.UserVoteLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for accessing and manipulating user voting for desired location.
 */

@Service
public class UserVoteLocationService {

    @Autowired
    UserVoteLocationRepository userVoteLocationRepository;

    @Autowired
    UserService userService;

    /**
     * Returns a collection of all user votes for location.
     */
    public Collection<UserVoteLocation> getAllUserVoteForLocation() {
        return userVoteLocationRepository.findAll();
    }

    /**
     * saves the new voting specified
     *
     * @param userVoteLocation the voting count to be saved
     * @return the updated user voting for the location
     */
    public UserVoteLocation voteLocation(UserVoteLocation userVoteLocation) {
        return userVoteLocationRepository.save(userVoteLocation);
    }

    /**
     * Deletes the location voting.
     *
     * @param userVoteLocation the user voting to delete
     */
    public void delete(UserVoteLocation userVoteLocation) {
        userVoteLocationRepository.delete(userVoteLocation);
    }

    /**
     * Loads a single location voting identified by the voting user and the id.
     *
     * @param currentUser the voting user
     * @param userVoteId  the id to search for
     * @return the voting by the given user with the specified id
     */
    public UserVoteLocation findUserVoteLocationByUserAndId(User currentUser, long userVoteId) {
        return userVoteLocationRepository.findUserVoteLocationByUserAndId(currentUser, userVoteId);
    }

    /**
     * Loads a single location voting identified by the voting user, the location and the event.
     *
     * @param currentUser the voting user
     * @param event       the event which the location/voting is assigned to
     * @param location    the location to search the voting for
     * @return the voting for the given location, by the given user for the specified event
     */
    public UserVoteLocation findUserVoteLocationByUserAndEventAndLocation(User currentUser, Event event, Location location) {
        return userVoteLocationRepository.findUserVoteLocationByUserAndEventAndLocation(currentUser, event, location);
    }

    /**
     * Loads a single location voting identified by the event.
     *
     * @param event the event to find the voting count of locations
     * @return the voting for the given location, by the event
     */
    public Collection<UserVoteLocation> findUserVoteLocationsByEvent(Event event) {
        return userVoteLocationRepository.findUserVoteLocationsByEvent(event);
    }

    /**
     * Loads a single location voting identified by the event and the location.
     *
     * @param event    the event connected to the desired location
     * @param location the location to search the voting for
     * @return the voting for the given location, by the given event
     */
    public Collection<UserVoteLocation> findUserVoteLocationsByEventAndLocation(Event event, Location location) {
        return userVoteLocationRepository.findUserVoteLocationsByEventAndLocation(event, location);
    }

    /**
     * Calculates the total location voting count for the specified event
     *
     * @param event    the event to calculate the voting for
     * @param location the location to get the total voting count
     * @return the total voting count for a given location
     */
    public int getTotalVoting(Event event, Location location) {
        Collection<UserVoteLocation> votesByEventLocation = findUserVoteLocationsByEventAndLocation(event, location);

        return votesByEventLocation
                .stream()
                .filter(userVoteLocation -> userVoteLocation.getUser().isEnabled())
                .mapToInt(UserVoteLocation::getVotingCount)
                .sum();

    }

    /**
     * Calculates the average voting for a given location
     *
     * @param event    the event connected to the desired location
     * @param location the location to calculate the average for
     * @return the average voting count for the given location
     */
    public double getAverageVoting(Event event, Location location) {
        int countVotesLocation = getTotalVoting(event, location);
        double countUsersAccepted = 0;

        for (UserInvited userInvited : event.getUserInvited()) {
            if (userInvited.getInvitationAccepted()) {
                countUsersAccepted += 1;
            }
        }
        if(countUsersAccepted == 0){
            return countVotesLocation;
        }else{
            return countVotesLocation / countUsersAccepted;
        }
    }

    /**
     * Sets a new voting amount for the user voting on the given location
     *
     * @param request      the userVoteLocation request to patch the voting
     * @param amountToVote the new amount to be saved for the user voting
     */
    public void patchVoting(UserVoteLocation request, int amountToVote) {
        UserVoteLocation userVoteFromDb = userVoteLocationRepository.findUserVoteLocationByUserAndId(userService.getAuthenticatedUser(), request.getVotingId());
        userVoteFromDb.setVotingCount(amountToVote);
        userVoteLocationRepository.save(userVoteFromDb);
    }

    public List<String> getUserLocationVoting(Event event, Location location){
      Collection<UserVoteLocation> userVotes=  userVoteLocationRepository.findUserVoteLocationsByEventAndLocation(event, location);
        List<String> userVotesList = new ArrayList<>();
        for (UserVoteLocation u : userVotes){
            if(u.getVotingCount() > 0) {
                userVotesList.add(u.getUser().getUsername() + " [ " + u.getVotingCount() + " ]");
            }
        }
       return userVotesList;
    }

}
