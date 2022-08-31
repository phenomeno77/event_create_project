package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.model.Tags;

import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;
import at.qe.skeleton.services.TagService;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controller for the location detail view.
 *

 */
@Component
@Scope("view")
public class LocationDetailController {
	

    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private TagService tagService;
   
    @Autowired
    private OpeningHoursService openingHourService;
    
    
    private String messageErrorHeader;

    
    /**
     * Attribute to cache the currently displayed location
     */

    private Location location;
    
    public String getMessageErrorHeader() {
    	return messageErrorHeader;
    }
    
    /**
     * Set Method for JUNit tests
     * 
     * @param locationService
     * @param openingHourService
     * @param tagServices
     */
    
    public void setServices(LocationService locationService, OpeningHoursService openingHourService, TagService tagServices) {
    	this.locationService = locationService;
    	this.openingHourService = openingHourService;
    	this.tagService = tagServices;
    }
    


    /**
     * Sets the currently displayed location and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadLocation()}, {@link #doSaveLocation()} and
     * {@link #doDeleteLocation()}.
     *
     */
    
    public void setLocation(Location location) {
    	
        this.location = location;
        doReloadLocation();
    }

    /**
     * Returns the currently displayed location.
     *
     * @return Location
     */

    
    public Location getLocation() {
        return location;
    }

    /**
     * Method to close the location by the status to "Closed"
     */
    
