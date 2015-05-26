package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.mvc.Security;
import controllers.Secured;

@Security.Authenticated(Secured.class)
@Entity
public class Rating extends Model {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    public String folder;
    public String usermail;
    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<Song> songs = new ArrayList<Song>();

    
    public Rating(String u, String folder, Long Songid) {
        this.usermail = u;
        this.folder = folder;
        this.songs.add(Song.find.ref(Songid));
        this.save();
    }

    public static Model.Finder<Long, Rating> find = new Finder<Long, Rating>(Long.class, Rating.class);

    public static Rating create(RelationalUser u, String folder, Long Songid) {
        Rating rating = new Rating(u.email, folder,Songid);
        rating.saveManyToManyAssociations("songs");
        rating.saveManyToManyAssociations("usermail");
        return rating;
    }

    public static List<Rating> findInvolving(String useremail) {
       // return find.where().eq("usermail.email", useremail).findList();
       //return find.fetch("project").where().eq("done", false).eq("project.members.email", useremail).findList();
    	
    	RelationalUser current = RelationalUser.findbyEmail(useremail);
    	System.out.println("got"+ useremail + " found"+ current.name);
    	
    	if(hasRatings(useremail))
    		return find.where().eq("usermail", current.email).findList();
    	else
    		return new ArrayList<Rating>();
    }

    public static boolean hasRatings(String usermail) {
    	
    	return find.where().eq("usermail", usermail).findRowCount() > 0;
    }

    public static String rename(Long Id, String newName) {
        Rating r = find.ref(Id);
        r.folder = newName;
        r.update();
        return r.folder;
    }

}