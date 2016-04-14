/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.test.meteocal;

import it.polimi.se2.meteocal.boundary.EventManager;
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
 * @author alessandronegrini
 */
public class EventManagerTest {
    
    private EventManager cut;
    
    public EventManagerTest(){
        
    }
    
    @Before
    public void setUp() {
        cut = new EventManager();
        cut.em = mock(EntityManager.class);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void newEventShouldSaveOnce() {
        Event newEvent = new Event();        
        Location newLocation= new Location();
        WForecast newForecast = new WForecast();
        
        cut.save(newLocation, newEvent, newForecast);
        
        //I check that the number of registered Users is 1
        verify(cut.em,times(1)).persist(newEvent);
        //I check that the number of Location is 1
        verify(cut.em,times(1)).persist(newLocation);
        //I check that the number of WeatherForecast is 1
        verify(cut.em,times(1)).persist(newForecast);
    }
}
