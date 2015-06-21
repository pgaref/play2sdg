package controllers;

import java.util.List;
import java.util.UUID;

import models.PlayList;
import models.Track;
import models.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;


@Security.Authenticated(Secured.class)
public class PlayListController extends Controller {

    private static final DynamicForm dynForm = play.data.Form.form();
    
    
    public static Result add(String SongTitle) {
    	PlayList newPlay = PlayListController.create(Login.findUser(request().username()), dynForm.bindFromRequest().get("folder"));
    	return ok(views.html.ratings.rategroup.render(newPlay));
    }

    
    public static Result rename(UUID playlistid) {
        if (isMemberOf(playlistid)) {
        	PlayListController.playListRename(playlistid, dynForm.bindFromRequest().get("name"));
            return ok();
        } else {
            return forbidden();
        }
    }

    public static Result delete(UUID playlistid) {
        if (isMemberOf(playlistid)) {
        	PlayListController.remove(playlistid);
            return ok();
        } else {
            return forbidden();
        }
    }

    private static boolean isMemberOf(UUID id) {
    	/*
    	 * Could be changed to an elegant query!!
    	 */
    	List<PlayList> l = PlayListController.findExisting(Context.current().request().username());
    	for(PlayList p : l){
    		if(p.getId() == id)
    			return true;
    	}
    	return false;
    }
    
    /*
	 * JPA Connector functionality for Easy accessibility
	 */
    
	//PlayList with one song
    public static PlayList create(User  u, String folder, String songTitle){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	Track listsong = CassandraController.findTrackbyTitle(songTitle);
    	pl.addRatingSong(listsong);
    	CassandraController.persist(pl);
    	return pl;
    }
    // Empty PlayList
    public static PlayList create(User  u, String folder){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	CassandraController.persist(pl);
    	return pl;
    }
    
    public static void remove(UUID id){
    	PlayList p = CassandraController.getByID(id);
    	CassandraController.remove(p);
    }
    
    public static List<PlayList> findExisting(String usermail){
    	List<PlayList> pl = CassandraController.getUserPlayLists(usermail);
    	if(pl == null){
    		System.out.println("User: " + usermail + " has no playlists!!!");
    		return null;
    	}
    	return pl;
    }
    
    public static boolean hasPlayLists(String usermail){
    	List<PlayList> pl = CassandraController.getUserPlayLists(usermail);
    	
    	if(pl == null)
    		return false;
    	
    	return (pl.size() > 0);
    }
    
    public static void addSong(PlayList playlist, Track song){
    	playlist.addRatingSong(song);
    	CassandraController.persist(playlist);
    }
    
    public static void playListRename(UUID id, String newname){
    	PlayList p = CassandraController.getByID(id);
    	CassandraController.playlistRename(p, newname);
    }
    
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
	
	public static Track create(String id, String title, String artist, String released){
		Track newSong = new Track(id, title, artist, released);
		CassandraController.persist(newSong);
		return newSong;
	}

	public static Track findByTitle(String title) {
		return CassandraController.findTrackbyTitle(title);
	}
	
	public static Track findBytTrackID(String id){
		return CassandraController.findByTrackID(id);
	}
	
	public static Track findByID(int id){
		List<Track> l = PlayListController.findAllSongs();
		if(id > l.size()){
			System.out.println("ID Cannot be greater than size!!");
			return null;
		}
		return l.get(id);
	}

	public static List<Track> findAllSongs() {
		return CassandraController.listAllTracks();
	}
	
	public static List<Track> getTracksPage(int PageNo){
		return CassandraController.getTracksPage(PageNo, 50);
	}
	
	public static List<Track> getnextTracksPage(String lastcurrentPageTrack){
		return CassandraController.getNextTracksPage(lastcurrentPageTrack);
	}
	
	
	public static int getSongID(String title) {
		List<Track> l = PlayListController.findAllSongs();
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i).title.equals(title))
				return i;
		}
		return -1;
	}
	
}
