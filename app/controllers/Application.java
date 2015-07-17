package controllers;

import java.util.List;
import java.util.UUID;

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
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.Logger;

public class Application extends Controller {

	/* TODO -> for sdg Version
	 * One CF per web App instance -- For SDG use only! 
	 *
	 private static LocalCollaborativeFiltering cf = new LocalCollaborativeFiltering();;
	*/
	private static EchoNestAPI en;
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	//change to  mail validation!!
        //return ok(views.html.index.render(Rating.findInvolving(request().username()), Song.findTodoInvolving(request().username()), User.find.byId(request().username())));
    	List<PlayList> tmp = controllers.CassandraController.getUserPlayLists("pgaref@example.com");
    	//List<PlayList> tmp = PlayList.findExisting(request().username());
    	for(PlayList p : tmp ){
    		Logger.debug("Listing PL : "+ p.folder);
    		if(p.getTracks()!= null)
    			Logger.debug("PL songs:  "+ p.getTracks() + " #### size: "+ p.getTracks().size());
    		else
    			Logger.debug("PL songs:  empty ");
    	}
    	Logger.debug("Logged in as User: "+ request().username() );
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getTracksPage(0), Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }

    @Security.Authenticated(Secured.class)
    public static Result getNextPage(String lastcurrentPageTrack){
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getnextTracksPage(lastcurrentPageTrack), Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    	//cf.loadUserRatings(request().username());
    	//List<Track> recList = cf.recc2Song(request().username());
    	Recommendation userRec = CassandraController.getUserRecc(request().username());
    	Stats jobStats = CassandraController.getSparkJobStats();
    	
    	return ok(views.html.ratings.cf.render(userRec, jobStats,controllers.CassandraController.findbyEmail(request().username())  ));	
    }
    
    @Security.Authenticated(Secured.class)
    public static Result rate(String track_id){
//    	final Map<String, String[]> values = request().body().asFormUrlEncoded();
//    	final String name = values.get("java_songid")[0];
//    	System.out.println("Managed to get SongId: "+ name);
    	Track found =  PlayListController.findByTrackID(track_id);
    	Logger.debug("\n\n\n ---> ############ Plain track id:  "+ track_id + " - Found Title "+ found.getTitle()); 
    	
    	/* Switched to period job
    		cf.addRating( Login.getUserID(request().username()), PlayListController.getSongID(found.getTitle()), 1);
    	*/
    	
    	PlayListController.addSong(request().username(), found);
    	
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    @Security.Authenticated(Secured.class)
    public static Result createPlayList(){
    	PlayList newPlay = PlayListController.create(Login.findUser(request().username()), "tmpList");
    	Logger.debug("Just create Playlist "+ newPlay.toString());
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deletePlayListSong(UUID playListID, String song){
    	
    	Logger.debug("\n -----Delete request: "+ playListID + " - " + song);
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
    	
    	Logger.debug("Receiced track: "+ song.getTitle() + " from "+ song.getArtistName());
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
            Logger.debug("----");
        }
        
		} catch (EchoNestException e) {
			Logger.error("Echo Nest API error");
			e.printStackTrace();
		}
        
		return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.findAllSongs(),  Login.findUser(request().username()), CassandraController.getCounterValue("tracks") ) );
    }
    
    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
        		controllers.routes.javascript.Application.deletePlayListSong(),
                controllers.routes.javascript.PlayListController.add(),
                controllers.routes.javascript.PlayListController.delete(),
                controllers.routes.javascript.PlayListController.rename()
                ));

    }

}
