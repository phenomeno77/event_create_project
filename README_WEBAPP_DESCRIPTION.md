============== README FILE ==============

THIS IS A DESCRIPTION OF OUR WEB APPLICATION AND THE GOOD TO KNOW STUFF TO TEST.
LOGIN: admin
PASSWORD: passwd
FURTHER USERS TO LOGIN: user1, user2, elvis
PASSWORD: passwd


ABOUT THE INITIALIZER BUTTONS
    To help the person is willing to test the Application, we decided to let the initializer buttons on the
    Location page as well as on the My Events page. We decided to do that way instead of hardcoding the data
    in the data.sql file, because of the purpose of the junit5 tests. That makes our tests more readable and
    clear instead of having bunch extra code there.
    ATTENTION: The Button to initialize Locations is viewed by Admins and Location Managers. The buttons
    to initialize events is viewable only by Admins.

INITIALIZE TEST-EVENT 1 AND TEST-EVENT 2 BUTTONS
    The Button Initialize Test-Event 1 will create an Event where only the user Elvis is not invited to that event
    and the Event Creator is the User1. The Button Initialize-Event 2 will create an Event where all users are invited
    to that Event and the Event Creator is the Admin. Both events will does not initialize Locations too. In order to
    create an Event with Locations, the Tester has first to initialize the Locations from the Location Page and then
    click a button to initialize an event. Each event will choose to have the first location with ID=1 and the second
    with ID=2. The Start and End time for the timeslots are set to be Start Time = time now and
    End Time = now + two hours. If the Tester wants to test the email sending, then he/she has to change the dummy
    emails of the users invited to a real ones.

INITIALIZE TEST-LOCATIONS BUTTON
    The Button initialises two locations. The Mensa at Innrain and the Mensa at Technikstraße. Both location provide
    a the correct address for the gmap to track in real time. If the Tester chooses he/she can alter the address in 
    the edit dialog to change the location of the marker on the map. At the initialisation the locations have the status 
    active for the event to have two locations to choose from. In the expension row the tester can find two possibilities of 
    showing opening hours. Mensa Campus Technik has in the afternoon a three hour break and therefore two opening hours 
    per day. Mensa Campus Innrain has no breaks and therefore only one slot each day.


USER ROLES
    - ADMIN
    - LOCATION_MANAGER
    - USER

ABOUT USER
    The User Tab is visible only by Users with Admin authorities. Users can be edited, removed, deleted or
    new created. By creating a new User a validation about the username as well as for the email address
    will run in the background to check if either the username or email address exists. Also a regex
    validation in xhtml about the email address.
    Editing an existing User will also check if the email address is taken by another User. If an User has been
    removed the email address is still shown in the data table, but it is available to user it by other user.
    Removing or banning a user who is the event creator of an active event, a dialog box with a spinning circle
    will appear. This may take some time, because of the email sending with the information to the users invited.

ABOUT ALL EVENTS - ADMIN ACTION TAB
    This is a tab visible only by Admins. There admin can see all events(active and non active). They have the rights
    to cancel or to end the event by clicking on the corresponding button.

ABOUT MY EVENTS TAB
    On that page there are actually 4 different buttons visible. The 2 Buttons to initialize events are visible only
    by Admins. By clicking on the Button + New Event the user will be redirected to the event create page.

    1) Tab Event Title: The user has to add an Event title at least 5 characters long.

    2) Tab Date and Timeslots: The user can select the timeslots for the event with a start time and end time.
    The validator will set the min date to be today and by selecting one of the start or end times the validator
    will set the other on one hour plus or minus. So that the user cannot select Start time to be 10:00 and end time
    to be 09:00. At least one Date and one Start time - End time has to be chosen to create valid timeslots.

    3)Tab Locations: The user can choose the preferred locations by just clicking on the row of the location.
    Each time the user selects one location a validation will run and will give an output to the user about which
    Timeslots are common for all selected locations together. If a location has been closed, removed or deleted
    wont be shown in the table as available to select.

    4)Tab Invite Users: Here can the user invite the users he want to have by just click on the row of the corresponding
    user. If a user is banned, removed or deleted wont be shown in the table as available to invite.

    5)Tag Voting End-Time: Here the user can set when the voting event should be end. The min date time to set, is set
    to be the day today and the max time is set to be the min date time of the timeslots. So that the user cannot pick
    an expiry date time to be before today or after the first timeslot.

    6)Tab Confirmation: Here the user can re-check all the input info about the Event e.g. Timeslots, Locations
    selected etc... The user can step back and change everything if he/she wants. There must be at least one valid
    common timeslot present to create the event. By clicking the Start Voting Event the user creates the event
    and confirmation will be sent to the invited users.

    On My Events homepage only Active Events will appear, where the current logged in user has been invited to and
    has accepted the email confirmation, otherwise the user will see no events.
    In the last column of each event there will appear 1 - 3 Buttons (Vote Now, Cancel Event and End Event), depending
    on the user. That means that only the event creator can see the buttons to cancel the event or to end it earlier.
    All others included event creator are seeing the Vote Now button.
    By clicking the Vote Now button the user will be redirected to the voting homepage where each user can vote.
    Two tables will appear with one the Locations to vote and the other the Timeslots. The voting event has a rating
    system with 5 stars. Users can vote by click on the stars. Users can change their mind as long as the event is
    still active.
    If the event is expired by itself or ended by the user creator, then the results where and when to eat will be sent
    to all invited users and the event will be shown in the history homepage with all the information. If an event got
    canceled, so no voting results will be count and all users will be informed about that.
    Further Cases where a Location has been removed, delete or closed while the event is running or expired, are described
    in the README_REMOVING_ENTITY file.




