package controllers;

import java.util.ArrayList;

import play.mvc.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import models.Counter;
import models.PlayList;
import models.Recommendation;
import models.Stats;
import models.Track;
import models.User;




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
		//User tmp = em.find(User.class, user.getEmail());
		Query q = em.createNativeQuery("SELECT * FROM \"users\" WHERE \"key\"='"+user.getEmail()+"';", User.class);
		q.setMaxResults(1);
		
		List<User> userList = q.getResultList();
		if(userList.isEmpty()){
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
		decrement(userCounter, "user");
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
		
		boolean userStr =  ( userl.isEmpty() ? false : true );
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
		 * 
			Map<String, String> propertyMap = new HashMap<String, String>();
        	propertyMap.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
        	EntityManagerFactory tmp = Persistence.createEntityManagerFactory("cassandra_pu", propertyMap);
        */
        EntityManager em  = getEmf().createEntityManager();
        
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
		
		//Possible Timeout Exception 
		//findQuery.setMaxResults(CassandraController.getCounterValue("tracks"));
		@SuppressWarnings("unchecked")
		List<Track> allSongs = findQuery. getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing Track page: " +pageNo + " ResultsNo: "+ resultsNo +" Total Size:" + allSongs.size() +" ############## \n ");
		logger.debug("\n ##############  Returning Tracks From: " + (pageNo*resultsNo) + " To: "+ ((pageNo+1)*resultsNo-1) +" ############## \n ");
		return allSongs.subList((pageNo*resultsNo), ((pageNo+1)*resultsNo-1));
	}
	
	public static List<Track> getNextTracksPage(String lastcurrentPageTrack){
		EntityManager em = getEmf().createEntityManager();
		
		if(lastcurrentPageTrack != null){
			Query findQuery = em
				.createNativeQuery("SELECT * FROM tracks WHERE token(key) > token('"+ lastcurrentPageTrack +"') LIMIT 100;", Track.class);
			@SuppressWarnings("unchecked")
			List<Track> nextPageTracks = findQuery. getResultList();
			em.close();
			logger.debug("\n ##############  Listing Next Track page:  After TrackID: "+ lastcurrentPageTrack +" ############## \n ");
			return nextPageTracks;
		} else{
			//Initial Page Case
			Query findQuery = em.createQuery("Select s from Track s", Track.class);
			findQuery.setMaxResults(100);
			@SuppressWarnings("unchecked")
			List<Track> nextPageTracks = findQuery. getResultList();
			em.close();
			logger.debug("\n ##############  Listing First Track page ############## \n ");
			return nextPageTracks;
		}
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
				.createQuery("Select p from PlayList p where p.usermail = " +usermail );
		@SuppressWarnings("unchecked")
		List<PlayList> tmp =  (List<PlayList>) findQuery.getResultList();
		
		//Avoid null saved playlists - scala Option is another alternative to catch null pointers
		if(tmp == null){
			logger.debug("User: "+ usermail + " has NO playlists!");
			return null;
		}
		
		//Avoid null pointers in SCALA viewsongs!
		for(PlayList p : tmp ){
			if(p.getTracks() == null)
				p.setTracks(new ArrayList<String>());
		}
		logger.debug("\n\n---->>> getUserPlayLists QUery returned: "+ tmp) ;
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
	/**
	 * Recommendation - Cassandra JPA
	 * @param Recommendation
	 * 
	 */
	public static void persist(Recommendation r) {
		EntityManager em = getEm();
		Recommendation tmp = em.find(Recommendation.class, r.getEmail());
		if(tmp == null){
			em.persist(r);
			System.out.println("\n Recommendation for : " + r.getEmail() + " record persisted using persistence unit -> cassandra_pu");
		}
		else{
			r.getRecList().putAll(tmp.getRecList());
			em.merge(r);
			System.out.println("\n Recommendation for : " + r.getEmail() + " record merged using persistence unit -> cassandra_pu");
		}
		em.close();	
	}
	
	public static List<Recommendation> listAllRecommendations(){
		EntityManager em = getEm();
		Query findQuery = em.createQuery("Select r from Recommendation r", Recommendation.class);
		findQuery.setMaxResults(Integer.MAX_VALUE);
		List<Recommendation> allRec = findQuery.getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing All Recommendations, Total Size:" + allRec.size() +" ############## \n ");
		return allRec;
	}
	
	public static Recommendation getUserRecc(String usermail){
		EntityManager em = getEmf().createEntityManager();
//		Query q = em.createNativeQuery("SELECT * FROM \"recommendations\" WHERE \"email\"='"+usermail+"';", Recommendation.class);
//		q.setMaxResults(1);
//		@SuppressWarnings("unchecked")
//		List<Recommendation> tmp = q.getResultList();
		
		Recommendation found = em.find(Recommendation.class, usermail);

		if(found == null){
			logger.debug("\n Recommendations for user : "+ usermail +" NOT FOUND!!!");
			return null;
		}
		else{
			return found;
		}

	}
	
	/**
	 * Stats - Cassandra JPA
	 * @param Stats
	 * 
	 */
	public static void persist(Stats s) {
		EntityManager em = getEm();
		Stats tmp = em.find(Stats.class, s.getId());
		if(tmp == null){
			em.persist(s);
			System.out.println("\n Recommendation for : " + s.getId() + " record persisted using persistence unit -> cassandra_pu");
		}
		else{
			s.getStatsMap().putAll(tmp.getStatsMap());
			em.merge(s);
			System.out.println("\n Recommendation for : " + s.getId() + " record merged using persistence unit -> cassandra_pu");
		}
		em.close();	
	}
	
	public static List<Stats> getAllStats(){
		EntityManager em = getEm();
		Query findQuery = em.createQuery("Select s from Stats s", Stats.class);
		findQuery.setMaxResults(Integer.MAX_VALUE);
		List<Stats> allStats = findQuery.getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing All Stats, Total Size:" + allStats.size() +" ############## \n ");
		return allStats;
	}
	
	public static Stats getSparkJobStats(){
		EntityManager em = getEm();
		Query findQuery = em.createQuery("Select s from Stats s WHERE s.id= 'sparkCF'");
		findQuery.setMaxResults(1);
		List<Stats> cfStats = findQuery.getResultList();
		em.close();
		
		logger.debug("\n ##############  Listing All Stats, Total Size:" + cfStats.size() +" ############## \n ");
		
		if(cfStats.isEmpty())
			return new Stats();
		return cfStats.get(0);
	}
	
	
	/**
	 * Entity Manager Factory and Wrapper
	 * @return
	 */
	
	private static EntityManager getEm() {
		logger.setLevel(Level.INFO);	
		if (emf == null) {
			EntityManager em = getEmf().createEntityManager();
			em.setProperty("cql.version", "3.0.0");
			logger.debug("\n emf"+ emf.toString());
		}
		emf =  getEmf();
		EntityManager em = emf.createEntityManager();
		em.setProperty("cql.version", "3.0.0");
		return em;
	}
	
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