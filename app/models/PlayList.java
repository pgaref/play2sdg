package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;

//@Security.Authenticated(Secured.class)
@Entity
@Table(name = "playlists", schema = "play_cassandra@cassandra_pu")
//Secondary index
@IndexCollection(columns = { @Index(name = "usermail") })
public class PlayList{
	
    @Id
    public UUID id;
    
    @Column(name = "folder")
    public String folder;
    
    @Column(name = "usermail")
    public String usermail;
    
    
    @Column(name = "song-titles")
    public List<String> titles = new ArrayList<String>();
    
    public PlayList(){}
    
    
    public PlayList(String usermail, String fname) {
    	
    	this.id = UUID.randomUUID();
    	this.folder = fname;
    	this.usermail= usermail;
    	
    }
    
    public void addRatingSong(Song s){
    	this.titles.add(s.getTitle());
    }

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the usermail
	 */
	public String getUsermail() {
		return usermail;
	}

	/**
	 * @param usermail the usermail to set
	 */
	public void setUsermail(String usermail) {
		this.usermail = usermail;
	}

	/**
	 * @return the songs
	 */
	public List<String> getSongs() {
		return titles;
	}

	/**
	 * @param songs the songs to set
	 */
	public void setSongs(List<String> songs) {
		this.titles = songs;
	}
	
	public String toString(){
		return "\n--------------------------------------------------"
				+ "\n Playlist: "+this.folder
				+"\n usermail: "+ this.usermail 
				+"\n Songs: "+ this.titles.toString();
	}
	
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
    
	//PlayList with one song
    public static PlayList create(User  u, String folder, String songTitle){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	Song listsong = controllers.CassandraController.findSongbyTitle(songTitle);
    	pl.addRatingSong(listsong);
    	controllers.CassandraController.persist(pl);
    	return pl;
    }
    // Empty PlayList
    public static PlayList create(User  u, String folder){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	controllers.CassandraController.persist(pl);
    	return pl;
    }
    
    public static void remove(UUID id){
    	PlayList p = controllers.CassandraController.getByID(id);
    	controllers.CassandraController.remove(p);
    }
    
    public static List<PlayList> findExisting(String usermail){
    	List<PlayList> pl = controllers.CassandraController.getUserPlayLists(usermail);
    	if(pl == null){
    		System.out.println("User: " + usermail + " has no playlists!!!");
    		return null;
    	}
    	return pl;
    }
    
    public static boolean hasPlayLists(String usermail){
    	List<PlayList> pl = controllers.CassandraController.getUserPlayLists(usermail);
    	
    	if(pl == null)
    		return false;
    	
    	return (pl.size() > 0);
    }
    
    public static void addSong(String usermail, Song song){
    	PlayList p = controllers.CassandraController.getUserPlayLists(usermail).get(0);
    	p.addRatingSong(song);
    	controllers.CassandraController.persist(p);
    }
    
    public static void playListRename(UUID id, String newname){
    	PlayList p = controllers.CassandraController.getByID(id);
    	controllers.CassandraController.playlistRename(p, newname);
    }

}