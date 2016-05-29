package builder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.json.simple.JSONObject;

import pojo.Connection;

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
    for(@SuppressWarnings("rawtypes")
    Iterator iterator = json.keySet().iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      if (json.get(key) instanceof JSONObject) {
        File newReadRoot = new File(readRoot.getCanonicalPath() + "/" + key);
        File newWriteRoot = new File(writeRoot.getCanonicalPath() + "/" + key);
        if (!newWriteRoot.mkdir()) {
          throw new IOException("Couldn't create " + key + " deploy directory");
        }
        buildDirectoryFromJsonRecursive((JSONObject) json.get(key), newReadRoot, newWriteRoot);
      } else {
        Files.copy(Paths.get(readRoot.getCanonicalFile() + "/" + key), Paths.get(writeRoot.getCanonicalFile() + "/" + key), StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }
  
}
