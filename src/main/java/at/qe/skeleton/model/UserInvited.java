package at.qe.skeleton.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class UserInvited implements Persistable<String>, Serializable, Comparable<UserInvited> {

    @Id
    @SequenceGenerator(
            name = "user_invited_sequence",
            sequenceName = "user_invited_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_invited_sequence"
    )
    private long id;

    @ManyToOne
    @JoinColumn(name = "invited_user")

    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id")
    private Event event;

    private boolean invitationAccepted;
    private String token;

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

    public boolean isInvitationAccepted() {
        return invitationAccepted;
    }

    public void setInvitationAccepted(boolean invitationAccepted) {
        this.invitationAccepted = invitationAccepted;
    }


    public boolean getInvitationAccepted() {
        return this.invitationAccepted;
    }


    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "UserInvited{" +
                "id=" + id +
                ", user=" + user +
                ", event=" + event +
                ", invitationAccepted=" + invitationAccepted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInvited)) return false;
        UserInvited that = (UserInvited) o;
        return Objects.equals(getId(), that.getId())
                && isInvitationAccepted() == that.isInvitationAccepted()
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getEvent(), that.getEvent())
                && Objects.equals(getToken(), that.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(UserInvited o) {
        return Comparator.comparing(UserInvited::getId).compare(this, o);
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
