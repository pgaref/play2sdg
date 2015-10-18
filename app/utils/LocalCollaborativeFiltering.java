package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import controllers.PlayListController;
import models.PlayList;
import models.Track;

public class LocalCollaborativeFiltering {
//
//	public static Vector<Vector<Integer>> userItem ;
//	public static Integer [][] coOcc ;
//	
//	
//	public LocalCollaborativeFiltering(){
//		int userSize = controllers.CassandraController.listAllUsers().size();;
//		int songSize = controllers.CassandraController.getCounterValue("tracks");
//		
//		userItem = new Vector<>(userSize);
//		coOcc = new Integer [songSize][songSize];
//		
//		System.out.println("\n ### CF Init ### Users Num: " +userSize + " Songs Num: "+ songSize);
//		
//		
//		for(int i = 0; i < userSize; i++ ){
//			userItem.add(new Vector<Integer>(songSize));
//			for(int j =0; j < songSize; j++ ){
//				userItem.get(i).add(0) ;
//			}
//		}
//		
//		for(int i = 0; i < songSize; i++ ){
//			for(int j =0; j < songSize; j++ ){
//				coOcc[i][j] = 0;
//			}
//		}
//		
//		System.out.println("userItem size: "+ userItem.size() +" X "+ userItem.get(0).size());
//		System.out.println("coOcc size: "+ coOcc.length +" X "+ coOcc[0].length);
//		
//	}
//
//	void addRating(int user, int item, int rating) {
//		userItem.get(user).set(item, rating);
//		
//		Vector<Integer> userRow = userItem.get(user);
//		
//		for (int i = 0; i < userRow.size(); i++){
//			if (userRow.get(i) > 0) {
//				int count = coOcc[item][i];
//				coOcc[item][i] = count +1;
//				coOcc[i][item] = count +1;
//			}
//		}
//	}
//
//	Vector<Integer> getRec(int user) {
//		
//		Vector<Integer> userRow = userItem.get(user);
//		Vector<Integer> userRec = multiplicar(userRow);
////		 Only for distributed behavior 
////		Vector<Integer> rec = merge(userRec);
//		return userRec;
//	}
//	
//	
//	/*
//	 * Tested functionality -> working
//	 */
//	public Vector<Integer> multiplicar(Vector<Integer> userRow) {
//
//		int aRows = 1;
//		int aColumns = userRow.size();
//		int bRows = coOcc.length;
//		int bColumns = coOcc[0].length;
//
//		if (aColumns != bRows) {
//			throw new IllegalArgumentException("coOcc: Rows: " + bRows
//					+ " did not match useRow:Columns " + aColumns + ".");
//		}
//		/*
//		 * Initialize new Vector
//		 */
//		Vector<Integer> C = new Vector<Integer>(bColumns);
//		for (int i = 0; i < aRows; i++) {
//			for (int j = 0; j < bColumns; j++) {
//				C.add(0);
//			}
//		}
//
//		for (int i = 0; i < aRows; i++) { // aRow
//			for (int j = 0; j < bColumns; j++) { // bColumn
//				for (int k = 0; k < aColumns; k++) { // aColumn
//					int tmp = C.get(j + i) + (userRow.get(k + i) * coOcc[k][j]);
//					C.set(j + i, tmp);
//					// C[i][j] += A[i][k] * B[k][j]
//				}
//			}
//		}
//
//		System.out.println("Old userRow" + userRow.toString());
//		System.out.println("New userRow: " + C.toString());
//		//printCoocMatrix();
//
//		return C;
//	}
//	
//	
//	public void printCoocMatrix(){
//		
//		System.out.println("Coocurence Matrix: ");
//		for(int i = 0; i < coOcc.length; i++){
//			for(int j=0; j < coOcc[0].length; j++){
//				System.out.print(coOcc[i][j]);
//			}
//			System.out.println();
//		}
//		
//	}
//	
//	/**
//	 * Usefull only when running at SEEP??
//	 * @param allUserRec
//	 * @return
//	 */
//	Vector<Integer> merge(Vector<Vector<Integer>> allUserRec) {
//		Vector<Integer> rec = new Vector<>(allUserRec.get(0).size());
//		for (Vector<Integer> cur : allUserRec){
//			
//			for (int i = 0; i < allUserRec.get(0).size(); i++){
//				rec.add(0);
//				rec.set(i, cur.get(i) + rec.get(i) );
//			}
//		}
//		return rec;
//	}
//	
//	/**
//	 * Helper Functions
//	 * @param username
//	 */
//	
//	public void loadUserRatings(String username) {
//		
//		List<PlayList> stored = controllers.CassandraController.getUserPlayLists(username);
//		System.out.println("Ratings for me: "+ stored.size());
//		int uid = controllers.CassandraController.getUserID(username);
//		System.out.println("User id:"+uid);
//		for (PlayList r : stored) {
//			for (String s : r.tracks){
//				int sid = PlayListController.getSongID(s);
//				System.out.println("Track id for me"+ sid);
//				this.addRating( uid, sid, 1);
//			}
//		}
//	}
//
//	public List<Track> recc2Song(String username) {
//		int userId = controllers.CassandraController.getUserID(username);
//		Vector<Integer> tmp = this.getRec(userId);
//		
//		System.out.println("Recom vector: "+ tmp);
//
//		List<Track> newRec = new ArrayList<Track>();
//		for (int i = 0; i < tmp.size(); i++) {
//			if (tmp.get(i) > 0)
//				newRec.add(PlayListController.findByID(i));
//		}
//		return newRec;
//
//	}
	

}
