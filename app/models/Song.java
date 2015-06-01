package models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;


@Entity
@Table(name = "songs", schema = "play_cassandra@cassandra_pu")
//Secondary index
//@IndexCollection(columns = { @Index(name = "title") })
public class Song implements Serializable{

    /**
	 * Default id
	 */
	private static final long serialVersionUID = 1L;

	
   // public UUID id;
    
    @Id
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
    	//this.id = UUID.randomUUID();
    	this.title = title;
    	this.artist = artist;
    	this.releaseDate = Song.convertDate(releaseDate);
    	this.link = link;
    }
    /**
     * Simple valid date converter
     * @param dateInString
     * @return
     */

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
	
	
	public String toString(){
		return "\n--------------------------------------------------"
				+ "\nsongTitle: " + this.title
				+ "\nreleaseDate: "+ this.releaseDate
				+ "\nLink: "+ this.link;
	}
	
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
	
	public static Song create(String title, String artist, String released, String link){
		Song newSong = new Song(title, artist, released, link);
		controllers.CassandraController.persist(newSong);
		return newSong;
	}

	public static Song findByTitle(String title) {
		return controllers.CassandraController.findSongbyTitle(title);
	}
	
	public static Song findByID(int id){
		List<Song> l = Song.findAllSongs();
		if(id > l.size()){
			System.out.println("ID Cannot be greater than size!!");
			return null;
		}
		return l.get(id);
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


}