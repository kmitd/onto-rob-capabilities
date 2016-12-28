package org.mksmart.ontoRob;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OntoRobCapabilityMain {

	private static Logger logger = LoggerFactory.getLogger(OntoRobCapabilityMain.class);

	public static void main(String[] args) {
		
		logger.info("#1 - Start...");
		
		
        logger.info("#2 - Loading TDB...");
        Location location = Location.create ("data/tdb");
        
 
		FileManager fm = FileManager.get();
		fm.addLocatorClassLoader(OntoRobCapabilityMain.class.getClassLoader());
		InputStream in = fm.open("data/ontoRob3.nt");

        TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph(location)), in, false);
        logger.info("#3 - All good, loaded.");
        
       
        Dataset dataset = TDBFactory.createDataset(location);   
        		
        List<String> listOfMsg = new ArrayList<String>();
        
      //  selectAll(dataset);
        listOfMsg.add("ApplyPlanningScene");
        listOfMsg.add("PoseWithCovarianceStamped");
        

        selectCapability(listOfMsg,dataset);
	}
	
	
	public static void selectCapability(List<String> listOfMsg, Dataset dataset) {
		String values = "VALUES(?res) { ";
		for (String s : listOfMsg){			
			values+="(<"+OntoRobVocabulary.NS+"/resource/"+s+">) ";
		}
		values= values.substring(0, values.length())+"}";
		Resource res = ResourceFactory.createResource(OntoRobVocabulary.NS+"/resource/"+listOfMsg.get(0));
		
		String queryString = "SELECT ?capa WHERE { "+values
				+ " ?capa <"+OntoRobVocabulary.evokedBy.getURI()+"> ?res  }"; 
		logger.info("Query : {}",queryString);
        dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet results = qexec.execSelect();
                
                if (!results.hasNext()) {
                	logger.info("Empty q! :(");
                }
                while ( results.hasNext() ) {
                    QuerySolution soln = results.nextSolution();
                    Resource name = soln.getResource("capa");
                    logger.info("Res - {}", name);
                }
            } finally {
                qexec.close();
            }
        } finally {
            dataset.end();
        }
        
		
	}


	public static void addStatement (Dataset dataset){
	    dataset.begin( ReadWrite.WRITE );
		Model model = null;	
		try{
			model = dataset.getDefaultModel();
			Statement stmt = model.createStatement
					 ( 	model.createResource(OntoRobVocabulary.NS+ "/resource/BodyPart_Mvt" ), OntoRobVocabulary.evokedBy , 
						model.createResource( OntoRobVocabulary.NS+ "/resource/JointState" ));

	
			
			model.add( stmt );
			dataset.commit();
		}
		finally {
			model.write(System.out, "NT");
			if( model != null ) model.close();
			dataset.end();
			logger.info("OK - Added statement ." );
		}
	}
	
	public static void selectAll(Dataset dataset){
		 String queryString = "SELECT * WHERE {  ?person ?a ?b   }";
         
         logger.info("Query : {}",queryString);
       
         
         dataset.begin(ReadWrite.READ);
         try {
             Query query = QueryFactory.create(queryString);
             QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
             try {
                 ResultSet results = qexec.execSelect();
                 while ( results.hasNext() ) {
                     QuerySolution soln = results.nextSolution();
                     Resource name = soln.getResource("person");
                     Resource a = soln.getResource("a");
                     Resource b = soln.getResource("b");
                     System.out.println(name+" "+a+" "+b);
                 }
             } finally {
                 qexec.close();
             }
         } finally {
             dataset.end();
         }
         
	}
	
}
