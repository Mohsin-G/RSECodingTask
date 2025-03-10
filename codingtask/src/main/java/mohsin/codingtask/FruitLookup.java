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
	
	 /**
     * Main method for command-line users.
     * @param args Command-line arguments: fruit name and optional "json" flag for machine-readable output.
     */
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
					// Print JSON output for machine-readable use
					ObjectMapper objectMapper = new ObjectMapper();
					System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getSimplifiedJSONFruitInfo(fruitName)));
				}
				else
				{
					// Print human-readable output
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
	
	/**
     * Fetches fruit information from the FruityVice API.
     * @param fruitName The name of the fruit to lookup.
     * @return A JsonNode containing the full API response.
     * @throws Exception If an error occurs during the API call.
     */
	public static JsonNode getFruitData(String fruitName) throws Exception
	{
		URI uri = URI.create(api_url + fruitName);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		int responseCode = conn.getResponseCode();
		 // If the API response is not 200 (OK), return null
		if(responseCode != 200)
		{
			return null;
		}
		 // Read API response
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while((line = br.readLine()) != null)
		{
			response.append(line);
		}
		br.close();
		conn.disconnect();
		// Convert the response to a JSON object
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(response.toString());
	}
	
	/**
     * Extracts only selected fields from the API response (name, id, family, sugar, carbohydrates).
     * @param fruitName The name of the fruit.
     * @return A JsonNode containing only the required fields.
     * @throws Exception If an error occurs during the API call.
     */
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
			JsonNode nutrition = rootNode.get("nutritions"); // Extract nutritional information
			filteredJson.put("sugar", nutrition.get("sugar").asDouble());
			filteredJson.put("carbohydrates", nutrition.get("carbohydrates").asDouble());
			return filteredJson; // Returns the simplified JSON object
	}
	
	

}
