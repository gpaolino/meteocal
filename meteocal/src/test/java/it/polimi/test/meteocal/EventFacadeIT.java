/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.test.meteocal;

import it.polimi.se2.meteocal.boundary.EventFacade;
import it.polimi.se2.meteocal.boundary.EventManager;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import it.polimi.se2.meteocal.meteoInfo.WeatherUpdate;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration test for the Event controller
 * @author paologuglielmino
 */
@RunWith(Arquillian.class)
public class EventFacadeIT {
    
    @Inject
    EventManager cut;
    
    @Inject
    EventFacade ef;
    
    @Inject
    UserManager um;
    
    @PersistenceContext
    EntityManager em;
    
    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(Event.class, EventManager.class, EventFacade.class, User.class, Location.class, WForecast.class, WeatherUpdate.class)
                .addPackage(Event.class.getPackage())
                .addPackage(EventFacade.class.getPackage())
                .addPackage(WeatherUpdate.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Test
    public void eventManagerShouldBeInjected() {
        assertNotNull(cut);
    }
    
    @Test
    public void eventFacadeShouldBeInjected() {
        assertNotNull(ef);
    }
    
    @Test
    public void EntityManagerShouldBeinjected() {
        assertNotNull(em);
    }
    
    @Test
    public void saveNewEventTest() {
        //I create a new user
        User user = new User();
        user.setName("Mario");
        user.setEmail("mario@meteocal.com");
        user.setPassword("Password1");
        Location userLocation = new Location();
        userLocation.setCity("Palo Alto");
        userLocation.setCoordinates("(37.4418834, -122.14301949999998)");
        //The forecast is empty,, doesn't matter for the tests.
        WForecast forecast = new WForecast();
        um.save(user, userLocation, forecast);
        
        //I create a new event
        Event event1 = new Event();
        event1.setName("Nuovo evento");
        event1.setStartsAt(new Date());
        event1.setEndsAt(new Date());
        event1.setDescription("");
        event1.setIsOutdoor(Boolean.FALSE);
        Location eventLocation = new Location();
        eventLocation.setCity("Milano");
        eventLocation.setCoordinates("(45.4654219, 9.18592430000001)");
        event1.setLocation(eventLocation);
        event1.setIsCanceled(Boolean.FALSE);
        event1.setIsPublic(Boolean.FALSE);
        event1.setNotIfTrigger(5);
        event1.setWeatherForecast(forecast);
        //Event's owner is user
        event1.setUser(user);
        //I save the new event
        cut.save(eventLocation, event1, new WForecast());
        
        //I retrieve the event just saved, through the EntityManager
        Event retrievedEvent = em.find(Event.class, new Long("1"));
    }
    
    @Test
    public void getUserEventsTest() {
        //I create a new user
        User user = new User();
        user.setName("Paolo");
        user.setEmail("paolo@meteocal.com");
        user.setPassword("Password1");
        Location userLocation = new Location();
        userLocation.setCity("Talamona");
        userLocation.setCoordinates("(46.1368155, 9.61057690000007)");
        //The forecast is empty,, doesn't matter for the tests.
        WForecast forecast = new WForecast();
        um.save(user, userLocation, forecast);
        
        //I create a new event
        Event event1 = new Event();
        event1.setName("Evento1");
        event1.setStartsAt(new Date());
        event1.setEndsAt(new Date());
        event1.setDescription("");
        event1.setIsOutdoor(Boolean.FALSE);
        Location eventLocation = new Location();
        eventLocation.setCity("Milano");
        eventLocation.setCoordinates("(45.4654219, 9.18592430000001)");
        event1.setLocation(eventLocation);
        event1.setIsCanceled(Boolean.FALSE);
        event1.setIsPublic(Boolean.FALSE);
        event1.setNotIfTrigger(5);
        event1.setWeatherForecast(forecast);
        //Event's owner is user
        event1.setUser(user);
        //I save the new event
        cut.save(eventLocation, event1, new WForecast());
        
        //I test the getUserEvents method
        List<Event> retrievedEvents = ef.getUserEvents(user.getEmail());
        //I check that the list of events is not empty, because an event exists
        assertNotNull(retrievedEvents);
        //I check the name of the retrieved event, must be the same of event1 I just saved
        assertEquals(event1.getName(), retrievedEvents.get(0).getName());
        //I check the owner of the retireved event, must be the same owner of event1, which is user
        assertEquals(event1.getUser(), retrievedEvents.get(0).getUser());
    }
    
}
