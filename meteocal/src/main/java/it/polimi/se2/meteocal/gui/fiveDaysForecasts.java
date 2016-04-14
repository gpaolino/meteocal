/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

import it.polimi.se2.meteocal.meteoInfo.WeatherForecastEvent;
import it.polimi.se2.meteocal.meteoInfo.WeatherForecastsFiveDays;
import java.text.ParseException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.json.JSONException;

/**
 *
 * @author alessandronegrini
 */
@ManagedBean(name="fiveDaysForecasts")
@RequestScoped
public class fiveDaysForecasts {

    FacesContext facesContext = FacesContext.getCurrentInstance();
    String parameter_value = (String) facesContext.getExternalContext().
                              getRequestParameterMap().get("city");
    WeatherForecastsFiveDays wFE=new WeatherForecastsFiveDays(parameter_value);

    public String getParameter_value() {
        return parameter_value;
    }

    public void setParameter_value(String parameter_value) {
        this.parameter_value = parameter_value;
    }

    
    
    private  String[][] weathers;

    private String data;

    public String getData(int a, int b) {
        return weathers[a][b];
        
    }

    public void setData(String data) {
        this.data = data;
    }
    
    
    
    
    public WeatherForecastsFiveDays getwFE() {
        return wFE;
    }

    public void setwFE(WeatherForecastsFiveDays wFE) {
        this.wFE = wFE;
    }

    
    public fiveDaysForecasts() throws JSONException, ParseException {
        
        weathers = wFE.getFiveDaysWeather();
        
    }
    
}
