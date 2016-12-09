package org.mksmart.ontoRob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBaseBuilder {
	
	private static Logger logger = LoggerFactory.getLogger(KnowledgeBaseBuilder.class);

	private String inputF;
	private String outputF;

	public KnowledgeBaseBuilder(String fileIn, String fileOut){
		inputF = fileIn;
		outputF = fileOut;
	}
	
	public void buildInitialKB(){
		
		List<String> lines = readFile();
		createModel(lines);
		
	}
	
	
	
	/**
	 * creates basic KB from simple <capability,Message,TypeOfMsg> file with headers, see instances.csv
	 * @param lines
	 */
	public  void createBasicModel(List<String> lines){
		Model model = ModelFactory.createDefaultModel();
		
		for (int i =1 ; i < lines.size(); i++){
			String line = lines.get(i);
			String[] split = line.split(",");
			
			// start instance
			Resource s = model.createResource(OntoRobVocabulary.NS+"/resource/"+split[0]);
			// add prop
			
			Resource o = model.createResource(OntoRobVocabulary.NS+"/resource/"+split[1]);
			s.addProperty(OntoRobVocabulary.evokedBy, o);
			
			
			// add class to S and O
			Resource capability = model.createResource(OntoRobVocabulary.CLASS+split[0].split("_")[1]+"_Capability");
			s.addProperty(RDF.type, capability);
			capability.addProperty(RDFS.subClassOf, OntoRobVocabulary.Capability);
			Resource msgType = model.createResource(OntoRobVocabulary.CLASS+split[2]);
			
			o.addProperty(RDF.type, msgType);
			msgType.addProperty(RDFS.subClassOf, OntoRobVocabulary.Message);
		}
		File file = new File(outputF);

		try {
			model.write(new FileOutputStream(file), "NT");
		} catch (FileNotFoundException e) {
			logger.info("File {} not found", file.getPath());
		} finally {
			logger.info("Written!");
		}
		
	}
	
	/**
	 * creates KB from < (msg|srv|act)_type,(msg|srv|act)_name,[listOfCapabilities]> with headers, see msgAndSrv.csv
	 * @param lines
	 */
	private void createModel(List<String> lines){
		
		Model model = ModelFactory.createDefaultModel();
		
		for (int i =1 ; i < lines.size(); i++){
			String line = lines.get(i);
			String[] split = line.split(",");
			
			String communicationItemType = split[0];
			Resource s = model.createResource(OntoRobVocabulary.CLASS+communicationItemType);

			// is it service, msg, action?
			if (communicationItemType.endsWith("msg")){	
				s.addProperty(RDFS.subClassOf, OntoRobVocabulary.Message);
				
			} else if  (communicationItemType.endsWith("srv")){
				s.addProperty(RDFS.subClassOf, OntoRobVocabulary.Service);
			} else if (communicationItemType.endsWith("act")){
				s.addProperty(RDFS.subClassOf, OntoRobVocabulary.Action);
			} else {
				logger.error("Hey! I am not a msg nor a srv nor an act! {}", communicationItemType);
			}
			
			// create msg/srv/act name
			Resource communicationItemName =  model.createResource(OntoRobVocabulary.NS+"/resource/"+split[1]);
			communicationItemName.addProperty(RDF.type, s);
			
			// create capabilities
			
			String[] caps = line.substring(line.indexOf("[")+1, line.indexOf("]")).split(",");

			for (String capa : caps){				
				Resource capability = model.createResource(OntoRobVocabulary.CLASS+capa);
				capability.addProperty(RDF.type, OntoRobVocabulary.Capability);
				
				// link msg/srv/act to capability
				capability.addProperty(OntoRobVocabulary.evokedBy, communicationItemName);
			}
		}
		
		File file = new File(outputF);

		try {
			model.write(new FileOutputStream(file), "NT");
		} catch (FileNotFoundException e) {
			logger.info("File {} not found", file.getPath());
		} finally {
			logger.info("Written!");
		}
		
	}
	
	private  List<String> readFile(){
		try {
			List<String> lines = IOUtils.readLines(new FileInputStream(inputF));
			
			return lines;
			
		} catch (FileNotFoundException e) {
			logger.error("File {} not found", inputF);
			return Collections.emptyList();
		} catch (IOException e) {
			logger.error("Could not write on {}", inputF);
			return Collections.emptyList();
		}
		
		
	}
}
