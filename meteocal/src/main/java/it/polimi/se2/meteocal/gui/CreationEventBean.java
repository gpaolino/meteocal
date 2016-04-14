/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import it.polimi.se2.meteocal.boundary.EventManager;
import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import it.polimi.se2.meteocal.meteoInfo.WeatherUpdate;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONException;

/**
 *
 * @author alessandronegrini
 */
@ManagedBean(name="creationEventBean")
@RequestScoped
public class CreationEventBean{

    @EJB
    private EventManager em;
    private User user;
    private Location location;
    private Event event;
    private int meteoTrigger;
    private int frequence;
    //non servono più giu
    private String fromD;
    private String toD;
    private String fromH;
    private String toH;
    
    private Date fromRep;
    private Date toRep;
    
    private String repeatEvent="false";
    
    @PersistenceContext
    private EntityManager um;

    public EntityManager getUm() {
        return um;
    }

    public void setUm(EntityManager um) {
        this.um = um;
    }
    
    private final long DAY=1000 * 60 * 60 * 24;
    private final long WEEK=1000 * 60 * 60 * 24 * 7;

    @EJB
    private WeatherUpdate wu;
    
    private FacesContext context = FacesContext.getCurrentInstance();

    
    public String getRepeatEvent() {
        return repeatEvent;
    }

    public void setRepeatEvent(String repeatEvent) {
        this.repeatEvent = repeatEvent;
    }
    
    public String getFromD() {
        return fromD;
    }

    public void setFromD(String fromD) {
        this.fromD = fromD;
    }

    public String getToD() {
        return toD;
    }

    public void setToD(String toD) {
        this.toD = toD;
    }

    public String getFromH() {
        return fromH;
    }

    public void setFromH(String fromH) {
        this.fromH = fromH;
    }

    public String getToH() {
        return toH;
    }

    public Date getFromRep() {
        return fromRep;
    }

    public void setFromRep(Date fromRep) {
        this.fromRep = fromRep;
    }

    public Date getToRep() {
        return toRep;
    }

    public void setToRep(Date toRep) {
        this.toRep = toRep;
    }
    
    public void setToH(String toH) {
        this.toH = toH;
    }
    
    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public int getMeteoTrigger() {
        return meteoTrigger;
    }

    public void setMeteoTrigger(int meteoTrigger) {
        this.meteoTrigger = meteoTrigger;
    }
    
