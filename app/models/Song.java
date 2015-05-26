package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;


@Entity
@Table(name = "songs", schema = "play_cassandra@cassandra_pu")
@IndexCollection(columns = { @Index(name = "title") })
public class Song {

    @Id
    public UUID id;
    
    @Column(name = "title")
    public String title;
    
    @Column(name = "artist")
    public String artist;
    
    @Column(name = "releaseDate")
    public Date releaseDate;
    
    @Column(name = "link")
    public String link;
    
    //default constructor needed for some reason!
    public Song(){}
    
    public Song(String title, String artist, String releaseDate, String link){
    	this.title = title;
    	this.artist = artist;
    	this.releaseDate = Song.convertDate(releaseDate);
    	this.link = link;
    	this.id = UUID.randomUUID();
    }


	public static Date convertDate(String dateInString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date date = null;
		try {
			date = formatter.parse(dateInString);
			// System.out.println(date);
			// System.out.println(formatter.format(date));
		} catch (ParseException e) {
			System.err.println("Error parsing Song date! ");
			e.printStackTrace();
		}

		return date;

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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}


	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}


	/**
	 * @return the releaseDate
	 */
	public Date getReleaseDate() {
		return releaseDate;
	}


	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}


	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}


	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	public static Song findByID(String id) {
		return controllers.CassandraController.findbySongID(id);
	}

	public static Song findByTitle(String title) {

	}

	public static List<Song> findAllSongs() {
		return controllers.CassandraController.listAllSongs();
	}
	
	
	public static int getSongID(String title) {
		List<Song> l = Song.findAllSongs();
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i).title.equals(title))
				return i;
		}
		return -1;
	}
	
	
	public String toString(){
		return "\n--------------------------------------------------"
				+ "\nsongTitle: " + this.title
				+ "\nsongID: "+ this.id
				+ "\nreleaseDate: "+ this.releaseDate
				+ "\nLink: "+ this.link;
	}
    
    
    
//    public static Song create(Song song, String title, String artist, Date released, String link) {
//    	song.title = title;
//    	song.artist = artist;
//    	song.releaseDate = released;
//    	song.link = link;
//    	t
//    	song.save();
//        return song;
//    }
//    
//    public static Model.Finder<Long, Song> find = new Model.Finder<Long, Song>(Long.class, Song.class);
//
//    public static List<Song> findAllSongs() {
//    	return find.all();
//    }
//    
//    public static Song findByTitle(String title){
//    	return find.where().eq("title", title).findUnique();
//    }
//    
//    public static Song findByID(int id){
//    	return Song.find.all().get(id);
//    }
//    
//    public static int getSongID(String title){
//    	List<Song> l = Song.find.all();
//    	for(int i =0 ; i < l.size(); i++){
//    		if(l.get(i).title.equals(title))
//    			return i;
//    	}
//    	return -1;
//    }

}