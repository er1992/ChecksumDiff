import java.io.File;
import java.util.Iterator;

import org.json.simple.JSONObject;

public class JSONDirectoryComparator {
  
  JSONObject jsonNew;
  JSONObject jsonOld;
  
  public JSONDirectoryComparator(JSONObject jsonNew, JSONObject jsonOld) {
    this.jsonNew = jsonNew;
    this.jsonOld = jsonOld;
  }
  
  public JSONObject compareDirectories() {
    
    JSONObject diffJson = compareDirectoriesRecursive(jsonNew, jsonOld);
    
    return diffJson;
  }
  
  
  @SuppressWarnings("unchecked")
  public JSONObject compareDirectoriesRecursive(JSONObject jsonNew, JSONObject jsonOld) {
    JSONObject diffJson = new JSONObject();
    for(Iterator iterator = jsonNew.keySet().iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      if (jsonNew.get(key) instanceof JSONObject) {
        JSONObject obj = (JSONObject) jsonNew.get(key);
        if (jsonOld.containsKey(key)) {
          if (jsonOld.get(key) instanceof JSONObject) {
            JSONObject subFolderObj = compareDirectoriesRecursive((JSONObject) jsonNew.get(key), (JSONObject) jsonOld.get(key));
            if (!subFolderObj.isEmpty()) {
              diffJson.put(key, subFolderObj);
            }
          } else {
            // TODO (What are the chances???) Case of and old file existing in the new structure as folder now 
          }
        } else {
          diffJson.put(key, obj);
        }
        
//        System.out.println(obj);
      } else {
        String newChecksum = (String) jsonNew.get(key);
        if (jsonOld.containsKey(key)) {
          if ( ((String) jsonOld.get(key)).equalsIgnoreCase(newChecksum) ) {
            continue;
          }
        } else {
          // TODO handle the deleted file cleanup if any
        }
        diffJson.put(key, newChecksum);
        System.out.println(key);
      }
//      System.out.println("end");
    }
    return diffJson;
  }
  

}
