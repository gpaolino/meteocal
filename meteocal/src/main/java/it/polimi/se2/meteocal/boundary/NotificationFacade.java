/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Notification;
import it.polimi.se2.meteocal.entity.User;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author andreagulino
 */
@Stateless
public class NotificationFacade extends AbstractFacade<Notification> {
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private UserManager um;
    
    @EJB
    private InvitationFacade ifa;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotificationFacade() {
        super(Notification.class);
    }
    
    public void sendNotification(User to, Event e, String type) {
        //read = false
        Notification not = new Notification();
        not.setIsRead(Boolean.FALSE);
        
        //set timestamp
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        
        not.setCreatedOn(new Timestamp(now.getTime()));
        
        //set type
        not.setType(type);
        
        //set sentto
        not.setSentTo(to);
        
        //set event
        not.setAboutEvent(e);
        
        this.create(not);
    }
    

    
    public void sendInvitationNotification(User to, Event e) {
        this.sendNotification(to,e,"Invitation");
    }
    
    /**
     * Set a notificatios as read
     * @param notificationID
     */
    public void notificationRead(long notificationID) {
        
        Notification n = this.find(notificationID);
        User u = (User) um.getLoggedUser();
       
        //security check
        if(n.getSentTo().equals(u)) {
            n.setIsRead(Boolean.TRUE);
            this.edit(n);
        }
        
    }
    
    public List<Notification> getUserNotifications(User u) {
        Query query = em.createNativeQuery("SELECT * FROM NOTIFICATION WHERE sentTo = '"+u.getEmail()+"' ORDER BY CREATEDON DESC;", Notification.class);
        List<Notification> results = query.getResultList();
        return results;
    }
    
    public void sendWeatherChangedNotification(Event e) {
        //Send to guests accepted/pending
        ifa.getInvitations(e.getId().toString() ) .stream().forEach((_item) -> {
            if(_item.isAccepted() || _item.isPending()) {
                this.sendNotification(_item.getInvitedUser(),e,"WeatherChanged");
            }  
        });
        //Send to owner
        this.sendNotification(e.getUser(), e, "WeatherChanged");
    }
    
    public void sendEventChangedNotification(Event e) {
        //Send to guests accepted/pending
        ifa.getInvitations(e.getId().toString() ) .stream().forEach((_item) -> {
            if(_item.isAccepted() || _item.isPending() || _item.isDeclined()) {
                this.sendNotification(_item.getInvitedUser(),e,"EventDetailsChanged");
            }  
        });
        
    }
    
    public void sendEventCanceledNotifications(Event e) {   
        //Send to guests accepted/pending
        ifa.getInvitations(e.getId().toString() ) .stream().forEach((_item) -> {
            if(_item.isAccepted() || _item.isPending()) {
                this.sendNotification(_item.getInvitedUser(),e,"EventCanceled");
            }  
        });
        
    }
    
}
