/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.test.meteocal;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Group;
import it.polimi.se2.meteocal.entity.Location;
import it.polimi.se2.meteocal.entity.User;
import it.polimi.se2.meteocal.entity.WForecast;
import it.polimi.se2.meteocal.meteoInfo.WeatherUpdate;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import pl.itcrowd.arquillian.mock_contexts.FacesContextRequired;
import pl.itcrowd.arquillian.mock_contexts.MockFacesContextProducer;

/**
 * Integration Test for User Manager
 * @author paologuglielmino
 */
@RunWith(Arquillian.class)
public class UserManagerIT {
    
    @Inject
    UserManager cut;
    
    @PersistenceContext
    EntityManager em;
    
    FacesContext mock;

    @Deployment
    public static WebArchive createArchiveAndDeploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(User.class, UserManager.class, Location.class, WForecast.class, Group.class, WeatherUpdate.class)
                .addPackage(User.class.getPackage())
                .addPackage(UserManager.class.getPackage())
                .addPackage(WeatherUpdate.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Test
    public void UserManagerShouldBeInjected() {
        assertNotNull(cut);
    }
    
    @Test
    public void EntityManagerShouldBeinjected() {
        assertNotNull(em);
    }
    
    @FacesContextRequired
    @Test
    public void facesContextNr1Availabile1()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull(facesContext);
        Assert.assertSame(mock, facesContext);
        //Assert.assertEquals(attributes, facesContext.getAttributes());
    }

    @MockFacesContextProducer
    public FacesContext mockFacesContext()
    {
        if (mock == null) {
            mock = mock(FacesContext.class);
        }
        return mock;
    }
    
    @Test
    public void testSaveAndFetchUser() {
        //At the beginning the table USERS should be empty
        assertTrue(null==em.find(User.class, "gp.guglielminopaolo@me.com"));
        
        //I Create a new user in the database
        User newUser = new User();
        newUser.setName("Paolo");
        newUser.setEmail("gp.guglielminopaolo@me.com");
        newUser.setPassword("Password1");
        Location newLocation = new Location();
        newLocation.setCity("Talamona");
        newLocation.setCoordinates("(46.1368155, 9.61057690000007)");
        //The forecast is empty,, doesn't matter for the tests.
        WForecast newForecast = new WForecast();
        
        cut.save(newUser, newLocation, newForecast);
        
        //I test the save method (Save)
        //I retrieve the user from the database through the EntityManager and the primary key
        User foundUser = em.find(User.class, "gp.guglielminopaolo@me.com");
        //Found User must be the one which I have created before
        assertEquals(newUser, foundUser);
        
        //I test the find method (Fetch)
        foundUser = cut.find(newUser.getEmail());
        assertEquals(newUser, foundUser);
    }
    
    @Test
    public void testEditUser() {
        User newUser = new User();
        //email cannot be changed (see specifications)
        final String email = "giuseppe.verdi@meteocal.com";
        newUser.setName("Giuseppe Verdi");
        newUser.setEmail(email);
        newUser.setPassword("Topolino313");
        Location newLocation = new Location();
        newLocation.setCity("Milano");
        newLocation.setCoordinates("(45.4654219, 9.18592430000001)");
        //The forecast is empty,, doesn't matter for the tests.
        WForecast newForecast = new WForecast();
        //I create a user instance in the db
        cut.save(newUser, newLocation, newForecast);
        
        //I retrieve the user I just saved, it must be the same I just saved
        User foundUser = em.find(User.class, email);
        assertEquals(newUser, foundUser);
        
        //I edit some user information
        String newName = "Giuseppino";
        String newCity = "Catania";
        String newCoordinates = "(37.5078772, 15.083030399999984)";
        foundUser.setName(newName);
        foundUser.getLocation().setCity(newCity);
        foundUser.getLocation().setCoordinates(newCoordinates);
        try {
            //I call the method in charge of updating user's information
            cut.edit(foundUser);
            //I retrieve the modified user from the db
            foundUser = em.find(User.class, email);
            
            //I check that fields are changed, and are equals to the choosed new values
            assertNotEquals(foundUser.getName(), newUser.getName());
            assertEquals(newName, foundUser.getName());
            assertNotEquals(foundUser.getLocation().getCity(), newUser.getLocation().getCity());
            assertEquals(newCity, foundUser.getLocation().getCity());
            assertNotEquals(foundUser.getLocation().getCoordinates(), newUser.getLocation().getCoordinates());
            assertEquals(newCoordinates, foundUser.getLocation().getCoordinates());
            
        } catch (JSONException | ParseException ex) {
            Logger.getLogger(UserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void passwordsShouldBeEncryptedOnDB() {
        //I Create a new user in the database
        User newUser = new User();
        newUser.setName("Mario Rossi");
        String email = "mario.rossi@meteocal.com";
        newUser.setEmail(email);
        String clearPassword = "Password1";
        newUser.setPassword(clearPassword);
        Location newLocation = new Location();
        newLocation.setCity("Talamona");
        newLocation.setCoordinates("(46.1368155, 9.61057690000007)");
        //The forecast is empty,, doesn't matter for the tests.
        WForecast newForecast = new WForecast();
        
        cut.save(newUser, newLocation, newForecast);
        
        User foundUser = em.find(User.class, email);
        //I found a user corresponding to that email
        assertNotNull(foundUser);

        //The password found should not be a simple string
        assertNotEquals(foundUser.getPassword(), clearPassword);
        
        try {
            //The password found should be an encrypted string
            String encryptedPassword = (new BigInteger(1,MessageDigest.getInstance("SHA-256").digest(clearPassword.getBytes("UTF-8")))).toString(16);
            assertEquals(encryptedPassword, foundUser.getPassword());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(UserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Test
    public void defaultAvatarShouldExist() {
        String defaultAvatar = cut.defaultAvatar();
        assertNotNull(defaultAvatar);
    }
    
}
