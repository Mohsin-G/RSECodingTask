package mohsin.codingtask;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FruitLookup {

	private static final String api_url = "https://www.fruityvice.com/api/fruit/";
	
	public static void main(String[] args) {
		if(args.length == 0)
		{
			System.out.println("Please provide a fruit name.");
			return;
		}
		
		String fruitName = args[0];
		boolean machineReadable = args.length > 1 && args[1].equalsIgnoreCase("json");
		
		try
		{
			JsonNode fruitData = getFruitData(fruitName);
			if(fruitData != null)
			{
				if(machineReadable == true)
				{
					ObjectMapper objectMapper = new ObjectMapper();
					System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getSimplifiedJSONFruitInfo(fruitName)));
				}
				else
				{
					System.out.println("Fruit Details: ");
					System.out.println("Name: " + fruitData.get("name").asText());
					System.out.println("ID: " + fruitData.get("id").asInt());
					System.out.println("Family: " + fruitData.get("family").asText());
					JsonNode nutrition = fruitData.get("nutritions");
					System.out.println("Sugar: " + nutrition.get("sugar").asDouble() + "g");
					System.out.println("Carbohydrates: " + nutrition.get("carbohydrates").asDouble() + "g");
				}
				
			}
			else
			{
				System.out.println("Fruit not found or API unavailable");
			}
		}
		catch (Exception e) 
		{
			System.out.println("Error fetching fruit data: " + e.getMessage());
		}
	}
	
	public static JsonNode getFruitData(String fruitName) throws Exception
	{
		URI uri = URI.create(api_url + fruitName);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		int responseCode = conn.getResponseCode();
		if(responseCode != 200)
		{
			return null;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while((line = br.readLine()) != null)
		{
			response.append(line);
		}
		br.close();
		conn.disconnect();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(response.toString());
	}
	
	public static JsonNode getSimplifiedJSONFruitInfo(String fruitName) throws Exception
	{
		JsonNode rootNode = getFruitData(fruitName);
		if(rootNode == null)
		{
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode filteredJson = objectMapper.createObjectNode();
		
			filteredJson.put("name", rootNode.get("name").asText());
			filteredJson.put("id", rootNode.get("id").asInt());
			filteredJson.put("family", rootNode.get("family").asText());
			JsonNode nutrition = rootNode.get("nutritions");
			filteredJson.put("sugar", nutrition.get("sugar").asDouble());
			filteredJson.put("carbohydrates", nutrition.get("carbohydrates").asDouble());
			return filteredJson;
	}
	
	

}
