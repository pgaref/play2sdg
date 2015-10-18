package controllers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.ListenableFuture;

import accessors.CounterAccessorI;
import accessors.PlaylistAccessorI;
import accessors.RecommendationAccessorI;
import accessors.StatsTimeseriesAccessorI;
import accessors.TrackAccessorI;
import accessors.UserAccessorI;
import models.Counter;
import models.PlayList;
import models.Recommendation;
import models.StatsTimeseries;
import models.Track;
import models.User;
import play.Logger;

//import static com.datastax.driver.core.querybuilder.QueryBuilder.*;


/**
 * Generic Object - Mapping Controller - similar to Facade Pattern
 * All Accessor interface methods should be called here
 *  
 * @author pgaref
 *
 */
public class CassandraDxQueryController {

	static Counter trackCounter = new Counter("tracks");
	static Counter userCounter = new Counter("users");
	static Counter playlistCounter = new Counter("playlists");	
	private Session clusterSession;
	private MappingManager manager;
	
	public CassandraDxQueryController(Session session){
		this.clusterSession = session;
		manager = new MappingManager (clusterSession);
	}
	
	/**
	 * User - Cassandra Object Mapping 
	 * @param user
	 */
	
	public void persist(User user) {
		Logger.debug("Persisting user: "+ user.toString());
		Mapper<User> userMapper = manager.mapper(User.class);
		userMapper.save(user);
		this.increment(userCounter);
	}

	public User find(String email) {
		Logger.debug("Looking for user: "+ email);
		Mapper<User> userMapper = manager.mapper(User.class);
		return userMapper.get(email);
	}
	
	public List<User> getAllUsers() {
		Logger.debug("Listing all users SYNC");
		UserAccessorI userAccessor = manager.createAccessor(UserAccessorI.class);
		Result<User> users = userAccessor.getAll();
		return users.all();
	}
	
	public List<User> getAllUsersAsync() throws InterruptedException, ExecutionException {
		Logger.debug("Listing all users ASYNC");
		UserAccessorI userAccessor = manager.createAccessor(UserAccessorI.class);
		ListenableFuture<Result<User>> users = userAccessor.getAllAsync();
		return users.get().all();
	}

	
	/**
	 * Track - Cassandra JPA
	 * @param Track
	 */
	
	public void persist(Track song) {
		Logger.debug("Persisting track: "+ song.getTrack_id());
		Mapper<Track> trackMapper = manager.mapper(Track.class);
		trackMapper.save(song);
		this.increment(trackCounter);
	}
	
	public void remove(Track song) {
		Logger.debug("Removing track: "+song.getTrack_id());
		Mapper<Track> trackMapper = manager.mapper(Track.class);
		trackMapper.delete(song);
		this.decrement(trackCounter);
	}
	
	public Track findByTrackID(String id){
		Logger.debug("Looking for track: "+id);
		Mapper<Track> trackMapper = manager.mapper(Track.class);
		return trackMapper.get(id);
	}
	
	public List<Track> findTrackbyTitle(String title) {
		TrackAccessorI taccessor = manager.createAccessor(TrackAccessorI.class);
		return taccessor.getbyTitle(title).all();
//		Statement s = QueryBuilder.select()
//				.all()
//				.from("play_cassandra", "tracks")
//				.where(eq("title",title));
//		ResultSet re  = this.manager.getSession().execute(s);
//		for(Row r : re.all()){
//			System.out.println("row: "+ r);
//		}
	}
	
	public List<Track> getAllTracks() {
		Logger.debug("Listing All Tracks SYNC");
		TrackAccessorI taccessor = manager.createAccessor(TrackAccessorI.class);
		return taccessor.getAll().all();
	}
	
	public List<Track> getAllTracksAsync() {
		Logger.debug("Listing All Tracks ASYNC");
		TrackAccessorI taccessor = manager.createAccessor(TrackAccessorI.class);
		return taccessor.getAll().all();
	}
	
