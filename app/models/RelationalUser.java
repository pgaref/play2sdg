package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class RelationalUser extends Model {

    @Id
    public String email;
    public String name;
    public String password;

    public RelationalUser(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static RelationalUser authenticate(String email, String password) {
        return find.where().eq("email", email).eq("password", password).findUnique();
    }
    
    public static RelationalUser findbyEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static int getUserID(String email){
    	List<RelationalUser> l = RelationalUser.find.all();
    	for(int i =0 ; i < l.size(); i++){
    		if(l.get(i).email.equals(email))
    			return i;
    	}
    	return -1;
    }

    public static Finder<String, RelationalUser> find = new Finder<String, RelationalUser>(String.class, RelationalUser.class);
}