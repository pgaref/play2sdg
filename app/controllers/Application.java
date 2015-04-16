package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.Rating;
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
	
	
    @Security.Authenticated(Secured.class)
    public static Result index() {
    	//change to  mail validation!!
        //return ok(views.html.index.render(Rating.findInvolving(request().username()), Song.findTodoInvolving(request().username()), User.find.byId(request().username())));
    	return ok(views.html.index.render(Rating.findInvolving(request().username()), Song.findAllSongs(), User.find.byId(request().username())));
    }

    
    
    @Security.Authenticated(Secured.class)
    public static Result getUserRecommendations(){
    	cf.loadUserRatings(request().username());
    	List<Song> recList = cf.recc2Song(request().username());
    	return ok(views.html.ratings.cf.render(recList ,User.find.byId(request().username())  ));	
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
