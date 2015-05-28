package controllers;

import models.PlayList;
import models.Song;
import models.User;



/**
 * Testing Kundera JPA functionality
 * @author pg1712
 *
 */
public class CassandraJPA
{
    public static void testCassandraUser(){
    	User user = new User( "pgaref@example.com", "pgaref" ,"secret", "Panagiotis", "Garefalakis");
        
        controllers.CassandraController.persist(user);
        System.out.println("Insert User Query finished");
        
        controllers.CassandraController.findbyEmail("pgaeref@example.com");
        controllers.CassandraController.findbyEmail("pgaref@example.com");
        System.out.println("Find User Query finished");
        
        controllers.CassandraController.listAllUsers();
        System.out.println("List all Users Query finished");
    }
    
    
	public static void testCassandraSong(){
		Song s = new Song("Contact Us (Live at ZDF Aufnahmezustand)", "Dillon", "1986-11-04", "https://www.youtube.com/watch?v=E6WqTL2Up3Y");
		controllers.CassandraController.persist(s);
		
		Song t = new Song("Wolves (Kill them with Colour Remix)", "Bon Hiver", "2014-11-11 ", "http://www.youtube.com/watch?v=5GXAL5mzmyw");
		controllers.CassandraController.persist(t);
		
		System.out.println("Insert Song Query finished");
		
		Song tmp = controllers.CassandraController.findbySongTitle("Contact Us (Live at ZDF Aufnahmezustand)");
		System.out.println("Find song by Title result : "+ tmp);
		
		controllers.CassandraController.listAllSongs();
		System.out.println("List all Songs Query finished");
	}
	
    public static void testCassandraPlayList(){
    	PlayList pl  =  new PlayList("pgaref@example.com", "testFolder");
    	Song tmp = controllers.CassandraController.findbySongTitle("Contact Us (Live at ZDF Aufnahmezustand)");
    	pl.addRatingSong(tmp);
    	
    	System.out.println("\n ############## User PlayList count  BEFORE: "+ controllers.CassandraController.getUserPlayListCount("pgaref@example.com"));

    	System.out.println("PlayList: "+ pl);
    	controllers.CassandraController.persist(pl);
    	controllers.CassandraController.remove(pl);
    	
    	System.out.println("\n ############## User PlayList count  AFTER: "+ controllers.CassandraController.getUserPlayListCount("pgaref@example.com"));
    	
    }
	
	public static void main(String[] args){
		
		testCassandraUser();
		testCassandraSong();
		testCassandraPlayList();
		
    }
    
    
}