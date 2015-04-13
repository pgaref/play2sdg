package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import static play.data.Form.*;
import play.mvc.Result;

public class Login extends Controller {

    private final static Form<Login.LoginModel> loginForm = new Form<Login.LoginModel>(Login.LoginModel.class);

    public static Result index() {
        return ok(views.html.login.render(loginForm));
    }

    public static class LoginModel {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result authenticate() {
        Form<Login.LoginModel> form = loginForm.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(views.html.login.render(form));
        } else {
            session().clear();
            session("email", form.get().email);
            return redirect(routes.Application.index());
        }
    }
}
