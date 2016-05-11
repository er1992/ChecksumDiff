
public class JSONBuilderLocal implements JSONBuilder {
  Connection conn = null;
  
  public JSONBuilderLocal(Connection conn) {
    this.conn = conn;
  }
}
