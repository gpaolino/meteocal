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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alessandronegrini
 */
public class WeatherForecastsFiveDays {
   
    private String URL;
    private JSONObject jsonObject;
    
    private final int threeHours=10800000;
    private final int oneDay=86400000 ;
    /**
     * After having set the URL with the specific coordinate it sets the JSON object with current weather forecast
     * @param lat latitude
     * @param longit longitude

     */
    public WeatherForecastsFiveDays(String city){   
        
        this.URL ="http://api.openweathermap.org/data/2.5/forecast/daily?q="+city+"&cnt=10&mode=json+&APPID=08ea3643077f63cfb4e6fa98e719ea95";
 
        String result = "";
         
        try {
            URL url_weather = new URL(this.URL);
 
            HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();
 
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
 
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
        
    }
    
    /**
     * It gets the five weather forecasts
     * @return the weather 
     * @throws org.json.JSONException
     */
    public String[][] getFiveDaysWeather() throws JSONException, ParseException{
        
         
        String result_weather;
        
        JSONArray JSONArray_weathers = jsonObject.getJSONArray("list");
        System.out.println("Mi appresto a scrivere");
        String toBeRet[][] = new String [JSONArray_weathers.length()][7];
        if(JSONArray_weathers.length() > 0){
            for(int i=0;i<JSONArray_weathers.length();i++){
                
                JSONObject JSONObject_weather = JSONArray_weathers.getJSONObject(i);
                
                //get info
                String date=JSONObject_weather.getString("dt");
                JSONObject temp = JSONObject_weather.getJSONObject("temp");
                Double hum = JSONObject_weather.getDouble("humidity");
                Double temp_min = Math.floor(temp.getDouble("min")-273.15);
                Double temp_max = Math.floor(temp.getDouble("max")-273.15);
                Double temp_day= Math.floor(temp.getDouble("day")-273.15);
                //get weather
                JSONArray JSONArray_weat = JSONObject_weather.getJSONArray("weather");
                String weather_descr=JSONArray_weat.getJSONObject(0).getString("main");
                String icon=JSONArray_weat.getJSONObject(0).getString("icon");

                long mill=new Date().getTime()+i*this.oneDay;
                Date d=new Date(mill);
                toBeRet[i][0]=d.toString().substring(0,11)+""+d.toString().substring(23,28);
                toBeRet[i][1]=temp_day.toString();
                toBeRet[i][2]=temp_min.toString();
                toBeRet[i][3]=temp_max.toString();
                toBeRet[i][4]=hum.toString();
                toBeRet[i][5]=weather_descr;
                toBeRet[i][6]=icon;
  
                
                
            }
        }else{
            result_weather = "weather empty!";
            System.out.println(result_weather);
        }
        
        return toBeRet;
    }
    
}


