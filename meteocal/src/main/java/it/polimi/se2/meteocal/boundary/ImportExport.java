package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Invitation;
import it.polimi.se2.meteocal.entity.WForecast;
import it.polimi.se2.meteocal.meteoInfo.WeatherUpdate;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import java.net.SocketException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.model.property.Geo;
import org.json.JSONException;

/**
 *
 * @author andreagulino
 */
@Stateless
public class ImportExport {
    
    @Inject
    InvitationFacade ifa;
    
    @Inject
    EventFacade ef;
    
    @Inject
    UserManager um;
    
    @EJB 
    WeatherUpdate wu;
    
    //unchecked user exists
    public String exportCalendar(String userEmail) {
        
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//GulinoGuglielminoNegrini//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        
        List<Event> events =  ef.getCalendarEvents(userEmail);
        for(Event e:events) {
            addEvent(e,calendar);
        }
        
        System.out.println(calendar);
        return calendar.toString();
        
    }
    
    
    public void addEvent(Event e, Calendar calendar) {
        
        //Set name and time
        String eventName = e.getName();
        DateTime start = new DateTime(e.getStartsAt());
        DateTime end = new DateTime(e.getEndsAt());
        VEvent meeting = new VEvent(start, end, eventName);

        
        //Set attendees
        for ( Invitation i: ifa.getInvitations(e.getId().toString()) ){
        
            if(i.getStatus().equals("ACCEPTED")) {
                
                Attendee dev1 = new Attendee(URI.create("mailto:"+i.getInvitedUser().getEmail()));
                dev1.getParameters().add(Role.REQ_PARTICIPANT);
                dev1.getParameters().add(new Cn(i.getInvitedUser().getName()));
                meeting.getProperties().add(dev1);
                
            }
               
        }
        
        //Add Location
        /*
       Geo geoloc = new Geo();
       if( !e.getLocation().getCoordinates().equals("") ){
        String[] coordinates = e.getLocation().getCoordinates().split(",");
        String latitude = coordinates[0].substring(1);
        String longitude = coordinates[1].substring(1, coordinates[1].length()-2);

        geoloc.setLatitude(new BigDecimal(latitude));
        geoloc.setLongitude(new BigDecimal(longitude));
        meeting.getProperties().add(geoloc);
       }*/
        
        // generate unique identifier..
        UidGenerator ug = null;
        try {
            ug = new UidGenerator("uidGen");
        } catch (SocketException ex) {
            Logger.getLogger(ImportExport.class.getName()).log(Level.SEVERE, null, ex);
        }
        Uid uid = ug.generateUid();
        meeting.getProperties().add(uid);
  
        
        calendar.getComponents().add(meeting);
    }
    
    
    //Import calendar
    public List<Event> importCalendar(InputStream sin) throws IOException, ParserException{
        
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(sin);
        System.out.println("##FILE RICEVUTO");
        
        List<Event> conflicts = new ArrayList<Event>();
        
        List<VEvent> evnts =  calendar.getComponents(Component.VEVENT);
        for(VEvent e: evnts) {
            
            Event event = new Event();
            event.setIsPublic(Boolean.FALSE);
            event.setName(e.getSummary().getValue());
            event.setStartsAt(e.getStartDate().getDate());
            event.setEndsAt(e.getEndDate().getDate());
            event.setUser(um.getLoggedUser());
            event.setIsOutdoor(Boolean.FALSE);
            event.setIsCanceled(Boolean.FALSE);
            event.setNotIfTrigger(2);
            event.setLocation(um.getLoggedUser().getLocation());
                        
           
            
            List<Event> cnfs = ef.getConflicts(event);
            
            if(cnfs.size() > 0) {
                 conflicts.add(event);
            } else {
                ef.create(event);
                try {
                    wu.startWeatherUpdatesEvents();
                } catch (JSONException ex) {
                    Logger.getLogger(ImportExport.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(ImportExport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
          
             
        }
        
        return conflicts;
    }
   
   
}
