package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class UserVotesFor implements Persistable<String>, Serializable, Comparable<UserVotesFor> {


    @Id
    @SequenceGenerator(
            name = "user_votes_sequence",
            sequenceName = "user_votes_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_votes_sequence"

    )
    private long id;


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

    @Override
    public String toString() {
        return "UserVotesFor{" +
                "id=" + id +
                ", user=" + user +
                ", event=" + event +
                '}';
    }

    @ManyToOne
    @JoinColumn(name = "voting_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private int votingCount;

    public int getVotingCount() {
        return votingCount;
    }

    public void setVotingCount(int votingCount) {
        this.votingCount = votingCount;
    }

    @Override
    public int compareTo(UserVotesFor o) {
        return Comparator.comparing(UserVotesFor::getId).compare(this, o);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserVotesFor)) return false;
        UserVotesFor that = (UserVotesFor) o;
        return Objects.equals(getId(), that.getId()) && getVotingCount() == that.getVotingCount() && Objects.equals(getUser(), that.getUser()) && Objects.equals(getEvent(), that.getEvent());
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
