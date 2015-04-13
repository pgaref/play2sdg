package controllers;

import models.*;
import play.*;
import play.data.*;
import play.mvc.*;
import static play.data.Form.*;
import views.html.*;

public class Application extends Controller {
  
//  public static Result index() {
//    return ok(index.render("This is a demo play2SDG Web Application"));
//    
//  }
	
//	public static Result index() {
//        return ok(index.render( User.find.all() )); 
//    }

	 public static Result index() {
	        return ok(index.render( 
	            Project.find.all(),
	            Task.find.all()
	        )); 
	    }

  
}