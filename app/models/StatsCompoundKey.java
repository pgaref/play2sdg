package models;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

@Embeddable
public class StatsCompoundKey
{
	//partition key
    @Column private String id;            
    //cluster/ remaining key
    @Column private java.util.Date timestamp;

    /**
     * 
     */
    public StatsCompoundKey(){}

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
    
}