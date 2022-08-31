package at.qe.skeleton.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity

public class Event implements Persistable<String>, Serializable, Comparable<Event> {

    @Id
    @SequenceGenerator(
            name = "event_sequence",
            sequenceName = "event_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_sequence"

    )
    private int eventId;

    @Column(nullable = false)
    private String eventName;

    //    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date votingExpiry;

    /**
     * User invited to event relation
     **/
//    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserInvited> userInvited = new HashSet<>();

    /**
     * Event voted by user relation
     **/
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<UserVotesFor> userVotesFor = new HashSet<>();

    /**
     * Location voted by user relation
     **/
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<UserVoteLocation> userVoteLocations = new HashSet<>();

    /**
     * Timeslot voted by user relation
     **/
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<UserVoteTimeslot> userVoteTimeslots = new HashSet<>();


    public Set<UserVoteLocation> getUserVoteLocations() {
        return userVoteLocations;
    }

    public void setUserVoteLocations(Set<UserVoteLocation> userVoteLocations) {
        this.userVoteLocations = userVoteLocations;
    }

    /**
     * Many to many relation for timeslots
     **/

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_timeslots",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id")
    )
    private Set<Timeslot> timeslots = new HashSet<>();

    /**
     * Many to many relation for timeslots
     **/
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_locations",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> eventLocations = new HashSet<>();

    /**
     * Event created by ... relation
     **/

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "created_by")
    private User user;


    public Set<UserVoteTimeslot> getUserVoteTimeslots() {
        return userVoteTimeslots;
    }

    public void setUserVoteTimeslots(Set<UserVoteTimeslot> userVoteTimeslots) {
        this.userVoteTimeslots = userVoteTimeslots;
    }

    private boolean isActive = true;

    private String eventStatus = "Active";

    private String eventLocationWinner;

    private String eventTimeslotWinner;

    private int winnerLocationId;

    private boolean votingDone;

    public int getWinnerLocationId() {
        return winnerLocationId;
    }

    public void setWinnerLocationId(int winnerLocationId) {
        this.winnerLocationId = winnerLocationId;
    }

    public String getEventTimeslotWinner() {
        return eventTimeslotWinner;
    }

    public void setEventTimeslotWinner(String eventTimeslotWinner) {
        this.eventTimeslotWinner = eventTimeslotWinner;
    }

    public boolean isVotingDone() {
        return votingDone;
    }

    public void setVotingDone(boolean votingDone) {
        this.votingDone = votingDone;
    }

    public String getEventLocationWinner() {
        return eventLocationWinner;
    }

    public void setEventLocationWinner(String eventLocationWinner) {
        this.eventLocationWinner = eventLocationWinner;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getVotingExpiry() {
        return votingExpiry;
    }

    public void setVotingExpiry(Date votingExpiry) {
        this.votingExpiry = votingExpiry;
    }

    public void setUserInvited(Set<at.qe.skeleton.model.UserInvited> userInvited) {
        this.userInvited = userInvited;
    }


    public Set<at.qe.skeleton.model.UserInvited> getUserInvited() {
        return userInvited;
    }

    public Set<UserVotesFor> getUserVotesFor() {
        return userVotesFor;
    }

    public Set<Timeslot> getTimeslots() {
        return timeslots;
    }

    public Set<Location> getEventLocations() {
        return eventLocations;
    }

    public User getUser() {
        return user;
    }

    public void setUserVotesFor(Set<UserVotesFor> userVotesFor) {
        this.userVotesFor = userVotesFor;
    }

    public void setTimeslots(Set<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }

    public void setEventLocations(Set<Location> eventLocations) {
        this.eventLocations = eventLocations;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addLocationToEvent(Location location) {
        eventLocations.add(location);
    }

    public void addTimeslotToEvent(Timeslot timeslot) {
        timeslots.add(timeslot);
    }


    @Override
    public int compareTo(Event o) {
        return Comparator.comparing(Event::getEventId).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return getEventId() == event.getEventId()
                && isActive() == event.isActive()
                && getWinnerLocationId() == event.getWinnerLocationId()
                && isVotingDone() == event.isVotingDone()
                && Objects.equals(getEventName(), event.getEventName())
                && Objects.equals(getVotingExpiry(), event.getVotingExpiry())
                && Objects.equals(getUser(), event.getUser())
                && Objects.equals(getEventStatus(), event.getEventStatus())
                && Objects.equals(getEventLocationWinner(), event.getEventLocationWinner())
                && Objects.equals(getEventTimeslotWinner(), event.getEventTimeslotWinner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId());
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}