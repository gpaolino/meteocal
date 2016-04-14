/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.AdminSettings;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author alessandronegrini
 */
@ManagedBean(name="adminBean")
@RequestScoped
public class AdminBean {

    @EJB
    private UserManager um;
    private AdminSettings adminSettings;

    public AdminSettings getAdminSettings() {       
        if (adminSettings == null) {
            adminSettings = new AdminSettings();
        }
        return adminSettings;
    }

    public void setAdminSettings(AdminSettings adminSettings) {
        this.adminSettings = adminSettings;
    }
    
    public int getFreq(){
        return um.getSettings();
    }

    public AdminBean() {
    }
    
    public void update(){
        System.out.println("Valori aggiornati : \n"
                + "Frequenza aggiornamento meteo "+this.adminSettings.getFreqWUser());
        
        um.saveSettings(adminSettings);
        
    }
    
}
