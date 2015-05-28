//package controllers;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//
//import models.PlayList;
//import models.Song;
//import models.RelationalUser;
//
//public class CollaborativeFiltering {
//
//	public static Vector<Vector<Integer>> userItem ;
//	public static Integer [][] coOcc ;
//	
//	
//	public CollaborativeFiltering(){
//		int userSize = RelationalUser.find.all().size();
//		int songSize = Song.find.all().size();
//		
//		userItem = new Vector<>(userSize);
//		coOcc = new Integer [songSize][songSize];
//		
//		System.out.println("User Size: " +userSize + " Songs Size: "+ songSize);
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
//		printCoocMatrix();
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
//		List<PlayList> stored = PlayList.findInvolving(username);
//		System.out.println("Ratings for me: "+ stored.size());
//		System.out.println("User id:"+RelationalUser.getUserID(username));
//		for (PlayList r : stored) {
//			for (Song s : r.songs){
//				int sid = Song.getSongID(s.title);
//				System.out.println("Song id for me"+ sid);
//				this.addRating( RelationalUser.getUserID(username), sid, 1);
//			}
//		}
//	}
//
//	public List<Song> recc2Song(String username) {
//		int userId = RelationalUser.getUserID(username);
//		Vector<Integer> tmp = this.getRec(userId);
//		
//		System.out.println("Recom vector: "+ tmp);
//
//		List<Song> newRec = new ArrayList<Song>();
//		for (int i = 0; i < tmp.size(); i++) {
//			if (tmp.get(i) > 0)
//				newRec.add(Song.findByID(i));
//		}
//		return newRec;
//
//	}
//	
//
//}
