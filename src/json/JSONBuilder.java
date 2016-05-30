package json;
import java.util.zip.CheckedInputStream;
import java.util.zip.CRC32;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import pojo.Connection;

public abstract class JSONBuilder {
  protected Connection conn = null;
  
  public JSONBuilder(Connection conn) {
    this.conn = conn;
  }
  
  abstract JSONObject getJsonFromDirectory() throws NoSuchAlgorithmException, IOException, FileNotFoundException, ParseException;
  
  protected String getFileChecksum(File file) throws IOException {
    CheckedInputStream cis = null;
    cis = new CheckedInputStream(new FileInputStream(file.getAbsolutePath()), new CRC32());
    
    byte[] buf = new byte[128];
    while(cis.read(buf) >= 0) {}
    
    String checksum = new Long(cis.getChecksum().getValue()).toString();
    cis.close();
    
    return checksum;

  }
  
}