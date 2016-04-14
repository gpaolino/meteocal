/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.polimi.se2.meteocal.boundary.EventFacade;
import it.polimi.se2.meteocal.boundary.EventManager;
import it.polimi.se2.meteocal.boundary.InvitationFacade;
import it.polimi.se2.meteocal.boundary.NotificationFacade;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Invitation;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Named("event")
@RequestScoped
public class EventView {
    @EJB
    private EventManager em;
    
    private FacesContext context = FacesContext.getCurrentInstance();

    @Inject 
    InvitationFacade ifa;
    
    @Inject
    NotificationFacade nf;
    
    @Inject 
    private EventFacade ef;
    
    @Inject
    private UserManager um;
     
    
    private String id;
    //Javascript array string (json)
    private String alreadyInvited;
    
    private String invitationList;
    private String invitationText;
    
    private String notifTrigger;
    
    private Invitation myInvitation;

    private Event event;
    
    
    
    public String getNotifTrigger() {
        if( this.id == null || um.getLoggedUser() == null) return null;
        
        switch(this.getEvent().getNotIfTrigger()){
            case 0:
                this.notifTrigger="SUN";
                break;
            case 1:
                this.notifTrigger="CLOUDS";
                break;
            case 2:
                this.notifTrigger="RAIN";
                break;
            case 3:
                this.notifTrigger="THUNDER";
                break;
            case 4:
                this.notifTrigger="SNOW";
                break;
            case 5:
                this.notifTrigger="NONE (indoor event)";
                break;
                
        }
        
        return notifTrigger;
    }

    public void setNotifTrigger(String notifTrigger) {
        this.notifTrigger = notifTrigger;
    }
    
 
   
    @PostConstruct
    public void setBean() {
        if(um.getLoggedUser() == null ) return;
        
        id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        
        if(id == null) {
            System.out.println("##Event View invoked without id parameter");
            return;
        }
        
        //If the event page is loaded after opening a notificatian, the notification is set as 'read'
        String notifId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("deleteNotifId");
       
        if( null != notifId ){
             nf.notificationRead(new Long(notifId));
        }
       
        
        //I build the array of already invited users for the invitation section
        alreadyInvited = "[";
        ifa.getInvitations(this.id).stream().forEach((i) -> {
            alreadyInvited = alreadyInvited+"'"+i.getInvitedUser().getEmail()+"',";
        });
        alreadyInvited += "]";
        
        //Get my invitation to this event if any
        myInvitation = ifa.getInvitation(this.um.getLoggedUser(), this.getEvent());
        
        //Default invitation text
        invitationText = "I am pleased to invite you to attend this event.";
        
    }
    
    /**
     * Cancels the event (flag isCanceled setted true)
     * @return 
     */
    public String cancelEvent(){
        if ( this.id == null || um.getLoggedUser() == null) return "home.xhtml?error=MissingEventID";
        
        //Send notification to all invited users
        nf.sendEventCanceledNotifications(this.getEvent());
        ef.edit(this.getEvent());
        
        //Set all invitations to canceled
        List<Invitation> invitedUsers = ifa.getInvitations(this.getId());
        invitedUsers.stream().forEach((i) -> {
            i.cancel();
            ifa.edit(i);
        });
        
        return "home.xhtml?faces-redirect=true";
    }
    
    //GET METHODS FOR ALREADY SENT INVITATIONS
    
    /**
     * Returns all invitations to the current event
     * @return 
     */
    public List<Invitation> getInvited() {
        if ( this.id == null || um.getLoggedUser() == null) return null;
        return ifa.getInvitations(this.id);
    }
    
    /**
     * Returns only accepted events
     * @return 
     */
    public List<Invitation> getAccepted() {
        return this.getWithStatus("ACCEPTED");
    }
    
    /**
     * Returns only declined invitations
     * @return 
     */
    public List<Invitation> getDeclined() {
        return this.getWithStatus("DECLINED");
    }
    
    /**
     * Returns only pending invitations
     * @return 
     */
    public List<Invitation> getPending() {
        return this.getWithStatus("PENDING");
    }
    
    /**
     * Returns invitations with status passed as parameter
     * @param status
     * @return 
     */
    private List<Invitation> getWithStatus(String status) {
        if ( this.id == null || um.getLoggedUser() == null) return null;
        
        List<Invitation> invs  = new ArrayList<>();
        for( Invitation i: ifa.getInvitations(this.id) ) {
            if(i.getStatus().equals(status))
                invs.add(i);
        }
        return invs;
    }    
    
