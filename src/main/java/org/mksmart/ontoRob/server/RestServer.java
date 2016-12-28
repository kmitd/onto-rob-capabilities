package org.mksmart.ontoRob.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RestServer {
	
	private static Logger logger = LoggerFactory.getLogger(RestServer.class);

	
	public static void main(String[] args) {
		logger.info("#1: Server starting");
		
		OntoRobApp config = new OntoRobApp();//ResourceConfig();
		
		ServletHolder servlet = new ServletHolder(new ServletContainer(config));

		Server server = new Server(5050);
		logger.info("#2: Server started on port 5050" );
		ServletContextHandler context = new ServletContextHandler(server, "/*");
		context.addServlet(servlet, "/*");
		

		try {
			server.start();
			System.out.println("#4: enjoy");
			server.join();
			System.out.println("#5: stopping server");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		} finally {			
			server.destroy();
			System.out.println("#6: thank you");
		}
	}

	
}
