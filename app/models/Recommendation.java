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
	
	@Column(name = "recmap")
	public Map<String, Double> recmap;
	
	
	public Recommendation() {}
	
	public  Recommendation(String usermail){
		this.email = usermail;
		this.recmap = new HashMap<String, Double>();
	}
	
	public  Recommendation(String usermail, HashMap<String, Double> recmap){
		this.email = usermail;
		this.recmap = recmap;
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
	 * @return the recmap
	 */
	public Map<String, Double> getRecmap() {
		return recmap;
	}

	/**
	 * @param recmap the recmap to set
	 */
	public void setRecmap(Map<String, Double> recmap) {
		this.recmap = recmap;
	}
	
	/**
	 * Add a new Recommendation for a user
	 * @param track
	 * @param score
	 */
	public void addRecommendation(String track , double score){
		if(this.recmap == null)
			this.recmap = new HashMap<String, Double>();
		this.recmap.put(track, score);
	}

	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("\n--------------------------------------------------");
		s.append("\n User: " + this.email);
		for(String key : this.recmap.keySet()){
			s.append("\n -> Rec Song: "+ key+ " Score: "+ recmap.get(key));
		}
		return s.toString();
		
	}

}