    public Event getEvent() {
        if (event == null) {
            event = new Event();
            event.setIsOutdoor(Boolean.FALSE);
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventManager getEm() {
        return em;
    }

    public void setUm(EventManager um) {
        this.em = um;
    }

    public Location getLocation() {
            if (location == null) {
            location = new Location();
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    public CreationEventBean() {
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean checkDate(){
        if(this.event.getEndsAt().getTime()<=this.event.getStartsAt().getTime()){
            return true;
        }
        return false;
    }
    
    public void create() throws SQLException, ParseException, IOException, JSONException {

        event.setIsCanceled(false);
        
        if(event.getIsOutdoor()){
            event.setNotIfTrigger(this.meteoTrigger);
        }else{
            //if it is indoor it mustn't be notified. So putting the trigger 5 the user won't be notified
            event.setNotIfTrigger(5);
        }

        //if repeat button is set
        if("true".equals(this.repeatEvent)){
            
            System.out.println("Frequence of repeat: "+this.frequence);

            //How many milliseconds does it last
            long diff=this.getOnlyDate(this.toRep).getTime()-this.getOnlyDate(this.fromRep).getTime();
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println("Orario inizio Millisecondi: "+sdf.parse(sdf.format(this.fromRep.getTime())).getTime());
            System.out.println("Orario fine Millisecondi: "+sdf.parse(sdf.format(this.toRep.getTime())).getTime());
            
            long diff_hours=sdf.parse(sdf.format(this.toRep.getTime())).getTime()-sdf.parse(sdf.format(this.fromRep.getTime())).getTime();
            
            System.out.println("Differenza di millisecondi: "+diff+"ms."+ " Differenza di ore: "+(diff_hours/(1000*60*60)));
            
            //if the frequence is daily
            if(this.frequence==1){
                //Daily Repeat
                int days = (int)(diff / this.DAY)+1;
                boolean err=false;

                System.out.print("L'evento dura "+days+" giorni");
                
                for(int i=0;i<days;i++){
                    long startMS=this.fromRep.getTime()+i*this.DAY;
                    long stopMS=startMS+diff_hours;
                    Date start=new Date(startMS);
                    Date stop=new Date(stopMS);
                    System.out.println("Why: "+start+" "+stop);
                    if(!this.checkDate(start, stop)){
                        System.out.println("EVENT NOT CREATED CORRECTLY: DATE INCONSISTENCY");
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "Please check if: - 'From' field is bigger than 'To' field - Date fields must be bigger than the current date - In case of repeated event, the ending hour must be bigger than the starting one")); 
                        err=true;
                    }else{
                        if(this.checkConflicts(start, stop)){
                            System.out.println("EVENT NOT CREATED CORRECTLY: EVENT CONFLICT");
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "There's a data conflict in the dates chosen. Check available timetables in the calendar. ")); 
                            err=true;
                        }
                    } 
                }
                
                for(int i=0;i<days;i++){
                    
                    long startMS=this.fromRep.getTime()+i*DAY;
                    long stopMS=startMS+diff_hours;
                    Date start=new Date(startMS);
                    Date stop=new Date(stopMS);
                    
                    if(i==0){
                        System.out.println("Day "+i+" start "+start+" stop "+stop);
                        event.setStartsAt(start);
                        event.setEndsAt(stop);
                        event.setIsCanceled(false);
                        event.setStartsAt(start);
                        event.setEndsAt(stop);
                        event.setUser(em.getLoggedUser());
                        event.setLocation(location);
                        WForecast wf=new WForecast();
                        wf.setTemperature(0);
                        wf.setHumidity(0);
                        wf.setWeather("Nan");
                        event.setWeatherForecast(wf);
                        
                        System.out.println("Problemi di incostenza date ?"+this.checkDate(this.fromRep,this.toRep));
                        if(!err){
                            em.save(location, event,wf);
                            wu.startWeatherUpdatesEvents();
                        }
                    }else{
                        System.out.println("Day "+i+" start "+start+" stop "+stop);
                        if(!err){
                            Event e =new Event();
                            Location l=new Location();
                            l.setCity(location.getCity());
                            l.setCoordinates(location.getCoordinates());
                            l.setExtraInfo(location.getExtraInfo());
                            l.setNumber(location.getNumber());
                            l.setStreet(location.getStreet());
                            e.setName(event.getName());
                            e.setDescription(event.getDescription());
                            e.setFacebookLink(event.getFacebookLink());
                            e.setIsCanceled(false);
                            e.setIsOutdoor(event.getIsOutdoor());
                            e.setUser(em.getLoggedUser());
                            e.setLocation(l);
                            e.setIsPublic(event.getIsPublic());
                            e.setNotIfTrigger(event.getNotIfTrigger());
                            e.setStartsAt(start);
                            e.setEndsAt(stop);
                            WForecast wf=new WForecast();
                            wf.setTemperature(0);
                            wf.setHumidity(0);
                            wf.setWeather("Nan");
                            e.setWeatherForecast(wf);
                            System.out.println("Problemi di incostenza date ?"+this.checkDate(this.fromRep,this.toRep));

                            em.save(l, e,wf);
                            wu.startWeatherUpdatesEvents();
                        }
                    }
                }
                if(!err){
                    ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
                    context_e.redirect(context_e.getRequestContextPath() + "/faces/user/home.xhtml");
                }
            }

            
            if(this.frequence==7){
                
               //Week Repeat
                int weeks = (int)(diff / this.WEEK)+1;
                boolean errW=false;
                
                System.out.print("L'evento dura "+weeks+" settimane");
                
                for(int i=0;i<weeks;i++){
                    long startMS=this.fromRep.getTime()+i*this.WEEK;
                    long stopMS=startMS+diff_hours;
                    Date start=new Date(startMS);
                    Date stop=new Date(stopMS);
                    
                    if(!this.checkDate(start, stop)){
                        System.out.println("EVENT NOT CREATED CORRECTLY: DATE INCONSISTENCY");
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "Please check if: - 'From' field is bigger than 'To' field  - Date fields must be bigger than the current date - In case of repeated event, the ending hour must be bigger than the starting one ")); 
                        errW=true;
                    }else{

                        if(this.checkConflicts(start, stop)){
                            System.out.println("EVENT NOT CREATED CORRECTLY: EVENT CONFLICT");
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "There's a data conflict in the dates chosen. Check available timetables in the calendar. ")); 
                            errW=true;
                        }
                    } 
                }
                
