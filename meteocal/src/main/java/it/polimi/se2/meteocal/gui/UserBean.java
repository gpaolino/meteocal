/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import static com.sun.faces.facelets.util.Path.context;
import it.polimi.se2.meteocal.boundary.EventManager;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.User;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.json.JSONException;


@Named
@RequestScoped
public class UserBean {

    @EJB
    UserManager um;
    private User user;
    private String newPassword;
    private boolean deleteAcc;
    private User lu;
    
    private FacesContext context = FacesContext.getCurrentInstance();
    
    //For testing
    private String username;
    private String password;
    
    private Collection<Event> e;
    
    @EJB
    EventManager em;
    
    //For testing
    @PostConstruct
    public void setBean() {
        this.username = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("username");
        this.password = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("password");  
    }
    
    //For testing
    public String getUsername() {
        if(username == null && password == null)
            return "";
        return this.username;
    }
    
    //For Testing
    public String getPassword() {
        if(username == null && password == null)
            return "";
        return this.password;
    }
    
    public boolean sessionValid () {
        return this.um.getLoggedUser() != null;
    }
    
    public boolean isAdmin() {
        return (this.getLu().getType().equals("admin"));
    }
    
    public Collection<Event> getE() {
        
        List<Event> e1=em.getHisEvent(this.getCurrentUser());
        e= e1;
        return e;
    }

    public void setE(List<Event> e) {
        this.e = e;
    } 
    
    public UserBean() {
    }
    
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public User getLu() {
        if (lu == null) {
            lu = this.getCurrentUser();
        }
        return lu;
    }
    
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public boolean isDeleteAcc() {
        return deleteAcc;
    }

    public void setDeleteAcc(boolean deleteAcc) {
        this.deleteAcc = deleteAcc;
    }
    
    public User getCurrentUser() {
        return um.getLoggedUser();
    }
    
    public String getName() {
        return um.getLoggedUser().getName();
    }
    
    public String getAvatar() {
        return um.getLoggedUser().getAvatarString();
    }
    
    public String getEmail() {
        return um.getLoggedUser().getEmail();
    }
    
    /**  
     * Update user personal information
     * @return the page to which user will be redirected
     */
    public String updateMyPersonalInfo() throws JSONException, ParseException {
        um.edit(lu);
        return "home?faces-redirect=true";
    }
    
    /**
     * Changes the logged user's password
     * @throws JSONException
     * @throws ParseException
     * @throws IOException 
     */
    public void changePassword() throws JSONException, ParseException, IOException {
        if((getCurrentUser().getPassword()).equals(user.getPassword()) && user!=null){
            um.changePassword(newPassword);
            ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
            context_e.redirect(context_e.getRequestContextPath() + "/faces/user/home.xhtml");
        }else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Old Password wrong", "Please try again"));
        }
    }
    
    /**
     * Allows logged user to change his/her privacy settings
     * @return
     * @throws JSONException
     * @throws ParseException 
     */
    public String changePrivacy() throws JSONException, ParseException {
        um.edit(lu);
        return "home?faces-redirect=true";
    }
    
    /**
     * Allows user to delete his/her account
     * @return 
     */
    public String deleteAccount() {
        if(deleteAcc){
            um.unregister();
            return "/index?faces-redirect=true";
        }
        return "myprofile?faces-redirect=true";
    }
    
    public String defaultIcon() {
        return um.defaultAvatar();
    }
}
