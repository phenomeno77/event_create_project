package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Location;
import at.qe.skeleton.model.OpeningHours;
import at.qe.skeleton.model.Tags;

import at.qe.skeleton.services.LocationService;
import at.qe.skeleton.services.OpeningHoursService;

import at.qe.skeleton.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@Scope("view")
public class LocationAddController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private OpeningHoursService openingHourService;

    @Autowired
    private TagService tagService;


    private Location location = new Location();
    
    private String messageErrorHeader;

    /**
     * =============================================================================
     * Method for Location
     * =============================================================================
     */
    

    public void setLocation(Location location) {

        this.location = location;
        
    }

    public Location getLocation() { 
        return this.location;
    }
    
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
     * Action to save new location. iff LocationName && Street && City not already exist
     * + ValidatesOpeningHours: that there is min one day with openingHour
     * + MapOpeniongHours: maps openingHours from String to LocalTimes with right possitions in DB
     * 
     * iff location saving is done, redirection from createLocation.xhtml -> locations.xhtml
     */
    public void addNewLocation() throws IOException {


        if (locationService.getAllLocations().stream()
                .filter(l -> l.getLocationName().equals(location.getLocationName())
                        && l.getStreet().equals(location.getStreet())
                        && l.getCity().equals(location.getCity())).count() > 0) {


        	messageErrorHeader = "Creating Location not successful";
        	String messageErrorInfoBox = "Location already in Database. Please check again.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
        }
        else if(!validateOpeningHours()){

            openingHourList.clear();
            messageErrorHeader = "Opening Hours missing!";
            String messageErrorInfoBox = "Please add at least one opening hour.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
        }
        else if(!this.mapOpeningHours()) {
        	openingHourList.clear();
        	this.opForSave.clear();
        	messageErrorHeader = "Opening Hours out of order!";
        	String messageErrorInfoBox = "Please keep the times in order.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox));
        }
        
        else if(location.getMenuUrl().contains("http")) {
        	openingHourList.clear();
        	this.opForSave.clear();
    		messageErrorHeader = "Menu URL";
    		String messageErrorInfoBox = "Please keep in mind, that the menu url should not contain http://. Please use only 'www....'.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageErrorHeader, messageErrorInfoBox)); 
    	}

        else {

        	Location savedLocation = locationService.addNewLocation(location);

            for(OpeningHours o: this.opForSave){
            	OpeningHours savedOpeningHours = openingHourService.addNewOpeningHours(o);
            	savedOpeningHours.setLocation(savedLocation);
                openingHourService.saveOpeningHours(savedOpeningHours);
            	
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect("/location/locations.xhtml");

        
        }

    }
    
    /**
     * Validates if set OpeningHours List has at least one closing and opening Time
     * 
     * @return true if openingHour is not empty
     */


    private boolean validateOpeningHours(){
    	
        for(String string : openingHourList){
            if (!string.equals("")){
            	
                return true;
            }
  
        }
        return false;
    }


    /**=============================================================================
     *             END
     * =============================================================================*/


    /**=============================================================================
     *             Method to create and work with tags
     * =============================================================================*/


    /**
     * Action to save all selected Tags for a Location.
     */

    private List<String> tags;
    
    private List<Tags> selectedTags = new ArrayList<>();

    public List<String> getTags() {
    	this.tags = new ArrayList<>();
        return tags;
    }
    
    
    /**
     * Tag List (String) gets remapped to Tags(Model)
     * Tag if not already in DB will be saved in DB, for further use
     * 
     * @param tags
     */

    public void setTags(List<String> tags) {
        this.tags = tags;

        Set<Tags> tagSet = new HashSet<>();
        Set<String> dumpSet = new HashSet<>();
        Set<String> allTagNames = new HashSet<>();

        if(tags != null && !tags.isEmpty()){
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
        this.location.setLocationTags(tagSet);
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


    /**
     * =============================================================================
     * Method to create openingHours with right mapping
     * =============================================================================
     */


    private List<String> openingHourList = new ArrayList<>();

    private String hour;

    
    public String getHour() {
        return hour;
    }

    public void setHour(String openingHourPart) {
        openingHourList.add(openingHourPart);
        this.hour = openingHourPart;
    }
    
    public List<String> getOHL(){
    	return openingHourList;
    }
    
    /**
     * Validates OpeningHours Order, true iff opening < breakStart < breakEnd < closing
     * String will be cast to LocalTime for compareTo method
     * 
     * @param opening
     * @param breakStart
     * @param breakEnd
     * @param closing
     * @return true 
     */
    
    public boolean validateOpeningHoursOrder(LocalTime opening, LocalTime breakStart, LocalTime breakEnd, LocalTime closing){
    
    	
    	if(breakEnd == null ) {
    		if((opening.compareTo(closing) >= 0)) {
    			return false;
    		}
    	}
    	else {
    		
    		if(((opening.compareTo(breakStart) >= 0) 
        			|| (breakEnd.compareTo(closing) >= 0) 
        			|| (breakStart.compareTo(breakEnd) >= 0) )) {
                return false;
        	}    
    	}
    	return true;
    }
    
    private List<OpeningHours> opForSave = new ArrayList<>();
    
     /**
      * Maps the set opening hours to correct tuple in DB and cast to LocalTime
      * returns true iff opening hour order check was correct  
      * 
      * @return
      */

    public boolean mapOpeningHours() {
        List<String> am = new ArrayList<>();
        List<String> pm = new ArrayList<>();
        int counter = 0;
        for (String s : openingHourList) {
            if ((counter % 2) == 0) {
                am.add(s);
                counter++;
            } else {
                pm.add(s);
                counter++;

            }
        }

        for (int i = 0; i < 7; i++) {
            OpeningHours openingH = new OpeningHours();

            String[] hr1 = am.get(i).split(" - ");
            String[] hr2 = pm.get(i).split(" - ");
            String hourFormat = "HH:mm";            

            if (hr1[0].isEmpty()) {
                if (hr2[0].isEmpty()) {
                	openingH.setDay(i+1);
                } else {
                	LocalTime opening = LocalTime.parse(hr2[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime closing = LocalTime.parse(hr2[1], DateTimeFormatter.ofPattern(hourFormat));

                	
                	if(!this.validateOpeningHoursOrder(opening, null, null, closing)) {
                		return false;
                	}
                    openingH.setDay(i+1);
                    openingH.setOpeningTime(opening);
                    openingH.setClosingTime(closing);
                }
            } else {
                if ( hr2[0].isEmpty()) {
                	LocalTime opening = LocalTime.parse(hr1[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime closing = LocalTime.parse(hr1[1], DateTimeFormatter.ofPattern(hourFormat));
                	if(!this.validateOpeningHoursOrder(opening, null, null, closing)){
                		return false;
                	}
                    openingH.setDay(i+1);
                    openingH.setOpeningTime(opening);
                    openingH.setClosingTime(closing);
                } else {
                	LocalTime opening = LocalTime.parse(hr1[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime breakStart = LocalTime.parse(hr1[1], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime breakEnd = LocalTime.parse(hr2[0], DateTimeFormatter.ofPattern(hourFormat));
                	LocalTime closing = LocalTime.parse(hr2[1], DateTimeFormatter.ofPattern(hourFormat));
                	
                	if(!this.validateOpeningHoursOrder(opening, breakStart, breakEnd, closing)) {
                		return false;
                	}
                    openingH.setDay(i+1);
                    openingH.setOpeningTime(opening);
                    openingH.setBreakTimeStart(breakStart);
                    openingH.setBreakTimeEnd(breakEnd);
                    openingH.setClosingTime(closing);
                }
            }

            this.opForSave.add(openingH);


        }
        return true;
    }
    

    /**=============================================================================
     *             End
     * =============================================================================*/
}

