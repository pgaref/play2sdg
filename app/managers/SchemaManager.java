package managers;

import com.datastax.driver.core.Session;

public class SchemaManager {
	
	private static String keyspace;
	private static Session session;
	private static int replication_factor;
	
	public SchemaManager(Session s){
		keyspace="play_cassandra";
		session=s;
		replication_factor=1;
	}
	
	public SchemaManager(String kspace, int rf, Session s){
		keyspace = kspace;
		session=s;
		replication_factor=rf;
	}
	
	public void createKeyspace(){
		session.execute("CREATE KEYSPACE IF NOT EXISTS "+SchemaManager.keyspace+" WITH replication " + 
				 "= {'class':'SimpleStrategy', 'replication_factor':"+SchemaManager.replication_factor+"};");
	}
	
	public void alterKeyspace(){
		session.execute("ALTER KEYSPACE "+SchemaManager.keyspace+" WITH REPLICATION  "+
				"=   { 'class' : 'SimpleStrategy', 'replication_factor' :" +SchemaManager.replication_factor+"};");
	}
	
	public void createCounterTable(){
		session.execute( "CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".counters (" +
					"key text PRIMARY KEY,"+
			    	"counter counter" +
				");");
	}
	public void createPlayListsTable(){
		session.execute("CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".playlists ("+
			    	"id uuid PRIMARY KEY,"+
			    	"folder text,"+
			    	"tracks list<text>,"+
			    	"usermail text"+
				");");
		// Secondary Index
		session.execute("CREATE INDEX IF NOT EXISTS ON "+SchemaManager.keyspace+".playlists (usermail);");
	}
	
	public void createUserTable(){
		session.execute("CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".users ("+
					"key text PRIMARY KEY,"+
					"firstname text,"+
					"lastname text,"+
					"password text,"+
					"username text"+
				");");
	}
	
	public void createTracksTable(){
		session.execute("CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".tracks ("+
				 	"key text PRIMARY KEY,"+
    				"artist text,"+
    				"releaseDate timestamp,"+
    				"title text,"+
    				"tags list<text>,"+
    				"similars list<text>"+
				");");
		// Secondary Index
		session.execute("CREATE INDEX IF NOT EXISTS ON "+SchemaManager.keyspace+".tracks (title);");	
	}
	
	public void createRecommendationsTable(){
		session.execute("CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".recommendations ("+
					"email text PRIMARY KEY,"+
					"recmap map<text, double>"+
				");");
	}
	
	public void createStatTimeseriesTable(){
		session.execute("CREATE TABLE IF NOT EXISTS "+SchemaManager.keyspace+".statseries (" +
				    "id text," +
				    "timestamp timestamp," +
				    "metricsmap map<text, text>," +
				    "PRIMARY KEY (id, timestamp)" +
				");");
	}
	
	public void createSchema() throws Exception {
		if(SchemaManager.session.isClosed()){
			throw new Exception("SchemaManager cannot proceed with closed Session!");
		}
		
		createKeyspace();
		createCounterTable();
		createPlayListsTable();
		createUserTable();
		createTracksTable();
		createRecommendationsTable();
		createStatTimeseriesTable();
		
	}
	
	public void dropSchema() {
		session.execute("DROP KEYSPACE "+SchemaManager.keyspace+";");
	}

	/**
	 * @return the keyspace
	 */
	public static String getKeyspace() {
		return keyspace;
	}

	/**
	 * @param keyspace the keyspace to set
	 */
	public static void setKeyspace(String keyspace) {
		SchemaManager.keyspace = keyspace;
	}

	/**
	 * @return the session
	 */
	public static Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public static void setSession(Session session) {
		SchemaManager.session = session;
	}

	public static int getReplication_factor() {
		return replication_factor;
	}

	public static void setReplication_factor(int replication_factor) {
		SchemaManager.replication_factor = replication_factor;
	}
	

}
