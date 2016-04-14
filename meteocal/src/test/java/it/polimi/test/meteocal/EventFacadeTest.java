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
import it.polimi.se2.meteocal.entity.WForecast;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author paologuglielmino
 */
public class EventFacadeTest {
    
    private EventManager cut;
    
    private EventFacade ef;
    
    public EventFacadeTest() {
        
    }
    
    @Before
    public void setUp() {
        cut = new EventManager();
        cut.em = mock(EntityManager.class);
        ef = new EventFacade();
        ef.setEm(mock(EntityManager.class));
        ef.setUm(mock(UserManager.class));
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void edtiEventBehavior() {
        Event newEvent = new Event();
        Location newLocation= new Location();
        WForecast newForecast = new WForecast();
        
        //I save the event
        cut.save(newLocation, newEvent, newForecast);
        
        //I modify every event field I want
        newEvent.setFacebookLink("http://this.is.the.new.link");
        newEvent.setName("New Name");
        newEvent.setIsPublic(Boolean.TRUE);
        
        //I call the update event procedure
        ef.edit(newEvent);
        
        //Merge operation should be executed once
        verify(ef.getEm(),times(1)).merge(newEvent);

        //No remove operations should be done
        verify(ef.getEm(),times(0)).remove(newEvent);
    }
    
}
