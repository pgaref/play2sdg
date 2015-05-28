package controllers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
	
	/**
	 * User - Cassandra JPA
	 * @param user
	 */

	public static void persist(User user) {
		EntityManager em = getEmf().createEntityManager();
		em.persist(user);
		em.close();
		System.out.println("User: " + user.getEmail() + " record persisted using persistence unit -> cassandra_pu");
	}

	public static User findbyEmail(String email) {
		EntityManager em = getEmf().createEntityManager();
		User user = em.find(User.class, email);
		em.close();
		
		String userStr =  ( user == null ? "Record not found": user.toString() );
		System.out.println("Looking for User: " +email + " in cassandra database... got: "
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
		System.out.println("Record updated: " + userStr);
	}

	public static void deletebyEmail(String email) {
		EntityManager em = getEmf().createEntityManager();
		User user = em.find(User.class, email);

		em.remove(user);
		user = em.find(User.class, email);
		em.close();
		
		String userStr =  ( user == null ? "Record not found": user.toString() );
		System.out.println("Record deleted: " + userStr);
	}

	public static List<User> listAllUsers() {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select p from User p", User.class);
		List<User> allUsers = findQuery.getResultList();
		em.close();
		
		System.out.println("-------- Listing All Users -------- ");
		for (User u : allUsers) {
			System.out.println(" Got User: " + u);
		}

	/*	findQuery = em
				.createQuery("Select p.password from User p where p.email = pgaref@example.com");
		System.out.println(findQuery.getResultList().get(0) + " res size "+ findQuery.getResultList().size() );*/
		return allUsers;
	}
	
	/**
	 * Check if user column family exists and if not create it
	 * EMF does that for me!!!
	
	private static int createKeyspace(){
		System.out.println("Init.....");
		EntityManager em = getEmf().createEntityManager();
		Query keyspaceQuery = em.createQuery("CREATE COLUMN FAMILY users with comparator=UTF8Type and default_validation_class=UTF8Type and key_validation_class=UTF8Type");
		System.out.println(keyspaceQuery.toString());
		return 1;
	}
	*/
	
	/**
	 * Song - Cassandra JPA
	 * @param Song
	 */
	
	public static void persist(Song song) {
		EntityManager em = getEmf().createEntityManager();
		em.persist(song);
		em.close();
		System.out.println("Song: " + song.getId() + " record persisted using persistence unit -> cassandra_pu");
	}
	
	
	public static Song findbySongID(String id) {
		EntityManager em = getEmf().createEntityManager();
		Song song = em.find(Song.class, id);
		em.close();
		
		String songStr =  ( song == null ? "Record not found": song.toString() );
		System.out.println("Looking for Song: " +id + " in cassandra database... got: "
				+ songStr);
		return song;
	}
	
	public static Song findbySongTitle(String title) {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from Song p where p.title = "+ title);
		
		
		if(findQuery.getResultList().size() ==0){
			System.out.println("Could not find any songs with title: "+ title);
			return null;	
		}
		
		return (Song) findQuery.getResultList().get(0);
	}

	
	public static List<Song> listAllSongs() {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select s from Song s", Song.class);
		List<Song> allSongs = findQuery.getResultList();
		em.close();
		
		System.out.println("-------- Listing All Songs -------- ");
		for (Song s : allSongs) {
			System.out.println(" Got Song: " + s);
		}
		return allSongs;
	}
	
	/**
	 * Playlist - Cassandra JPA
	 * @param Playlist
	 * 
	 */
	public static void persist(PlayList p) {
		EntityManager em = getEmf().createEntityManager();
		em.persist(p);
		em.close();
		System.out.println("PlayList: " + p.getId() + " record persisted using persistence unit -> cassandra_pu");
	}
	
	
	public static int getUserPlayListCount(String usermail){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from PlayList p where p.usermail = "+ usermail);
		
		List<PlayList> tmp = (List<PlayList>) findQuery.getResultList();
		
//		for(PlayList p : tmp){
//			System.out.println("Got from retreived playlist: " +p.folder);
//			System.out.println("song list count "+ p.songs.size());
//			for(Song s: p.songs){
//				System.out.println("Song: "+s);
//			}
//		}
		
		return tmp.size();
	}
	
	/**
	 * Needs seriously to be checked
	 * @param p
	 * @param newname
	 */
	public static void playlistRename(PlayList p, String newname){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from Playlists p where p.id = "+ p.id);
		
		List<PlayList> tmp = (List<PlayList>) findQuery.getResultList();
		if(findQuery.getResultList().size() ==0){
			System.out.println("Could not find any songs with title: "+ p.getFolder());
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
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("cassandra_pu");
			System.out.println("emf"+ emf.toString());
		}
		return emf;
	}

	
}