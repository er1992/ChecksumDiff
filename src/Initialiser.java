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

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


public class Initialiser {
  
  public enum InputType {
    SSH, LOCAL, JSON
  }
  
  public static void main(String[] args) {

    List<Connection> connections = new ArrayList<>();
    List<JSONObject> directoryJsons = new ArrayList<>();
    
    // No arguments supplied then use prompt
    if (args.length == 0) {
      for (int i = 0 ; i < 2 ; i++) {
        Connection conn = promptConnection();
        try {
          buildJSON(connections, directoryJsons, conn);
        } catch (JsonSyntaxException e) {
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
        } catch (JsonIOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } else {
      // TODO
      // Create Connection object based on the details input from the Command Line
      // Pass to builders accordingly
      List<String> argsList = Arrays.asList(args);
      
      for (int i = 1; i < 3; i++) {
        if ( !argsList.contains("-t" + i) || !argsList.contains("-u" + i) ) {
          System.exit(-1);
          // TODO proper error handling
        } else {
          Connection conn = new Connection();
          conn.type = InputType.valueOf(argsList.get(argsList.indexOf("-t" + i) + 1).toUpperCase());
          conn.url = argsList.get(argsList.indexOf("-u" + i) + 1);
          connections.add(conn);
          try {
            buildJSON(connections, directoryJsons, conn);
          } catch (JsonSyntaxException e) {
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
          } catch (JsonIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        }
      }
    }
    
    if (connections.get(0).valid() && connections.get(1).valid()) {
      JSONObject diff = null;
      DirectoryBuilder dirBuilder = null;
      if (connections.get(0).type == InputType.JSON && connections.get(1).type == InputType.JSON) {
        JSONDirectoryComparator comparator = new JSONDirectoryComparator(directoryJsons.get(0), directoryJsons.get(1));
        diff = comparator.compareDirectories();
        dirBuilder = new DirectoryBuilder(promptJSONSource());
      } else if (connections.get(0).type == InputType.LOCAL && connections.get(1).type == InputType.JSON) {
        JSONDirectoryComparator comparator = new JSONDirectoryComparator(directoryJsons.get(0), directoryJsons.get(1));
        diff = comparator.compareDirectories();
        dirBuilder = new DirectoryBuilder(connections.get(0));
      }
      try {
        dirBuilder.buildDirectoryFromJson(diff);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }

  private static void buildJSON(List<Connection> connections, List<JSONObject> directoryJsons, Connection conn) throws FileNotFoundException, UnsupportedEncodingException, IOException, JsonSyntaxException, JsonIOException, ParseException {
    connections.add(conn);
    JSONObject dirJson = buildJSONFromConnection(conn);      
    if (dirJson != null) {
      directoryJsons.add(dirJson);
    } else {
      throw new JsonSyntaxException("JSON file wasn't generated properly");
    }
    writeToJSONFile(dirJson, conn);
  }

  private static void writeToJSONFile(JSONObject jsonObj, Connection conn) throws FileNotFoundException, UnsupportedEncodingException, IOException {
    if (conn.type == InputType.JSON)
      return;
    File file = new File(conn.url);
    PrintWriter writer = new PrintWriter(file.getParentFile().getCanonicalPath() + "/" + file.getName() + ".json", "UTF-8");
    writer.print(jsonObj);
    writer.close();
  }

  private static JSONObject buildJSONFromConnection(Connection conn) throws JsonIOException, JsonSyntaxException, FileNotFoundException, ParseException {
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
  
  public static boolean promptJSON() {
    String[] jsonQuestionValues = {"YES", "NO"};
    Object jsonQuestionSelected = JOptionPane.showInputDialog(null, "Do you already have the JSON files?", "Selection", JOptionPane.DEFAULT_OPTION, null, jsonQuestionValues, "NO");
    return jsonQuestionSelected.toString().equalsIgnoreCase("yes") ? true : false;
  }

  public static InputType promptConnectionType() {
    String[] connectionQuestionValues = Arrays.copyOf(InputType.values(), InputType.values().length, String[].class);
    Object jsonQuestionSelected = JOptionPane.showInputDialog(null, "Do you already have the JSON files?", "Selection", JOptionPane.DEFAULT_OPTION, null, connectionQuestionValues, "NO");
    return InputType.valueOf(jsonQuestionSelected.toString());
  }
  
  public static Connection promptJSONSource() {
    return new Connection(JOptionPane.showInputDialog(null, "Enter the Source Folder to copy from"), null, null, null);
  }
  
  public static Connection promptConnection() {
    Connection conn = new Connection();
    
    JTextField urlField = new JTextField(5);
    JTextField userField = new JTextField(5);
    JTextField typeField = new JTextField(5);
    JTextField portField = new JTextField(5);
    
    JPanel myPanel = new JPanel();
    myPanel.add(new JLabel("Type: (SSH, LOCAL, JSON)"));
    myPanel.add(typeField);
    myPanel.add(Box.createHorizontalStrut(10)); // a spacer
    myPanel.add(new JLabel("URL:"));
    myPanel.add(urlField);
    myPanel.add(Box.createVerticalStrut(10)); // a spacer
    myPanel.add(new JLabel("User:"));
    myPanel.add(userField);
    myPanel.add(Box.createVerticalStrut(10)); // a spacer
    myPanel.add(new JLabel("Port:"));
    myPanel.add(portField);

    int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter the values", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      if (!typeField.getText().isEmpty()) {
        conn.type = InputType.valueOf(typeField.getText().toUpperCase());
      } else {
        System.exit(-1);
      }
      if (!urlField.getText().isEmpty()) {
        conn.url = urlField.getText();
      } else {
        System.exit(-1);
      }
      if (!userField.getText().isEmpty()) {
        conn.user = userField.getText();
      }
      if (!portField.getText().isEmpty()) {
        conn.port = Integer.parseInt(portField.getText());
      }
    } else {
      // TODO Proper error handling
      System.exit(-1);
    }

    return conn;
  }
  
}
