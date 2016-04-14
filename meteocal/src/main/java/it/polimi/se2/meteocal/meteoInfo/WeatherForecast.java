/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.meteoInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alessandronegrini
 */
public class WeatherForecast {
 
    private String URL;
    private JSONObject jsonObject;
    
    /**
     * After having set the URL with the specific city it sets the JSON object with current weather forecast
     * @param city 
     * @throws JSONException
     * @throws IOException
     * @throws MalformedURLException
     */
    public WeatherForecast(String city){   
        
        this.URL = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=08ea3643077f63cfb4e6fa98e719ea95";
 
        String result = "";
         
        try {
            URL url_weather = new URL(this.URL);
 
            HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();
 
            if (httpURLConnection!=null && httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
 
                InputStreamReader inputStreamReader =
                    new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader =
                    new BufferedReader(inputStreamReader, 8192);
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                 
                bufferedReader.close();
                                
                jsonObject = new JSONObject(result);
                
 
            } else {
                System.out.println("Error in httpURLConnection.getResponseCode()!!!");
            }
 
        } catch (MalformedURLException ex) {
            Logger.getLogger(WeatherForecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WeatherForecast.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(WeatherForecast.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (EJBException ex) {
            Logger.getLogger(WeatherForecast.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * It gets the temperature in the JSON object
     * @return the current temperature
     * @throws JSONException 
     */
    public Float getTemperature() throws JSONException{      
        
        try{
            JSONObject JSONObject_coord = jsonObject.getJSONObject("main");
            Double result_temp = JSONObject_coord.getDouble("temp");
            //System.out.println("La temperatura è di "+ result_temp);
            return result_temp.floatValue()-273.15F;
        }
        catch (NullPointerException e){
            return 0F;
        }
        catch (JSONException e){
            return 0F;
        }
    
    }
    
    /**
     * It gets the humidity percentage in the JSON object
     * @return the current humidity percentage
     * @throws JSONException 
     */
    public Float getHumidity() throws JSONException{      
        try{
            JSONObject JSONObject_coord = jsonObject.getJSONObject("main");
            Double result_hum = JSONObject_coord.getDouble("humidity");
            return result_hum.floatValue();    
        }
        catch (NullPointerException e){
            return 0F;
        }
        catch (JSONException e){
            return 0F;
        }
    }
    
    /**
     * It gets the weather (intended as forecast) in the JSON Object
     * @return the current weather
     * @throws JSONException 
     */
    public String getWeather() throws JSONException {
        try{
            JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");
            String result_main;
            if(JSONArray_weather.length() > 0){
                JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
                //int result_id = JSONObject_weather.getInt("id");
                result_main = JSONObject_weather.getString("main");
                //String result_description = JSONObject_weather.getString("description");
                //String result_icon = JSONObject_weather.getString("icon");        
            }else{
                return "Nan";
            }
            return result_main;
        }
        catch (NullPointerException e){
            return "Not Available yet";
        }
        catch (JSONException e){
            return "Not Available yet";
        }
    }

    
}
