
import java.io.InputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mksmart.ontoRob.OntoRobVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTDBTester2 {

	public Dataset dataset ;
	public Location location; 
	private  Logger logger = LoggerFactory.getLogger(JTDBTester2.class);


	@Before 
	public void loadData(){
		FileManager fm = FileManager.get();
        fm.addLocatorClassLoader(JTDBTester2.class.getClassLoader());
        InputStream in = fm.open("data/ontoRob.nt");

        location = Location.create ("data/tdb");

        // Load some initial data
        TDBLoader.load(TDBInternal.getBaseDatasetGraphTDB(TDBFactory.createDatasetGraph(location)), in, false);
        logger.info("All good - loaded");
	}
	
	
	@Test
	public void selectAny(){
		
        String queryString = 
            
            "SELECT * WHERE { " +
            "    ?person ?a ?b " +
            "} LIMIT 5";
      
        logger.info("Query : {}",queryString);
      
        dataset = TDBFactory.createDataset(location);
        dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet results = qexec.execSelect();
                while ( results.hasNext() ) {
                    QuerySolution soln = results.nextSolution();
                    Resource name = soln.getResource("person");
                    System.out.println(name);
                }
            } finally {
                qexec.close();
            }
        } finally {
            dataset.end();
        }
    }
	
	
	@After
	public void selectMessages(){
		
        String queryString = 
            
            "SELECT * WHERE { " +
            "?message <"+RDF.type+"> <http://data.open.ac.uk/kmi/ontoRob/class/Message> " +
            "} ";
        logger.info("Query : {}",queryString);
        
        dataset = TDBFactory.createDataset(location);
        dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet results = qexec.execSelect();
                while ( results.hasNext() ) {
                    QuerySolution soln = results.nextSolution();
                    Resource name = soln.getResource("message");
                    System.out.println(name);
                }
            } finally {
                qexec.close();
            }
        } finally {
            dataset.end();
        }
    }
	
	
	@After
	public void selectCapability(){
		String message = "<http://data.open.ac.uk/kmi/ontoRob/resource/Illuminance>";
        String queryString = 
            
            "SELECT * WHERE {  ?capa <"+OntoRobVocabulary.PROP+"evokedBy> "+message+ " } ";
        
        logger.info("Query : {}",queryString);

        dataset = TDBFactory.createDataset(location);
        dataset.begin(ReadWrite.READ);
        try {
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
            try {
                ResultSet results = qexec.execSelect();
                while ( results.hasNext() ) {
                    QuerySolution soln = results.nextSolution();
                    Resource name = soln.getResource("capa");
                    System.out.println(name);
                }
            } finally {
                qexec.close();
            }
        } finally {
            dataset.end();
        }
    }
	

}
