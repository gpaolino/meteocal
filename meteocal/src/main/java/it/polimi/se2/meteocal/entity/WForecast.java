/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author alessandronegrini
 */
@Entity
public class WForecast implements Serializable {

    @Id@GeneratedValue
    private Long id;
    @Column(nullable=true)
    private String weather;
    @Column(nullable=true)
    private float temperature;
    @Column(nullable=true)
    private float humidity;
    /*
    @Column(nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    */
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

}
