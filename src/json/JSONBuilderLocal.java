package json;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONObject;

import pojo.Connection;

public class JSONBuilderLocal extends JSONBuilder {
  
  public JSONBuilderLocal(Connection conn) {
    super(conn);
  }
  
  @Override
	public JSONObject getJsonFromDirectory() throws NoSuchAlgorithmException, IOException {
		File[] files = new File(conn.url).listFiles();
		JSONObject folderJson = walkFolder(files);
    
    return folderJson;
	}
  
  @SuppressWarnings("unchecked")
  private JSONObject walkFolder(File[] files) throws NoSuchAlgorithmException, IOException {
    JSONObject folderJson = new JSONObject();
    for (File file : files) {
      if (!file.canRead())
        continue;
      if (file.isDirectory()) {
        folderJson.put(file.getName(), walkFolder(file.listFiles()));
      } else {
        folderJson.put(file.getName(), getFileChecksum(file).toString());
      }
    }
    return folderJson;
  }
  
}
