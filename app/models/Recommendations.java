package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recommendations", schema = "play_cassandra@cassandra_pu")
public class Recommendations implements Serializable{
	
	private static final long serialVersionUID = 33L;
	
	@Id
	private String email;
	
	@Column(name = "song-titles")
	public List<String> titles;
	
	public Recommendations() {
		
	}
	
	public  Recommendations(String usermail){
		this.email = usermail;
		this.titles = new ArrayList<String>();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the titles
	 */
	public List<String> getTitles() {
		return titles;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param titles the titles to set
	 */
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
	public void addRecSong(String songTitle){
		if(titles == null)
			this.titles = new ArrayList<String>();
		this.titles.add(songTitle);
	}

}