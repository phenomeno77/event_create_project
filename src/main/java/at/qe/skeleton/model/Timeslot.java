package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Timeslot implements Persistable<Integer>, Serializable, Comparable<Timeslot> {

    @Id
    @SequenceGenerator(
            name = "timeslot_sequence",
            sequenceName = "timeslot_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "timeslot_sequence"

    )
    private int timeslotId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    private int timeSlotVotingCount;


    /**
     * User votes for Timeslot relation
     */
    @OneToMany(mappedBy = "timeslot", cascade = CascadeType.ALL)
    private Set<UserVoteTimeslot> userVoteTimeslots = new HashSet<>();


    /**
     * Event has many timeslots relation
     */
    @ManyToMany(mappedBy = "timeslots")
    private Set<Event> events = new HashSet<>();

    @Override
    public Integer getId() {
        return getTimeslotId();
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public int getTimeslotId() {
        return timeslotId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Set<UserVoteTimeslot> getUserVoteTimeslots() {
        return userVoteTimeslots;
    }

    public int getTimeSlotVotingCount() {
        return timeSlotVotingCount;
    }

    public void setTimeSlotVotingCount(int timeSlotVotingCount) {
        this.timeSlotVotingCount = timeSlotVotingCount;
    }

    public void setTimeslotId(int timeslotId) {
        this.timeslotId = timeslotId;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setUserVoteTimeslots(Set<UserVoteTimeslot> userVoteTimeslots) {
        this.userVoteTimeslots = userVoteTimeslots;
    }

    @Override
    public int compareTo(Timeslot o) {
        return Comparator.comparing(Timeslot::getTimeslotId).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeslot)) return false;
        Timeslot timeslot = (Timeslot) o;
        return getTimeslotId() == timeslot.getTimeslotId()
                && getTimeSlotVotingCount() == timeslot.getTimeSlotVotingCount()
                && Objects.equals(getStartTime(), timeslot.getStartTime())
                && Objects.equals(getEndTime(), timeslot.getEndTime());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimeslotId());
    }

    @Override
    public String toString() {
        return "Timeslot{" +
                "timeslotId=" + timeslotId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", timeSlotVotingCount=" + timeSlotVotingCount +
                ", events=" + events +
                '}';
    }

    public void addEventToTimeslot(Event event) {
        events.add(event);
    }
}
