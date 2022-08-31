package at.qe.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Basic request scoped bean to test bean initialization.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("request")
public class HelloWorldBean {

    private String welcome;

    /**
     * Returns a hello-world-string.
     *
     * @return hello-world-string
     */
    public String getHello() {
         welcome = "Here you can create Events by choosing Locations, Timeslots as well as inviting other Users to Vote.";
        return welcome;
    }

    public String getWelcomeToEvent(){
        welcome = "Welcome to Event Page!";
        return welcome;
    }
}
