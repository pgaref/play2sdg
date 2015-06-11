package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "stats", schema = "play_cassandra@cassandra_pu")
public class Stats implements Serializable {
	
	private static final long serialVersionUID = 6L;
	
//	@SequenceGenerator(name = "stat_", allocationSize = 20, initialValue = 80)
//  @GeneratedValue(generator = "stat_", strategy = GenerationType.SEQUENCE)
	@Id
	private String id;
	
	@Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date timestamp;
	
	@Column(name = "doubleVal")
	private Map<String, Double> statsMap;

	
	public Stats(){
		
	}
	
	public Stats(String id){
		this.id = id;
		this.statsMap = new HashMap<String, Double>();
		this.timestamp = new Date();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the timestamp
	 */
	public java.util.Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(java.util.Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the statsMap
	 */
	public Map<String, Double> getStatsMap() {
		if(statsMap == null)
			statsMap = new HashMap<String, Double>();
		return statsMap;
	}

	/**
	 * @param statsMap the statsMap to set
	 */
	public void setStatsMap(Map<String, Double> statsMap) {
		this.statsMap = statsMap;
	}
	

}
