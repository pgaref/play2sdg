package controllers;

import models.RelationalUser;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Login extends Controller {

    private final static Form<LoginModel> loginForm = new Form<LoginModel>(LoginModel.class);

    public static Result index() {
        return ok(views.html.login.render(loginForm));
    }

    public static class LoginModel {

        public String email;
        public String password;

        public String validate() {
            if (RelationalUser.authenticate(email, password) == null) {
                return "Invalid usermail or password";
            }
            return null;
        }

    }

    public static Result authenticate() {
        Form<LoginModel> form = loginForm.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(views.html.login.render(form));
        } else {
            session().clear();
            session("email", form.get().email);
            return redirect(routes.Application.index());
        }
    }
}
