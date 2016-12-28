package org.mksmart.ontoRob.server;

import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class OntoRobApp extends ResourceConfig implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(OntoRobApp.class);
	
	public OntoRobApp(){
		packages("org.mksmart.ontoRob.server");
//		System.out.println("FUFU");
	}

	public void contextInitialized(ServletContextEvent sce) {
		
        logger.info("#1 Loading TDB...");
        Location location = Location.create ("data/tdb");
    	
		FileManager fm = FileManager.get();
		fm.addLocatorClassLoader(OntoRobApp.class.getClassLoader());
		InputStream in = fm.open("data/ontoRob3.nt");

        TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph(location)), in, false);
            
        Dataset dataset = TDBFactory.createDataset(location);  
        logger.info("#2 All good, loaded.");
        
        sce.getServletContext().setAttribute("dataset", dataset); 
//        System.out.println("OK");
        
	}

	public void contextDestroyed(ServletContextEvent sce) {
		
	}

	

}
