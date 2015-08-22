package controllers;

import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.SongParams;

import models.Counter;
import models.PlayList;
import models.Recommendation;
import models.Stats;
import models.Track;
import models.User;
import play.Routes;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Application extends Controller {

	/* TODO -> for sdg Version
	 * One CF per web App instance -- For SDG use only! 
	 *
	 private static LocalCollaborativeFiltering cf = new LocalCollaborativeFiltering();;
	*/
	private static EchoNestAPI en;
	
	private static String userName;
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	userName = request().username();
    	System.out.println("Logged in as User: "+ request().username() );
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getTracksPage(0), Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
   
    
    @Security.Authenticated(Secured.class)
    public static Result getNextTracks(String lastcurrentPageTrack){
    	return ok(new ObjectMapper().convertValue(PlayListController.getnextTracksPage(lastcurrentPageTrack), JsonNode.class));
    }

    @Security.Authenticated(Secured.class)
    public static Result getNextPage(String lastcurrentPageTrack){
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getnextTracksPage(lastcurrentPageTrack), Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    	Recommendation userRec = CassandraController.getUserRecc(request().username());
    	Stats jobStats = CassandraController.getSparkJobStats();
    	return ok(new ObjectMapper().convertValue(userRec, JsonNode.class));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result getJobStats(){
    	Stats jobStats = CassandraController.getSparkJobStats();
    	return ok(new ObjectMapper().convertValue(jobStats, JsonNode.class));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result rate(UUID playListID, String track_id){

    	Track found =  PlayListController.findBytTrackID(track_id);
    	System.out.println("\n\n\n ---> ############ Plain track id:  "+ track_id + " - Found Title "+ found.getTitle()); 
    	PlayList p = CassandraController.getByID(playListID);
    	PlayListController.addSong(p, found);
    	return ok(views.html.ratings.rateitem.render(found.title, p.id, p.folder));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result createPlayList(){
    	PlayList newPlay = PlayListController.create(Login.findUser(request().username()), "tmpList");
    	System.out.println("Just create Playlist "+ newPlay.toString());
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deletePlayListSong(UUID playListID, String song){
    	
    	System.out.println("\n -----Delete request: "+ playListID + " - " + song);
    	CassandraController.deleteUserPlayListSong(playListID, song);
    	//return ok();	
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Login.index());
    }
    
    
    
    public static void dumpSong(com.echonest.api.v4.Song song) throws EchoNestException {
    	
    	System.out.println("Receiced track: "+ song.getTitle() + " from "+ song.getArtistName());
    	Track mySong = new Track(song.getID(), song.getTitle(), song.getArtistName(), "2014-11-11");
        controllers.CassandraController.persist(mySong);
    }
    
    @Security.Authenticated(Secured.class)
    public static Result addSpotifySongsbyArtist(String artist){
    	en = new EchoNestAPI("H8PUFR4HPWFVVEQLW");
        en.setTraceSends(true);
        en.setTraceRecvs(false);
        SongParams p = new SongParams();
        p.setArtist(artist);
        p.setResults(20);
        p.includeAudioSummary();
        p.includeArtistHotttnesss();
        p.includeSongHotttnesss();
        p.includeArtistFamiliarity();
        p.includeArtistLocation();
        p.sortBy("song_hotttnesss", true);


        List<com.echonest.api.v4.Song> songs;
		try {
			songs = en.searchSongs(p);
		
        for (com.echonest.api.v4.Song song : songs) {
            dumpSong(song);
            System.out.println();
        }
        
		} catch (EchoNestException e) {
			System.err.println("Echo Nest API error");
			e.printStackTrace();
		}
        
		return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    public static Result allSongs(){
    	return ok(new ObjectMapper().convertValue(PlayListController.findAllSongs().size(), JsonNode.class));
    }
    
    public static Result getUserName(){
    	return ok(new ObjectMapper().convertValue(userName, JsonNode.class));
    }
    
    public static Result getPlaylists(){
    	List<PlayList> tmp = controllers.CassandraController.getUserPlayLists(userName);
    	return ok(new ObjectMapper().convertValue(tmp, JsonNode.class));
    }
    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
        		controllers.routes.javascript.Application.deletePlayListSong(),
        		controllers.routes.javascript.Application.rate(),
        		controllers.routes.javascript.Application.getNextTracks(),
                controllers.routes.javascript.Application.getPlaylists(),
                controllers.routes.javascript.Application.allSongs(),
                controllers.routes.javascript.Application.getUserName(),
                controllers.routes.javascript.PlayListController.add(),
                controllers.routes.javascript.PlayListController.delete(),
                controllers.routes.javascript.PlayListController.rename()
                ));

    }

}
