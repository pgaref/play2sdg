package models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;

@Entity
@Table(name = "tracks", schema = "play_cassandra@cassandra_pu")
//Secondary index
@IndexCollection(columns = { @Index(name = "title") })
public class Track implements Serializable{


	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "key")
	public String track_id;
	
	@Column(name = "title")
	public String title;
	
	@Column(name = "artist")
	public String artist;
	
	@Column(name = "releaseDate")
	public 	Date releaseDate;
    
    public Track(){
    	
    }
    
    public Track(String id, String title, String artist, String releaseDate){
    	
    	this.track_id = id;
    	this.title = title;
    	this.artist = artist;
    	this.releaseDate = Track.convertDate(releaseDate);
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
			System.err.println("Error parsing Track date! ");
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
	 * @return the track_id
	 */
	public String getTrack_id() {
		return track_id;
	}

	/**
	 * @param track_id the track_id to set
	 */
	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}
	
	public String toString(){
		return "\n-------------------- Track -----------------------------"
				+ "\n track_id: " + this.track_id
				+ "\n artist: "+ this.artist
				+ "\n title: " + this.title
				+ "\n releaseDate: "+ this.releaseDate
				;
	}

}