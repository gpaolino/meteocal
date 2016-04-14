/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.test.meteocal;

import it.polimi.se2.meteocal.gui.CreationEventBean;
import java.text.ParseException;
import java.util.Date;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author alessandronegrini
 */
public class CreationEventBeanTest {
    
    private CreationEventBean cut;
    
    public CreationEventBeanTest(){
        
    }
    
    @Before
    public void setUp() {
        cut = new CreationEventBean();
        cut.setUm(mock(EntityManager.class));
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void dateFormattedCorrectly() throws ParseException {       
        assertEquals(cut.getOnlyDate(new Date("17/01/2015 18:00:00")).getTime(),1462053600000L);
        assertEquals(cut.getOnlyDate(new Date("17/01/2015 18:00:00")).getTime(),cut.getOnlyDate(new Date("17/01/2015 13:00:00")).getTime());
        assertNotEquals(cut.getOnlyDate(new Date("18/01/2015 18:00:00")).getTime(),cut.getOnlyDate(new Date("17/01/2015 18:00:00")).getTime());
    }
    
    @Test 
    public void dataConsistency() throws ParseException{
        
        Date from = new Date("27/06/2015 08:00");
        Date to = new Date("27/06/2015 18:00");
        assertEquals(cut.checkDate(from,to),true);
        
        from = new Date("27/06/2015 08:00");
        to = new Date("26/06/2015 18:00");
        assertEquals(cut.checkDate(from,to),false);
        
        from = new Date("27/06/2015 08:00");
        to = new Date("27/06/2015 08:00");
        assertEquals(cut.checkDate(from,to),false);
        
        from = new Date("27/01/1999 08:00");
        to = new Date("29/01/1999 08:00");
        assertEquals(cut.checkDate(from,to),false);
        
    }
    
}
