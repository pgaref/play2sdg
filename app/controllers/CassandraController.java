package controllers;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import models.Counter;
import models.PlayList;
import models.Song;
import models.User;
import play.mvc.Controller;

/**
 * User Controller for Apache Cassandra back-end
 * @author pg1712
 *
 */

public class CassandraController extends Controller {
	
	static EntityManagerFactory emf;
	
	static Counter songCounter;
	static Counter userCounter;
	
	/**
	 * User - Cassandra JPA
	 * @param user
	 */
	static Logger  logger = Logger.getLogger("controllers.CassandraController");
	
	public static void persist(User user) {
		EntityManager em = getEmf().createEntityManager();
		User tmp = em.find(User.class, user.getEmail());
		if(tmp == null){
			em.persist(user);
			increment(userCounter, "user");
		}
		else
			em.merge(user);
		em.close();
		logger.debug("\n User: " + user.getEmail() + " record persisted using persistence unit -> cassandra_pu");
	}
	
	public static void remove(User user) {
		EntityManager em = getEmf().createEntityManager();
		em.remove(user);
		em.close();
		logger.debug("\n User: " + user.getEmail() + " record REMOVED using persistence unit -> cassandra_pu");
	}

	public static User findbyEmail(String email) {
		EntityManager em = getEmf().createEntityManager();
		User user = em.find(User.class, email);
		em.close();
		
		boolean userStr =  ( user == null ? false : true );
		logger.debug("\n Looking for User: " +email + " in cassandra database... found: "
				+ userStr);
		return user;
	}

	public static void updatePassword(String email, String newPass) {
		EntityManager em = getEmf().createEntityManager();
		User user = em.find(User.class, email);
		user.setPassword(newPass);
		em.merge(user);
		user = em.find(User.class, email);
		em.close();
		
		String userStr =  ( user == null ? "Record not found": user.toString() );
		logger.debug("Record updated: " + userStr);
	}

	public static void deletebyEmail(String email) {
		EntityManager em = getEmf().createEntityManager();
		User user = em.find(User.class, email);
		em.remove(user);
		decrement(userCounter, "users");
		em.close();
		
		String userStr =  ( user == null ? "Record not found": user.toString() );
		logger.debug("Record deleted: " + userStr);
	}

	public static List<User> listAllUsers() {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select p from User p", User.class);
		List<User> allUsers = findQuery.getResultList();
		em.close();
		
		logger.debug("\n-------- Listing All Users -------- ");
		for (User u : allUsers) {
			logger.debug(" Got User: " + u.username);
		}
		logger.debug("\n ---------------- ");
	/*	findQuery = em
				.createQuery("Select p.password from User p where p.email = pgaref@example.com");
		System.out.println(findQuery.getResultList().get(0) + " res size "+ findQuery.getResultList().size() );*/
		return allUsers;
	}
	
	public static int getUserID(String username){
		int count = 0;
		 List<User> tmp =  listAllUsers();
		 for(User  u : tmp ){
			 if(u.getEmail().equalsIgnoreCase(username))
				 return count;
			 count ++;
		 }
		 //not found - error case
		 return -1;
	}
	
	
	/**
	 * Song - Cassandra JPA
	 * @param Song
	 */
	
	public static void persist(Song song) {
		EntityManager em = getEmf().createEntityManager();
		Song tmp = em.find(Song.class, song.getTitle());
		if(tmp == null){
			em.persist(song);
			increment(songCounter, "songs");
			logger.debug("\n Song: " + song.getTitle() + " record persisted using persistence unit -> cassandra_pu");
		}
		else{
			em.merge(song);
			logger.debug("\n Song: " + song.getTitle() + " record merged using persistence unit -> cassandra_pu");
		}
		em.close();
		
	}
	
	public static void remove(Song song) {
		EntityManager em = getEmf().createEntityManager();
		em.remove(song);
		decrement(songCounter, "songs");
		em.close();
		logger.debug("\n Song: " + song.getTitle() + " record REMOVED using persistence unit -> cassandra_pu");
	}
	
	
	public static Song findSongbyTitle(String title) {
		EntityManager em = getEmf().createEntityManager();
		Song song = em.find(Song.class, title);
		em.close();
		
		String songStr =  ( song == null ? "Record not found": song.toString() );
		logger.debug("\n Looking for Song: " +title + " in cassandra database... got: "
				+ songStr);
		return song;
	}
	/*
	public static Song findbySongTitle(String title) {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from Song p where p.title = "+ title);
		
		
		if(findQuery.getResultList().size() ==0){
			logger.debug("Could not find any songs with title: "+ title);
			return null;	
		}
		
		return (Song) findQuery.getResultList().get(0);
	}*/

	
	public static List<Song> listAllSongs() {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select s from Song s", Song.class);
		List<Song> allSongs = findQuery.getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing All Songs, Total Size:" + allSongs.size() +" ############## \n ");
//		for (Song s : allSongs) {
//			logger.debug("\n Got Song: \n" + s);
//		}
		return allSongs;
	}
	
