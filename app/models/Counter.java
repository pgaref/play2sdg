/**
 * Accessor Interface Implementing Datastax Object Mapping
 * Specific interface for Counter class
 * NOTE: Counter Objects cannot be set, just incremented and decremented
 * @author pgaref
 *
 */
package models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;


@Table(keyspace = "play_cassandra", name = "counters")
public class Counter{
	
	@PartitionKey
	@Column(name = "key")
	private String id;
	
	@Column(name = "counter")
	private long counter;
	
	public Counter() { 	this.counter=0; }
	
	public Counter(String id){
		this.id = id;
		this.counter=0;
	}
	
	/**
	 * @return the id
	 */
	public String getId()
	{
	    return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
	    this.id = id;
	}

	/**
	 * @return the counter
	 */
	public long getCounter()
	{
	    return counter;
	}

	/**
	 * @param counter
	 *  the counter to set
	 */
	public void setCounter(long counter)
	{
	    this.counter = counter;
	}
	
	
	public void incrementCounter(){
		this.counter++;
	}
	
	public void decrementCounter(){
		this.counter--;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public String toString(){
		return "\n--------------------------------------------------"
				+ "\nCounter: "+this.id + "\nValue:"+this.counter;
	}

}