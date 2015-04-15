package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.Song;
import models.User;

public class CollaborativeFiltering {

	Vector<Vector<Integer>> userItem ;
	Integer [][] coOcc ;
	
	
	CollaborativeFiltering(){
		List<User> users = User.find.all();
		List<Song> songs = Song.find.all();
		
		userItem = new Vector(users.size());
		coOcc = new Integer [users.size()][songs.size()];
		
		System.out.println("saads" +users.size() );
		for(int i = 0; i < users.size(); i++ ){
			userItem.set(i, new Vector<Integer>(songs.size()));
			for(int j =0; j < songs.size(); j++ ){
				userItem.get(i).set(j, 0) ;
			}
		}
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
	 * TODO CHECK Correctness
	 */
	Vector<Vector<Integer>> multiply(Vector<Integer> userRow, int user){
		
		Vector<Vector<Integer>> userRec = new Vector<Vector<Integer>>(userRow.size());
		
		for(int i =0 ;i < coOcc.length; i++){
			for(int j =0 ;j < coOcc[i].length; i++){
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
		Vector<Integer> rec = new Vector<Integer>(allUserRec.get(0).size());
		for (Vector<Integer> cur : allUserRec){
			for (int i = 0; i < allUserRec.get(0).size(); i++)
				rec.set(i, cur.get(i) + rec.get(i) );
		}
		return rec;
	}
	
	
	

}
