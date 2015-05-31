package util;

import java.util.List;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;

public class NestApi_Test {

	public NestApi_Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI("H8PUFR4HPWFVVEQLW");
        List<Artist> artists = echoNest.searchArtists("Weezer");

        if (artists.size() > 0) {
            Artist weezer = artists.get(0);
            System.out.println("Similar artists for " + weezer.getName());
            for (Artist simArtist : weezer.getSimilar(10)) {
                System.out.println("   " + simArtist.getName());
            }
        }
    }
}
