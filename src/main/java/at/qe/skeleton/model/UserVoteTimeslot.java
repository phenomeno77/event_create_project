package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class UserVoteTimeslot implements Persistable<String>, Serializable, Comparable<UserVoteTimeslot> {

    @Id
    @SequenceGenerator(
            name = "user_vote_timeslot_sequence",
            sequenceName = "user_vote_timeslot_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_vote_timeslot_sequence"

    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "voting_user")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "timeslot_id")
    private Timeslot timeslot;

    private int votingCount;


    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public int getVotingCount() {
        return votingCount;
    }

    public void setVotingCount(int votingCount) {
        this.votingCount = votingCount;
    }

    public long getVotingId() {
        return id;
    }


    @Override
    public int compareTo(UserVoteTimeslot o) {

        return Comparator.comparing(UserVoteTimeslot::getId).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserVoteTimeslot)) return false;
        UserVoteTimeslot that = (UserVoteTimeslot) o;
        return Objects.equals(getId(), that.getId())
                && getVotingCount() == that.getVotingCount()
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getEvent(), that.getEvent())
                && Objects.equals(getTimeslot(), that.getTimeslot());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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
