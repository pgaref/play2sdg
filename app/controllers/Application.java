package controllers;

import models.Project;
import models.Song;
import models.User;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Application extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(views.html.index.render(Project.findInvolving(request().username()), Song.findTodoInvolving(request().username()), User.find.byId(request().username())));

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