	public List<Track> getTracksPage(int tracksNum) {
		Logger.debug("Listing "+tracksNum+" Page Tracks");
		TrackAccessorI taccessor = manager.createAccessor(TrackAccessorI.class);
		return taccessor.getTacksPage(tracksNum).all();
	}
	
	public List<Track> getNextTracksPage(String id, int tracksNum){
		Logger.debug("Listing next Index Page: "+id + " # Tracks: "+ tracksNum);
		TrackAccessorI taccessor = manager.createAccessor(TrackAccessorI.class);
		return taccessor.getNextTracksPage(id, tracksNum).all();
	}
	
	/**
	 * GENERIC counter - Cassandra JPA
	 * 
	 */
	
	public Counter getCounter(String key){
		Logger.debug("Looking for counter: "+ key);
		Mapper<Counter> cmapper = manager.mapper(Counter.class);
		return cmapper.get(key);
	}
	
	public void increment(Counter counter) {
		Logger.debug("Incrementing counter: "+ counter.getId());
		CounterAccessorI caccessor = manager.createAccessor(CounterAccessorI.class);
		caccessor.incrementCounter(counter.getId());
	}
	
	public void incrementByValue(Counter counter, long val){
		Logger.debug("Incrementing counter:"+counter.getId()+" by value: "+ val);
		CounterAccessorI caccessor = manager.createAccessor(CounterAccessorI.class);
		caccessor.increaseCounterByValue(counter.getId(), val);
	}
	
	public void decrement(Counter counter) {
		Logger.debug("Decrementing counter: "+ counter.getId());
		CounterAccessorI caccessor = manager.createAccessor(CounterAccessorI.class);
		caccessor.decrementCounter(counter.getId());
	}
	
	public long getCounterValue(String key){
		Logger.debug("Getting counter value: "+key);
		Mapper<Counter> cmapper = manager.mapper(Counter.class);
		return cmapper.get(key).getCounter();
	}
	
	/**
	 * Recommendation - Cassandra JPA
	 * @param Recommendation
	 * 
	 */
	public void persist(Recommendation r) {
		Logger.debug("Persisting recommendation: "+ r.toString());
		Mapper<Recommendation> recMapper = manager.mapper(Recommendation.class);
		recMapper.save(r);
	}
	
	public List<Recommendation> getAllRecommendations(){
		Logger.debug("Listing all recommendations SYNC");
		RecommendationAccessorI raccessor = manager.createAccessor(RecommendationAccessorI.class);
		return raccessor.getAll().all();
	}
	
	public List<Recommendation> getAllRecommendationsAsync(){
		Logger.debug("Listing all recommendations ASYNC");
		RecommendationAccessorI raccessor = manager.createAccessor(RecommendationAccessorI.class);
		return raccessor.getAll().all();
	}
	
	public Recommendation getUserRecommendations(String usermail){
		Logger.debug("Getting user recommendation: "+usermail);
		Mapper<Recommendation> recMapper = manager.mapper(Recommendation.class);
		return recMapper.get(usermail);
	}
	/**
	 * Playlist - Cassandra JPA
	 * @param Playlist
	 * 
	 */

	public void persist(PlayList p) {
		Logger.debug("Persisting playlsit: "+p.toString());
		Mapper<PlayList> plMapper = manager.mapper(PlayList.class);
		plMapper.save(p);
		this.increment(playlistCounter);
	}
	
	public void remove(PlayList p) {
		Logger.debug("Removing Playlist "+ p.toString());
		Mapper<PlayList> plMapper = manager.mapper(PlayList.class);
		plMapper.delete(p);
	}
	
	public PlayList findByPlaylistID(UUID id){
		Logger.debug("Looking for playlist: "+ id);
		Mapper<PlayList> plMapper = manager.mapper(PlayList.class);
		return plMapper.get(id);
	}
	
