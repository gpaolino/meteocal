package it.polimi.se2.meteocal.gui;

import java.util.Map;
import javax.ejb.Singleton;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * This class needs only to pass values from the home page to signup one. 
 * It passes attributes like name,email,password from home page to signup page
 * @author alessandronegrini
 */

@ManagedBean
@SessionScoped
public class FromHomeToSignUp {

    private String name;
    private String email;
    private String password;

    /**
     * Default constructor
     */
    public FromHomeToSignUp(){
        
    }
    
    /** 
     * getter method for attribute name
     * @return attribute name
     */
    public String getName() {
        return name;
    }

    /**
     * setter method for attribute name
     * @param Name 
     */
    public void setName(String Name) {
        this.name = Name;
    }

    /**
     * method for attribute email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * setter method for email
     * @param Email 
     */
    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getPassword() {
        return password;
    }

    /**
     * setter method for password
     * @param password 
     */
    public void setPassword(String Password) {
        this.password = Password;
    }
    
    /**
     * It redirects us to the signup page and sets values to it 
     */
    public String goToSignUp(){
        return "signup?faces-redirect=true";
    }
    
 
}
