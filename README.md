This is an Web Application Project i had to do in a team of 4 People (3 + me).
The parts i've worked on were the Front-End, where i used xhtml with JSF and Primefaces extensions, 
as well as on Back-End using JAVA (Maven project). We used H2 in memory database to keep the project simple
and light. 

Project Description (Maven Project / JAVA) and the Tasks we had to cover

Description:
When organizing a “meal event” (e.g.: common Lunch for all employees in a research group), that requires in principle
two  Decisions, namely WHERE (local) and WHEN to eat (time). Both choices are made mainly by the people participating in the event, i.e. the creator
of such an event determines a selection of locations and timeslots, which he then gives to the others submitted to participants for voting. At the end, the location and the timeslot are chosen with the most votes.

TASKS:
• A web application is to be created with which food events are created and can be maintained. This application should have a central server backend,
which persists the data and contains the business logic. Also, for the users a front-end can be developed that runs in the most common browsers.

• There are basically three roles in the system: The administrator, who controls all functionalities of the system and is responsible for user administration. Furthermore there is the location manager, who is responsible for the administration (creation, editing, deletion) of the locations responsible is. The location manager is also responsible for maintaining “food tags” (e.g.: Asian, Italian cuisine, ...), with which the individual restaurants are provided able, responsible. 
User can create and manage events, can be part of an event and can take part in voting.

• There are three main topics: user administration, local administration and the organization of the food events.

• The user administration offers the possibility of creating new users and editing them and delete. Furthermore, the administrator should assign roles to the 
new users or the roles of the users can also be removed again. The users should be displayed in a filterable and sortable table. Each user should have at least one
Username, password and e-mail address are saved. Notifications for the user are realized via emails and to the stored Email address sent.

• Local administration shall have functionality to create, edit and delete be provided locally. In addition to the name and description of the restaurant, additional
the URL to a menu (if available) and the geographic location of the establishment get saved. It is also important to record the opening hours of the restaurants. Also the Local administration should provide a filterable or sortable overview list of all locales. In addition, the system should be able to manage “food tags” 
(e.g.: Asian, Italian cuisine, ...), which can be used to tag the local. In the Detailed view of a restaurant should also be integrated with Google Maps and the position of the premises based on the geographic data collected.

• The organization of the event lunches should proceed as follows: 
A any user can create a new event. To do this, he chooses from the list of available locations and also determines possible timeslots. Included
the system checks whether the selected timeslots match the opening times of the bars are compatible and displays a corresponding message in the event of an error. Subsequently loads the user enters other participants with whom he would like to have lunch together. the invited participants receive a notification by e-mail and are thereby entitled to vote. An event is also accompanied by a time  Boundary recorded for voting.

• Each participant can now specify their preferences for local and time slots (weighted ranking). You can view further details by clicking on the restaurant, i.e.
The name, description, the URL with the menu and the geographic location of the restaurant. He can also see how the other participants have voted. The vote is
limited in time, but as long as it is still open, the user can select the local and Change timeslots as often as you like.

• The creator of the meal event can also end the vote early or Cancel event completely. In this case, the participants will receive a corresponding
Notification. If, after the end of voting (due to the expiry of the deadline or manual termination) there is a tie among options, then decides
either the creator of the event where and how late is eaten, or it is per random generator decided.

• After a vote has been completed (time limit, manual termination, has taken place “Runoff election”), the voting result, i.e. the location and the timeslot with the
most votes, displayed. In addition, all participants will be informed of the time and place by e-mail notified.

• The user should have access to a listing of past and future have food events. This listing should be filterable and sortable.

• The system should react appropriately to unexpected system states (exceptions) and return error messages to the user.

• The deletion of entities (users, local, ...) should be supported. It should be noted,
that deleting certain entities will have further effects on the system (e.g.: if a restaurant is to be deleted which is currently part of a vote).
A suitable “deletion policy” should be considered and implemented for this purpose.





