package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class User extends Model {

    @Id
    public String email;
    public String name;
    public String password;
    
    public User(String email, String name, String password) {
      this.email = email;
      this.name = name;
      this.password = password;
    }

    public static Finder<String,User> find = new Finder<String,User>(String.class, User.class); 
    
    public static User authenticate(String email, String password) {
    	System.out.println("USers Row count: "+User.find.findRowCount());
    	List<User> list = User.find.all();
    	for(User tmp: list)
    		System.out.println("User Name: "+tmp.name+ " mail: " + tmp.email + " pass: "+tmp.password);
    	System.out.println("Looking for: " + email + " pass: " + password);
    	
    	System.out.println("Projects Row count: "+ Project.find.findRowCount());
    	System.out.println("Tasks Row count: "+ Task.find.findRowCount());
    	
    	
        return find.where().eq("email", email)
            .eq("password", password).findUnique();
    }
}