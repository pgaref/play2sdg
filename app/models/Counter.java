package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "counters", schema = "play_cassandra@cassandra_pu")
public class Counter {

	@Id
	private String id;
	
	
	@Column
	private int counter;
	
	public Counter() {
		// TODO Auto-generated constructor stub
		this.counter=0;
	}
	
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
	public int getCounter()
	{
	    return counter;
	}

	/**
	 * @param counter
	 *  the counter to set
	 */
	public void setCounter(int counter)
	{
	    this.counter = counter;
	}
	
	
	public void incrementCounter(){
		this.counter++;
	}
	
	public void decrementCounter(){
		this.counter--;
	}

}
