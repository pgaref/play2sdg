package models;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class SongTest {

   
//    public static void setUp() {
//       org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
//       Object data =  yaml.load(new ByteArrayInputStream("test-data.yml".getBytes()));
//       //for(int i = 0 ; i < data.size(); i ++ ){
//    	   System.out.println("got data"+ data);
//       //}
//    }

	
    public void testData() {

		Song s1 =
				Song.create("Wolves (Kill them with Colour Remix)", "Bon Hiver", "2014-11-11", "http://www.youtube.com/watch?v=5GXAL5mzmyw");
		
		Song s2 = 
				Song.create("Contact Us (Live at ZDF Aufnahmezustand)", "Dillon", "1986-11-04", "https://www.youtube.com/watch?v=E6WqTL2Up3Y");
        // Count things
        assertEquals("pgaref", User.findUser("pgaref@example.com").username);
        //assertEquals(1, PlayList.findExisting("pgaref@example.com"));
        assertEquals(s1.artist, Song.findByTitle(s1.getTitle()).getArtist());
        assertEquals(s2.artist, Song.findByTitle(s2.getTitle()).artist);
    }
    public static void main(String[] args) {
    	SongTest t = new SongTest();
    	t.testData();
	}

//    @Test
//    public void findTodoTasksInvolving() {
//
//        // Find all pgaref todo tasks
//        List<Song> pgsongs = Song.findAllSongs("bob@example.com");
//        assertEquals(31, bobsTasks.size());
//    }
}
