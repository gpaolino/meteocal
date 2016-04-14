/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;


class InvitationId {
    String invitedUser;
    Long toEvent;
}

/**
 *
 * @author andreagulino
 */
@Entity @IdClass(InvitationId.class)
public class Invitation implements Serializable {
    private static final long serialVersionUID = 1L;
            
    @Id User invitedUser;
    @Id Event toEvent;
    @Column(nullable=true)
    String status;
    //PENDING|ACCEPTED|DECLINED|CANCELED
    @Column
    String text;
    
    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }
    
    public User getInvitedUser() {
        return this.invitedUser;
    }

    public void setToEvent(Event toEvent) {
        this.toEvent = toEvent;
    }
    
    public Event getToEvent() {
        return this.toEvent;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText( String text ) {
        this.text = text;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public Invitation() {
        super();
        this.status = "PENDING";
    }
       
    public void accept() {
        this.status = "ACCEPTED";
    }
    
    public void decline() {
        this.status = "DECLINED";
    }
    
    public void cancel() {
        this.status = "CANCELED";
    }
    
    public Boolean isPending() {
        return this.status.equals("PENDING");
    }
    
    public Boolean isAccepted() {
        return this.status.equals("ACCEPTED");
    }
    
    public Boolean isDeclined() {
        return this.status.equals("DECLINED");
    }
    
    public Boolean isCanceled() {
        return this.status.equals("CANCELED");
    }
  
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invitation)) {
            return false;
        }
        Invitation other = (Invitation) object;
        if ( (this.invitedUser != other.invitedUser) || (this.toEvent != other.toEvent) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Invitation to event: "+this.toEvent+" sent to "+this.invitedUser+" with status "+this.status;
    }
    
}
