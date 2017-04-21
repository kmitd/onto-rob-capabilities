package org.mksmart.ontoRob.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.mksmart.ontoRob.OntoRobUtilities;
import org.mksmart.ontoRob.OntoRobVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("onto-rob-server")
public class OntoRobServer {

	private static Logger logger = LoggerFactory.getLogger(OntoRobServer.class);

	@Context
	private ServletContext context;

	@GET
	// rest action type
	@Path("add")
	// uri path
	@Produces(MediaType.APPLICATION_JSON)
	// how the response will be returned.
	public String add(@QueryParam("input") double input,
			@QueryParam("input2") double input2) {

		return Double.toString(input + input2);

	}

	@GET
	@Path("triples")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTriples() {

		String queryString = "SELECT * WHERE {  ?x ?a ?b   }";
		StringBuilder sb = new StringBuilder();

		logger.info("Query : {}", queryString);
		Dataset dataset = (Dataset) context.getAttribute("dataset");

		// System.out.println(dataset);
		dataset.begin(ReadWrite.READ);
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
			try {
				ResultSet results = qexec.execSelect();
				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					Resource name = soln.getResource("x");
					Resource a = soln.getResource("a");
					Resource b = soln.getResource("b");
					sb.append(name + " " + a + " " + b + "\n");
				}
			} finally {
				qexec.close();
			}
		} finally {
			dataset.end();
		}
		return sb.toString();
	}

	
	@GET
	@Path("capabilities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getCapability(@QueryParam("jsonString") String jsonNodes) {

		/**
		 * from a json stinrg{}  
		 **/
				
		JSONArray root = new JSONArray();	
		Map<String, List<String>> componentList = OntoRobUtilities.parseJsonNodeList(jsonNodes);
		
		// one query per component
		for (String component : componentList.keySet()){	
//			logger.info("{}",component);
			
			JSONObject o = new JSONObject();
			o.put("name", component);
			
			String values = "VALUES(?res) { ";
			for (String s : componentList.get(component)) {
				// list of messages for a component
				values += "(<" + OntoRobVocabulary.NS + "/resource/" + s + ">) ";
			}
			
			values = values.substring(0, values.length()) + "}";
			
			String queryString = "SELECT ?capa WHERE { " + values + " ?capa <"
					+ OntoRobVocabulary.evokedBy.getURI() + "> ?res  }";
			logger.info("For component {}, query : {}", component, queryString);
			Dataset dataset = (Dataset) context.getAttribute("dataset");
			
			dataset.begin(ReadWrite.READ);
			
			try {
				Query query = QueryFactory.create(queryString);
				QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
				try {
					ResultSet results = qexec.execSelect();
					
					if (!results.hasNext()) {
						logger.info("Empty q! :(");
					}
					
					JSONArray jArray = new JSONArray();
					while (results.hasNext()) {
						QuerySolution soln = results.nextSolution();
						Resource name = soln.getResource("capa");
						jArray.add(name.toString());
					}
					o.put("capabilities", jArray);
				} finally {
					qexec.close();
				}
			} finally {
				dataset.end();
			}
			

			root.add(o);
			logger.info("Response for '{}': {}",component, root.toJSONString(JSONStyle.MAX_COMPRESS));
		}
		

		return root.toString()+"\n";
	}
	
	@GET
	@Path("capabilitiesFromList")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCapabilityFromList(@QueryParam("msgs") String msgs) {

		/**
		 * from a list of messages [msg1,mgs2]  
		 **/
		
		StringBuilder sb = new StringBuilder();
		String[] listOfMsg = msgs.split(",");
		String values = "VALUES(?res) { ";

		for (String s : listOfMsg) {
			values += "(<" + OntoRobVocabulary.NS + "/resource/" + s + ">) ";
		}
		values = values.substring(0, values.length()) + "}";


		String queryString = "SELECT ?capa WHERE { " + values + " ?capa <"
				+ OntoRobVocabulary.evokedBy.getURI() + "> ?res  }";
		logger.info("Query : {}", queryString);
		Dataset dataset = (Dataset) context.getAttribute("dataset");
		
		dataset.begin(ReadWrite.READ);
		
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
			try {
				ResultSet results = qexec.execSelect();

				if (!results.hasNext()) {
					logger.info("Empty q! :(");
				}
				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					Resource name = soln.getResource("capa");
					sb.append( name+"\n");
				}
			} finally {
				qexec.close();
			}
		} finally {
			dataset.end();
		}

		return sb.toString();
	}
}
