/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.meteoInfo;

import it.polimi.se2.meteocal.boundary.InvitationFacade;
import it.polimi.se2.meteocal.boundary.NotificationFacade;
import it.polimi.se2.meteocal.entity.AdminSettings;
import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Invitation;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import it.polimi.se2.meteocal.gui.CreationEventBean;
import it.polimi.se2.meteocal.mail.EmailTemplate;
import it.polimi.se2.meteocal.mail.Mail;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.json.JSONException;

/**
 *
 * @author alessandronegrini
 */

@Startup
@Singleton
public class WeatherUpdate extends Thread{
     
    @PersistenceContext
    EntityManager em;
      
    @Inject 
    NotificationFacade nF;
    
    @Inject 
    InvitationFacade iF;
    
    CreationEventBean cEB=new CreationEventBean();

    private boolean send=false;
    
    private String pastWeather="Nan";
    
    private final long twHours=259200000;
    private final long oneDay = 86400000 ;
    
    private Mail mail=new Mail();
    
    private int frequency;
    
    public WeatherUpdate(){
        
    }
    
    @Resource
    private TimerService timerService;
    
    @PostConstruct
    private void init(){  
        List<AdminSettings> results = null;
        TypedQuery<AdminSettings> query =(TypedQuery<AdminSettings>) em.createNativeQuery("SELECT * FROM ADMINSETTINGS", AdminSettings.class);

        do {
            results = query.getResultList();
            if(results.size()!=0)
                frequency = results.get(0).getFreqWUser();
            else {
                AdminSettings as = new AdminSettings();
                as.setFreqWUser(12);
                as.setId(1L);
                this.em.persist(as);
            }
        } while(results.size()==0);
                
        ScheduleExpression every5Seconds = new ScheduleExpression().minute("*/"+frequency).hour("*");
        timerService.createCalendarTimer(every5Seconds, new TimerConfig("", false));
    }

    @Timeout
    public void checkWeatherProgTimer() throws JSONException, ParseException{
        
        TypedQuery<AdminSettings> query =(TypedQuery<AdminSettings>) em.createNativeQuery("SELECT * FROM ADMINSETTINGS", AdminSettings.class);
        List<AdminSettings> results = query.getResultList();
        int frequencyUpdated = results.get(0).getFreqWUser();
        
        if(frequency != frequencyUpdated){
            frequency=frequencyUpdated;
            System.out.print("La frequenza è cambiata");
            
            Collection <Timer> t= timerService.getAllTimers();
            
            for(Timer ti:t){
                ti.cancel();
            }
            
            ScheduleExpression every5Seconds = new ScheduleExpression().minute("*/"+frequency).hour("*");
            timerService.createCalendarTimer(every5Seconds, new TimerConfig("", false));
        }
        
        
        System.out.println("Aggiornamento ogni "+frequency+" minuti");
        
        //starts All Timers
        this.startWeatherUpdates();
        this.startWeatherUpdatesEvents();
        this.sendNotifications();
    }
    
    /**
     * It updates periodically the weather forecast associated to the user
     * @throws org.json.JSONException
     * @throws java.text.ParseException
    */
    public void startWeatherUpdates() throws JSONException, ParseException{
   
        //Get all users list
        TypedQuery<User> query =(TypedQuery<User>) em.createNativeQuery("SELECT * FROM USERS", User.class);
        List<User> results = query.getResultList();   
            
        //Starts the for cycle in order to update each user with his/her weather forecast
        for (User u : results) {
            
            //Get Weather service and get current weather forecast
            WeatherForecast wf = new WeatherForecast(u.getLocation().getCity());

            //Get Weather associated to the specific user
            WForecast wFor = em.find(WForecast.class,u.getWeather().getId());

            try {
                //wFor object updated
                wFor.setWeather(wf.getWeather());
                wFor.setTemperature(wf.getTemperature());
                wFor.setHumidity(wf.getHumidity());
            } catch (JSONException ex) {
                Logger.getLogger(WeatherUpdate.class.getName()).log(Level.SEVERE, null, ex);
            }       

            //Dummy print 
            System.out.println("A "+u.getLocation().getCity()+" ci sono "+wFor.getTemperature()+" umidità: "+wFor.getHumidity()+" ed è "+ wFor.getWeather()+" tempo.");
            
            //Data made persistent
            if(wFor.getWeather()!="Not Available yet"){
                em.merge(wFor);
                em.flush();
            }
        }
      
    }
    
