package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;


@Table(keyspace="play_cassandra", name = "statseries")
public class StatsTimeseries {
	
	//partition key
	@PartitionKey
	@Column(name = "id")
    private String id;            
    //cluster/ remaining key
    @ClusteringColumn
    @Column(name = "timestamp")
	private java.util.Date timestamp;
	
	@Column(name = "metrics-map")
	private Map<String, String> metricsMap;
	
	public StatsTimeseries(){}
	
	public StatsTimeseries(String id){
		this.id = id;
		this.timestamp = new Date();
		this.metricsMap = new HashMap<String, String>();
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
	 * @return the metricsMap
	 */
	public Map<String, String> getMetricsMap() {
		return metricsMap;
	}

	/**
	 * @param metricsMap the metricsMap to set
	 */
	public void setMetricsMap(Map<String, String> metricsMap) {
		this.metricsMap = metricsMap;
	}

	
	public String toString(){
		StringBuffer toret = new StringBuffer();
		for(String k :this.getMetricsMap().keySet() )
			toret.append( "K: "+ k + " V: "+ this.getMetricsMap().get(k) );
		
		return "D: "+ this.getTimestamp() +
				"ID: "+ this.getId() +
				"["+toret.toString()+"]";
	}



}