                for(int i=0;i<weeks;i++){
                    
                    long startMS=this.fromRep.getTime()+i*this.WEEK;
                    long stopMS=startMS+diff_hours;
                    Date start=new Date(startMS);
                    Date stop=new Date(stopMS);
                    
                    if(i==0){
                        System.out.println("Week "+i+" start "+start+" stop "+stop);
                        event.setStartsAt(start);
                        event.setEndsAt(stop);
                        event.setIsCanceled(false);
                        event.setStartsAt(start);
                        event.setEndsAt(stop);
                        event.setUser(em.getLoggedUser());
                        event.setLocation(location);
                        WForecast wf=new WForecast();
                        wf.setTemperature(0);
                        wf.setHumidity(0);
                        wf.setWeather("Nan");
                        event.setWeatherForecast(wf);
                        
                        System.out.println("Problemi di incostenza date ?"+this.checkDate(this.fromRep,this.toRep));
                        if(!errW){
                            em.save(location, event,wf);
                            wu.startWeatherUpdatesEvents();
                        }
                    }else{
                        System.out.println("Week "+i+" start "+start+" stop "+stop);
                        if(!errW){
                            Event e =new Event();
                            Location l=new Location();
                            l.setCity(location.getCity());
                            l.setCoordinates(location.getCoordinates());
                            l.setExtraInfo(location.getExtraInfo());
                            l.setNumber(location.getNumber());
                            l.setStreet(location.getStreet());
                            e.setName(event.getName());
                            e.setDescription(event.getDescription());
                            e.setFacebookLink(event.getFacebookLink());
                            e.setIsCanceled(false);
                            e.setIsOutdoor(event.getIsOutdoor());
                            e.setUser(em.getLoggedUser());
                            e.setLocation(l);
                            e.setIsPublic(event.getIsPublic());
                            e.setNotIfTrigger(event.getNotIfTrigger());
                            e.setStartsAt(start);
                            e.setEndsAt(stop);
                            WForecast wf=new WForecast();
                            wf.setTemperature(0);
                            wf.setHumidity(0);
                            wf.setWeather("Nan");
                            e.setWeatherForecast(wf);
                            System.out.println("Problemi di incostenza date ?"+this.checkDate(this.fromRep,this.toRep));

                            em.save(l, e,wf);
                            wu.startWeatherUpdatesEvents();
                        }
                    }
                }
                if(!errW){
                    ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
                    context_e.redirect(context_e.getRequestContextPath() + "/faces/user/home.xhtml");
                }
            }
        }
        else{
            
            event.setStartsAt(this.fromRep);
            event.setEndsAt(this.toRep);
            event.setUser(em.getLoggedUser());
            event.setLocation(location);
            WForecast wf=new WForecast();
            wf.setTemperature(0);
            wf.setHumidity(0);
            wf.setWeather("Nan");
            event.setWeatherForecast(wf);
            
            System.out.println("Problemi di incostenza date ?"+this.checkDate(this.fromRep,this.toRep));
            System.out.println("Problemi di conflitto date ? "+this.checkConflicts(this.fromRep, this.toRep));
            
            if(!this.checkDate(fromRep, toRep)){
                System.out.println("EVENT NOT CREATED CORRECTLY: DATE INCONSISTENCY");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "Please check if: - 'From' field is bigger than 'To' field - Date fields must be bigger than the current date - In case of repeated event, the ending hour must be bigger than the starting one")); 
            }else{
                
                if(this.checkConflicts(fromRep, toRep)){
                    System.out.println("EVENT NOT CREATED CORRECTLY: EVENT CONFLICT");
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Date Consistency issues", "There's a data conflict in the dates chosen. Check available timetables in the calendar.")); 
                }else{

                    System.out.println("EVENT CREATED CORRECTLY");
                    em.save(location, event,wf);
                    wu.startWeatherUpdatesEvents();
                    ExternalContext context_e = FacesContext.getCurrentInstance().getExternalContext();
                    context_e.redirect(context_e.getRequestContextPath() + "/faces/user/home.xhtml");
                }
            }   
        }

    }

    public boolean checkConflicts(Date from,Date to) throws ParseException{
        System.out.print("SELECT * FROM EVENT WHERE isCanceled=0 AND owner='"+em.getLoggedUser().getEmail()+"' AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))"
                + "UNION((SELECT EVENT.*\n" +
        "FROM EVENT, INVITATION\n" +
        "WHERE TOEVENT_ID = EVENT.ID \n" +
        "AND STATUS=\"ACCEPTED\"\n" +
        "AND ISCANCELED=0\n" +
        "AND INVITEDUSER_EMAIL = '"+em.getLoggedUser().getEmail()+"'\n" +
        "AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))));");

        Query q = um.createNativeQuery(
                "SELECT * FROM EVENT WHERE isCanceled=0 AND owner='"+em.getLoggedUser().getEmail()+"' AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))"
                + "UNION((SELECT EVENT.*\n" +
        "FROM EVENT, INVITATION\n" +
        "WHERE TOEVENT_ID = EVENT.ID \n" +
        "AND STATUS=\"ACCEPTED\"\n" +
        "AND ISCANCELED=0\n" +
        "AND INVITEDUSER_EMAIL = '"+em.getLoggedUser().getEmail()+"'\n" +
        "AND ((endsAt > '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND startsAt < '"+this.wFD(to)+"') OR (startsAt <= '"+this.wFD(from)+"' AND endsAt >= '"+this.wFD(to)+"') OR (startsAt >= '"+this.wFD(from)+"' AND endsAt <= '"+this.wFD(to)+"'))));"
        );
        List<Event> results = q.getResultList();
        
        return !results.isEmpty();
                    
    }
    
    private String wFD(Date d){
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
    }
    
    public boolean checkDate(Date from, Date to) throws ParseException{
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        //Check if the current date is 
        Date now=new Date();
        if(from.getTime()< now.getTime())
            return false;
        
        //I check if the 'to' date is bigger that the 'from' one
        if(from.getTime()>=to.getTime())
            return false;
        
        //In case of repeated event I check that the 'from' hour is bigger than the 'to'  
        if("true".equals(this.repeatEvent))
            if(sdf.parse(sdf.format(from.getTime())).getTime()>=sdf.parse(sdf.format(to.getTime())).getTime())
                return false;
        
        return true;
    }
    
    public Date getOnlyDate(Date d) throws ParseException{
       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");      
       Date timeWithoutDate = sdf.parse(sdf.format(d));
       return timeWithoutDate; 
    }
    
}

