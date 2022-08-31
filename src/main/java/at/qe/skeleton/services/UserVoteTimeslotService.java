package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.UserVoteTimeslotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service for accessing and manipulating user voting for desired timeslot.
 */

@Service
public class UserVoteTimeslotService {
    @Autowired
    UserVoteTimeslotRepository userVoteTimeslotRepository;

    @Autowired
    UserService userService;

    /**
     * Returns a collection of all user votes for Timeslots.
     */
    public Collection<UserVoteTimeslot> getAllUserVoteForTimeslot() {
        return userVoteTimeslotRepository.findAll();
    }

    /**
     * saves the new voting specified
     *
     * @param userVoteTimeslot the voting count to be saved
     * @return the updated user voting for the timeslot
     */
    public UserVoteTimeslot voteTimeslot(UserVoteTimeslot userVoteTimeslot) {
        return userVoteTimeslotRepository.save(userVoteTimeslot);
    }

    /**
     * Deletes the timeslot voting.
     *
     * @param userVoteTimeslot the user voting to delete
     */
    public void delete(UserVoteTimeslot userVoteTimeslot) {
        userVoteTimeslotRepository.delete(userVoteTimeslot);
    }

    /**
     * Loads a single timeslot voting identified by the voting user and the id.
     *
     * @param user the voting user
     * @param id   the id to search for
     * @return the voting by the given user with the specified id
     */
    public UserVoteTimeslot findUserVoteTimeslotByUserAndId(User user, long id) {
        return userVoteTimeslotRepository.findUserVoteTimeslotByUserAndId(user, id);
    }

    /**
     * Loads a single timeslot voting identified by the voting user, the timeslot and the event.
     *
     * @param user     the voting user
     * @param event    the event which the timeslot/voting is assigned to
     * @param timeslot the timeslot to search the voting for
     * @return the voting for the given timeslot, by the given user for the specified event
     */
    public UserVoteTimeslot findUserVoteTimeslotByUserAndEventAndTimeslot(User user, Event event, Timeslot timeslot) {
        return userVoteTimeslotRepository.findUserVoteTimeslotByUserAndEventAndTimeslot(user, event, timeslot);
    }

    /**
     * Loads a single timeslot voting identified by the event.
     *
     * @param event the event to find the voting count of timeslots
     * @return the voting for the given timeslot, by the event
     */
    public Collection<UserVoteTimeslot> findUserVoteTimeslotByEvent(Event event) {
        return userVoteTimeslotRepository.findUserVoteTimeslotByEvent(event);
    }

    /**
     * Loads a single timeslot voting identified by the event and the timeslot.
     *
     * @param event    the event connected to the desired timeslot
     * @param timeslot the timeslot to search the voting for
     * @return the voting for the given timeslot, by the given event
     */
    public Collection<UserVoteTimeslot> findUserVoteTimeslotByEventAndTimeslot(Event event, Timeslot timeslot) {
        return userVoteTimeslotRepository.findUserVoteTimeslotsByEventAndTimeslot(event, timeslot);
    }

    /**
     * Calculates the total timeslot voting count for the specified event
     *
     * @param event    the event to calculate the voting for
     * @param timeslot the timeslot to get the total voting count
     * @return the total voting count for a given timeslot
     */
    public int getTotalVoting(Event event, Timeslot timeslot) {
        Collection<UserVoteTimeslot> votesByEventTimeslot = findUserVoteTimeslotByEventAndTimeslot(event, timeslot);

        return votesByEventTimeslot
                .stream()
                .filter(userVoteTimeslot -> userVoteTimeslot.getUser().isEnabled())
                .mapToInt(UserVoteTimeslot::getVotingCount)
                .sum();
    }

    /**
     * Calculates the average voting for a given timeslot
     *
     * @param event    the event connected to the desired timeslot
     * @param timeslot the timeslot to calculate the average for
     * @return the average voting count for the given timeslot
     */
    public double getAverageVoting(Event event, Timeslot timeslot) {
        int countVotesLocation = getTotalVoting(event, timeslot);
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
     * Sets a new voting amount for the user voting on the given timeslot
     *
     * @param request      the userVoteTimeslot request to patch the voting
     * @param votingAmount the new amount to be saved for the user voting
     */
    public void patchTimeslotVoting(UserVoteTimeslot request, int votingAmount) {
        UserVoteTimeslot userVoteFromDb = userVoteTimeslotRepository.findUserVoteTimeslotByUserAndId(userService.getAuthenticatedUser(), request.getVotingId());
        userVoteFromDb.setVotingCount(votingAmount);
        userVoteTimeslotRepository.save(userVoteFromDb);
    }

    public List<String> getUserTimeslotVoting(Event event, Timeslot timeslot){
        Collection<UserVoteTimeslot> userVotes=  userVoteTimeslotRepository.findUserVoteTimeslotsByEventAndTimeslot(event, timeslot);
        List<String> userVotesList = new ArrayList<>();
        for (UserVoteTimeslot u : userVotes){
            if(u.getVotingCount() > 0)
            userVotesList.add(u.getUser().getUsername()+" [ " + u.getVotingCount()+" ]" );
        }

        return userVotesList;
    }

}
