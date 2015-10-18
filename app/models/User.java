package models;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;


@Table(keyspace="play_cassandra", name = "users")
public class User{
	
	@PartitionKey
	@Column(name = "key")
	public String email;

	@Column(name = "username")
	public String username;

	@Column(name = "password")
	public String password;

	@Column(name = "firstname")
	public String firstname;

	@Column(name = "lastname")
	public String lastname;	
	
	//default constructor
	public User(){}
	
	public User(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User(String email, String username, String password, String fname, String lname) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.firstname = fname;
		this.lastname = lname;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the firstname
	 */
	public String getFistname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFistname(String fistname) {
		this.firstname = fistname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this.email);
	}
	
	public String toString() {
		return "\n--------------------------------------------------"
				+ "\n\t userEmail: " + this.getEmail() + "\nusername: "+ this.getUsername()
				+ "\n\t firstName:" + this.getFistname() + "\nlastName: " + this.getLastname()
				+ "\n\t pass: " + this.password;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	
}