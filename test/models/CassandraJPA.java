package models;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import controllers.CassandraController;


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
    
    public static void testCassandraRating(){
    	
    }
    
	public static void testCassandraSong(){
		Song s = new Song("Contact Us (Live at ZDF Aufnahmezustand)", "Dillon", "1986-11-04", "https://www.youtube.com/watch?v=E6WqTL2Up3Y");
		controllers.CassandraController.persist(s);
		System.out.println("Insert Song Query finished");
		
		Song tmp = controllers.CassandraController.findbySongTitle("Contact Us (Live at ZDF Aufnahmezustand)");
		System.out.println("Find song by Title result : "+ tmp);
		
		controllers.CassandraController.listAllSongs();
		System.out.println("List all Songs Query finished");
	}
	
	public static void main(String[] args){
		
		testCassandraUser();
		testCassandraRating();
		testCassandraSong();
    }
    
    
}