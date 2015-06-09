package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import models.Counter;
import models.PlayList;
import models.Track;
import models.User;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import play.mvc.Controller;

import com.impetus.client.cassandra.common.CassandraConstants;

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
		Query q = em.createNativeQuery("SELECT * FROM \"users\" WHERE \"key\"='"+email+"';", User.class);
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<User> userl = q.getResultList();
		//User user = em.find(User.class, email);
		em.close();
		
		boolean userStr =  ( userl == null ? false : true );
		logger.debug("\n Looking for User: " +email + " in cassandra database... found: "
				+ userStr);
		return userl.get(0);
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
		@SuppressWarnings("unchecked")
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
	 * Track - Cassandra JPA
	 * @param Track
	 */
	
	public static void persist(Track song) {
		EntityManager em = getEmf().createEntityManager();
		/*
		 * TODO CHANGE QUERY!!!!!!!!!!!
		 */
		Track tmp = em.find(Track.class, song.getTrack_id());
		if(tmp == null){
			em.persist(song);
			increment(songCounter, "tracks");
			logger.debug("\n Track: " + song.getTitle() + " record persisted using persistence unit -> " + getEmf().getProperties());
		}
		else{
			em.merge(song);
			logger.debug("\n Track: " + song.getTitle() + " record merged using persistence unit ->" +getEmf().getProperties());
		}
		em.close();
		
	}
	
	public static void remove(Track song) {
		EntityManager em = getEmf().createEntityManager();
		em.remove(song);
		decrement(songCounter, "tracks");
		em.close();
		logger.debug("\n Track: " + song.getTitle() + " record REMOVED using persistence unit ->" +getEmf().getProperties());
	}
	
	public static Track findByTrackID(String id){
		EntityManager em = getEmf().createEntityManager();
		//Track t = em.find(Track.class, id);
		Query q = em.createNativeQuery("SELECT * FROM \"tracks\" WHERE \"key\"='"+id+"';", Track.class);
		q.setMaxResults(1);
		List<Track> t = q.getResultList();
		if(t == null)
			logger.debug("Tack "+ id +" not found in the database!");
		return t.get(0);
		
	}
	public static Track findTrackbyTitle(String title) {
		
		/*
		 * Use NativeQuery to avoid Strings being used as cassandra keywords!
		 * https://github.com/impetus-opensource/Kundera/issues/151
		 * Incompatible with CQL2 !!
		 */
		Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
        EntityManagerFactory tmp = Persistence.createEntityManagerFactory("cassandra_pu", propertyMap);
        EntityManager em  = tmp.createEntityManager();
        
		Query findQuery = em
				.createNativeQuery("SELECT * FROM tracks WHERE title = '"+ title+"';", Track.class);
		findQuery.setMaxResults(1);
		if(findQuery.getResultList().size() == 0){
			logger.debug("Could not find any Tracks with title: "+ title);
			return null;	
		}
		if(findQuery.getResultList().size() >1 )
			logger.warn("Query Returned more than one Tracks with title: "+ title);
		return (Track) findQuery.getResultList().get(0);
	}

	
	public static List<Track> listAllTracks() {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select s from Track s", Track.class);
		@SuppressWarnings("unchecked")
		List<Track> allSongs = (List<Track>) findQuery. getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing All Track, Total Size:" + allSongs.size() +" ############## \n ");
//		for (Song s : allSongs) {
//			logger.debug("\n Got Song: \n" + s);
//		}
		return allSongs;
	}
	
	/* TODO Check alternative
	 * Cassandra pagination Alternative: select * from tracks where token(key)> token('TRAQUFS12903CF9F32') limit 10;
	 */
	
	public static List<Track> getTracksPage(int pageNo, int resultsNo) {
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em.createQuery("Select s from Track s", Track.class);
		findQuery.setMaxResults(CassandraController.getCounterValue("tracks"));
		@SuppressWarnings("unchecked")
		List<Track> allSongs = findQuery. getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing Track page: " +pageNo + " ResultsNo: "+ resultsNo +" Total Size:" + allSongs.size() +" ############## \n ");
		logger.debug("\n ##############  Returning Tracks From: " + (pageNo*resultsNo) + " To: "+ ((pageNo+1)*resultsNo-1) +" ############## \n ");
		return allSongs.subList((pageNo*resultsNo), ((pageNo+1)*resultsNo-1));
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
		//SELECT * FROM "counters" WHERE "id"='tracks'
		Query q = em.createNativeQuery("SELECT * FROM \"counters\" WHERE \"key\"='"+id+"';", Counter.class);
		q.setMaxResults(1);
		//Counter tmp = em.find(Counter.class, id);
		@SuppressWarnings("unchecked")
		List<Counter> tmp = q.getResultList();
		if(tmp == null){
			logger.debug("\n Counter: "+ id +" NOT FOUND!!!");
			return 0;
		}
		else{
			return tmp.get(0).getCounter();
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
		@SuppressWarnings("unchecked")
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
		@SuppressWarnings("unchecked")
		List<PlayList> allPlaylists= findQuery.getResultList();
		em.close();
		return allPlaylists;
	}
	


	public static List<PlayList> getUserPlayLists(String usermail){
		EntityManager em = getEmf().createEntityManager();
		Query findQuery = em
				.createQuery("Select p from PlayList p");
		@SuppressWarnings("unchecked")
		List<PlayList> tmp =  (List<PlayList>) findQuery.getResultList();
		//Avoid null saved playlists - scala Option is another alternative to catch null pointers
		for(PlayList p : tmp ){
			if(p.getTracks() == null)
				p.setTracks(new ArrayList<String>());
		}
		System.out.println("\n\n---->>>QUery returned: "+ tmp) ;
		
		return tmp;
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
		
		@SuppressWarnings("unchecked")
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
	
	/** 
	 * TODO - REFACTOR!
	 
	private void createColumnFamily() {
		try {
			CassandraCli.executeCqlQuery("USE \"KunderaExamples\"");
			CassandraCli
					.executeCqlQuery("CREATE TABLE blog_posts (post_id int PRIMARY KEY, body text, tags set<text>, liked_by list<int>, comments map<int, text>)");
			CassandraCli.executeCqlQuery("CREATE INDEX ON blog_posts(body)");
		} catch (Exception e) {
		}
	}

	private void createKeyspace() {
		try {
			CassandraCli
					.executeCqlQuery("CREATE KEYSPACE \"KunderaExamples\" WITH replication = {'class':'SimpleStrategy','replication_factor':3}");
		} catch (Exception e) {

		}
	}
	*/
	
	private static EntityManagerFactory getEmf() {
		logger.setLevel(Level.DEBUG);
		
		if (emf == null) {
			Map<String, String> propertyMap = new HashMap<String, String> ();
	        propertyMap.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
			emf = Persistence.createEntityManagerFactory("cassandra_pu", propertyMap);
			logger.debug("\n emf"+ emf.toString());
		}
		return emf;
	}	
}