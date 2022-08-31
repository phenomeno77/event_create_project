###### POLICY TO REMOVE AN ENTITY ######

NOTE: IN ORDER TO COMPLETELY REMOVE AN ENTITY FROM THE DATABASE, THERE ARE 2 STEPS REQUIRED.

FIRST STEP: BY CLICK ON BUTTON NEXT TO ENTITY (REMOVE BUTTON). THAT WILL REMOVE THE ENTITY
FROM EVENTS (IF ITS PART OF IT), BUT ITS STILL AVAILABLE IN THE DATABASE.

SECOND STEP: AFTER "REMOVING" AN ENTITY ANOTHER BUTTON WILL APPEAR WITH TITLE DELETE,
UPON CLICKING THAT BUTTON THE ENTITY WILL BE COMPLETELY REMOVED FROM THE DATABASE.


USER ENTITY-REMOVING

1. REMOVE/DELETE USER

CASE 1: USER IS NOT A PART OF ANY EVENT
THE 2 STEPS DESCRIBED ABOVE ARE REQUIRED TO COMPLETELY REMOVE AN USER.

CASE 2: USER IS PART OF AN EVENT
IF THE USER HAS CONFIRMED THAT HE WILL TAKE PLACE IN THE EVENT, AFTER REMOVING THAT USER
ITS VOTE WONT BE COUNT FOR THE FINAL RESULTS OF THAT EVENT. THE USER IS STILL SHOWN AS
AN INVITED USER OF THAT EVENT. AFTER COMPLETELY REMOVING THAT USER, IT WILL NOT SHOWN
ANYMORE IN THE INVITED USERS LIST.

CASE 3: USER IS AN EVENT CREATOR
IF THE EVENT IS STILL RUNNING AND THAT USER IS REMOVED, THEN THE EVENT WILL BE CANCELED
AND ALL USERS WILL BE INFORMED THAT THE EVENT GOT CANCELED. BY REMOVING THE USER IT STILL
SHOWN ITS NAME AS EVENT CREATOR. IF THAT USER GOT COMPLETELY REMOVED, THEN ITS USERNAME
IN THE LIST OF INVITED USERS (MAYBE FROM OTHER EVENTS) OR AS EVENT CREATOR WILL BE REPLACED
BY THE STRING "User not found." WHILE THE EVENT STILL EXISTS.

2. USER BANNED

CASE 1: IF THE USER IS NOT PART OF ANY EVENT T
HEN ITS SIMPLY SETTED TO NOT ACTIVE AND CANNOT ANYMORE LOGIN TO THE APPLICATION.

CASE 3: IF THE USER IS AN EVENT CREATOR AND THAT EVENT IS STILL RUNNING
 AFTER GETTING BANNED THE EVENT WILL BE CANCELED AND ALL USERS WILL GET INFORMED THAT THE EVENT GOT CANCELED.

CASE 2: IF THE USER IS PART OF AN EVENT AND CONFIRMED THE INVITATION
AFTER GETTING BANNED ITS VOTING WONT BE COUNT FOR THE FINAL RESULTS OF THAT EVENT. ITS USERNAME WILL STILL BE SHOWN
AS AN INVITED USER.

PS: IN ALL ABOVE CASES, THE USER WHICH GOT BANNED, REMOVED OR DELETED WONT BE SHOWN IN THE LIST
OF SELECTING USERS TO INVITE TO AN EVENT.



LOCATION ENTITY-REMOVING

1. CLOSING LOCATION

CASE 1: CLOSING A LOCATION IF ITS NOT PART OF ANY EVENT
WILL JUST SET THE LOCATION STATUS TO CLOSED AND THAT LOCATION WONT BE ABLE TO BE SELECTED FOR
A FURTHER EVENTS UNTIL ITS STATUS WILL BE SET BACK TO ACTIVE.

CASE 2: CLOSING A LOCATION WHILE ITS PART OF AN ACTIVE EVENT
THAT WILL SET THE LOCATION TO STATUS CLOSED AND ON THE VOTING PAGE THAT LOCATION WILL BE DISABLED
FROM VOTING. ON THE VOTING PAGE NEAR THE LOCATION DETAIL BUTTON A YELLOW TRIANGLE WILL APPEAR WHICH
DESCRIBES THAT THIS LOCATION IS CLOSED. IF SOMEONE VOTED FOR THAT LOCATION, THAT VOTE WONT BE COUNT
FOR THAT LOCATION WHILE THE EVENT IS STILL RUNNING AND THE LOCATION IS STILL CLOSED.

CASE 3: CLOSING A LOCATION AFTER THE EVENT EXPIRED AND THAT LOCATION GOT THE MOST VOTES
ON THE HISTORY PAGE NEAR THE FINAL RESULTS A YELLOW TRIANGLE WILL APPEAR WITH THE INFORMATION
THAT THE LOCATION IS CLOSED.

2. REMOVING A LOCATION

CASE 1: LOCATION IS NOT PART OF AN EVENT
LOCATION WILL GET THE STATUS REMOVED AND THAT LOCATION WONT BE SHOWN AS AVAILABLE TO CHOSE IN THE
EVENT CREATING LOCATION LIST.

CASE 2: LOCATION IS PART OF AN ACTIVE EVENT
IN THAT CASE THE LOCATION WILL BE SET TO REMOVED AND ITS VOTING COUNT WILL NOT BE COUNT IN THE FINAL RESULTS.
USER WONT BE ABLE TO VOTE FOR THAT LOCATION ANYMORE.
ON THE VOTING PAGE THE LOCATION DETAILS AND THE GOOGLE MAP WILL BE DEACTIVATED AND A RED TRIANGLE WILL APPEAR
WITH THE INFO THAT THE LOCATION IS NOT AVAILABLE ANYMORE.

CASE 3: REMOVING A LOCATION AFTER THE EVENT EXPIRED AND THAT LOCATION GOT THE MOST VOTES
ON THE HISTORY PAGE NEAR THE FINAL RESULTS A RED TRIANGLE WILL APPEAR WITH THE INFO THAT THE LOCATION IS NOT
ANYMORE AVAILABLE. THE GOOGLE MAP AND LOCATION DETAILS BUTTON WILL BE DEACTIVATED.
THE LOCATION NAME AND ADDRESS WILL STILL SHOWN IN THE FINAL RESULTS COLUMN.

3. COMPLETELY REMOVING A LOCATION

BEFORE COMPLETELY REMOVING A LOCATION, THE CASES ABOVE FOR REMOVING LOCATION WILL BE THE SAME, SINCE A LOCATION
HAS FIRST TO BE REMOVED TO BE ABLE TO BE COMPLETELY DELETED FROM THE DATABASE.

CASE 1: LOCATION IF PART OF AN ACTIVE EVENT
THE LOCATION WILL BE COMPLETELY REMOVED FROM THE VOTING EVENT TOO.

CASE 2: DELETING A LOCATION AFTER THE EVENT EXPIRED AND THAT LOCATION GOT THE MOST VOTES
ON THE HISTORY PAGE NEAR THE FINAL RESULTS A RED TRIANGLE WILL BE SHOWN WITH THE INFO THAT THE LOCATION HAS BEEN
REMOVED FROM THE APPLICATION. AT THE FINAL RESULTS COLUMN THE LOCATION NAME AND THE ADDRESS WILL NOT APPEAR ANYMORE,
THEY WILL BE REPLACED BY THE STRING "Location not found.".
