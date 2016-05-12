import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.json.simple.JSONObject;

public class DirectoryBuilder {
  private Connection root;

  public DirectoryBuilder(Connection root) {
    this.root = root;
  }
  
  public void buildDirectoryFromJson(JSONObject json) throws IOException {
    File readRoot = new File(this.root.url);
    File writeRoot = new File(readRoot.getParentFile().getCanonicalFile() + "/deploy" );
    
    if (!writeRoot.mkdir()) {
      throw new IOException("Couldn't create root deploy directory");
    }
    
    buildDirectoryFromJsonRecursive(json, readRoot, writeRoot);
        
  }
  
  private void buildDirectoryFromJsonRecursive(JSONObject json, File readRoot, File writeRoot) throws IOException {
    for (Object obj : json.entrySet()) {
      if (obj instanceof JSONObject) {
//        JSONObject folderObj = (JSONObject) obj;
//        readRoot = new File(readRoot.getCanonicalPath() + "/" + folderObj)
//        buildDirectoryFromJsonRecursive((JSONObject) obj, readRoot, writeRoot);
      } else {
        String fileName = (String) obj.toString();
        Files.copy(Paths.get(readRoot.getCanonicalFile() + "/" + fileName), Paths.get(writeRoot.getCanonicalFile() + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
      }
      
    }
  }
  
}
