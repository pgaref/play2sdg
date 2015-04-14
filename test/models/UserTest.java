package models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import org.junit.Before;
import org.junit.Test;

import base.AbstractDBApplicationTest;

public class UserTest extends AbstractDBApplicationTest {

    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveZenUser() {
        new User("pangaref@gmail.com", "Panagiotis", "secret").save();
        User pg = User.find.where().eq("email", "pangaref@gmail.com").findUnique();
        assertNotNull(pg);
        assertEquals("Panagiotis", pg.name);
    }

    @Test
    public void tryAuthenticateZenUser() {
        new User("pangaref@gmail.com", "Panagiotis", "secret").save();

        assertNotNull(User.authenticate("pangaref@gmail.com", "secret"));
        assertNull(User.authenticate("pangaref@gmail.com", "badpassword"));
        assertNull(User.authenticate("pangaref1@gmail.com", "secret"));
    }
}