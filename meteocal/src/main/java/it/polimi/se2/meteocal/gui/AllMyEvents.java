/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.boundary.EventFacade;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Event;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author paologuglielmino
 */
@Named("allMyEvents")
@RequestScoped
public class AllMyEvents {
    
    @Inject
    private EventFacade ef;
    
    @Inject
    private UserManager um;
    
    private List<Event> events;

    public List<Event> getEvents() {
        events = ef.getCalendarEvents(um.getLoggedUser().getEmail());
        return events;
    }
    
    public List<Event> getAcceptedEvents() {
        events = ef.getEventsWithStatus(um.getLoggedUser().getEmail(), "ACCEPTED");
        return events;
    }
    
    public List<Event> getPendingEvents() {
        events = ef.getEventsWithStatus(um.getLoggedUser().getEmail(), "PENDING");
        return events;
    }
    
    public List<Event> getDeclinedEvents() {
        events = ef.getEventsWithStatus(um.getLoggedUser().getEmail(), "DECLINED");
        return events;
    }
    
}
