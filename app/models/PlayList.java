package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    
    
    @Column(name = "songs")
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name="songs")
    public Set<Song> songs = new HashSet<Song>();
    
    public PlayList(){}
    
    
    public PlayList(String usermail, String fname) {
    	
    	this.id = UUID.randomUUID();
    	this.folder = fname;
    	this.usermail= usermail;
    	
    }
    
    public void addRatingSong(Song s){
    	this.songs.add(s);
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
	public Set<Song> getSongs() {
		return songs;
	}

	/**
	 * @param songs the songs to set
	 */
	public void setSongs(Set<Song> songs) {
		this.songs = songs;
	}
	
	public String toString(){
		return "\n--------------------------------------------------"
				+ "\n Playlist: "+this.folder
				+"\n usermail: "+ this.usermail 
				+"\n Songs: "+ this.songs.toString();
	}
	
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
    
    public static PlayList create(User  u, String folder, UUID songid){
    	PlayList pl = new PlayList(u.getEmail(), folder);
    	Song listsong = controllers.CassandraController.findbySongID(songid);
    	pl.addRatingSong(listsong);
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
    
    public static void playListRename(UUID id, String newname){
    	PlayList p = controllers.CassandraController.getByID(id);
    	controllers.CassandraController.playlistRename(p, newname);
    }

}