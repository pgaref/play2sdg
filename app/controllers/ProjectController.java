package controllers;

import models.Rating;
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
    	Rating newRate = Rating.create(User.findbyEmail(request().username()),dynForm.bindFromRequest().get("folder"),Songid);
        return ok(views.html.ratings.rategroup.render(newRate));
    }

    
    public static Result rename(Long rating) {
        if (isMemberOf(rating)) {
            return ok(Rating.rename(rating, dynForm.bindFromRequest().get("name")));
        } else {
            return forbidden();
        }
    }

    public static Result delete(Long project) {
        if (isMemberOf(project)) {
            Rating.find.ref(project).delete();
            return ok();
        } else {
            return forbidden();
        }
    }

    private static boolean isMemberOf(Long rating) {
        return Rating.hasRatings(Context.current().request().username());
    }
}
