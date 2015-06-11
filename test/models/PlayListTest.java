package models;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.PlayListController;
import base.AbstractDBApplicationTest;

public class PlayListTest{

	public void create(){
		controllers.CassandraController.persist(new PlayList("pgaref@example.com", "whatever"));
	}
	
	
	public void fillUp(){
		Track tmp=  PlayListController.findByTitle("Contact Us (Live at ZDF Aufnahmezustand)");
		List<PlayList> l = controllers.CassandraController.getUserPlayLists("pgaref@example.com");
    	for(PlayList pp : l ){
    		pp.addRatingSong(tmp);
    		controllers.CassandraController.persist(pp);
    	}
	}
    public void findProjectsInvolving() {
    	List<PlayList> l = controllers.CassandraController.getUserPlayLists("pgaref@example.com");
    	for(PlayList pp : l ){
    		System.out.println("pl name "+ pp.getFolder() + " song size: "+ pp.getTracks().size());
    		for(String s :pp.getTracks() )
    			System.out.println("SONGGGG: "+ s);	
    	}
    }
    
    public static void main(String[] args) {
		PlayListTest t = new PlayListTest();
		t.create();
		t.fillUp();
		t.findProjectsInvolving();
	}
}
