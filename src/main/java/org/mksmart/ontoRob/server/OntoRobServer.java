package org.mksmart.ontoRob.server;


import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
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
	@Produces(MediaType.TEXT_PLAIN)
	public String getCapability(@QueryParam("msgs") String msgs) {

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
