package managers;

import java.util.ArrayList;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

import play.Logger;

public class ClusterManager {

	private Cluster cluster;
	private Session session;
	private int repl_factor;
	private String keyspace;
	private static SchemaManager sm = null;
	private ArrayList<String> cassandraNodes;
	
	public ClusterManager(String kspace, int rf, String ... nodes){
		this.keyspace = kspace;
		this.repl_factor = rf;
		this.cassandraNodes = new ArrayList<String>();
		for(String node: nodes)
			this.cassandraNodes.add(node);
		this.connect(this.cassandraNodes.toArray(new String[cassandraNodes.size()]));
	}
	
	public Session getSession(){
		return this.session;
	}

	public void connect(String ... nodes) {
		PoolingOptions poolingOptions = new PoolingOptions();
		// customize options...
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 4)
				.setMaxConnectionsPerHost(HostDistance.LOCAL, 10)
				.setCoreConnectionsPerHost(HostDistance.REMOTE, 2)
				.setMaxConnectionsPerHost(HostDistance.REMOTE, 4);
		cluster = Cluster.builder()
				.addContactPoints(nodes)
				.withPoolingOptions(poolingOptions)
				.build();
		
		Metadata metadata = cluster.getMetadata();
		Logger.info("Connected to cluster: "+
				metadata.getClusterName());
		
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		session = cluster.connect();
	}
	
	
	public void createSchema() {
		try {
			if(sm == null)
				sm = new SchemaManager(this.getSession());
			sm.createSchema();
		} catch (Exception e) {
			System.err.println("Cassandra Session error when creating schema.");
			e.printStackTrace();
		}
		Logger.info("Schema created");

	}

	public void dropSchema() {
		try {
			if(sm == null)
				sm = new SchemaManager(this.getSession());
			sm.dropSchema();
		} catch (Exception e) {
			System.err.println("Cassandra Session error when creating schema.");
			e.printStackTrace();
		}
	}

	public void loadData(String userDataPath, String trackDataPath) {
		Logger.info("Loading Data");
		//PrepareData p = new PrepareData(userDataPath, trackDataPath, this.getSession(), true);
		Logger.info("Finished Loading data");
	}

	public void disconnect(){
		this.session.close();
		this.cluster.close();
	}

	/*	 
	static
	{
	    Logger rootLogger = Logger.getRootLogger();
	    rootLogger.setLevel(Level.INFO);
	    rootLogger.addAppender(new ConsoleAppender(
	               new PatternLayout("%-6r [%p] %c - %m%n")));
	}
	
	public static void main(String[] args) {
		logger.setLevel(Level.INFO);
		//ClusterManager pg = new ClusterManager("play_cassandra", 1, "146.179.131.141");
		ClusterManager pg = new ClusterManager("play_cassandra", 1, "155.198.198.12");
		pg.dropSchema();
		pg.createSchema();
		
		pg.loadData("data/users.txt", "data/LastFM/lastfm_train");
		//pg.loadData("data/users.txt", "data/LastFM/lastfm_subset");
		
		Mapper<User> mapper = new MappingManager(pg.getSession()).mapper(User.class);
		User me = mapper.get("pangaref@example.com");
		System.out.println("got user "+ me);
		//map(pg.getSession().execute("SELECT * FROM play_cassandra.users"));
		
		pg.session.close();
		pg.cluster.close();
		 
	}
	*/
	

}
