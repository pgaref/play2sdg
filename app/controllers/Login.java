package controllers;

import java.util.List;

import models.User;
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
            if (Login.authenticate(email, password) == null) {
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
    
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
	
	public static User create(String email, String username, String password) {
		User newUser = new User(email, username, password);
		CassandraController.persist(newUser);
		return newUser;
	}
	
	public static User create(String email, String username, String password, String fname, String lname) {
		User newUser = new User(email, username, password, fname, lname);
		CassandraController.persist(newUser);
		return newUser;
	}
	
	public static User authenticate(String email, String password){
		User tmp = CassandraController.findbyEmail(email);
		
		if(tmp == null){
			System.out.println("User: " + email + " does not exist!!!");
			return null;
		}
		
		if(tmp.getPassword().compareTo(password) == 0)
			return tmp;
		else{
			System.out.println("User: " + email + " password does not match!!!");
			return null;
		}
	}
	
	public static User findUser(String email){
		return CassandraController.findbyEmail(email);
	}
	
	public static int getUserID(String email){
    	List<User> l = CassandraController.listAllUsers();
    	for(int i =0 ; i < l.size(); i++){
    		if(l.get(i).getEmail().equals(email))
    			return i;
    	}
    	return -1;
    }
    
}
