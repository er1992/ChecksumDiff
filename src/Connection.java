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
  }

  public boolean valid() {
    if (type == Initialiser.InputType.SSH) {
      return (this.type != null && this.port != null && this.user != null && this.url != null && !this.url.isEmpty());
    } else if (type == Initialiser.InputType.LOCAL) {
      return (this.type != null && this.url != null && !this.url.isEmpty());
    } else if (type == Initialiser.InputType.JSON) {
      // TODO
      return (this.type != null && this.url != null && !this.url.isEmpty());
    } else {
      return false;
    }
  };
}
