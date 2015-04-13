package controllers;

import models.*;
import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;

public class Application extends Controller {

	// public static Result index() {
	// return ok(index.render("This is a demo play2SDG Web Application"));
	//
	// }

	// public static Result index() {
	// return ok(index.render( User.find.all() ));
	// }
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(views.html.index.render(Project.findInvolving(request().username()), Task.findTodoInvolving(request().username()), User.find.byId(request().username())));
    }

//	public static Result indexAll() {
//		return ok(index.render(Project.find.all(), Task.find.all()));
//	}
	
	public static Result logout() {
	    session().clear();
	    flash("success", "You've been logged out");
	    return redirect(routes.Login.index());
	}

}