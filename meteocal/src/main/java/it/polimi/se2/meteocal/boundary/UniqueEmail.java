/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.User;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author alessandronegrini
 */
@RequestScoped
@FacesValidator("uniqueEmail")
public class UniqueEmail implements Validator, Serializable {

   @PersistenceContext
    protected EntityManager em;
    private boolean isValid;
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
                
        Query q = em.createNativeQuery("SELECT * FROM USERS WHERE EMAIL='"+value.toString()+"' AND TYPE!='NotRegisteredUser';");
        List<User> results = q.getResultList();
        
        if(results.isEmpty()){
            isValid=true;
        }else{
            isValid=false;
        }
           
        if (!isValid) {
            throw new ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Email is already in use.", null));
        }
    }
           
}