	/**
	 * GENERIC counter - Cassandra JPA
	 * @param Generic counter
	 * 
	 */
	
	public static void increment(Counter counter, String id) {
		EntityManager em = getEmf().createEntityManager();
		Counter tmp = em.find(Counter.class, id);
		if(tmp == null){
			songCounter = new Counter(id);
			songCounter.incrementCounter();
			em.persist(songCounter);
			logger.debug("Generic: " + songCounter.getId() + "Counter persisted using persistence unit -> cassandra_pu");
		}
		else{
			songCounter = tmp;
			songCounter.incrementCounter();
			em.merge(songCounter);
			logger.debug("Generic: " + songCounter.getId() + "Counter merged using persistence unit -> cassandra_pu");
		}
		em.close();
		
		
	}
	
	public static void decrement(Counter counter, String id) {
		EntityManager em = getEmf().createEntityManager();
		Counter tmp = em.find(Counter.class, id);
		if(tmp == null){
			songCounter = new Counter(id);
			songCounter.decrementCounter();
			em.persist(songCounter);
			logger.debug("Generic: " + songCounter.getId() + "Counter persisted using persistence unit -> cassandra_pu");
		}
		else{
			songCounter = tmp;
			songCounter.decrementCounter();
			em.merge(songCounter);
			logger.debug("Generic: " + songCounter.getId() + "Counter merged using persistence unit -> cassandra_pu");
		}
		em.close();

	}
	
	public static int getCounterValue(String id){
		EntityManager em = getEmf().createEntityManager();
		Counter tmp = em.find(Counter.class, id);
		if(tmp == null){
			logger.debug("\n Counter: "+ id +" NOT FOUND!!!");
			return 0;
		}
		else{
			return tmp.getCounter();
		}

	}
	
	/**
	 * Playlist - Cassandra JPA
	 * @param Playlist
	 * 
	 */
	public static void persist(PlayList p) {
		EntityManager em = getEmf().createEntityManager();
		PlayList tmp = em.find(PlayList.class, p.getId());
		if(tmp == null){
			em.persist(p);
			logger.debug("\n PlayList: " + p.getId() + " record persisted using persistence unit -> cassandra_pu");
		}
		else{
			em.merge(p);
			logger.debug("\n PlayList: " + p.getId() + " record merged using persistence unit -> cassandra_pu");
		}
		em.close();
		
	}
	
	public static void remove(PlayList p) {
		EntityManager em = getEmf().createEntityManager();
		em.remove(p);
		em.close();
		logger.debug("\n PlayList: " + p.getId() + " record REMOVED using persistence unit -> cassandra_pu");
	}
	
	public static PlayList getByID(UUID id){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select p from PlayList p where p.id = "+ id );
		List<PlayList> tmp = (List<PlayList>) findQuery.getResultList();
		if(tmp == null){
			logger.debug("\n PlayList: "+ id + " could not be found!!");
			return null;
		}
		return tmp.get(0);
	}
	
	public static List<PlayList> listAllPlaylists(){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select p from PlayList p", PlayList.class);
		List<PlayList> allPlaylists= findQuery.getResultList();
		em.close();
		return allPlaylists;
	}
	


	public static List<PlayList> getUserPlayLists(String usermail){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from PlayList p");
		List<PlayList> tmp =  (List<PlayList>) findQuery.getResultList();
		System.out.println("\n\n---->>>QUery returned: "+ tmp) ;
		return (List<PlayList>) findQuery.getResultList();
	}
	
	public static int getUserPlayListCount(String usermail){
		if (getUserPlayLists(usermail) == null )
			return 0;
		return getUserPlayLists(usermail).size();
	}
	
	/**
	 * Needs seriously to be checked
	 * @param p
	 * @param newname
	 */
	public static void playlistRename(PlayList p, String newname){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from PlayList p where p.id = "+ p.id);
		
		List<PlayList> tmp = (List<PlayList>) findQuery.getResultList();
		if(findQuery.getResultList().size() ==0){
			logger.debug("\n Could not find any songs with title: "+ p.getFolder());
			return;	
		}
		
		for(PlayList pp : tmp){
			if(pp.getId().equals(p.id)){
				pp.setFolder(newname);
				persist(pp);
			}
		}
	}
	
	
	private static EntityManagerFactory getEmf() {
		logger.setLevel(Level.DEBUG);
		
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("cassandra_pu");
			logger.debug("\n emf"+ emf.toString());
		}
		return emf;
	}	
}