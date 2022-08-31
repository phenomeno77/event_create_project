package at.qe.skeleton.ui.beans;


import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.model.Timeslot;
import at.qe.skeleton.services.OpeningHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@Scope("request")
public class TimeslotsLocationsValidationBean {

    @Autowired
    OpeningHoursService openingHoursService;

    public List<Timeslot> validateEachLocationTimeslot(List<Location> locationSelected, List<Timeslot> timeslotsSelected){

       List<OpeningHours> openingHoursList;
       List<Timeslot> commonValidDateTimes = new ArrayList<>(timeslotsSelected);
       Set<Timeslot> timeslotsToRemove = new HashSet<>();

       Set<String> locationsClosedOnDay = new HashSet<>();

        /**Lists to just print the output to the user*/
        Set<LocalDateTime> invalidDatesToPrint = new HashSet<>();
        Set<LocalTime> invalidStartTimesToPrint = new HashSet<>();
        Set<LocalTime> invalidEndTimesToPrint = new HashSet<>();

        /**Iterate through all selected location   */
            for (Location location : locationSelected) {

                //get the opening hours for each location
                openingHoursList = getOpeningHoursFromLocation(location);

                /**first check if there are days where the location is open
                 * if the location is closed on that day add that day in the list of invalidDates*/
                for(Timeslot timeslotDates : timeslotsSelected) {

                    /**Convert each timeslot to local date time (LDT) to check the days.
                     * If the day of that timeslot is not present in the list of opening days
                     * then add that timeslot to the list to remove it after the for loop.*/
                    LocalDateTime convertedDateToLDT = timeslotDates.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                    if (!getDays(openingHoursList).contains(convertedDateToLDT.getDayOfWeek().getValue())) {
                        invalidDatesToPrint.add(convertedDateToLDT);
                        timeslotsToRemove.add(timeslotDates);

                        locationsClosedOnDay.add(location.getLocationName());
                    }

                    /**Iterate over the opening hours (times from-to) if the location is open on that day*/
                        for (Timeslot timeslotDateTimes : timeslotsSelected) {


                            /**Converting each Date from timeslot to local date time to local time to compare the hours*/
                            LocalDateTime convertedStartTimeToLDT = timeslotDateTimes.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalTime convertedStartTimeToLT = convertedStartTimeToLDT.toLocalTime();

                            LocalDateTime convertedEndTimeToLDT = timeslotDateTimes.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalTime convertedEndTimeToLT = convertedEndTimeToLDT.toLocalTime();

                            for (OpeningHours openingHours : openingHoursList) {



                                /**If the location has break times check if the selected timeslot of start time is in between the break time start
                                 * and break time end.*/

                                if ((openingHours.getBreakTimeStart() != null)) {
                                    /**Case 1 where the start time is before break start and end time after break time ends*/
                                    if (convertedStartTimeToLT.compareTo(openingHours.getBreakTimeStart()) <= 0
                                            && convertedEndTimeToLT.compareTo(openingHours.getBreakTimeEnd()) >= 0) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);
                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    /**Case 2 where the start time is before break start and the end time before break time ends*/
                                    if (convertedStartTimeToLT.compareTo(openingHours.getBreakTimeStart()) <= 0
                                            && convertedEndTimeToLT.compareTo(openingHours.getBreakTimeStart()) > 0 ) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);

                                        timeslotsToRemove.add(timeslotDateTimes);

                                    }

                                    /**Case 3 where the start time is after break start and the end time after break time ends*/
                                    if (convertedStartTimeToLT.compareTo(openingHours.getBreakTimeEnd()) < 0
                                            && convertedEndTimeToLT.compareTo(openingHours.getBreakTimeEnd()) >= 0 ) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);

                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    /**Case 4 where the start and end time is in between the break time start and end*/
                                    if (convertedStartTimeToLT.compareTo(openingHours.getBreakTimeStart()) >= 0
                                            && convertedEndTimeToLT.compareTo(openingHours.getBreakTimeEnd()) <= 0 ) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);

                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    /**Case 5 where the start time is before opening time or end time after closing time*/
                                    if (convertedStartTimeToLT.compareTo(openingHours.getOpeningTime()) < 0) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);

                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    if(convertedEndTimeToLT.compareTo(openingHours.getClosingTime()) > 0){
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);