    public String getId() {
        if( this.id == null || um.getLoggedUser() == null) return null;
        
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public EventView() {
    }
    
    public Event getEvent() {
        
        if(this.event == null) {
            if( this.id == null || um.getLoggedUser() == null) {
                //Rturn an empty event just to display the html
                Event e =  new Event();
                e.setName("No event ID provided");
                return e;
            }
                
            
            Long id1 = (new Long(id));
            if( id1 >=0) {
                
                this.event =  ef.find(id1);
            }
        } 
        return this.event;
    }
    
    
    public void setEvent(Event e) {
        this.event = e;
    }
    //INVITATION METHODS TO SEND NEW INVITATION
    
    /**
     * Returns the list of new invitations formatted as JS array
     * @return 
     */
    public String getInvitationList() {
        return this.invitationList;
    }
    
    /**
     * Used to set the new list of invitations
     * @param invitationList 
     */
    public void setInvitationList(String invitationList) {
        this.invitationList = invitationList;
    }
    
   /**
    * Invitation text provided by the owner
    * @param invitationText 
    */
    public void setInvitationText(String invitationText) {
        this.invitationText = invitationText;
    }
    
    /**
     * Get invitationt text
     * @return 
     */
    public String getInvitationText() {
        return this.invitationText;
    }
    
    /**
     * Returns the list of already invited users formatted as a Javascript array
     * @return 
     */
    public String getAlreadyInvited() {
        if( this.id == null || um.getLoggedUser() == null) return "[]";
        return this.alreadyInvited;
    }
    
    /**
     * Send invitations to guests (background operation)
     * @return 
     */
    public String sendInvitations() {

        if( !this.invitationList.equals("") ) {
            System.out.println("#INVITATI: "+invitationList);
           //Do stuff in background
            new Thread(() -> {
                ifa.sendInvitations(getEvent(), invitationList, invitationText);
            }).start();
            System.out.println("event.xhtml?faces-redirect=true&&id="+this.id);
       } else {
            System.out.println("##Empty invitation list submitted. ");
        }
       return "event.xhtml?faces-redirect=true&&id="+this.id;
    }
    
    //INVITATION METHODS CONCERNING THE INVITATION FOR THE SINGLE GUEST
    /**
     * After checking with iCanSeeThis() if I can see this events returns true
     * if there is an invitation to display, false otherwise
     * 
     * @return 
     */
    public Boolean iAmInvited() {
        if( this.id == null || um.getLoggedUser() == null) return false;
        
        String loggedEmail  = this.um.getLoggedUser().getEmail();
        String ownerEmail = this.getEvent().getUser().getEmail();
        
        //I am the owner: no invitation
        if(loggedEmail.equals(ownerEmail))
            return false;
        //Otherwise: there should be an invitation for me
            if(myInvitation != null) {
                return true;
            } else {

                return false;
            }
            

    }
    
    /**
     * Returns the invitation of current user for this event if exists 
     * @return 
     */
    public Invitation getMyInvitation() {

        return myInvitation;
    }
    
    
    /**
     * Returns true if it is possible to perform actions on the invitation (no conflicts)
     * @return
     */
    public boolean canPerformActionOnInvitation() {
       
        if ( !myInvitation.isAccepted() ) {
            System.out.print("##DENTRO CHECK");
            if(ef.getConflicts(myInvitation.getToEvent()).isEmpty())
                return true;
            else return false;
            
        }
        return true;
    }
    
    
    //METHODS CONCERNING SECURITY
    
    /**
     * Return true if i am allowed to see this event
     **/
    public Boolean iCanSeeThis() {
        if( this.id == null || um.getLoggedUser() == null) return true;
        
        String loggedEmail  = this.um.getLoggedUser().getEmail();
        String ownerEmail = this.getEvent().getUser().getEmail();
        
        //I am the owner: can see this
        if(loggedEmail.equals(ownerEmail))
            return true;
        //Otherwise the event is public or i have the invitation
        if( this.getEvent().getIsPublic() == true 
                || this.myInvitation!=null)
            return true;
        else 
            return false;
    }
    
    /**
     * Check if i am the owner of this event
     * @return 
     */
    public Boolean iAmTheOwner() {
        if( this.id == null || um.getLoggedUser() == null) return true;
        String loggedEmail  = this.um.getLoggedUser().getEmail();
        String ownerEmail = this.getEvent().getUser().getEmail();
        return (loggedEmail.equals(ownerEmail));
    }
    
    public String acceptInvitation() {
        if( this.id == null || um.getLoggedUser() == null) return null;
       
        if(this.myInvitation != null){
            this.myInvitation.accept();
        }
        ifa.edit(myInvitation);
       ;
        return "event.xhtml?faces-redirect=true&&id="+this.id;
    }
    
    public String declineInvitation() {
        if( this.id == null || um.getLoggedUser() == null) return null;
        
        if(this.myInvitation != null) {
            this.myInvitation.decline();
        }
        ifa.edit(myInvitation);
        return "event.xhtml?faces-redirect=true&&id="+this.id;
    }
    
    /**
     * Save the changes in event settings.
     */
    public void saveEdits() throws IOException {
        
        boolean err=false;
        
        if( this.id == null || um.getLoggedUser() == null) {
            ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
            context_e.redirect(context_e.getRequestContextPath() + "/faces/user/home.xhtml"+this.id);
        }
        
        if(!(ef.checkDateConsistency(event))){
            //Date inconsistenti
            System.out.println("Date inconsistenti");
            err=true;
        }
        
        List<Event> conflicts = ef.getConflicts(event);
        //Non ci sono conflitti se ne restituisce 0 o quello che restituisce ha id uguale all'evento che si vuole modificare
        boolean noConflicts = (conflicts.size() == 0)||(conflicts.size()==1 && (conflicts.get(0).getId()).equals(event.getId()));
        if(!noConflicts){
            //Conflicts found
            System.out.println("Conflitti trovati: "+ef.getConflicts(event).size());
            for(Event x : conflicts){
                System.out.println("Evento in conflitto: "+x.getName());
            }
            err=true;
        }

        //If the event is indoor set the trigger value 5
        if(!(this.getEvent().getIsOutdoor())){
            this.getEvent().setNotIfTrigger(5);
        }
        
        if(err){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Issues", "Please check if date inserted correctly or if another event is scheduled in those hours")); 
        }else{
            System.out.println("##Edits submitted "+this.getEvent().getName()+"Errore ? "+err);
            ef.edit(this.getEvent());
            nf.sendEventChangedNotification(this.getEvent());
            ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
            context_e.redirect(context_e.getRequestContextPath() + "/faces/user/event.xhtml?faces-redirect=true&&id="+this.id);
        }
    }
    
}

