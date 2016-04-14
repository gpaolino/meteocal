/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.entity;


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author andreagulino
 */
@Entity
public class Notification implements  JSONAware, Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private Boolean isRead;
    
    @Column
    private Timestamp createdOn;
    
    @Column
    private String type;
    
    @OneToOne(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE})
    @JoinColumn(name="sentTo",nullable=false)
    private User sentTo;
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="aboutEvent",nullable=false)
    private Event aboutEvent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    public void setIsRead(Boolean isRead){
        this.isRead = isRead;
    }
    
    public void setCreatedOn (Timestamp timestamp) {
        this.createdOn = timestamp;
    }
    
    public void setType (String type) {
        this.type = type;
    }

    public User getSentTo() {
        return this.sentTo;
    }
    
    public void setSentTo(User sentTo) {
        this.sentTo = sentTo;
    }
    
    public void setAboutEvent(Event event){
        this.aboutEvent = event;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se2.meteocal.entity.Notification[ id=" + id + " ]";
    }
    
    @Override
    public String toJSONString(){
        JSONObject obj = new JSONObject();
        obj.put("notID", this.getId().toString());
        obj.put("evID", this.aboutEvent.getId().toString());
        obj.put("read", isRead.toString());  
        obj.put("createdOn", createdOn.toString() );  
        //(new Long(createdOn.toInstant().toEpochMilli())).toString()
        String title = "";
        String text = "";
        


        switch (type) {
            case "Invitation":
                title = this.aboutEvent.getUser().getName();
                text = "I sent you an invitation to the event "+this.aboutEvent.getName()+".";
                break;
            case "EventCanceled":
                title = this.aboutEvent.getName();
                text = "The event has been canceled";
                break;
            case "WeatherChanged":  
                title = this.aboutEvent.getName();
                text = "The weather has changed";
                break;      
            case "EventDetailsChanged":
                title = this.aboutEvent.getName();
                text = "The information about this event has changed";
                break;
            
        }

        obj.put("title", title);
        obj.put("text", text);
        
            
        return obj.toString();     
    }
    
}
