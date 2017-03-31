package org.mksmart.ontoRob.server;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RestServer  {
	
	private static Logger logger = LoggerFactory.getLogger(RestServer.class);

	public static void main(String[] args) {
		logger.info("#1: Server starting");
		

		Server server = new Server(5050);
		WebAppContext webApp = new WebAppContext();
		webApp.setContextPath("/");

		String webxmlLocation = "src/main/webapp/WEB-INF/web.xml";
		webApp.setDescriptor(webxmlLocation);
		
		
		String resLocation = "src/main/webapp/WEB-INF/static";
		webApp.setResourceBase(resLocation);
		webApp.setParentLoaderPriority(true);
		server.setHandler(webApp);
		

		try {
			server.start();
//			server.dumpStdErr();
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
