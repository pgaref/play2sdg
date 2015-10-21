package controllers;

import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import managers.ClusterManager;
import models.Counter;
import models.PlayList;
import models.Recommendation;
import models.StatsTimeseries;
import models.Track;
import models.User;
import play.Routes;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Application extends Controller {

	/* TODO -> for SDG Version
	 * One CF per web App instance -- For SDG use only! 
	 *
	 private static LocalCollaborativeFiltering cf = new LocalCollaborativeFiltering();;
	*/
	public static ClusterManager clusterManager;
	public static CassandraDxQueryController dxController;
	public static String currentUserMail;
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	currentUserMail = request().username();
    	System.out.print(".");
    	Logger.debug("Logged in as User: "+ request().username() );
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getTracksPage(0), Login.findUser(request().username()), (int)dxController.getCounterValue(CassandraDxQueryController.trackCounter.getId())) );
    }
   
    
    @Security.Authenticated(Secured.class)
    public static Result getNextTracks(String lastcurrentPageTrack){
    	return ok(new ObjectMapper().convertValue(PlayListController.getnextTracksPage(lastcurrentPageTrack), JsonNode.class));
    }

    @Security.Authenticated(Secured.class)
    public static Result getNextPage(String lastcurrentPageTrack){
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getnextTracksPage(lastcurrentPageTrack), Login.findUser(request().username()), (int)dxController.getCounterValue(CassandraDxQueryController.trackCounter.getId())) );
    }
    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    	Recommendation userRec = dxController.getUserRecommendations(request().username());
    	Logger.debug("Got Recommendation: "+ userRec.toString());
    	//StatsTimeseries jobStats = CassandraController.getSparkJobStats();
    	return ok(new ObjectMapper().convertValue(userRec, JsonNode.class));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result getJobStats(){
    	StatsTimeseries jobStats = dxController.getAllStatsTimeseries("sparkCF").get(0);
    	return ok(new ObjectMapper().convertValue(jobStats, JsonNode.class));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result rate(UUID playListID, String track_id){
    	Track found =  PlayListController.findBytTrackID(track_id);
    	Logger.debug("\n\n\n ---> ############ Plain track id:  " + track_id + " - Found Title " + found.getTitle());
    	PlayList p = dxController.findByPlaylistID(playListID);
    	PlayListController.addSong(p, found);
    	return ok(views.html.ratings.rateitem.render(found.title, p.id, p.folder));
    }
    
    @Security.Authenticated(Secured.class)
    public static Result createPlayList(){
    	PlayList newPlay = PlayListController.create(Login.findUser(request().username()), "tmpList");
    	Logger.debug("Just create Playlist "+ newPlay.toString());
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getIndexPageTracks(),  Login.findUser(request().username()),  (int)dxController.getCounterValue(CassandraDxQueryController.trackCounter.getId())) );
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deletePlayListSong(UUID playListID, String song){
    	
    	Logger.debug("\n -----Delete request: "+ playListID + " - " + song);
    	dxController.deleteUserPlayListSong(playListID, song);
    	//return ok();	
    	return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getIndexPageTracks(),  Login.findUser(request().username()), (int)dxController.getCounterValue(CassandraDxQueryController.trackCounter.getId())) );
    }


    public static Result getTimeseriesStats(String statsID){
        return ok(new ObjectMapper().convertValue(dxController.getAllStatsTimeseries(statsID), JsonNode.class));
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Login.index());
    }
    
    @Security.Authenticated(Secured.class)
    public static Result addSpotifySongsbyArtist(String artist){
		return ok(views.html.index.render(PlayListController.findExisting(request().username()), PlayListController.getIndexPageTracks(),  Login.findUser(request().username()), (int)dxController.getCounterValue(CassandraDxQueryController.trackCounter.getId())) );
    }
    
    public static Result allSongs(){
    	return ok(new ObjectMapper().convertValue(PlayListController.getIndexPageTracks().size(), JsonNode.class));
    }
    
    public static Result getUserName(){
    	return ok(new ObjectMapper().convertValue(currentUserMail, JsonNode.class));
    }
    
    public static Result getPlaylists(){
    	List<PlayList> tmp = dxController.getUserPlayLists(currentUserMail);
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
