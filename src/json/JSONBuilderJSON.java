package json;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pojo.Connection;

public class JSONBuilderJSON extends JSONBuilder {

  public JSONBuilderJSON(Connection conn) {
    super(conn);
  }

  @Override
  JSONObject getJsonFromDirectory() throws NoSuchAlgorithmException, IOException, FileNotFoundException, ParseException {
    JsonParser parser = new JsonParser();
  
    JsonElement jsonElement = parser.parse(new FileReader(conn.url));
    JsonObject jsonObj = jsonElement.getAsJsonObject();
    
    JSONParser returnParser = new JSONParser();
    JSONObject returnObj = (JSONObject) returnParser.parse(jsonObj.toString());
    
    return returnObj;
  }

}
