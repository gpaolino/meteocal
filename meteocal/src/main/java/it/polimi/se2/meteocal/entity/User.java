/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 *
 * @author alessandronegrini
 */
@Entity(name = "USERS")
public class User implements Serializable, JSONAware{
    
    @Id
    private String email;
    @Column(nullable=false)
    private String fullname;
    @Column(nullable=false)
    private String password;
    @Column(nullable=true)
    private Boolean hasPublicCalendar;
    @Column(nullable=true)
    private String type;
    @Column(nullable=true)
    private String groupName;
    @Lob @Basic(fetch=LAZY)
    @Column(length=1000)
    private String avatarString;
    @OneToOne(fetch=FetchType.LAZY,cascade = {CascadeType.ALL},orphanRemoval = true)
    @JoinColumn(name="livesIn",nullable=true)
    private Location location;
    @ManyToMany  
    @JoinTable(name="FOLLOW",
     joinColumns=@JoinColumn(name="follows"),
     inverseJoinColumns=@JoinColumn(name="followed")
    )
    private Collection<User> follows;
    @ManyToMany
    @JoinTable(name="FOLLOW",
     joinColumns=@JoinColumn(name="followed"),
     inverseJoinColumns=@JoinColumn(name="follows")
    ) 
    private Collection<User> followedBy;
    
    @OneToOne(fetch=FetchType.LAZY,cascade = {CascadeType.ALL},orphanRemoval = true)
    @JoinColumn(name="weather",nullable=true)
    private WForecast weather;

    public WForecast getWeather() {
        return weather;
    }

    public void setWeather(WForecast weather) {
        this.weather = weather;
    }
    
    private String ResPassTime;

    public String getResPassTime() {
        return ResPassTime;
    }

    public void setResPassTime(String ResPassTime) {
        this.ResPassTime = ResPassTime;
    }
    
    public Collection<User> getFollows() {
        return this.follows;
    }
    
    public Collection<User> getFollowedBy() {
        return this.followedBy;
    }
    
    public void setFollows(Collection<User> follows) {
        this.follows = follows;
    }
    
    public void setFollowedBy(Collection<User> followedBy) {
        this.followedBy = followedBy;
    }
     
    public Location getLocation() {
        if (location == null) {
            location = new Location();
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    public String getAvatarString() {
        return avatarString;
    }

    public void setAvatarString(String avatarString) {
        this.avatarString = avatarString;
    }
    
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    

    public Boolean getHasPublicCalendar() {
        return hasPublicCalendar;
    }

    public void setHasPublicCalendar(Boolean hasPublicCalendar) {
        this.hasPublicCalendar = hasPublicCalendar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return fullname;
    }

    public void setName(String name) {
        this.fullname = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, hash);
            this.password = bigInt.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.email);
        return hash;
    }
    
    public User(){}
    
    @Override
     public String toJSONString(){
            JSONObject obj = new JSONObject();
            obj.put("id", email);    
            obj.put("fullname", fullname);
            obj.put("avatar", avatarString);
            obj.put("city", location.getCity());
            return obj.toString();
         }
    
    
}
