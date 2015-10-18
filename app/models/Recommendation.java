package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * Reccommendations must implement Serializable Interface 
 * to satisfy Spark RDD requirements
 * @author pg1712
 *
 */
@Table(keyspace = "play_cassandra", name = "recommendations")
public class Recommendation implements Serializable{
	
	@PartitionKey
	@Column(name = "email")
	public String email;
	
	@Column(name = "rec-map")
	public Map<String, Double> recMap;
	
	
	public Recommendation() {}
	
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
	 * @return the recMap
	 */
	public Map<String, Double> getRecMap() {
		return recMap;
	}

	/**
	 * @param recMap the recMap to set
	 */
	public void setRecMap(Map<String, Double> recMap) {
		this.recMap = recMap;
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
