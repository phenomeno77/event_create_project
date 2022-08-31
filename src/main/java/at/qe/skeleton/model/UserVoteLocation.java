package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class UserVoteLocation implements Persistable<String>, Serializable, Comparable<UserVoteLocation> {

    @Id
    @SequenceGenerator(
            name = "user_vote_location_sequence",
            sequenceName = "user_vote_location_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_vote_location_sequence"

    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "voting_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public int compareTo(UserVoteLocation o) {
        return Comparator.comparing(UserVoteLocation::getId).compare(this, o);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserVoteLocation)) return false;
        UserVoteLocation that = (UserVoteLocation) o;
        return Objects.equals(getId(), that.getId())
                && getVotingCount() == that.getVotingCount()
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getLocation(), that.getLocation())
                && Objects.equals(getEvent(), that.getEvent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
