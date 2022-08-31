package at.qe.skeleton.controllers;

import at.qe.skeleton.model.Event;
import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.model.User;
import at.qe.skeleton.services.*;
import at.qe.skeleton.ui.beans.AutoInitLocations;
import at.qe.skeleton.ui.beans.TimeslotsLocationsValidationBean;
import at.qe.skeleton.ui.controllers.EventAddController;
import org.joinfaces.test.mock.JsfMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@WebAppConfiguration
@SpringBootTest
public class EventAddControllerTest {
    public JsfMock jsfMock = new JsfMock();

    @Autowired
    TimeSlotService timeSlotService;

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    LocationService locationService;

    @Autowired
    AutoInitLocations autoInitLocations;

    @Autowired
    TimeslotsLocationsValidationBean timeslotsLocationsValidationBean;

    @Autowired
    EmailService emailService;

    @Autowired
    ApplicationContext applicationContext;

    EventAddController controller = new EventAddController();

    @BeforeEach
    public void init() {
        jsfMock.init(applicationContext);

        controller.setServices(eventService,userService,emailService,taskScheduler,timeSlotService,
                timeslotsLocationsValidationBean);

        EmailService emailServiceMocked = Mockito.mock(EmailService.class);
        controller.setEmailService(emailServiceMocked);
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testSetterGetterEvent() {
        Event newEvent = controller.getEvent();

        Assertions.assertNull(controller.getEvent().getEventName());

        newEvent.setEventName("TEST");
        controller.setEvent(newEvent);

        Assertions.assertEquals("TEST",controller.getEvent().getEventName());

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testEventCreatorAdmin() {

        controller.initEventCreator();

        Assertions.assertEquals("admin",controller.getEventCreator().getUsername());

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testEventCreator() {

        controller.initEventCreator();

        Assertions.assertEquals("user1",controller.getEventCreator().getUsername());

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testIsTimeslotNotEmpty() throws ParseException {
        controller.setEvent(new Event());

        Assertions.assertFalse(controller.isTimeSlotsNotEmpty(),"No timeslots there, so should be false");

        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        Assertions.assertTrue(controller.isTimeSlotsNotEmpty(),"After combining the dates and times there should be valid timeslots");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testGetCombinedTimeslots() throws ParseException {
        controller.setEvent(new Event());

        Assertions.assertEquals(0,controller.getCombinedTimeslotsList().size(),"No timeslots there, so should be size of 0");

        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        Assertions.assertEquals(2,controller.getCombinedTimeslotsList().size(),"After combining the dates and times there should be 2 timeslots in the list");
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testFormattedDateTime() throws ParseException {
        controller.setEvent(new Event());

        Set<String> expectedOutput = new HashSet<>();

        Assertions.assertEquals(expectedOutput,controller.getFormattedDateTime(),"No timeslots there, so both lists should be empty");

        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        final SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyy");
        final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        for(Timeslot timeslot : controller.getValidCommonDateTimes()){
            String day =dayFormatter.format( timeslot.getStartTime());
            String startTime = timeFormatter.format(timeslot.getStartTime());
            String endTime = timeFormatter.format(timeslot.getEndTime());
            expectedOutput.add(
                    day + " " + startTime + " - " + endTime + "\n"
            );
        }

        Assertions.assertEquals(expectedOutput,controller.getFormattedDateTime());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testGetterSetterLocationsSelected() {
        controller.setEvent(new Event());

        autoInitLocations.initializeLocations();

        Location location1 = locationService.loadLocation(1);
        Location location2 = locationService.loadLocation(2);

        Assertions.assertEquals(0,controller.getLocationsSelected().size());

        controller.getLocationsSelected().add(location1);

        Assertions.assertEquals(1,controller.getLocationsSelected().size());

        controller.getLocationsSelected().add(location1);

    }





    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventFailedWhenTimeslotValidationMissing() throws IOException {

        User adminUser = userService.loadUser("admin");

        controller.getEvent().setUser(adminUser);

        controller.getEvent().setEventName("Lunch IT-Department");

        long oneDay = 24 * 60 * 60 * 1000;

        Date date1 = new Date(new Date().getTime() + oneDay*4);
        Date date2 = new Date(new Date().getTime() + oneDay*5);


        Calendar calendar = Calendar.getInstance();

       controller.initTimeslots();

        Timeslot timeslot;

        timeslot = new Timeslot();
        timeslot.setStartTime(date1);

        calendar.setTime(date1);
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeslot.setEndTime(calendar.getTime());
        controller.getSelectedTimeslotList().add(this.timeSlotService.saveTimeslot(timeslot));

        timeslot = new Timeslot();
        calendar.setTime(date2);
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        timeslot.setStartTime(calendar.getTime());

        calendar.setTime(date2);
        calendar.add(Calendar.HOUR_OF_DAY, 4);
        timeslot.setEndTime(calendar.getTime());

        controller.getSelectedTimeslotList().add(this.timeSlotService.saveTimeslot(timeslot));

       controller.getEvent().setTimeslots(new HashSet<>(controller.getSelectedTimeslotList()));

        controller.doSaveEvent();

        Assertions.assertEquals("Timeslots not Available",
                controller.getMessageInfo(),
                "Creating an event with timeslot validation should not create the event.");

        Assertions.assertEquals(0,eventService.getAllEvents().size());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventDoCombineTimeslotsMethod() throws ParseException {
        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        Assertions.assertEquals(2,controller.getCombinedTimeslotsList().size(),"We have 2 days and 1 timeslot, the cross product of days x timeslots should be 2.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testEventAddNotEmptyTimeslotList() throws ParseException {

        Assertions.assertFalse(controller.isTimeSlotsNotEmpty(),"At first the variable should be false, because no combined timeslots created yet.");

        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        Assertions.assertTrue(controller.isTimeSlotsNotEmpty(),"After creating combined timeslots successfully the variable will be set to true");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventValidateTimeslotsWithOpeningHours() throws ParseException {

        //first we have to initialize locations, with the help of the managed bean
        autoInitLocations.initializeLocations();

        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        //combining the timeslots, the result should be 24-01-2022 19:00,24-01-2022 20:00,25-01-2022 19:00,25-01-2022 20:00
        controller.doCombineDateTime();

        List<Timeslot> combinedTimeslots = controller.getCombinedTimeslotsList();
        List<Location> allLocation = new ArrayList<>(locationService.getAllLocations());

        //validate the timeslots with the opening hours from locations using the bean which takes the locations and the combined timeslots as parameters
        List<Timeslot> commonValidTimeslots = timeslotsLocationsValidationBean.validateEachLocationTimeslot(allLocation,combinedTimeslots);

        Assertions.assertEquals(2,commonValidTimeslots.size(),"Both location are opened at 24th and 25th at from 19:00 to 20:00 o'clock, " +
                "so the return result should have 2 common-valid timeslots");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventValidateTimeslotsWithOpeningHoursToFail() throws ParseException {

        //first we have to initialize locations, with the help of the managed bean
        autoInitLocations.initializeLocations();

        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 18:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        //combining the timeslots, the result should be 24-01-2022 19:00,24-01-2022 20:00,25-01-2022 19:00,25-01-2022 20:00
        controller.doCombineDateTime();

        List<Timeslot> combinedTimeslots = controller.getCombinedTimeslotsList();
        List<Location> allLocation = new ArrayList<>(locationService.getAllLocations());

        //validate the timeslots with the opening hours from locations using the bean which takes the locations and the combined timeslots as parameters
        List<Timeslot> commonValidTimeslots = timeslotsLocationsValidationBean.validateEachLocationTimeslot(allLocation,combinedTimeslots);

        Assertions.assertEquals(0,commonValidTimeslots.size(),"One location has from 15:00 - 18:00 break time, the result of validation will be zero" +
                "common valid timeslots for both locations, since there are no common timeslots");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventExpiryMaxDate() throws ParseException {
        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 18:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        controller.doCombineDateTime();

        Date maxExpiryDate = controller.getMaxExpiryDate();

        //the smallest date is 24/01/2022 16:00 from the timeslots above
        Date maxExpiryDateExpected =  new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("24-Jan-2022 16:00:00");

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyy HH:mm");

        Assertions.assertEquals(dateTimeFormatter.format(maxExpiryDate),dateTimeFormatter.format(maxExpiryDateExpected),
                "The max expiry date should be equal to the min of the selected days." +
                        "No user should be able to set an expiry date after the date of the min Date from the selected timeslots.");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventCreatedSuccessfully() throws ParseException, IOException {
        //first we have to initialize locations, with the help of the managed bean
        autoInitLocations.initializeLocations();

        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 19:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        //combining the timeslots, the result should be 24-01-2022 19:00,24-01-2022 20:00,25-01-2022 19:00,25-01-2022 20:00
        controller.doCombineDateTime();

        List<Timeslot> combinedTimeslots = controller.getCombinedTimeslotsList();
        List<Location> allLocation = new ArrayList<>(locationService.getAllLocations());

        //validate the timeslots with the opening hours from locations using the bean which takes the locations and the combined timeslots as parameters
        List<Timeslot> commonValidTimeslots = timeslotsLocationsValidationBean.validateEachLocationTimeslot(allLocation,combinedTimeslots);
        controller.getValidCommonDateTimes().addAll(commonValidTimeslots);

        controller.getEvent().setEventName("Test Event 1");

        //inviting all users from db except admin, user creator is auto invited
        List<User> usersToInvite = userService.getAllUsers().stream().filter(user -> !user.getUsername().equals("admin")).collect(Collectors.toList());

        User eventCreator = userService.loadUser("admin");

        controller.setEventCreator(eventCreator);

        controller.setUsersSelected(usersToInvite);

        Assertions.assertEquals(3,controller.getUsersSelected().size());

        controller.setExpiryDateTime(controller.getMaxExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        controller.doSaveEvent();

        Assertions.assertEquals(4,eventService.findEventByEventName("Test Event 1").getUserInvited().size(),"Number of invited users should be 4, Event creator + 3 invited users");
       Assertions.assertEquals(1,eventService.getAllEvents().size(),"One event should be successfully created");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAddEventFailedToSaveNoCommonTimeslots() throws ParseException, IOException {
        //first we have to initialize locations, with the help of the managed bean
        autoInitLocations.initializeLocations();

        //creating dates and times
        String date1="24/01/2022"; //Monday
        String date2="25/01/2022"; //Tuesday
        String time1 = "24-Jan-2022 16:00:00";
        String time2 = "24-Jan-2022 20:00:00";

        Date parsedDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1);
        Date parsedDate2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2);
        Date parsedTime1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time1);
        Date parsedTime2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(time2);

        Timeslot timeslot1 = new Timeslot();
        timeslot1.setStartTime(parsedTime1);
        timeslot1.setEndTime(parsedTime2);

        controller.getSelectedDays().add(parsedDate1);
        controller.getSelectedDays().add(parsedDate2);

        controller.initTimeslots();

        controller.getSelectedTimeslotList().get(0).setStartTime(parsedTime1);
        controller.getSelectedTimeslotList().get(0).setEndTime(parsedTime2);

        //combining the timeslots, the result should be 24-01-2022 19:00,24-01-2022 20:00,25-01-2022 19:00,25-01-2022 20:00
        controller.doCombineDateTime();

        List<Timeslot> combinedTimeslots = controller.getCombinedTimeslotsList();
        List<Location> allLocation = new ArrayList<>(locationService.getAllLocations());

        //validate the timeslots with the opening hours from locations using the bean which takes the locations and the combined timeslots as parameters
        List<Timeslot> commonValidTimeslots = timeslotsLocationsValidationBean.validateEachLocationTimeslot(allLocation,combinedTimeslots);
        controller.getValidCommonDateTimes().addAll(commonValidTimeslots);

        controller.getEvent().setEventName("Test Event 1");

        //inviting all users from db except admin, user creator is auto invited
        List<User> usersToInvite = userService.getAllUsers().stream().filter(user -> !user.getUsername().equals("admin")).collect(Collectors.toList());

        User eventCreator = userService.loadUser("admin");

        controller.setEventCreator(eventCreator);

        controller.setUsersSelected(usersToInvite);

        controller.setExpiryDateTime(controller.getMaxExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        controller.doSaveEvent();

        Assertions.assertEquals(0,eventService.getAllEvents().size(),"There are no common timeslots because one location is closed on one of the timeslots selected." +
                "without at least one common timeslot event should not be created.");
        Assertions.assertEquals("Timeslots not Available", controller.getMessageInfo());
        Assertions.assertEquals("Dear User. The Timeslots you chosen are not available. " +
                "In order to create a successfully event, please select at least one valid Timeslot.", controller.getFacesMessage());

    }


}
