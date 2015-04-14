package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class Song extends Model {

    @Id
    public Long id;
    public String title;
    public String artist;
    public Date releaseDate;
    public String link;
    
    
    
    public static Model.Finder<Long, Song> find = new Model.Finder(Long.class, Song.class);

    public static List<Song> findTodoInvolving(String useremail) {
    	return find.all();
        //return find.fetch("project").where().eq("done", false).eq("project.members.email", useremail).findList();
    }

    public static Song create(Song song, String title, String artist, Date released, String link) {
    	song.title = title;
    	song.artist = artist;
    	song.releaseDate = released;
    	song.link = link;
    	song.save();
        return song;
    }
}