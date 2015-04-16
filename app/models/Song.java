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
    public Integer id;
    public String title;
    public String artist;
    public Date releaseDate;
    public String link;
    
    
    public static Song create(Song song, String title, String artist, Date released, String link) {
    	song.title = title;
    	song.artist = artist;
    	song.releaseDate = released;
    	song.link = link;
    	song.save();
        return song;
    }
    
    public static Model.Finder<Long, Song> find = new Model.Finder<Long, Song>(Long.class, Song.class);

    public static List<Song> findAllSongs() {
    	return find.all();
    }
    
    public static Song findByTitle(String title){
    	return find.where().eq("title", title).findUnique();
    }
    
    public static Song findByID(int id){
    	return Song.find.all().get(id);
    }
    
    public static int getSongID(String title){
    	List<Song> l = Song.find.all();
    	for(int i =0 ; i < l.size(); i++){
    		if(l.get(i).title.equals(title))
    			return i;
    	}
    	return -1;
    }

}