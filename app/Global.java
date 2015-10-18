import java.util.List;

import com.typesafe.config.ConfigFactory;

import controllers.CassandraDxQueryController;
import managers.ClusterManager;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
    	
    	List<String> hosts = ConfigFactory.load().getStringList("cassandra.nodes");
    	Logger.debug("Cassandra hosts from Config: "+ hosts.toString());
    	if(hosts == null || hosts.size() == 0 ){
    		Logger.error("Cassandra HOSTs argument cannot be null!");
    		System.exit(-1);
    	}
    	else{
    		controllers.Application.clusterManager = new ClusterManager("play_cassandra", 1, hosts.toArray(new String[hosts.size()]));
    		controllers.Application.dxController = new CassandraDxQueryController(controllers.Application.clusterManager.getSession());
    	}
    }
    
    @Override
    public void onStop(Application app) {
    	Logger.info("Shutting down Cassandra cluster Connections..");
    	controllers.Application.clusterManager.disconnect();
    }
}