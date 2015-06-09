package controllers;

import java.util.List;
import java.util.regex.Pattern;

import models.PlayList;
import models.Track;
import models.User;

/**
 * Testing Kundera JPA functionality
 * 
 * @author pg1712
 *
 */
public class CassandraJPA {
	public static void testCassandraUser() {
		User user = new User("pgaref@example.com", "pgaref", "secret",
				"Panagiotis", "Garefalakis");

		controllers.CassandraController.persist(user);
		System.out.println("Insert User Query finished");

		controllers.CassandraController.findbyEmail("pgaeref@example.com");
		controllers.CassandraController.findbyEmail("pgaref@example.com");
		System.out.println("Find User Query finished");

		controllers.CassandraController.listAllUsers();
		System.out.println("List all Users Query finished");
	}

	public static void testCassandraSong() {
		Track s = new Track("TRBIJYB128F14AE3266",
				"Contact Us (Live at ZDF Aufnahmezustand)", "Dillon",
				"1986-11-04");
		controllers.CassandraController.persist(s);

		Track t = new Track("TRBIJNK128F93093ECC",
				"Wolves (Kill them with Colour Remix)", "Bon Hiver",
				"2014-11-11 ");
		controllers.CassandraController.persist(t);

		System.out.println("Insert Song Query finished - Song Count= "
				+ controllers.CassandraController.getCounterValue("tracks"));

		Track tmp = controllers.CassandraController
				.findTrackbyTitle("Contact Us (Live at ZDF Aufnahmezustand)");
		System.out.println("Find song by Title result : " + tmp);

		controllers.CassandraController.listAllTracks();
		System.out.println("List all Songs Query finished");
	}

	public static void testCassandraPlayList() {
		PlayList pl = new PlayList("pgaref@example.com", "testPlay");
		Track tmp = controllers.CassandraController
				.findTrackbyTitle("Contact Us (Live at ZDF Aufnahmezustand)");
		pl.addRatingSong(tmp);

		System.out.println("\n ############## User PlayList count  BEFORE: "
				+ controllers.CassandraController
						.getUserPlayListCount("pgaref@example.com"));

		System.out.println("PlayList: " + pl);
		controllers.CassandraController.persist(pl);

		System.out.println("\n ############## User PlayList count  AFTER: "
				+ controllers.CassandraController
						.getUserPlayListCount("pgaref@example.com"));

		List<PlayList> l = controllers.CassandraController
				.getUserPlayLists("pgaref@example.com");
		for (PlayList pp : l) {
			System.out.println("pl name " + pp.getFolder() + " song size: "
					+ pp.getTracks().size());
			System.out.println("SONG: " + pp.getTracks());
			for (String s : pp.getTracks())
				System.out.println("SONGGGG: " + s);
		}
		System.out.println("\n ############## ALL PlayLists: "
				+ controllers.CassandraController.listAllPlaylists());

		// controllers.CassandraController.remove(pl);
	}

	public static void testPlayListLookup() {
		String trackTitle = "Love My Way";
		trackTitle = trackTitle.replaceAll(Pattern.quote("+"),
				Pattern.quote(" "));
		System.out.println("Looking for: " + trackTitle);
		Track found = CassandraController.findTrackbyTitle(trackTitle);

		System.out.println("Got track " + found);

	}

	public static void main(String[] args) {

//		testCassandraUser();
//		testCassandraSong();
//		testCassandraPlayList();

		testPlayListLookup();

	}

}