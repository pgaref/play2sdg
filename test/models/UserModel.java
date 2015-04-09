package models;
import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import org.junit.Before;
import org.junit.Test;
import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;


public class UserModel{
	@Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }
   
    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "Bob", "secret").save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.name);
    }
    
    @Test
    public void tryAuthenticateUser() {
        new User("bob@gmail.com", "Bob", "secret").save();
        
        assertNotNull(User.authenticate("bob@gmail.com", "secret"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "secret"));
    }
    
//    @Test
//    public void fullTest() {
//        Ebean.save((List) Yaml.load("test-data.yml"));
//
//        // Count things
//        assertEquals(3, User.find.findRowCount());
//
//        // Try to authenticate as users
//        assertNotNull(User.authenticate("bob@example.com", "secret"));
//        assertNotNull(User.authenticate("jane@example.com", "secret"));
//        assertNull(User.authenticate("jeff@example.com", "badpassword"));
//        assertNull(User.authenticate("tom@example.com", "secret"));
//
//
//    }

}
