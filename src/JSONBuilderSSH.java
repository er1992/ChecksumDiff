
public class JSONBuilderSSH implements JSONBuilder {

  Connection conn = null;
  
  public JSONBuilderSSH(Connection conn) {
    this.conn = conn;
  }
  
}
