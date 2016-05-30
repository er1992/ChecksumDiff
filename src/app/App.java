package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import builder.DirectoryBuilder;
import commandLine.CommandLineHandler;
import json.JSONBuilderLocal;
import json.JSONBuilderSSH;
import json.JSONDirectoryComparator;
import pojo.Connection;
import ui.ConnectionPrompt;


public class App {
  
  static Logger log = Logger.getLogger(App.class.getName());
  private String[] args;
  
  List<Connection> connections;
  List<JSONObject> directoryJsons;
  
  public enum InputType {
    SSH, LOCAL, JSON
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    App init = new App(args);
    init.run();
  }
  
  public App(String[] args) {
    this.args = args;
    connections = new ArrayList<>();
    directoryJsons = new ArrayList<>();
  }
  
  public void run() {
    // No arguments supplied then use prompt
    if (args.length == 0) {
      log.info("Input from GUI");
      ConnectionPrompt connectionUI = new ConnectionPrompt(this);
      connectionUI.initUI();
    } else {
      log.info("Input from command line argument");
      CommandLineHandler cmHandler = new CommandLineHandler(this);
      try {
        cmHandler.getConnectionsFromArgs(Arrays.asList(args));
      } catch (JsonSyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (JsonIOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public void buildDir() {
    if (connections.get(0).valid() && connections.get(1).valid()) {
      log.info("Connections Valid");
      JSONObject diff = null;
      DirectoryBuilder dirBuilder = null;
      if (connections.get(0).type == InputType.JSON && connections.get(1).type == InputType.JSON) {
        log.info("JSON vs JSON");
        JSONDirectoryComparator comparator = new JSONDirectoryComparator(directoryJsons.get(0), directoryJsons.get(1));
        diff = comparator.compareDirectories();
        log.info("Building JSON diff");
        dirBuilder = new DirectoryBuilder(promptJSONSource());
      } else if (connections.get(0).type == InputType.LOCAL && connections.get(1).type == InputType.JSON) {
        log.info("LOCAL vs JSON");
        JSONDirectoryComparator comparator = new JSONDirectoryComparator(directoryJsons.get(0), directoryJsons.get(1));
        diff = comparator.compareDirectories();
        log.info("Building JSON diff");
        dirBuilder = new DirectoryBuilder(connections.get(0));
      } else if (connections.get(0).type == InputType.LOCAL && connections.get(1).type == InputType.LOCAL) {
        log.info("LOCAL vs LOCAL");
        JSONDirectoryComparator comparator = new JSONDirectoryComparator(directoryJsons.get(0), directoryJsons.get(1));
        diff = comparator.compareDirectories();
        log.info("Building JSON diff");
        dirBuilder = new DirectoryBuilder(connections.get(0));
      }
      try {
        log.info("Building direcotry from JSON diff");
        dirBuilder.buildDirectoryFromJson(diff);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void buildJSON(Connection conn) throws FileNotFoundException, UnsupportedEncodingException, IOException, JsonSyntaxException, JsonIOException, ParseException {
    log.info("Building JSON");
    connections.add(conn);
    JSONObject dirJson = buildJSONFromConnection(conn);      
    if (dirJson != null) {
      directoryJsons.add(dirJson);
    } else {
      throw new JsonSyntaxException("JSON file wasn't generated properly");
    }
    writeToJSONFile(dirJson, conn);
  }

  private void writeToJSONFile(JSONObject jsonObj, Connection conn) throws FileNotFoundException, UnsupportedEncodingException, IOException {
    if (conn.type == InputType.JSON)
      return;
    File file = new File(conn.url);
    PrintWriter writer = new PrintWriter(file.getParentFile().getCanonicalPath() + "/" + file.getName() + ".json", "UTF-8");
    writer.print(jsonObj);
    writer.close();
  }

  private JSONObject buildJSONFromConnection(Connection conn) throws JsonIOException, JsonSyntaxException, FileNotFoundException, ParseException {
    JSONObject dirJson = null;
    if (conn.type == InputType.SSH) {
      JSONBuilderSSH sshBuilder = new JSONBuilderSSH(conn);
      try {
        dirJson = sshBuilder.getJsonFromDirectory();
      } catch (NoSuchAlgorithmException | IOException e) {
        // TODO
        e.printStackTrace();
      }
    } else if (conn.type == InputType.LOCAL) {
      JSONBuilderLocal localBuilder = new JSONBuilderLocal(conn);
      try {
        dirJson = localBuilder.getJsonFromDirectory();
      } catch (NoSuchAlgorithmException | IOException e) {
        // TODO
        e.printStackTrace();
      }
    } else if (conn.type == InputType.JSON) {
      JsonParser parser = new JsonParser();
      
      JsonElement jsonElement = parser.parse(new FileReader(conn.url));
      JsonObject jsonObj = jsonElement.getAsJsonObject();
      
      JSONParser returnParser = new JSONParser();
      dirJson = (JSONObject) returnParser.parse(jsonObj.toString());
    }
    
    return dirJson;
  }
  
  public static Connection promptJSONSource() {
    return new Connection(JOptionPane.showInputDialog(null, "Enter the Source Folder to copy from"), null, null, null);
  }
  
}
