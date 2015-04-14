package models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import play.libs.Yaml;
import base.AbstractDBApplicationTest;

import com.avaje.ebean.Ebean;

public class SongTest extends AbstractDBApplicationTest {

    @BeforeClass
    public static void setUp() {
        Ebean.save((List<?>) Yaml.load("test-data.yml"));
    }

    @Test
    public void testData() {

        // Count things
        assertEquals(3, User.find.findRowCount());
        assertEquals(7, Project.find.findRowCount());
        assertEquals(5, Song.find.findRowCount());
    }

    @Test
    public void findTodoTasksInvolving() {

        // Find all Bob's todo tasks
        List<Song> bobsTasks = Song.findTodoInvolving("bob@example.com");
        assertEquals(31, bobsTasks.size());
    }
}
