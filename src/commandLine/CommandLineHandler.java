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
  
  public CommandLineHandler(App app) {
    this.app = app;
  }
  
  public void getConnectionsFromArgs(List<String> args) throws JsonSyntaxException, JsonIOException, FileNotFoundException, UnsupportedEncodingException, IOException, ParseException {
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
