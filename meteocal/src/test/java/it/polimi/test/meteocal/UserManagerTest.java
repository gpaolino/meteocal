/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.test.meteocal;

import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Group;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import static org.hamcrest.CoreMatchers.is;
import org.json.JSONException;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
/**
 *
 * @author alessandronegrini
 */
public class UserManagerTest {
    
    private UserManager cut;
    
    public UserManagerTest(){
        
    }
    
    @Before
    public void setUp() {
        cut = new UserManager();
        cut.em = mock(EntityManager.class);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void newUsersShouldBelongToUserGroupAndSavedOnce() {
        User newUser = new User();        
        Location newLocation= new Location();
        WForecast newForecast = new WForecast();
        
        cut.save(newUser,newLocation,newForecast);
        
        //I check that the groupName of the newUser is USER
        assertThat(newUser.getGroupName(), is(Group.USERS));
        //I check that the number of registered Users is 1
        verify(cut.em,times(1)).persist(newUser);
        //I check that the number of Location is 1
        verify(cut.em,times(1)).persist(newLocation);
        //I check that the number of WeatherForecast is 1
        verify(cut.em,times(1)).persist(newForecast);
    }
    
    @Test
    public void editUserBehavior() {
        User newUser = new User();        
        Location newLocation= new Location();
        WForecast newForecast = new WForecast();
        
        cut.save(newUser,newLocation,newForecast);
        
        //I edit the User name
        newUser.setName("User xxx");
        try {
            cut.edit(newUser);
        } catch (JSONException | ParseException ex) {
            Logger.getLogger(UserManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //I check that the merge operation is excecuted once
        verify(cut.em,times(1)).merge(newUser);
        //No users should be removed in the operation
        verify(cut.em,times(0)).remove(newUser);
    }
    
    @Test
    public void deleteUserBehavior() {
        User newUser = new User();
        newUser.setEmail("a@a.com");
        newUser.setPassword("Password1");
        Location newLocation= new Location();
        WForecast newForecast = new WForecast();
        
        cut.save(newUser,newLocation,newForecast);
        
        //Need to call the remove instead unregister() because I can't access to the logged user.
        cut.em.remove(newUser);
        
        //I check that the remove operation is executed once
        verify(cut.em,times(1)).remove(newUser);
    }
   
}
