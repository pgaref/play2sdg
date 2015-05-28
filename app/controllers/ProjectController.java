package controllers;

import models.PlayList;
import models.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;


@Security.Authenticated(Secured.class)
public class ProjectController extends Controller {

    private static final DynamicForm dynForm = play.data.Form.form();

    public static Result add(Long Songid) {

      //  Rating newProject = Rating.create("New project", dynForm.bindFromRequest().get("folder"), request().username());
    	/*PlayList newRate = PlayList.create(RelationalUser.findbyEmail(request().username()),dynForm.bindFromRequest().get("folder"),Songid);
        return ok(views.html.ratings.rategroup.render(newRate));
        */
        return ok();
    }

    
    public static Result rename(Long rating) {
        if (isMemberOf(rating)) {
        	return ok();
            /*return ok(PlayList.rename(rating, dynForm.bindFromRequest().get("name")));*/
        } else {
            return forbidden();
        }
    }

    public static Result delete(Long project) {
        if (isMemberOf(project)) {
          /*  PlayList.find.ref(project).delete();*/
            return ok();
        } else {
            return forbidden();
        }
    }

    private static boolean isMemberOf(Long rating) {
    	return true;
       /* return PlayList.hasRatings(Context.current().request().username());*/
    }
}
