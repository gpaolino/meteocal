/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se2.meteocal.boundary;

import it.polimi.se2.meteocal.entity.Event;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import java.security.Principal;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EventManager {

    @PersistenceContext
    public EntityManager em;
    
    @Inject
    Principal principal;

    public void save(Location location,Event event,WForecast wf) {
       em.persist(event);
       em.persist(location);
       em.persist(wf);
    }

    public void unregister() {
        em.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    public void cancelEvent(String id){
        Query query = em.createNativeQuery("UPDATE EVENT SET ISCANCELED = '1' WHERE ID = ?1;");
        query.setParameter(1, id);
        query.executeUpdate();
        System.out.println("EVENTO ID ?1 CANCELLATO CORRETTAMENTE");
    }
    
    public void privacySettings(boolean pub,String id){
        Query query = em.createNativeQuery("UPDATE EVENT SET ISPUBLIC = '"+pub+"' WHERE ID ="+id+";");
        query.executeUpdate();
        System.out.println("EVENTO ID "+id+" AGGIORNATO CORRETTAMENTE");
    }
    
    public void oudoorSettings(boolean out,String id){
        Query query = em.createNativeQuery("UPDATE EVENT SET ISPUBLIC = '"+out+"' WHERE ID ="+id+";");
        query.executeUpdate();
        System.out.println("EVENTO ID "+id+" AGGIORNATO CORRETTAMENTE");
    }
    
    public List<Event> getHisEvent(User u){
        Query query = em.createNativeQuery("SELECT * FROM EVENT;");
        return query.getResultList();
    }
    
    
}
