package models;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import base.AbstractDBApplicationTest;

public class ProjectTest extends AbstractDBApplicationTest {

	//TODO 
	
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void findProjectsInvolving() {
//        new User("bob@gmail.com", "Bob", "secret").save();
//        new User("jane@gmail.com", "Jane", "secret").save();
//
//        PlayList.create("Play 2", "play", "bob@gmail.com");
//        PlayList.create("Play 1", "play", "jane@gmail.com");
//
//        List<PlayList> results = PlayList.findInvolving("bob@gmail.com");
//        assertEquals(1, results.size());
//        assertEquals("Play 2", results.get(0).name);
    }
}
