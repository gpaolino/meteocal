/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.User;
import java.io.IOException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import it.polimi.se2.meteocal.boundary.Base64;

/**
 *
 * @author alessandronegrini
 */
@ManagedBean(name="resetPassword")
@RequestScoped
public class ResetPassword {

    @EJB
    private UserManager um;
    private String email;
    private String password;
    private String pastMill;
    private String pastMillDec;
    private final long currMill=System.currentTimeMillis();

    public String getEmail() {
        return email;
    }

    public String getPastMill() {
        return pastMill;
    }

    public void setPastMill(String pastMill) {
        this.pastMill = pastMill;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * It changes password if and only if the link is still valid. 
     * In the other case it will show an error context message 
     * @throws IOException
     * @throws InterruptedException 
     */
    public void resetPassword() throws IOException, InterruptedException{
        User u = um.find(email);
        byte[] bytes=Base64.decode(pastMill);
        pastMillDec = new String(bytes, "UTF-8");
        System.out.println(pastMillDec);
        
        if(isStillValid(currMill,Long.valueOf(pastMillDec))){
            um.resetPassword(password, u,Long.valueOf(pastMillDec));           
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Password has been reset"));
        }else{
            System.out.println("Sessione scaduta ");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Session Expired. ", "The possible reasons are : you have already used this link, or a time lapse greater than 12 hours is passed")); 
        }
    }
    
    public ResetPassword() {
    }
    
    /**
     * It checks if the link is still valid. 
     * The possible reasons why this link can be invalid could be : 
     * 1) the user has already changed his password 
     * 2) the user tries to change his password after a time lapse of 12 hours
     * @param curr current hour
     * @param past hours past
     * @return true if it's still valid, false on contrary
     */
    public boolean isStillValid(long curr,long past){
        User u = um.find(email);
        if(((curr-past)/(60*60*1000) > 12) || (Long.valueOf(u.getResPassTime())==past) ){
            return false;
        }
        else{
            return true;
        }
    }
}
