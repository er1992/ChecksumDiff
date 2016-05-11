import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class Initialiser {

  public enum InputType {
    SSH, LOCAL, JSON
  }
  
  public static void main(String[] args) {

    // No arguments supplied then use prompt
    if (args.length == 0) {
      if (promptJSON()) {
        
      } else {
        
        for (int i = 0 ; i < 2 ; i++) {
          Connection conn = promptConnection();
          switch(conn.type) {
            case SSH:
              JSONBuilderSSH sshBuilder = new JSONBuilderSSH(conn);
              break;
            case LOCAL:
              JSONBuilderLocal localBuilder = new JSONBuilderLocal(conn);
              break;
            case JSON:
              break;
            default:
          }
        }
      }
    } else {
    System.out.println("User cancelled");
    }
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
        conn.type = InputType.valueOf(typeField.getText());
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
    }

    return conn;
  }
  
}