                                        timeslotsToRemove.add(timeslotDates);
                                    }



                                }
                                /**check if selected timeslots of start time is in range of opening-closing hours without break times  */
                                if ((openingHours.getBreakTimeStart() == null)) {

                                    /**Case 1 and 2 where  the start time or end time are before the opening time
                                     * or after closing time*/
                                    if (convertedEndTimeToLT.compareTo(openingHours.getOpeningTime()) <= 0
                                    || convertedStartTimeToLT.compareTo(openingHours.getClosingTime()) >= 0) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);
                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    /**Case 3 and 4 where the start time is before opening time */
                                    if (convertedStartTimeToLT.compareTo(openingHours.getOpeningTime()) < 0) {

                                        invalidStartTimesToPrint.add(convertedStartTimeToLT);
                                        timeslotsToRemove.add(timeslotDateTimes);
                                    }

                                    /**Case 3 and 4 where the end time is after closing time*/
                                    if (convertedEndTimeToLT.compareTo(openingHours.getClosingTime()) > 0){

                                        invalidEndTimesToPrint.add(convertedEndTimeToLT);
                                        timeslotsToRemove.add(timeslotDateTimes);

                                    }
                                }
                            }
                        }
                    }

                }



        /**Call to the methods to output which location is closed on which day or time*/
        validateMessageOnClosedDay(invalidDatesToPrint, locationsClosedOnDay);
        validateMessageOnClosedDateTime(invalidStartTimesToPrint,invalidEndTimesToPrint);


        /**At the end remove from the list all unavailable timeslots and return the list of common valid*/
        commonValidDateTimes.removeAll(timeslotsToRemove);

        return commonValidDateTimes;

    }

    /**=============================================================================
     *              CONVERTING OPENING HOURS TO COMPARE
     * =============================================================================*/

    /**Method to return a list of ALL Opening Hours of each Location*/
    private List<OpeningHours> getOpeningHoursFromLocation(Location location){
        return  openingHoursService.getAllOpeningHours()
                .stream()
                .filter(openingHours -> openingHours.getLocation().equals(location) && openingHours.getOpeningTime() != null)
                .collect(Collectors.toList());
    }

    /**Method to add all Days where each Location is opened*/
    private List<Integer> getDays(List<OpeningHours> openingHoursList){
        List<Integer> days = new ArrayList<>();
        for(OpeningHours openingHours : openingHoursList){
            if(openingHours.getOpeningTime() != null) {
                days.add(openingHours.getDay());
            }
        }
        return days;
    }


    /**=============================================================================
     *              FORMATTING OF INVALID TIMESLOTS FOR THE OUTPUT
     * =============================================================================*/

    /**METHODS TO PRINT A NICE OUTPUT TO THE USER DEPENDING ON OPENING HOURS.
     * E.G. IF THE LOCATION IS CLOSED AT THAT DAY AN OUTPUT WILL BE PRINTED, OTHERWISE
     * IF THE LOCATION IS CLOSED ON THAT DATE TIME ANOTHER OUTPUT WILL BE PRINTED.
     */

    /**Method to print a Message to the User, if there are Timeslots chosen where that Location
     * is closed all day*/
    private void validateMessageOnClosedDay(Set<LocalDateTime> invalidDates, Set<String> locationName){
        if(!invalidDates.isEmpty()){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Location closed on selected Timeslot(s)",
                    "Dear User. The Location " + locationName +" is at the chosen Timeslot(s): <br/>"
                            + formattedDateTimeString(invalidDates)
                            +"<br/> closed. Please check the Location's Opening Hours and the chosen Timeslots again." +
                            "If you want to proceed the Event without adapting the Timeslots, then the  invalid Timeslots wont appear as choice for the Rating." );

            FacesContext.getCurrentInstance().addMessage(null,message );
        }
    }


    /**Method to print a Message to the User, the timeslots From, To or From-To are unavailable.
     * Overloaded method formattedString will be call, depending on which Timeslot is unavailable.
     * */
    private void validateMessageOnClosedDateTime(Set<LocalTime> invalidStartDateTimes, Set<LocalTime> invalidEndDateTimes){

        StringBuilder formattedInvalidTimeslots = new StringBuilder("");

        if(!invalidStartDateTimes.isEmpty() || !invalidEndDateTimes.isEmpty()) {
            formattedInvalidTimeslots.append(formattedString(invalidStartDateTimes)).append(formattedString(invalidEndDateTimes));

        }

        if(!formattedInvalidTimeslots.toString().equals("")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Timeslot(s) out of Opening Hours",
                    "Dear User. There are Locations which  are at the chosen Timeslot(s): <br/>"
                            + formattedInvalidTimeslots
                            + "<br/> closed. Please check the Location's Opening Hours and the chosen Timeslots again." +
                            "If you want to proceed the Event without adapting the Timeslots, then the  invalid Timeslots wont appear as choice for the Rating.");

            FacesContext.getCurrentInstance().addMessage(null, message);
        }


    }

    /**Methods to build a string to print a nice formatted message to the User
     * On unavailable opening days.
     * @param dateTimes
     * @return
     */
    private String formattedDateTimeString(Set<LocalDateTime> dateTimes){
        StringBuilder formattedInvalidTimeslots = new StringBuilder("\n ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        List<String> checkDuplicates = new ArrayList<>();

        if(!dateTimes.isEmpty()) {
            for (LocalDateTime localDateTime : dateTimes) {
                if (checkDuplicates.contains(formatter.format(localDateTime))) {
                    continue;
                }
                checkDuplicates.add(formatter.format(localDateTime));

                formattedInvalidTimeslots.append("\"")
                        .append(formatter.format(localDateTime))
                        .append("\"").append(", ");
            }
        }

        return formattedInvalidTimeslots.toString();

    }

    private String formattedString(Set<LocalTime> dateTimes){
        StringBuilder formattedInvalidTimeslots = new StringBuilder("\n ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


        List<String> checkDuplicates = new ArrayList<>();

        for(LocalTime localTime : dateTimes) {
            if (checkDuplicates.contains(formatter.format(localTime))) {
                continue;
            }
            checkDuplicates.add(formatter.format(localTime));

            formattedInvalidTimeslots.append("\"")
                    .append(formatter.format(localTime))
                    .append("\"").append(", ");
        }

        return formattedInvalidTimeslots.toString();
    }
/**=============================================================================
 *              END - FORMATTING OF INVALID TIMESLOTS FOR THE OUTPUT
 * =============================================================================*/
}
