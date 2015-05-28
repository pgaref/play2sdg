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
    
    
    public PlayList(String user, String fname) {
    	
    	this.id = UUID.randomUUID();
    	this.folder = fname;
    	this.usermail= user;
    	
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
    
    

//    public static Model.Finder<Long, Rating> find = new Finder<Long, Rating>(Long.class, Rating.class);
//
//    public static Rating create(RelationalUser u, String folder, Long Songid) {
//        Rating rating = new Rating(u.email, folder,Songid);
//        rating.saveManyToManyAssociations("songs");
//        rating.saveManyToManyAssociations("usermail");
//        return rating;
//    }
//
//    public static List<Rating> findInvolving(String useremail) {
//       // return find.where().eq("usermail.email", useremail).findList();
//       //return find.fetch("project").where().eq("done", false).eq("project.members.email", useremail).findList();
//    	
//    	RelationalUser current = RelationalUser.findbyEmail(useremail);
//    	System.out.println("got"+ useremail + " found"+ current.name);
//    	
//    	if(hasRatings(useremail))
//    		return find.where().eq("usermail", current.email).findList();
//    	else
//    		return new ArrayList<Rating>();
//    }
//
//    public static boolean hasRatings(String usermail) {
//    	
//    	return find.where().eq("usermail", usermail).findRowCount() > 0;
//    }
//
//    public static String rename(Long Id, String newName) {
//        Rating r = find.ref(Id);
//        r.folder = newName;
//        r.update();
//        return r.folder;
//    }

}