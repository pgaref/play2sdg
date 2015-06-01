package controllers;

import java.util.List;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.SongParams;

import models.PlayList;
import models.Song;
import models.User;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Application extends Controller {

	/*
	 * One CF per web App instance
	*/ 
	private static CollaborativeFiltering cf = new CollaborativeFiltering();
	
	private static EchoNestAPI en;
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	//change to  mail validation!!
        //return ok(views.html.index.render(Rating.findInvolving(request().username()), Song.findTodoInvolving(request().username()), User.find.byId(request().username())));
    	List<PlayList> tmp = controllers.CassandraController.getUserPlayLists("pgaref@example.com");
    	//List<PlayList> tmp = PlayList.findExisting(request().username());
    	for(PlayList p : tmp ){
    		System.out.println("Listing PL : "+ p.folder);
    		System.out.println("PL songs:  "+ p.getSongs() + " #### size: "+ p.getSongs().size());
    	}
    	return ok(views.html.index.render(PlayList.findExisting(request().username()), Song.findAllSongs(), User.findUser(request().username())) );
    }

    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    	cf.loadUserRatings(request().username());
    	List<Song> recList = cf.recc2Song(request().username());
    	
    	return ok(views.html.ratings.cf.render(recList ,controllers.CassandraController.findbyEmail(request().username())  ));	
    }
    
    @Security.Authenticated(Secured.class)
    public static Result rate(String songtitle){
//    	final Map<String, String[]> values = request().body().asFormUrlEncoded();
//    	final String name = values.get("java_songid")[0];
//    	System.out.println("Managed to get SongId: "+ name);
    	
    	System.out.println("\n\n\n ---> ############ Plain song id:  "+ songtitle); 	
    	cf.addRating( User.getUserID(request().username()), Song.getSongID(songtitle), 1);

    	Song found =  Song.findByTitle(songtitle);
    	System.out.println("FOUND: "+ found);
    	PlayList.addSong(request().username(), found);
    	return ok(views.html.index.render(PlayList.findExisting(request().username()), Song.findAllSongs(),  User.findUser(request().username())) );
    }
    
    @Security.Authenticated(Secured.class)
    public static Result createPlayList(){
    	PlayList newPlay = PlayList.create(User.findUser(request().username()), "tmpList");
    	System.out.println("Just create Playlist "+ newPlay);
    	return ok(views.html.index.render(PlayList.findExisting(request().username()), Song.findAllSongs(),  User.findUser(request().username())) );
    }
    
    public static Result deletePlayListSong(String song){
    	
    	System.out.println("\n -----Delete request: "+ song);
    	return ok();
    	//return ok(views.html.index.render(PlayList.findExisting(request().username()), Song.findAllSongs(),  User.findUser(request().username())) );
    }
    
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Login.index());
    }
    
    
    
    public static void dumpSong(com.echonest.api.v4.Song song) throws EchoNestException {
    	
    	System.out.println("Receiced track: "+ song.getTitle() + " from "+ song.getArtistName());
    	Song mySong = new Song(song.getTitle(), song.getArtistName(), "2014-11-11", song.getAnalysisURL());
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
        
		return ok(views.html.index.render(PlayList.findExisting(request().username()), Song.findAllSongs(),  User.findUser(request().username())) );
    }
    
    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
                controllers.routes.javascript.PlayListController.add(),
                controllers.routes.javascript.PlayListController.delete(),
                controllers.routes.javascript.PlayListController.rename()
                ));

    }

}
