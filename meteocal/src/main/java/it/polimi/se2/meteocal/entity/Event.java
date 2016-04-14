/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.entity;

import it.polimi.se2.meteocal.boundary.UserManager;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author andreagulino
 */
@Entity
public class Event implements Serializable,JSONAware { 
    private static final long serialVersionUID = 1L;
    
    @Id @GeneratedValue
    private Long id;
    @Column(nullable=false)
    private String name;
    @Column(nullable=true)
    private String description;
    @Column(nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startsAt;
    @Column(nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endsAt;
    @Column(nullable=true)
    private Boolean isPublic;
    @Column(nullable=true)
    private Boolean isOutdoor;
    @Column(nullable=true)
    private Boolean isCanceled;
    @Column(nullable=true)
    private int notIfTrigger;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="owner",nullable=true)
    private User user;   
    @OneToOne(fetch=FetchType.LAZY,cascade = CascadeType.MERGE)
    @JoinColumn(name="location",nullable=false)
    private Location location;
    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name="weatherForecast",nullable=true)
    private WForecast weatherForecast;
    @Column(nullable=true)
    private String facebookLink;
    @Column(nullable=true)
    private String twitterLink;
    
    @Transient
    private boolean hide = false;

    public WForecast getWeatherForecast() {
        return weatherForecast;
    }

    public void setWeatherForecast(WForecast weatherForecast) {
        this.weatherForecast = weatherForecast;
    }
    
    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Date startsAt) {
        this.startsAt = startsAt;
    }

    public Date getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Date endsAt) {
        this.endsAt = endsAt;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsOutdoor() {
        return isOutdoor;
    }

    public void setIsOutdoor(Boolean isOutdoor) {
        this.isOutdoor = isOutdoor;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public int getNotIfTrigger() {
        return notIfTrigger;
    }

    public void setNotIfTrigger(int notIfTrigger) {
        this.notIfTrigger = notIfTrigger;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    public void setHidden() {
        this.hide = true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se2.meteocal.entity.Event[ id=" + id + " ]";
    }
    
    public Event hide() {
        Event e = null;
        try {
            e = (Event) this.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
        }
        e.setHidden();
        return e;
    }
    /**
     * Returns true if the event is in conflict 
     * with another, it is possible for an event to start when another ends
     * @param e
     * @return 
     */
   /* public boolean isInConflictWith(Event e) {
        Long eStart = e.startsAt.toInstant().toEpochMilli();
        Long eEnd = e.endsAt.toInstant().toEpochMilli();
        Long thisStart = this.startsAt.toInstant().toEpochMilli();
        Long thisEnd = this.endsAt.toInstant().toEpochMilli();
        //E in included in this OR This is included in E OR intersections not void
        if (eStart>=thisStart && eEnd<=thisEnd ||
            thisStart>=eStart && thisEnd <=eEnd ||
            thisStart<eStart && thisEnd>eStart ||
            eStart<thisStart && eEnd>eStart ||
            
    }*/
    
    public String toJSONString(){
        
          String colorClass = "dot-purple";
          int n = this.name.hashCode();
          switch (n%6) {
              case 0: colorClass = "dot-red"; break;
              case 1: colorClass = "dot-blue"; break;
              case 2: colorClass = "dot-grey"; break;
              case 3: colorClass = "dot-green"; break;
              case 4: colorClass = "dot-purple"; break;
              case 5: colorClass = "dot-brown"; break;
          }
        
          JSONObject  obj = new JSONObject();
          
          
          //If requester is not owner and event is  private don't write details {
           if(this.hide){
                obj.put("id", "");
                obj.put("title", "Private Event");
                obj.put("url", "");
                obj.put("class", "dot-grey");
           } else {
                obj.put("id", id);
                obj.put("title",name);
                obj.put("url", "event.xhtml?id="+id);
                obj.put("class", colorClass);
           }
          
   
          obj.put("start", new Long(startsAt.toInstant().getEpochSecond()).toString()+"000");
          obj.put("end", new Long(endsAt.toInstant().getEpochSecond()).toString()+"000");
          
          return obj.toString();

     }


 
}
