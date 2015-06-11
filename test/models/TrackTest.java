package models;

import static org.junit.Assert.assertEquals;

import controllers.Login;
import controllers.PlayListController;

public class TrackTest {

   
//    public static void setUp() {
//       org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
//       Object data =  yaml.load(new ByteArrayInputStream("test-data.yml".getBytes()));
//       //for(int i = 0 ; i < data.size(); i ++ ){
//    	   System.out.println("got data"+ data);
//       //}
//    }

	
    public void testData() {

    	Track s1 =
    			PlayListController.create("UUID123123","Wolves (Kill them with Colour Remix)", "Bon Hiver", "2014-11-11");
		
    	Track s2 = 
    			PlayListController.create("UUID0234234", "Contact Us (Live at ZDF Aufnahmezustand)", "Dillon", "1986-11-04");
        // Count things
        assertEquals("pgaref", Login.findUser("pgaref@example.com").username);
        //assertEquals(1, PlayList.findExisting("pgaref@example.com"));
        assertEquals(s1.artist, PlayListController.findByTitle(s1.getTitle()).getArtist());
        assertEquals(s2.artist, PlayListController.findByTitle(s2.getTitle()).artist);
    }
    public static void main(String[] args) {
    	TrackTest t = new TrackTest();
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
