package at.qe.skeleton.ui.controllers;


import java.util.List;


import javax.annotation.PostConstruct;


import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.GeocodeResult;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Controller for the location list view.
 *
 */
@Component
@RequestScope
//@Scope("view")
public class LocationMapController {



    /**
     * Returns a string with the coordinates LatLing for google maps
     *
     * @return
     */
    private MapModel geoModel;
    private String centerGeoMap = "47.2612497,11.3995803";
    
    
    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }

    
    
    
    public void onGeocode(GeocodeEvent event) {
    	geoModel = new DefaultMapModel();
        List<GeocodeResult> results = event.getResults();

        if (results != null && !results.isEmpty()) {
            LatLng center = results.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < results.size(); i++) {
                GeocodeResult result = results.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
            }
        }
    }
    
    
    
    public MapModel getGeoModel() {

        return geoModel;
    }


    public String getCenterGeoMap() { 
        return centerGeoMap;
    }

    
   
 

    
    

}
