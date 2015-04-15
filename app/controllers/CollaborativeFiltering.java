package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.Rating;
import models.Song;
import models.User;

public class CollaborativeFiltering {

	Vector<Vector<Integer>> userItem ;
	Integer [][] coOcc ;
	
	
	CollaborativeFiltering(){
		int userSize = User.find.all().size();
		int songSize = Song.find.all().size();
		
		userItem = new Vector<>(userSize);
		coOcc = new Integer [songSize][songSize];
		
		System.out.println("User Size: " +userSize + " Songs Size: "+ songSize);
		
		
		for(int i = 0; i < userSize; i++ ){
			userItem.add(new Vector<Integer>(songSize));
			for(int j =0; j < songSize; j++ ){
				userItem.get(i).add(0) ;
			}
		}
		
		for(int i = 0; i < songSize; i++ ){
			for(int j =0; j < songSize; j++ ){
				coOcc[i][j] = 0;
			}
		}
		
		System.out.println("userItem size: "+ userItem.size() +" X "+ userItem.get(0).size());
		System.out.println("coOcc size: "+ coOcc.length +" X "+ coOcc[0].length);
		
	}

	void addRating(int user, int item, int rating) {
		userItem.get(user).set(item, rating);
		
		Vector<Integer> userRow = userItem.get(user);
		
		for (int i = 0; i < userRow.size(); i++){
			if (userRow.get(i) > 0) {
				int count = coOcc[item][i];
				coOcc[item][i] = ++count;
				coOcc[i][item] = ++count;
			}
		}
	}

	Vector<Integer> getRec(int user) {
		Vector<Integer> userRow = userItem.get(user);
		//Vector<Integer> userRec = coOcc.multiply(userRow);
		Vector<Vector<Integer>> userRec = multiply(userRow, user);
		Vector<Integer> rec = merge(userRec);
		return rec;
	}
	
	/*
	 * TODO Validate behavior
	 */
	Vector<Vector<Integer>> multiply(Vector<Integer> userRow, int user){
		
		Vector<Vector<Integer>> userRec= new Vector<>(userItem.size());
		
		for(int i =0 ;i < coOcc.length; i++){
			userRec.add(new Vector<Integer>(userRow.size()));
			for(int j =0 ;j < coOcc[i].length; j++){
				userRec.get(i).add(0);
				int value = coOcc[i][j];
				if(i == user){
					value =  coOcc[i][j] * userRow.get(j);
				}
				userRec.get(i).set(j, value);
			}
		}
		return userRec;
	}

	Vector<Integer> merge(Vector<Vector<Integer>> allUserRec) {
		Vector<Integer> rec = new Vector<>(allUserRec.get(0).size());
		for (Vector<Integer> cur : allUserRec){
			
			for (int i = 0; i < allUserRec.get(0).size(); i++){
				rec.add(0);
				rec.set(i, cur.get(i) + rec.get(i) );
			}
		}
		return rec;
	}
	
	/**
	 * Helper Functions
	 * @param username
	 */
	
	public void loadUserRatings(String username) {
		List<Rating> stored = Rating.findInvolving(username);

		System.out.println("Ratings for me: "+ stored.size());
		System.out.println("User id:"+User.getUserID(username));
		for (Rating r : stored) {
			for (Song s : r.songs){
				System.out.println("Song id for me"+ s.id);
				this.addRating( User.getUserID(username), s.id, 1);
			}
		}
	}

	public List<Song> recc2Song(String username) {
		int userId = User.getUserID(username);
		Vector<Integer> tmp = this.getRec(userId);
		
		System.out.println("Recom vector: "+ tmp);

		List<Song> newRec = new ArrayList<Song>();
		for (int i = 0; i < tmp.size(); i++) {
			if (tmp.get(i) > 0)
				newRec.add(Song.find.ref((long) i));
		}
		return newRec;

	}
	

}
