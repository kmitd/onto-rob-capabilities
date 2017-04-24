package org.mksmart.ontoRob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class OntoRobUtilities {
	
	// static class of utils
	
	public  static Map<String, List<String>> parseJsonNodeList(String json){
		
		Map<String, List<String>> response = new HashMap<String,List<String>>();
			
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
		Integer len =  JsonPath.read(document, "$.length()");
//		System.out.println(document);
//		System.out.println(len);
		
		for (int i = 0 ; i < len ; i++){
			String node = JsonPath.read(document, "$.["+i+"].node");
			String topic = JsonPath.read(document, "$.["+i+"].topic");
			String component = node+"::"+topic;
			if (!response.containsKey(component)){
				response.put(component, new ArrayList<String>());
			}
			String msg = JsonPath.read(document, "$.["+i+"].message").toString();
			response.get(component).add(msg.substring(msg.lastIndexOf("/")+1));
		
		}
		return response ;
	}

	
	
}
