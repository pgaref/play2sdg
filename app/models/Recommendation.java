package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recommendations", schema = "play_cassandra@cassandra_pu")
public class Recommendation implements Serializable{
	
	private static final long serialVersionUID = 3L;
	
	@Id
	public String email;
	
	@Column(name = "rec-list")
	public Map<String, Double> recMap;
	
	
	public Recommendation() {
		
	}
	
	public  Recommendation(String usermail){
		this.email = usermail;
		this.recMap = new HashMap<String, Double>();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the recList
	 */
	public Map<String, Double> getRecList() {
		return recMap;
	}

	/**
	 * @param recList the recList to set
	 */
	public void setRecList(Map<String, Double> recList) {
		this.recMap = recList;
	}
	
	/**
	 * Add a new Recommendation for a user
	 * @param track
	 * @param score
	 */
	public void addRecommendation(String track , double score){
		if(this.recMap == null)
			this.recMap = new HashMap<String, Double>();
		this.recMap.put(track, score);
	}

	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("\n--------------------------------------------------");
		s.append("\n User: " + this.email);
		for(String key : this.recMap.keySet()){
			s.append("\n -> Rec Song: "+ key+ " Score: "+ recMap.get(key));
		}
		return s.toString();
		
	}

}
