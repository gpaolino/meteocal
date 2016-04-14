/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Invitation;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.Notification;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.gui.EventView;
import it.polimi.se2.meteocal.mail.EmailTemplate;
import it.polimi.se2.meteocal.mail.Mail;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author andreagulino
 */



@Stateless
public class InvitationFacade extends AbstractFacade<Invitation> {
    @Inject
    UserManager um;
    
    @Inject 
    NotificationFacade nf;

    
    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvitationFacade() {
        super(Invitation.class);
    }
    
    /**
     * Returns all the invitations for eventId
     * @param eventId
     * @return 
     */
    public List<Invitation> getInvitations(String eventId) {
       // controllo possiede evento if( um.getLoggedUser().)
        Query query = em.createNativeQuery("SELECT * FROM INVITATION WHERE TOEVENT_ID = '"+eventId+"'", Invitation.class);
        List<Invitation> results = query.getResultList();
        return results;
    }
    
     /**
     * Get invitation for the user passed as parameter, null if not exists
     * @param u
     * @return 
     */
    public List<Invitation> getInvitationForUser(User u) {
        Query query = em.createNativeQuery("SELECT * FROM INVITATION WHERE INVITEDUSER_EMAIL = '"+u.getEmail()+"'", Invitation.class);
        List<Invitation> results = query.getResultList();
        if(results.isEmpty())
            return null;
        return results;
    }
   
    
    /**
     * Get invitation for the user and event passed as parameters, null if not exists
     * @param u
     * @param e
     * @return 
     */
    public Invitation getInvitation(User u, Event e) {
        Query query = em.createNativeQuery("SELECT * FROM INVITATION WHERE TOEVENT_ID = '"+e.getId()+"' AND INVITEDUSER_EMAIL = '"+u.getEmail()+"'", Invitation.class);
        List<Invitation> results = query.getResultList();
        if(results.isEmpty())
            return null;
        return results.get(0);
    }
    
    /**
     * For each invitation sends notification, email and adds a new instance of invitation calling sendInvitation()
     * @param e
     * @param invitationList
     * @param invitationText 
     */
    public void sendInvitations(Event e, String invitationList, String invitationText) {
        
        //Parse the invitationList string
        JSONParser parser = new JSONParser();
        Object obj  = null;
        try {
           obj = parser.parse(invitationList);
        } catch (ParseException ex) {
            Logger.getLogger(EventView.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONArray array=(JSONArray)obj;
        
        //Each email
        for(Object email : array) {
            
            String prepend = "";
            
            //The user exists?
            if( null == um.find((String) email) ) {
                //L'UTENTE NON E' NELLA TABELLA UTENTI
                um.createUnregistred((String)email);
                prepend = " <br /> Please <a href='http://localhost:8080/meteocal/faces/signup.xhtml?email="+email+"'>sign up</a> to see details. ";              
            }
            
            //Send invitation and send notification
            this.sendInvitation( um.find((String) email), e, invitationText );
            nf.sendInvitationNotification(um.find((String) email), e);
            
            //Send Email
            String ownerName = e.getUser().getName();
            String eventName = e.getName();
            String eventId = e.getId().toString();
            String eventUrl = "<a href='http://localhost:8080/meteocal/faces/user/event.xhtml?id="+eventId+"'>"+eventName+"</a>";
            String html = EmailTemplate.getTemplate("You have been invited by <b>"+ownerName+"</b> to the event "+eventUrl+" .<br/>Message: "+invitationText+"<br/>"+prepend);
            Mail mail = new Mail();
          
            mail.setFrom("MeteoCal");;
            mail.setTo((String) email);
            mail.setSubject(ownerName+" invited you");
            mail.sendMail(html);

        } 
       
    }
    
    
    //true  = ok || false = already exists
    private boolean sendInvitation(User u, Event e, String text) {
        
        Invitation i = new Invitation();
        i.setInvitedUser(u);
        i.setToEvent(e);
        i.setText(text);

        try {
            em.persist(i);
        } catch (EntityExistsException ee) {
            System.err.println("Entity invitation exists:"+ee.getMessage());
            return false;
        }
        System.out.println(i);
        em.flush();
       
        
        return true;
               
    }
    
}
