package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	 
	private static CollaborativeFiltering cf = new CollaborativeFiltering();
	*/
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	//change to  mail validation!!
        //return ok(views.html.index.render(Rating.findInvolving(request().username()), Song.findTodoInvolving(request().username()), User.find.byId(request().username())));
    	return ok();
    	/*return ok(views.html.index.render(PlayList.findInvolving(request().username()), Song.findAllSongs(), RelationalUser.find.byId(request().username())));*/
    }

    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    /*	cf.loadUserRatings(request().username());
    	List<Song> recList = cf.recc2Song(request().username());
    */
    	return ok();
    	/*return ok(views.html.ratings.cf.render(recList ,RelationalUser.find.byId(request().username())  ));*/	
    }
    
    @Security.Authenticated(Secured.class)
    public static Result rate(String songtitle){
//    	final Map<String, String[]> values = request().body().asFormUrlEncoded();
//    	final String name = values.get("java_songid")[0];
//    	System.out.println("Managed to get SongId: "+ name);
    	
    	System.out.println("Plain song id: "+ songtitle); 	
/*    	cf.addRating(RelationalUser.getUserID(request().username()), Song.getSongID(songtitle), 1);*/
    	return ok();
    	/*return ok(views.html.index.render(PlayList.findInvolving(request().username()), Song.findAllSongs(), RelationalUser.find.byId(request().username())));*/
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Login.index());
    }
    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
                controllers.routes.javascript.ProjectController.add(),
                controllers.routes.javascript.ProjectController.delete(),
                controllers.routes.javascript.ProjectController.rename()
                ));

    }

}