	public List<PlayList> getAllPlaylists(){
		Logger.debug("Listing all Playlists SYNC");
		PlaylistAccessorI plAccessor = manager.createAccessor(PlaylistAccessorI.class);
		Result<PlayList> playlists = plAccessor.getAll();
		return playlists.all();
	}
	public List<PlayList> getAllPlaylistsAsync() throws InterruptedException, ExecutionException{
		Logger.debug("Listing all PlayList ASYNC");
		PlaylistAccessorI plAccessor = manager.createAccessor(PlaylistAccessorI.class);
		ListenableFuture<Result<PlayList>> playlists = plAccessor.getAllAsync();
		return playlists.get().all();
	}
	
	public List<PlayList> getUserPlayLists(String usermail){
		Logger.debug("Get user Playlist: "+usermail);
		PlaylistAccessorI plAccessor = manager.createAccessor(PlaylistAccessorI.class);
		return plAccessor.getUserPlaylists(usermail).all();
	}
	
	public boolean deleteUserPlayListSong(UUID playlistid, String song){
		Logger.debug("Deleting user PlayList:  "+ playlistid + " Song: "+ song);
		Mapper<PlayList> plMapper = manager.mapper(PlayList.class);
		PlayList existing = plMapper.get(playlistid);
		if(existing == null)
			return false;
		List<String> tracksToUpdate = existing.getTracks();
		tracksToUpdate.remove(song);
		PlaylistAccessorI plAccessor = manager.createAccessor(PlaylistAccessorI.class);
		plAccessor.deleteUserPlayListSong(playlistid, tracksToUpdate);
		
		return (existing.getTracks().size() != tracksToUpdate.size());
	}
	
	/**
	 * New TimeSeries Stats - Cassandra Object Mapper
	 * @param Stats
	 * 
	 */
	public void persist(StatsTimeseries s) {
		Logger.debug("Persisting StatTimeseries: "+ s.toString());
		Mapper<StatsTimeseries> userMapper = manager.mapper(StatsTimeseries.class);
		userMapper.save(s);
	}
	
	
	public List<StatsTimeseries> getAllStatsTimeseries(String statsID){
		Logger.debug("Listing all StatsTimeseries SYNC: "+statsID);
		StatsTimeseriesAccessorI saccessor = manager.createAccessor(StatsTimeseriesAccessorI.class);
		return saccessor.getAll().all();
	}
	public List<StatsTimeseries> getAllStatsTimeseriesAsync(String statsID) throws InterruptedException, ExecutionException{
		Logger.debug("Listing all StatsTimeseries ASYNC: "+statsID);
		StatsTimeseriesAccessorI saccessor = manager.createAccessor(StatsTimeseriesAccessorI.class);
		return saccessor.getAllAsync().get().all();
	}
	
/*	
	public static void main(String[] args) {
		//ClusterManager cm = new ClusterManager("play_cassandra", 1, "146.179.131.141");
		ClusterManager cm = new ClusterManager("play_cassandra", 1, "155.198.198.12");
		CassandraDxQueryController dx = new CassandraDxQueryController(cm.getSession());
		

		List<PlayList> pgLists = dx.getUserPlayLists("pangaref@example.com");
		for(PlayList list : pgLists)
			System.out.println("pgList "+ list.toString());
		System.out.println("Deleted: "+ dx.deleteUserPlayListSong(pgLists.get(0).getId(), "Goodbye"));
		
		
		List<Track> byTitle = dx.findTrackbyTitle("Goodbye");
		System.out.println("Got By Title: Goodbye , TOTAL="+ byTitle.size());
		
		
		StatsTimeseries ts = new StatsTimeseries("pgSeriesTest");
		ts.getMetricsMap().put("cpu-util", "90%");
		ts.getMetricsMap().put("ram", "50%");
		dx.persist(ts);
		
		ts = new StatsTimeseries("pgSeriesTest");
		ts.getMetricsMap().put("cpu-util", "90%");
		ts.getMetricsMap().put("ram", "50%");
		dx.persist(ts);
				
		cm.disconnect();
		System.out.println("Cluster Manager disconnected! ");
		
	}
*/
}