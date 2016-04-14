/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.fortuna.ical4j.model.DateTime;

/**
 *
 * @author andreagulino
 */
@Stateless
public class EventFacade extends AbstractFacade<Event> {
    
    @PersistenceContext
    private EntityManager em;

    @Inject
    UserManager um;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public Boolean sendInvitation( Event e, User to, String text) {
       // Invitation inv = new Invitation();
        return false;
    }
    
    public EventFacade() {
        super(Event.class);
    }

    /**
     * Get all events created by the user no matter they are canceled or not
     * @param email
     * @return 
     */
    public List<Event> getUserEvents(String email) {
        
        Query query = em.createNativeQuery("SELECT * FROM EVENT WHERE owner = '"+email+"'", Event.class);
        List<Event> events =  query.getResultList();
        return events;
    }
    
    /**
     * Events to which the user is partecipating with status
     * @param email
     * @param status
     * @return 
     */
    public List<Event> getEventsWithStatus(String email, String status) {
        
       String queryString = "SELECT EVENT.*\n" +
            "FROM EVENT, INVITATION\n" +
            "WHERE TOEVENT_ID = EVENT.ID \n" +
            "AND STATUS='"+status+"'\n" +
            "AND ISCANCELED=0\n" +
            "AND INVITEDUSER_EMAIL = '"+email+"'\n";
        
        Query query = em.createNativeQuery(queryString, Event.class);
        List<Event> events =  query.getResultList();
        return events;
    }
    
    /**
     * Get all events that should be displayed on the calendar (not canceled owned + invited)
     * @param email
     * @return 
     */
    public List<Event> getCalendarEvents(String email) {
        
        String queryString = "SELECT EVENT.*\n" +
            "FROM EVENT, INVITATION\n" +
            "WHERE TOEVENT_ID = EVENT.ID \n" +
            "AND STATUS=\"ACCEPTED\"\n" +
            "AND ISCANCELED=0\n" +
            "AND INVITEDUSER_EMAIL = '"+email+"'\n" +
            "UNION \n" +
            "SELECT *\n" +
            "FROM EVENT\n" +
            "WHERE ISCANCELED=0\n" +
            "AND owner = '"+email+"'";
        
        Query query = em.createNativeQuery(queryString, Event.class);
        List<Event> events =  query.getResultList();
        return events;
    }
    
    /**
     * Returns the CALENDAR events in conflict with that passed as parameter (empty list if none)
     * @param e
     * @return 
     */
    public List<Event> getConflicts(Event e) {
        
        List<Event> results = new ArrayList<Event>();
        
        Date from = e.getStartsAt();
        Date to = e.getEndsAt();
        
        String email = um.getLoggedUser().getEmail();
        
        Query q = em.createNativeQuery(
                "SELECT * FROM EVENT WHERE isCanceled=0 AND owner='"+email+"' AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))"
                + "UNION((SELECT EVENT.*\n" +
        "FROM EVENT, INVITATION\n" +
        "WHERE TOEVENT_ID = EVENT.ID \n" +
        "AND STATUS=\"ACCEPTED\"\n" +
        "AND ISCANCELED=0\n" +
        "AND INVITEDUSER_EMAIL = '"+email+"'\n" +
        "AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))));"
        , Event.class);
        
        results = q.getResultList();
             
        
        return results;
    }
    
    /**
     * Returns true if date is consisten (start<end) and (start>currentDate) otherwise returns false
     * @param e
     * @return 
     */
    public boolean checkDateConsistency(Event e){
        Date date = new Date();
        //true if end date is greater than start date, false otherwise
        Boolean endGreaterThanStart = e.getEndsAt().getTime() > e.getStartsAt().getTime();
        //true if start date is greater than current date, false otherwise
        Boolean startInTheFuture = e.getStartsAt().getTime() > date.getTime();
        return endGreaterThanStart && startInTheFuture;
    }
    
    private String wFD(Date d){
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setUm(UserManager um) {
        this.um = um;
    }

    public EntityManager getEm() {
        return em;
    }

    public UserManager getUm() {
        return um;
    }
    
}
