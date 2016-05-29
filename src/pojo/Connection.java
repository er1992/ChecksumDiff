package pojo;

import com.sun.istack.internal.Nullable;

import app.App;

public class Connection {
  public String url;
  public String user;
  public App.InputType type;
  public Integer port = null;
  
  
  public Connection(String url, @Nullable String user, App.InputType type, @Nullable Integer port) {
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
    this.type = App.InputType.SSH;
    this.port = 22;
  }

  public boolean valid() {
    if (type == App.InputType.SSH) {
      return (this.type != null && this.port != null && this.user != null && this.url != null && !this.url.isEmpty());
    } else if (type == App.InputType.LOCAL) {
      return (this.type != null && this.url != null && !this.url.isEmpty());
    } else if (type == App.InputType.JSON) {
      // TODO
      return (this.type != null && this.url != null && !this.url.isEmpty());
    } else {
      return false;
    }
  };
}
