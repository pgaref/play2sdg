package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import base.AbstractDBApplicationTest;

public class UserTest {

    @Before
    public void setUp() {
        
    	//start(fakeApplication());
    	UserTest.class.getClassLoader().getResource("META-INF/persistence.xml");
    }

    @Test
    public void createAndRetrieveZenUser() {
    	User.create("pangaref@gmail.com", "pgaref", "secret");
    	
        User pg = User.findUser("pangaref@gmail.com");
        assertNotNull(pg);
        assertEquals("pgaref", pg.getUsername());
    }

    @Test
    public void tryAuthenticateZenUser() {
    	User.create("pangaref@gmail.com", "pgaref", "secret");

        assertNotNull(User.authenticate("pangaref@gmail.com", "secret"));
        assertNull(User.authenticate("pangaref@gmail.com", "badpassword"));
        assertNull(User.authenticate("badname@gmail.com", "secret"));
    }
}