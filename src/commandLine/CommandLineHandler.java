package commandLine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import app.App;
import app.App.InputType;
import pojo.Connection;

public class CommandLineHandler {

  App app;
  List<String> args;
  
  public CommandLineHandler(App app, List<String> args) {
    this.args = args;
    if (args.get(0).equalsIgnoreCase("-h")) {
      App.log.info("Usage Example:");
      App.log.info("-t1 [Local, Json, SSH] -u1 [URL/Path to source folder/file] -t2 [Local, Json, SSH] -u2 [URL/Path to target folder/file] ");
      // TODO Throw an excepion and let App deal with it
      System.exit(0);
    }
    this.app = app;
  }
  
  public void getConnectionsFromArgs() throws JsonSyntaxException, JsonIOException, FileNotFoundException, UnsupportedEncodingException, IOException, ParseException {
    List<Connection> connections = new ArrayList<Connection>();
    
    for (int i = 1; i < 3; i++) {
      if ( !args.contains("-t" + i) || !args.contains("-u" + i) ) {
        System.exit(-1);
        // TODO proper error handling
      } else {
        Connection conn = new Connection();
        conn.type = InputType.valueOf(args.get(args.indexOf("-t" + i) + 1).toUpperCase());
        conn.url = args.get(args.indexOf("-u" + i) + 1);
        connections.add(conn);
        app.buildJSON(conn);
      }
    }
    
    app.buildDir();
    
  }

}
