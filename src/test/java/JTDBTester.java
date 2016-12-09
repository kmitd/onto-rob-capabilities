import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.util.FileManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JTDBTester {
	
	private  Logger logger = LoggerFactory.getLogger(JTDBTester.class);
	private Dataset dataset;
	
	private Model model;
	@Test
	public void before() throws IOException {
		
		dataset = initTDB("data/tdb");
			
		model = ModelFactory.createDefaultModel() ;
		RDFDataMgr.read(model, "transcripts.nt") ;

//		StmtIterator listStatements = model.listStatements();
//		while (listStatements.hasNext()){
//			addStatement( listStatements.next());			
//		}
		
//		model.close();
		
		getStatements( null, null, null);
	}
	
	
	
	public  Dataset initTDB(String dataDirectory) {
		logger.info("Initializing context.");

		logger.info("Setting up database");
		Dataset dataset;
		
		File data = (File) new File(dataDirectory);
		if (!data.exists()) {
			logger.info("Creating directory " + data);
			data.mkdirs();
		}
		if (data.canRead() && data.canWrite() && data.canExecute()) {
			dataset = TDBFactory.createDataset(Location.create(data.getAbsolutePath()));
		} else {
			throw new RuntimeException("Cannot setup database. Not enough permissions on folder " + data);
		}
		
		return dataset;
		
	}
	
	public  void addStatement(  String subject, String property, String object )
	{
		
		dataset.begin( ReadWrite.WRITE );
		try
		{
			model = dataset.getNamedModel( "test01" );
			
			Statement stmt = model.createStatement
							 ( 	
								model.createResource( subject ), 
								model.createProperty( property ), 
								model.createResource( object ) 
							 );
			
			model.add( stmt );
			dataset.commit();
		}
		finally
		{
//			model.write(System.out, "NT");

			if( model != null ) model.close();
			dataset.end();
			
			
			logger.info("OK - Added statement {} {} {}", new Object[] {subject, property, object});
		}
	}
	
	public  void addStatement( Statement stmt )
	{
		
		dataset.begin( ReadWrite.WRITE );
		try
		{	
			dataset.commit();
		}
		finally
		{
			dataset.end();						
			logger.info("OK - Added statement {} {} {}", new Object[] {stmt.asTriple()});
		}
	}
	
	public  List<Statement> getStatements( String subject, String property, String object )
	{
		List<Statement> results = new ArrayList<Statement>();
			
//		Model model = null;
			
		dataset.begin( ReadWrite.READ );
		try
		{
//			model = dataset.getNamedModel( "test01" );
			System.out.println(model.isEmpty()+" "+model.size());
			Selector selector = new SimpleSelector(
						( subject != null ) ? model.createResource( subject ) : (Resource) null, 
						( property != null ) ? model.createProperty( property ) : (Property) null,
						( object != null ) ? model.createResource( object ) : (RDFNode) null
						);
				
			StmtIterator it = model.listStatements( selector );
			{
				while( it.hasNext() )
				{
					Statement stmt = it.next(); 
					results.add( stmt );
//					System.out.println(stmt.toString());
				}
			}
				
			dataset.commit();
			logger.info("OK");
		}
		finally
		{
			if( model != null ) model.close();
			dataset.end();
		}
			
		return results;
	}
	
	
	public  void loadExistingModel(Dataset ds ,  String modelName, String path){
		Model model = null;
		
		ds.begin( ReadWrite.WRITE );
		try
		{
			model = ds.getNamedModel( modelName );
			FileManager.get().readModel( model, path );
			ds.commit();
		}
		finally
		{
			ds.end();
		}
	}
	

}
