package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = "play_cassandra@cassandra_pu")
//create column family users with comparator=UTF8Type and default_validation_class=UTF8Type and key_validation_class=UTF8Type;
public class User2 {
	@Id
	private String userId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "city")
	private String city;

	//default constructor
	public User2(){
		
	}
	
	// Getters/ setters/ constructors go here
	public User2(String fname, String lname) {
		this.firstName = fname;
		this.lastName = lname;
	}
	
	public User2(String fname, String lname, String cname) {
		this.firstName = fname;
		this.lastName = lname;
		this.city = cname;
	}
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}