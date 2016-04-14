/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author alessandronegrini
 */
@Named(value = "systemBean")
@Dependent
public class SystemBean {

    private final String pn="http://localhost:8080/MeteoCal_Gulino_Guglielmino_Negrini";
    
    public String getPn() {
       return pn;
    }
  

    /**
     * Creates a new instance of SystemBean
     */
    
    public SystemBean() {
    }
    
}
