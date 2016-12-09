package org.mksmart.ontoRob;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;



public class OntoRobVocabulary {
	
	public static final String NS = "http://data.open.ac.uk/kmi/ontoRob";
	public static final String CLASS = "http://data.open.ac.uk/kmi/ontoRob#";	
	public static final String PROP = "http://data.open.ac.uk/kmi/ontoRob/property";

	
	public static final Resource Capability = ResourceFactory.createResource(CLASS + "Capability");
	
	public static final Resource CommunicationItem = ResourceFactory.createResource(CLASS + "CommunicationItem");
	public static final Resource CommunicationType = ResourceFactory.createResource(CLASS+ "CommunicationType");
	public static final Resource CommunicationChannel = ResourceFactory.createResource(CLASS + "CommunicationChannel");
	public static final Resource Node = ResourceFactory.createResource(CLASS + "/Node");
	
	public static final Resource Action = ResourceFactory.createResource(CLASS + "Action");
	public static final Resource Service = ResourceFactory.createResource(CLASS + "Service");
	public static final Resource Topic = ResourceFactory.createResource(CLASS + "Topic");
	
	public static final Resource ActionMsg = ResourceFactory.createResource(CLASS + "ActionMessage");
	public static final Resource ServiceMsg = ResourceFactory.createResource(CLASS + "ServiceMessage");
	public static final Resource TopicMsg = ResourceFactory.createResource(CLASS + "TopicMessage");
	public static final Resource Message = ResourceFactory.createResource(CLASS + "Message");
	
	public static final Property expects = ResourceFactory.createProperty(PROP + "/expects");
	public static final Property communicatesThrough = ResourceFactory.createProperty(PROP + "/communicatesThrough");
	public static final Property communicatesWith= ResourceFactory.createProperty(PROP + "/communicatesWith");

	public static final Property produces = ResourceFactory.createProperty(PROP + "/produces");
	public static final Property hasType = ResourceFactory.createProperty(PROP + "/hasType");
	public static final Property usedBy = ResourceFactory.createProperty(PROP + "/usedBy");
	
	public static final Property implementedBy = ResourceFactory.createProperty(PROP + "/isImplementedBy");
	public static final Property evokedBy = ResourceFactory.createProperty(PROP + "/isEvokedBy");
	public static final Property hasMessage = ResourceFactory.createProperty(PROP + "/hasMessage");

	
}
