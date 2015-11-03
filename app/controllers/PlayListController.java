package controllers;

import java.util.List;
import java.util.UUID;

import managers.ClusterManager;
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
	
    /*
	private static Track lastPageTrack=null;
    */ 
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
    	Track listsong = Application.dxController.findTrackbyTitle(songTitle).get(0);
    	pl.addRatingSong(listsong.getTitle());
    	Application.dxController.persist(pl);
    	return pl;
    }
    // Empty PlayList
    public static PlayList create(User  u, String folder){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	Application.dxController.persist(pl);
    	return pl;
    }
    
    public static void remove(UUID id){
    	PlayList p = Application.dxController.findByPlaylistID(id);
    	Application.dxController.remove(p);
    }
    
    public static List<PlayList> findExisting(String usermail){
    	List<PlayList> pl = Application.dxController.getUserPlayLists(usermail);
    	if(pl == null){
    		System.out.println("User: " + usermail + " has no playlists!!!");
    		return null;
    	}
    	return pl;
    }
    
    public static boolean hasPlayLists(String usermail){
    	List<PlayList> pl = Application.dxController.getUserPlayLists(usermail);
    	
    	if(pl == null)
    		return false;
    	
    	return (pl.size() > 0);
    }
    
    public static void addSong(PlayList playlist, Track song){
    	playlist.addRatingSong(song.getTitle());
    	Application.dxController.persist(playlist);
    }
    
    public static void playListRename(UUID id, String newname){
    	PlayList p = Application.dxController.findByPlaylistID(id);
    	p.setFolder(newname);
    	Application.dxController.persist(p);
    }
    
    
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
	
	public static Track create(String id, String title, String artist, String released){
		Track newSong = new Track(id, title, artist, released);
		Application.dxController.persist(newSong);
		return newSong;
	}

	public static Track findByTitle(String title) {
		return Application.dxController.findTrackbyTitle(title).get(0);
	}
	
	public static Track findBytTrackID(String id){
		return Application.dxController.findByTrackID(id);
	}
	
	/*
	public static List<Track> getIndexPageTracks() {
		List<Track> tmp;
		if(PlayListController.lastPageTrack == null){
			tmp = Application.dxController.getTracksPage(20);
			PlayListController.lastPageTrack = tmp.get(tmp.size()-1);
			return tmp;
		}
		tmp =  Application.dxController.getNextTracksPage(PlayListController.lastPageTrack.getTrack_id(), 20);
		//In case we run out of Tracks!! (a benchmark can do that ) 
		if(tmp.isEmpty())
			tmp = Application.dxController.getTracksPage(20);
		
		PlayListController.lastPageTrack = tmp.get(tmp.size()-1);
		return tmp;
	}
	*/
	
	/*
	 * Function called FROM main.html ajax to refresh page Tracks
	 * 
		public static List<Track> getnextTracksPage(String lastcurrentPageTrack){
			return getIndexPageTracks();
		}
	 */
	public static List<Track> getTracksPage(int PageNo){
		return Application.dxController.getTracksPage(20);
	}
	
	//Iterating through all results is performance killer!!

//	public static Track findByID(int id){
//	List<Track> l = PlayListController.findAllSongs();
//	if(id > l.size()){
//		System.out.println("ID Cannot be greater than size!!");
//		return null;
//	}
//	return l.get(id);
//}
	
//	public static int getSongID(String title) {
//		List<Track> l = PlayListController.findAllSongs();
//		for (int i = 0; i < l.size(); i++) {
//			if (l.get(i).title.equals(title))
//				return i;
//		}
//		return -1;
//	}
	
}
