package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;


@Table(keyspace="play_cassandra", name = "tracks")
public class Track{

	@PartitionKey
	@Column(name = "key")
	public String track_id;
	
	@Column(name = "title")
	public String title;
	
	@Column(name = "artist")
	public String artist;
	
	@Column(name = "releaseDate")
	public 	Date releaseDate;
	
	@Column(name = "tags")
	public List<String> tags;
	
	@Column(name = "similars")
	public List<String> similars;
    
    public Track(){}
    
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
	
	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		if(tags == null)
			tags = new LinkedList<String>();
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	/**
	 * @return the similars
	 */
	public List<String> getSimilars() {
		if(similars == null)
			similars = new LinkedList<String>();
		return similars;
	}

	/**
	 * @param similars the similars to set
	 */
	public void setSimilars(List<String> similars) {
		this.similars = similars;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(this.track_id);
	}
	
	@Override
	public String toString(){
		return "\n-------------------- Track -----------------------------"
				+ "\n\t track_id: " + this.track_id
				+ "\n\t artist: "+ this.artist
				+ "\n\t title: " + this.title
				+ "\n\t releaseDate: "+ this.releaseDate
				;
	}

}