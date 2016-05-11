import com.sun.istack.internal.Nullable;

public class Connection {
  String url;
  String user;
  Initialiser.InputType type;
  Integer port = null;
  
  
  public Connection(String url, @Nullable String user, Initialiser.InputType type, @Nullable Integer port) {
    super();
    this.url = url;
    this.user = user;
    this.type = type;
    if (port != null) {
      this.port = port;
    }
  }
  
  public Connection() {
    super();
    this.url = "localhost";
    this.user = "root";
    this.type = Initialiser.InputType.SSH;
    this.port = 22;
  };
}
