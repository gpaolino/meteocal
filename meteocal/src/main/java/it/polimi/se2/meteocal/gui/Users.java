/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.User;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author andreagulino
 */
@ManagedBean
@ViewScoped

public class Users {

   
   private String email;
    
    User otherUser;
    
    @Inject 
    UserManager um;
  
    @PostConstruct
    public void setBean() {
        email = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email");
        this.otherUser = um.find(email);
    }

    public boolean isItMe() {
        return email.equals(um.getLoggedUser().getEmail());
    }
 
    public String getEmail(){
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Users() {
      
    }
    
    public String getFullName() {     
        return otherUser.getName();
    }
    
    public String getAvatar() {     
        return otherUser.getAvatarString();
    }
    
    public Boolean hasPrivateCalendar() {
        return otherUser.getHasPublicCalendar();
    }
    
    public String getCity() {
        return otherUser.getLocation().getCity();
    }
    
    //If i'm following the other user
    public Boolean followed() {
       return um.follows(email);
    }
    
    public void follow() {
        this.um.follow(email);
    }
    
    public void unfollow() {
        this.um.unfollow(email);
    }
    
    public  Collection<User> following() {
        return this.um.find(email).getFollows();
    }
    
    public  Collection<User> followers() {
        return this.um.find(email).getFollowedBy();
    }

}
