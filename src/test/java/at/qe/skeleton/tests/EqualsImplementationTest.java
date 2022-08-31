package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the
 * contract. See {@linkplain http://www.jqno.nl/equalsverifier/} for more
 * information.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
public class EqualsImplementationTest {

    @Test
    public void testUserEqualsContract() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Event event1 = new Event();
        event1.setEventId(1);
        Event event2 = new Event();
        event2.setEventId(2);

        Location location1 = new Location();
        location1.setLocationId(1);

        Location location2 = new Location();
        location2.setLocationId(2);

        EqualsVerifier.forClass(User.class).withPrefabValues(User.class, user1, user2).withPrefabValues(Event.class, event1, event2).withPrefabValues(Location.class, location1, location2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testUserRoleEqualsContract() {
        EqualsVerifier.forClass(UserRole.class).verify();
    }

}