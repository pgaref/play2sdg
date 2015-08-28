package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name = "statseries", schema = "play_cassandra@cassandra_pu")
public class StatsTimeseries {
	
	@EmbeddedId
	private StatsCompoundKey key;
	
	@Column(name = "doubleVal")
	private Map<String, String> statsMap;
	
	public StatsTimeseries(){
		
	}
	
	public StatsTimeseries(String id){
		StatsCompoundKey k  = new StatsCompoundKey();
		this.key = k;
		this.key.setId(id);
		this.key.setTimestamp(new Date());
		this.statsMap = new HashMap<String, String>();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.key.getId();
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.key.setId(id);
	}

	/**
	 * @return the timestamp
	 */
	public java.util.Date getTimestamp() {
		return this.key.getTimestamp();
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(java.util.Date timestamp) {
		this.key.setTimestamp(timestamp);
	}

	/**
	 * @return the statsMap
	 */
	public Map<String, String> getStatsMap() {
		if(statsMap == null)
			statsMap = new HashMap<String, String>();
		return statsMap;
	}

	/**
	 * @param statsMap the statsMap to set
	 */
	public void setStatsMap(Map<String, String> statsMap) {
		this.statsMap = statsMap;
	}

	/**
	 * @return the key
	 */
	public StatsCompoundKey getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(StatsCompoundKey key) {
		this.key = key;
	}

	
	public String toString(){
		StringBuffer toret = new StringBuffer();
		for(String k :this.getStatsMap().keySet() )
			toret.append( "K: "+ k + " V: "+ this.getStatsMap().get(k) );
		
		return "D: "+ this.getTimestamp() +
				"ID: "+ this.getId() +
				"["+toret.toString()+"]";
	}

}
