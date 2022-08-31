package at.qe.skeleton.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

@Entity
public class OpeningHours implements Persistable<String>, Serializable, Comparable<OpeningHours> {

    /**
     * Generates the pk automatically (auto-increment)
     */
    @Id
    @SequenceGenerator(
            name = "openingHours_sequence",
            sequenceName = "openingHours_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "openingHours_sequence"

    )
    private int openingHoursId;

    /**
     * Relation: User creates OpeningHours (Many side)
     */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User user;

    /**
     * Relation: Location has OpeningHours (Many side)
     */
    @ManyToOne
    @JoinColumn(name = "assigned_location")
    private Location location;

    @Column(nullable = false)
    private int day;
    private LocalTime openingTime;
    private LocalTime closingTime;
    @Column()
    private LocalTime breakTimeStart;
    private LocalTime breakTimeEnd;

    public void setBreakTimeStart(LocalTime breakTimeStart) {
        this.breakTimeStart = breakTimeStart;
    }

    public void setBreakTimeEnd(LocalTime breakTimeEnd) {
        this.breakTimeEnd = breakTimeEnd;
    }

    public LocalTime getBreakTimeStart() {
        return breakTimeStart;
    }

    public LocalTime getBreakTimeEnd() {
        return breakTimeEnd;
    }

    public void setOpeningHoursId(int openingHoursId) {
        this.openingHoursId = openingHoursId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public int getOpeningHoursId() {
        return openingHoursId;
    }

    public User getUser() {
        return user;
    }

    public Location getLocation() {
        return location;
    }

    public int getDay() {
        return day;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    @Override
    public int compareTo(OpeningHours o) {
        return Comparator.comparing(OpeningHours::getId).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpeningHours)) return false;
        OpeningHours that = (OpeningHours) o;
        return getOpeningHoursId() == that.getOpeningHoursId()
                && getDay() == that.getDay()
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getLocation(), that.getLocation())
                && Objects.equals(getOpeningTime(), that.getOpeningTime())
                && Objects.equals(getClosingTime(), that.getClosingTime())
                && Objects.equals(getBreakTimeStart(), that.getBreakTimeStart())
                && Objects.equals(getBreakTimeEnd(), that.getBreakTimeEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOpeningHoursId());
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
