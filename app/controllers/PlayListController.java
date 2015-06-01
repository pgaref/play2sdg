package controllers;

import java.util.List;
import java.util.UUID;

import models.PlayList;
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
    	PlayList newPlay = PlayList.create(User.findUser(request().username()), dynForm.bindFromRequest().get("folder"));
    	return ok(views.html.ratings.rategroup.render(newPlay));
    }

    
    public static Result rename(UUID playlistid) {
        if (isMemberOf(playlistid)) {
        	PlayList.playListRename(playlistid, dynForm.bindFromRequest().get("name"));
            return ok();
        } else {
            return forbidden();
        }
    }

    public static Result delete(UUID playlistid) {
        if (isMemberOf(playlistid)) {
        	PlayList.remove(playlistid);
            return ok();
        } else {
            return forbidden();
        }
    }

    private static boolean isMemberOf(UUID id) {
    	/*
    	 * Could be changed to an elegant query!!
    	 */
    	List<PlayList> l = PlayList.findExisting(Context.current().request().username());
    	for(PlayList p : l){
    		if(p.getId() == id)
    			return true;
    	}
    	return false;
    }
}
