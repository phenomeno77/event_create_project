package at.qe.skeleton.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Location implements Persistable<String>, Serializable, Comparable<Location> {

    /**
     * Generates the pk automatically (auto-increment)
     */
    @Id
    @SequenceGenerator(
            name = "location_sequence",
            sequenceName = "location_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "location_sequence"

    )
    private int locationId;

    /**
     * User votes for Location relation
     */
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private Set<UserVoteLocation> userVoteLocations = new HashSet<>();


    /**
     * Relation: User creates Location (Many side)
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by")
    private User user;


    /**
     * Event has many locations relation
     */
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "event_locations",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();


    /**
     * Location has many tags relation
     **/

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "location_tags",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )


    private Set<Tags> locationTags = new HashSet<>();

    @Column(nullable = false)
    private String locationName;

    private boolean isActive = true;

    private String description;

    private String menuUrl;

    @Column(nullable = false)

    private int zipCode;
    private int houseNumber;
    private String street;
    private String city;
    private String country;
    private String locationStatus = "Active";


    public void removeEvent(Event event) {
        events.remove(event);
        event.getEventLocations().remove(this);
    }

    public void removeTag(Tags tag) {
        locationTags.remove(tag);
        tag.getLocations().remove(this);
    }

    public String getLocationStatus() {
        return locationStatus;
    }
    
    public void setLocationStatus(String locationStatus) {
        this.locationStatus = locationStatus;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void setLocationTags(Set<Tags> locationTags) {
        this.locationTags = locationTags;
    }

    public User getUser() {
        return user;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Set<Tags> getLocationTags() {
        return locationTags;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public void addTagToLocation(Tags tag) {
        locationTags.add(tag);
    }

    @Override
    public int compareTo(Location o) {
        return Comparator.comparing(Location::getId).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return getLocationId() == location.getLocationId()
                && isActive() == location.isActive()
                && getZipCode() == location.getZipCode()
                && getHouseNumber() == location.getHouseNumber()
                && Objects.equals(getUser(), location.getUser())
                && Objects.equals(getLocationName(), location.getLocationName())
                && Objects.equals(getDescription(), location.getDescription())
                && Objects.equals(getMenuUrl(), location.getMenuUrl())
                && Objects.equals(getStreet(), location.getStreet())
                && Objects.equals(getCity(), location.getCity())
                && Objects.equals(getCountry(), location.getCountry())
                && Objects.equals(getLocationStatus(), location.getLocationStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationId());
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", user=" + user +
                ", events=" + events +
                ", locationTags=" + locationTags +
                ", locationName='" + locationName + '\'' +
                ", isActive=" + isActive +
                ", description='" + description + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                '}';
    }
}
