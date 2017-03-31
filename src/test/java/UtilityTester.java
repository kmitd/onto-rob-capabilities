import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;


public class UtilityTester {

	
	
	@Test
	public   void parseJson(){
		
		Map<String, List<String>> response = new HashMap<String,List<String>>();
		
		String json ="[ { \"node\": \"/stageros\", \"topic\":\"/odom\", \"message\":\"nav_msgs/Odometry\", \"method\":\"msg\" }, { \"node\":\"/stageros\", \"topic\":\"/scan\", \"message\":\"sensor_msgs/LaserScan\",\"method\":\"msg\" }, {\"node\":\"/move_base\",\"topic\":\"/move_base/goal\",\"message\":\"move_base_msgs/MoveBaseActionGoal\",\"method\":\"msg\"}, { \"node\":\"/dht22\",\"topic\":\"/dht22_readings\",\"message\":\"dht22_msgs/StdDht22Msg\",\"method\":\"msg\" },{ \"node\":\"/dht22\",\"topic\":\"/dht22_readings\",\"message\":\"TemperatureMsg\",\"method\":\"msg\"}, {\"node\":\"/dht22\", \"topic\":\"/dht22_readings\", \"message\":\"HumidityMsg\", \"method\":\"msg\"}, { \"node\":\"/dht22\",  \"topic\":\"/dht22_air\", \"message\":\"CO2\",\"method\":\"msg\"  }	]";
		//String json = "[{'node':'/stageros','topic':'/hello','message':'/LaserScan'}]";
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
		Integer len = JsonPath.read(document, "$.length()");
		
		System.out.println(len);
		
		for (int i = 0 ; i < len ; i++){
			String component = JsonPath.read(document, "$.["+i+"].node").toString()+""+JsonPath.read(document, "$.["+i+"].topic").toString();
			if (!response.containsKey(component)){
				response.put(component, new ArrayList<String>());
			}
			String msg = JsonPath.read(document, "$.["+i+"].message").toString();
			response.get(component).add(msg.substring(msg.lastIndexOf("/")+1));
		
		}
		System.out.println(response);
		
	}
}
