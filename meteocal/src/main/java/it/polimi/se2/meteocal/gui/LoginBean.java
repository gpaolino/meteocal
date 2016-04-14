/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.boundary.UserManager;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author alessandronegrini
 */
@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;
        
    @EJB
    UserManager em;
    
    public LoginBean() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** It deals with login operation
     * @return the user home page 
     */
    public String login() {
        System.out.print("Login tempted by "+this.username+" with password: "+this.password);
        FacesContext context = FacesContext.getCurrentInstance();
        
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            if(em.find(this.getUsername())!=null && "NotRegisteredUser".equals(em.find(this.getUsername()).getType())){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"", "Email or password wrong"));
                return null;
            }else{
                request.login(this.username, this.password);
            }
        } catch (ServletException e ) {
            System.out.printf("!!!!! Login failed !!!!!");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"", "Email or password wrong"));
            return null;
        }
        

        
        return "/user/home?faces-redirect=true";
        
    }

   
    /**
     * It deals with logout operation
     * @return the url to which user will be redirected 
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {            
            request.logout();
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed."));
        }
        return "/index?faces-redirect=true";
    }
    

    
}