    /**
    * It starts the weather update
    */
    @Override
    public void run() {
        try {
            this.startWeatherUpdates();
            this.startWeatherUpdatesEvents();
        } catch (JSONException ex) {
            Logger.getLogger(WeatherUpdate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(WeatherUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    /**
     * It updates periodically the weather forecast associated to the events
     * 
     * @throws org.json.JSONException
     * @throws java.text.ParseException
    */
    public void startWeatherUpdatesEvents() throws JSONException, ParseException{
        //Now starts weather update for events
        TypedQuery<Event> query_e =(TypedQuery<Event>) em.createNativeQuery("SELECT * FROM EVENT WHERE isCanceled=0 AND isOutdoor=1 AND STARTSAT >='"+this.wFD(new Date())+"';", Event.class);
        List<Event> results_e = query_e.getResultList();  
        
        //Starts the for cycle in order to update each user with his/her weather forecast
        for (Event e : results_e) {           
            
            String latitude,longitude;
            latitude= this.getSubstring(e.getLocation().getCoordinates(), '(', ',');
            longitude=(this.getSubstring(e.getLocation().getCoordinates(), ',', ')')).trim();
                    
            WeatherForecastEvent wfe = new WeatherForecastEvent(latitude,longitude);      
            
            //Get weatherforecast fot the specific event 
            String weather_forecast=wfe.getWeather(e.getStartsAt());
            
            //Now we have to persist it or update it
            System.out.println("Per l'evento "+e.getName()+" Meteo: "+weather_forecast);
            
            WForecast wFor = em.find(WForecast.class,e.getWeatherForecast().getId());

            //Set the past weather
            this.pastWeather=e.getWeatherForecast().getWeather();
            
            //Set the new weather
            wFor.setWeather(weather_forecast);       
            
            //If weather is changed I send a notification to every invited pending or accepted
            if(!this.pastWeather.equals(weather_forecast) && !"Nan".equals(this.pastWeather) && !"Nan".equals(weather_forecast)){
                System.out.println("Le previsioni meteo sono cambiate,(Prima "+this.pastWeather+" ora "+weather_forecast+" mando notifiche"+this.pastWeather);            
                nF.sendWeatherChangedNotification(e);
            }else{
                System.out.println("Le previsioni meteo per evento "+e.getName()+" non sono cambiate (Prima "+this.pastWeather+" ora "+weather_forecast);
            }

            
            //Data made persistent
            em.merge(wFor);
            em.flush();
        }
    }
    
     /**
     * It checks if it's necessary to sent the email
     * 
    */
    public void sendNotifications() throws ParseException{
                
        //I retrieve all events that are not canceled and outdoor
        TypedQuery<Event> query =(TypedQuery<Event>) em.createNativeQuery("SELECT * FROM EVENT WHERE isCanceled=0 AND isOutdoor=1 AND STARTSAT >='"+this.wFD(new Date())+"';", Event.class);
        List<Event> results = query.getResultList();
        
        //This will contain the current value of meteo expected converted in integer
        int expTrigg=0;
        
        for (Event e : results) { 
            
            //expTrigg will be updated only if weather forecast are available
            if(!"Nan".equals(e.getWeatherForecast().getWeather())){
                switch(e.getWeatherForecast().getWeather()){
                    case "Clear":
                        expTrigg=0;
                        break;
                    case "Clouds":
                        expTrigg=1;
                        break;
                    case "Rain":
                        expTrigg=2;
                        break;
                    case "Mist":
                        expTrigg=4;
                        break;
                    case "Snow":
                        expTrigg=4;
                        break;
                    default:
                        expTrigg=4;
                        System.out.println();
                }
                
                //Check if event's owner would to be notified 
                if(e.getNotIfTrigger()<=expTrigg){
                    //System.out.println("L'utente dell'evento "+e.getName()+" voleva essere avvisato con meteo superiore a "+e.getNotIfTrigger()+" e il meteo previsto è : "+expTrigg);
                    
                    
                    //******** SE TRE GIORNI PRIMA E' ANCORA BRUTTO TEMPO AVVISO L' AMMINISTRATORE *********                     
                    if(cEB.getOnlyDate(e.getStartsAt()).getTime()-cEB.getOnlyDate(new Date()).getTime()<=this.twHours && send==false){
                        System.out.println("Mancano meno di 3 giorni all'inizio dell'evento, invio mail di notifica all'owner");
                        String stringToSend= "Dear "+e.getUser().getName()+", <br/> the expected weather forecast for the event "+e.getName()+" are not very good.<br/> MeteoCal informs that the weather in that day will be : "+e.getWeatherForecast().getWeather()+" <br/><br/> What would you like to do ? <br/><a href='http://localhost:8080/meteocal/faces/user/event.xhtml?id="+e.getId()+"'>Cancel the event</a><br/><a href='http://localhost:8080/meteocal/faces/user/weatherForecasts.xhtml?city="+e.getLocation().getCity()+"&id="+e.getId()+"'>Find the closest sunny day</a><br/><br/><br/> Best regards <br/> MeteoCal Team";
                        mail.setTo(e.getUser().getEmail());
                        mail.setFrom("meteocalpoli@gmail.com");
                        mail.setSubject("Bad Weather notification");
                        mail.sendMail(EmailTemplate.getTemplate(stringToSend));
                        send=true;
                    }
                    
                    //******** SE UN GIORNO PRIMA E' ANCORA BRUTTO TEMPO AVVISO GLI INVITATI *********                     
                    if(cEB.getOnlyDate(e.getStartsAt()).getTime()-cEB.getOnlyDate(new Date()).getTime()<=this.oneDay){
                        System.out.println("Mancano meno di 1 giorno all'inizio dell'evento, invio mail di notifica agli invitati");
                        
                        //Get the list of users with status ACCEPTED or PENDING
                        List <User> acceptedUsers = new ArrayList<>();
                        for( Invitation i: iF.getInvitations(e.getId().toString()) ) {
                            if(i.getStatus().equals("ACCEPTED") || i.getStatus().equals("PENDING"))
                                acceptedUsers.add(i.getInvitedUser());
                        }
                    
                       //Send Email to all those users
                        for(User u: acceptedUsers) {
                            System.out.println("Ho mandato una mail all'invitato");
                            String stringToSend= "Dear "+u.getName()+", <br/> the expected weather forecast for the event "+e.getName()+" are not very good.<br/> MeteoCal informs that the weather in that day will be : "+e.getWeatherForecast().getWeather()+" <br/><br/>Contact the event creator.<br/><br/> Best regards <br/> MeteoCal Team";
                            mail.setTo(u.getEmail());
                            mail.setFrom("meteocalpoli@gmail.com");
                            mail.setSubject("Bad Weather notification");
                            mail.sendMail(EmailTemplate.getTemplate(stringToSend));
                        }
                        
                    }
                    
                    
                    
                        
                }else{
                    System.out.println("L'evento non è a rischio");  
                }
            }
            
            
            
        }
    }
    
    private String wFD(Date d){
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
    }
    
    /**
     * It gets the substring between a and b character excluded
     * @param s string to be evaluated
     * @param a starting character
     * @param b ending character
     * @return the substring between a and b
     */
    private String getSubstring(String s,char a,char b){
        
        s = s.substring(s.indexOf(a) + 1,s.indexOf(b));
        return s;
    }
 

}
