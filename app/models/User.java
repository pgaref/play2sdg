package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = "play_cassandra@cassandra_pu")
//create column family users with comparator=UTF8Type and default_validation_class=UTF8Type and key_validation_class=UTF8Type;
public class User {
	@Id
	private String email;

	@Column(name = "username")
	public String username;

	@Column(name = "password")
	private String password;

	@Column(name = "first_name")
	private String fistname;

	@Column(name = "last_name")
	private String lastname;	
	
	//default constructor
	public User(){
	}
	
	public User(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User(String email, String username, String password, String fname, String lname) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.fistname = fname;
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
	 * @return the fistname
	 */
	public String getFistname() {
		return fistname;
	}

	/**
	 * @param fistname the fistname to set
	 */
	public void setFistname(String fistname) {
		this.fistname = fistname;
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
	
	public String toString() {
		return "\n--------------------------------------------------"
				+ "\nuserEmail: " + this.getEmail() + "\nusername: "+ this.getUsername()
				+ "\nfirstName:" + this.getFistname() + "\nlastName: " + this.getLastname()
				+ "\npass: " + this.password;
	}
	
	/*
	 * JPA Connector functionality for Easy accessibility
	 */
	
	public static User create(String email, String username, String password) {
		User newUser = new User(email, username, password);
		controllers.CassandraController.persist(newUser);
		return newUser;
	}
	
	public static User create(String email, String username, String password, String fname, String lname) {
		User newUser = new User(email, username, password, fname, lname);
		controllers.CassandraController.persist(newUser);
		return newUser;
	}
	
	public static User authenticate(String email, String password){
		User tmp = controllers.CassandraController.findbyEmail(email);
		
		if(tmp == null){
			System.out.println("User: " + email + " does not exist!!!");
			return null;
		}
		
		if(tmp.getPassword().compareTo(password) == 0)
			return tmp;
		else{
			System.out.println("User: " + email + " password does not match!!!");
			return null;
		}
	}
	
	public static User findUser(String email){
		return controllers.CassandraController.findbyEmail(email);
	}
	
	public static int getUserID(String email){
    	List<User> l = controllers.CassandraController.listAllUsers();
    	for(int i =0 ; i < l.size(); i++){
    		if(l.get(i).email.equals(email))
    			return i;
    	}
    	return -1;
    }

	
}