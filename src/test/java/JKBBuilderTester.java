import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Ignore;
import org.junit.Test;
import org.mksmart.ontoRob.KnowledgeBaseBuilder;
import org.mksmart.ontoRob.OntoRobVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JKBBuilderTester {

	private  Logger logger = LoggerFactory.getLogger(JKBBuilderTester.class);

	@Test
	public void testData(){
		logger.debug("Test 1");
		KnowledgeBaseBuilder kbb = new KnowledgeBaseBuilder("./data/msgAndServ.csv", "./data/ontoRob3.nt");
		kbb.buildInitialKB();
	}
	
	@Ignore
	@Test
	public void test(){
		
		List<String> lines = readFile("data/instances.csv");
		createModel(lines);
		
	}
	
	public void createModel(List<String> lines){
		Model model = ModelFactory.createDefaultModel();
		
		for (int i =1 ; i < lines.size(); i++){
			String line = lines.get(i);
			String[] split = line.split(",");
			
			// start instance
			Resource s = model.createResource(OntoRobVocabulary.NS+"/resource/"+split[0]);
			// add prop
			Property p = model.createProperty(OntoRobVocabulary.PROP+"/"+split[1]);
			Resource o = model.createResource(OntoRobVocabulary.NS+"/resource/"+split[2]);
			s.addProperty(p, o);
			
			// add class to S and O
			s.addProperty(RDF.type, model.createResource(OntoRobVocabulary.CLASS+split[0].split("_")[1]+"_Capability"));
			o.addProperty(RDF.type, model.createResource(OntoRobVocabulary.CLASS+"Message"));
		}
		File file = new File("data/ontoRob.nt");

		try {
			model.write(new FileOutputStream(file), "NT");
		} catch (FileNotFoundException e) {
			logger.info("File {} not found", file.getPath());
		} finally {
			logger.info("OK - written!");
		}
		
	}
	
	public List<String> readFile(String file){
		try {
			List<String> lines = IOUtils.readLines(new FileInputStream(file));
			
			return lines;
			
		} catch (FileNotFoundException e) {
			logger.error("File {} not found", file);
			return Collections.emptyList();
		} catch (IOException e) {
			logger.error("Could not write on {}", file);
			return Collections.emptyList();
		}
		
		
	}
}
