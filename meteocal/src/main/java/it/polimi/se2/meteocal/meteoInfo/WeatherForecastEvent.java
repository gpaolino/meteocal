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
import java.text.DateFormat;
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
public class WeatherForecastEvent {
 
    private String URL;
    private JSONObject jsonObject;
    
    private final int threeHours=10800000;
    
    /**
     * After having set the URL with the specific coordinate it sets the JSON object with current weather forecast
     * @param lat latitude
     * @param longit longitude

     */
    public WeatherForecastEvent(String lat,String longit){   
        
        this.URL ="http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+longit+"&APPID=08ea3643077f63cfb4e6fa98e719ea95";
 
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
     * It gets the weather in an specific date and hour
     * @param d date 
     * @return the weather 
     * @throws org.json.JSONException
     */
    public String getWeather(Date d) throws JSONException, ParseException{
        
        String toBeRet = "Nan"; 
        String result_weather;
        try{
            JSONArray JSONArray_weathers = jsonObject.getJSONArray("list");

            if(JSONArray_weathers.length() > 0){
                for(int i=0;i<JSONArray_weathers.length();i++){

                    JSONObject JSONObject_weather = JSONArray_weathers.getJSONObject(i);

                    //get date
                    String date=JSONObject_weather.getString("dt_txt");
                    //get weather
                    JSONArray JSONArray_weat = JSONObject_weather.getJSONArray("weather");
                    String weather_descr=JSONArray_weat.getJSONObject(0).getString("main");

                    if(d.getTime()<=this.fromStringToDate(date).getTime()+this.threeHours&&d.getTime()>=this.fromStringToDate(date).getTime()){
                        toBeRet=weather_descr;
                    }

                }
            }else{
                result_weather = "weather empty!";
                System.out.println(result_weather);
            }

            return toBeRet;
        }
        catch (NullPointerException e){
            return "Nan";
        }
        catch (JSONException e){
            return "Nan";
        }
    }
    
    
    
    
    /**
     * Transform a string to date 
     * @param d date string
     * @return date formatted
     * @throws ParseException 
     */
    
    private Date fromStringToDate(String d) throws ParseException{
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
        Date startDate=null;
        try {
            startDate = df.parse(d);
            String newDateString = df.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }
}