    public void doCloseLocation(){
        this.location.setLocationStatus("Closed");
        messageErrorHeader = "Warning: closed";
        String messageErrorInfoBox = location.getLocationName() + " is closed. When creating an event this location can't be chosen.";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, messageErrorHeader, messageErrorInfoBox));
        doSaveLocation();
    }

    /**
     * Method to open the location by setting active to true and the status to "Active"
     */
    public void doOpenLocation(){
        this.location.setActive(true);
        this.location.setLocationStatus("Active");
        messageErrorHeader = "Location Opened";
        String messageErrorInfoBox = location.getLocationName() + " is now open. When creating an event this location now can be chosen.";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage( messageErrorHeader, messageErrorInfoBox));
        doSaveLocation();
    }

    /**
     * Method to Remove the location be setting active to false and the status to Removed.
     * Removing that Location the button to Edit, Close or Open will not any more be rendered.
     */
    public void doRemoveLocation(){
        this.location.setActive(false);
        this.location.setLocationStatus("Removed");
        messageErrorHeader = "Location Removed";
        String messageErrorInfoBox = location.getLocationName() + " has been removed.";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
        doSaveLocation();
    }
    
    /**
     * Method to delete location from db. usage: rear 
     */
    public void doDeleteLocation(){
        this.locationService.delete(location);

    }
    
    /**=============================================================================
     *             OPENINGHOURS
     * =============================================================================*/
    
    
    /**
     * WrapperStringSublistAM - returns List of all opening hours if there is only opening and closing time
     * WrapperStringSublistPM - returns List of all opening hours if there is a break
     */
 
    
    private List<StringWrapperDummy> wrapperStringSublistAM = new ArrayList<>();

    private List<StringWrapperDummy> wrapperStringSublistPM = new ArrayList<>();

    private List<String> openingHourList = new ArrayList<>();
    
    private List<StringWrapperDummy> listOfStringsWrapperForUpdatingOH;


    private List<OpeningHours> currentLocationOHList = new ArrayList<>();
    
    /**
     * Method to bring existing opening hours in form incl space for editing or adding new ones
     * 
     * @return List StringH (Wrapper class to get and set String)
     */

    public List<StringWrapperDummy> getHour(int section){

    	listOfStringsWrapperForUpdatingOH = new ArrayList<>();
    	currentLocationOHList = openingHourService.getAllOpeningHours().stream().filter(oh -> oh.getLocation().getLocationName().equals(this.location.getLocationName())).collect(Collectors.toList());

		for (int i = 0; i < 14; i++){
			listOfStringsWrapperForUpdatingOH.add(new StringWrapperDummy(""));
		}


		for(OpeningHours o : currentLocationOHList) {
			if(o.getBreakTimeStart() == null) {
				listOfStringsWrapperForUpdatingOH.set(o.getDay()-1, new StringWrapperDummy(o.getOpeningTime() + "-" + o.getClosingTime()));
				listOfStringsWrapperForUpdatingOH.set(o.getDay()+6, new StringWrapperDummy(""));
			}
			else {
				listOfStringsWrapperForUpdatingOH.set(o.getDay()-1, new StringWrapperDummy(o.getOpeningTime() + "-" + o.getBreakTimeStart()));
				listOfStringsWrapperForUpdatingOH.set(o.getDay()+6, new StringWrapperDummy(o.getBreakTimeEnd() + "-" + o.getClosingTime()));
			}
		}
		if(section == 1) {
			wrapperStringSublistAM = listOfStringsWrapperForUpdatingOH.subList(0, 7);
			return wrapperStringSublistAM;
		}
		else {
			wrapperStringSublistPM = listOfStringsWrapperForUpdatingOH.subList(7, 14);
			return wrapperStringSublistPM;

		}

    }
    
    public void setListsForTest(StringWrapperDummy am, StringWrapperDummy pm, int i) {
    	this.wrapperStringSublistAM.set(i, am);
    	this.wrapperStringSublistPM.set(i, pm);
    }
    


    /**
     * Wrapper Class for Strings to get and set strings
     *
     * @author christinabruckl
     *
     */

    // WRAPPER CLASS
    public class StringWrapperDummy {
    	private String hour;

    	public StringWrapperDummy(String hour) {
    		this.hour = hour;
    	}


    	public void setStringH(String hour) {
    		this.hour = hour;
    	}

    	public String getStringH(){
    		return hour;
    	}


    }

    
    /**
     * Function to get all days of the week for editing location view
     * 
     * @return List of Strings
     */

	public List<String> getDays(){
    	List<String> listOfDays = new ArrayList<>();
    	listOfDays.add("MO");
    	listOfDays.add("TU");
    	listOfDays.add("WE");
    	listOfDays.add("TH");
    	listOfDays.add("FR");
    	listOfDays.add("SA");
    	listOfDays.add("SU");
    	
    	
    	return listOfDays;
    }
	

	/**
	 * Function to map openingHours in right Order
	 * Transfers Strings into LocalTime for Model
	 *
	 * @return true if mapping worked and validation was correctly checked
	 */

    

    public boolean mapOpeningHours() {

        List<String> am = new ArrayList<>();
        List<String> pm = new ArrayList<>();

        for(int i = 0; i < 7; i++) {
        	am.add("");
        	pm.add("");
        	am.set(i, openingHourList.get(i));
        	pm.set(i, openingHourList.get(i+7));

        }

        for (int i = 0; i < 7; i++) {
            String hourFormat = "HH:mm";            


            String[] hr1 = am.get(i).split("-");
            String[] hr2 = pm.get(i).split("-");

            if (hr1[0].isEmpty() || hr1 == null) {
                if (hr2[0].isEmpty() || hr2 == null) {

                	this.setNewValidOpeningHours(null, null, null, null, i);


                } else {

                	LocalTime open = LocalTime.parse(hr2[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime close = LocalTime.parse(hr2[1], DateTimeFormatter.ofPattern(hourFormat));

                	if(!this.validateOpeningHoursOrder(open, null, null, close)) {
                		return false;
                	} else {
                		this.setNewValidOpeningHours(open, null, null, close, i);
                	}
                }
            } else {

                if ( hr2[0].isEmpty() || hr2 == null) {

                	LocalTime open = LocalTime.parse(hr1[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime close = LocalTime.parse(hr1[1], DateTimeFormatter.ofPattern(hourFormat));

                	if(!this.validateOpeningHoursOrder(open, null, null, close)) {
                		return false;
                	}
                	else {
                		this.setNewValidOpeningHours(open, null, null, close, i);
                	}

                } else {

                	LocalTime open = LocalTime.parse(hr1[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime breakS = LocalTime.parse(hr1[1], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime breakE = LocalTime.parse(hr2[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime close = LocalTime.parse(hr2[1], DateTimeFormatter.ofPattern(hourFormat));

                	if(!this.validateOpeningHoursOrder(open, breakS, breakE, close)) {
                		return false;
                	}
                	else {
	                	this.setNewValidOpeningHours(open, breakS, breakE, close, i);
                	}
                }
            }
        }
        return true;
    }


    /**
     * Function to set old opening Hours in DB with new ones
     *
     * @param opening
     * @param breakStart
     * @param breakEnd
     * @param closing
     * @param day
     */

    private void setNewValidOpeningHours(LocalTime opening, LocalTime breakStart, LocalTime breakEnd, LocalTime closing, int day) {

    		OpeningHours openingH = this.currentLocationOHList.get(day);
    		openingH.setOpeningTime(opening);
        	openingH.setClosingTime(closing);
       		openingH.setBreakTimeStart(breakStart);
        	openingH.setBreakTimeEnd(breakEnd);
    

    }


    /**
     * Function to check if order of the hours are correct
     *
     *
     * @param opening
     * @param breakStart
     * @param breakEnd
     * @param closing
     * @return true if opening < breakStart < breakEnd < closing
     */

    private boolean validateOpeningHoursOrder(LocalTime opening, LocalTime breakStart, LocalTime breakEnd, LocalTime closing){

    	if(breakEnd == null) {
    		if((opening.compareTo(closing) >= 0)) {
            return false;
    		}
    	}
    	else{

    		if(((opening.compareTo(breakStart) >= 0)
        			|| (breakEnd.compareTo(closing) >= 0)
        			|| (breakStart.compareTo(breakEnd) >= 0) )) {

                return false;
        	}
    	}
    	return true;
    }

    /**
     * Checks if mininmum one OpeningHour is added
     *
     * @return true if not empty
     */

    public boolean validateOpeningHoursifEmpty(){

        for(String string : this.openingHourList){
            if (!string.equals("")){
                return true;
            }
        }
        return false;
    }
    

    
    

    /**=============================================================================
     *             End
     * =============================================================================*/


    /**=============================================================================
     *             TAGS
     * =============================================================================*/
    private List<Tags> selectedTags = new ArrayList<>();

    public List<String> getTags() {
        Set<String> tags = new HashSet<>();

        for(Tags t : location.getLocationTags()) {
            tags.add(t.getTagName());
        }

        return new ArrayList<>(tags);
    }

    public void setTags(List<String> tags) {

        Set<Tags> tagSet = new HashSet<>();
        Set<String> dumpSet = new HashSet<>();
        Set<String> allTagNames = new HashSet<>();

        if(tags!=null && !tags.isEmpty()){
            dumpSet.addAll(tags);
        }

        for(Tags tag : tagService.getAllTags()){
            allTagNames.add(tag.getTagName());
        }


        for (String s : dumpSet) {
            if (!allTagNames.contains(s)) {
                Tags t = new Tags();
                t.setTagName(s);
                tagSet.add(tagService.saveTag(t));
            } else {
                tagSet.add(tagService.getAllTags().stream().
                        filter(tags1 -> tags1.getTagName().equals(s))
                        .findFirst()
                        .orElse(null));
                
            }
        }
        location.setLocationTags(tagSet);
    }

    public List<Tags> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(List<Tags> selectedTags) {
        this.selectedTags = selectedTags;

        Set<Tags> tagsSet = new HashSet<>(selectedTags);

        this.location.getLocationTags().addAll(tagsSet);

    }

    
    /**=============================================================================
     *             End
     * =============================================================================*/

    
    /**=============================================================================
     *             LOCATION
     * =============================================================================*/
    
    
    
    /**
     * Action to force a reload of the currently displayed location.
     */
    public void doReloadLocation() {
        location = locationService.loadLocation(location.getLocationId());
    }




    /**
     * Method save edited location. 
     * + incl. sublists (wrapperStringSublistAM, ...PM) contend transfered to one list
     * + validateOpeningHoursIfEmpty: checks if still min one openingHour is present in the edited version, check passed if returned true
     * + mapOpeningHours: maps String to LocalTime in correct order; check passed if returned true
     * + Warning Massage if Location gets set to inactive
     * 
     * ErrorMessage if checks are not passed, else location gets saved in db and locations.xhtml gets reloaded
     * 
     */
    
public void doSaveLocation() {
		boolean save = true;


    	if(listOfStringsWrapperForUpdatingOH != null) {
    		for(StringWrapperDummy s : wrapperStringSublistAM) {
    			openingHourList.add(s.getStringH());
    		}
    		for(StringWrapperDummy s : wrapperStringSublistPM) {
    			openingHourList.add(s.getStringH());
    		}


    		if(!validateOpeningHoursifEmpty()) {
    			openingHourList.clear();
        		messageErrorHeader = "OpeningHours are missing.";
        		String messageErrorInfoBox = "Please keep in mind, that at least one opening hour must be present. Or set the location as inactive.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
                save = false;
    		}
        	else if(!mapOpeningHours()) {
    			openingHourList.clear();

        		messageErrorHeader = "OpeningHours are NOT in order.";
        		String messageErrorInfoBox = "Please keep in mind, that the opening hours are in correct order. Not allowed: \n opening: 12:00\n closing: 10:00.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
                save = false;
        	}
    	}

    	
    	if(save) {
    		if(!location.isActive()) {
        		messageErrorHeader = "Warning status: inactive";
        		String messageErrorInfoBox = location.getLocationName() + " is inactive. When creating an event this location can't be choosen.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, messageErrorHeader, messageErrorInfoBox));
        	} 
    		else {
        		messageErrorHeader = "Location saved.";
        	}
    		
    		if(location.getMenuUrl().contains("http")) {
    			openingHourList.clear();
        		messageErrorHeader = "MENU URL";
        		String messageErrorInfoBox = "Please keep in mind, that the menu url should not contain http://. Please use only 'www....'.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox)); 
        	}else {
        		location = this.locationService.saveLocation(location);
                PrimeFaces.current().executeScript("setTimeout(function() { location.reload() },500)");
        	}
    	}
    }

    
    
    /**=============================================================================
     *             End
     * =============================================================================*/
}
