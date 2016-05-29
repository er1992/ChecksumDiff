package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.json.simple.parser.ParseException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import app.App;
import pojo.Connection;

public class ConnectionPrompt extends JFrame {

  private static final long serialVersionUID = 3932350688147644588L;
  private JFrame mainFrame;
  private JLabel headerLabel;
  private App app;
  private int stateCounter;

  public ConnectionPrompt(App app) throws HeadlessException {
    this.app = app;
    this.stateCounter = 0;
  }

  public void initUI() {
    mainFrame = new JFrame("Connection Prompt");
    mainFrame.setSize(400, 400);
    mainFrame.setLayout(new GridLayout(6, 1));
    mainFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {
        System.exit(0);
      }
    });

    headerLabel = new JLabel("", JLabel.CENTER);
    setHeaderLabel("Choose the source");

    mainFrame.add(headerLabel);
    generateConnectionPromptRow("Process source connection and proceed");
    generateConnectionPromptRow("Process target connection and proceed");
    mainFrame.setVisible(true);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void generateConnectionPromptRow(String processButtonText) {
    JButton processButton = new JButton(processButtonText);
    JButton fileChooserButton = new JButton("Select a Folder");
    JPanel controlPanel = new JPanel();
    final DefaultComboBoxModel connectionNames = new DefaultComboBoxModel();
    final JComboBox connectionsCombo = new JComboBox(connectionNames);
    JScrollPane connectionsListScrollPane = new JScrollPane(connectionsCombo);
    final JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    controlPanel.setLayout(new FlowLayout());
    
    connectionNames.addElement("LOCAL");
    connectionNames.addElement("JSON");
    connectionNames.addElement("SSH");

    connectionsCombo.setSelectedIndex(0);
    
    connectionsCombo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedText = connectionsCombo.getItemAt(connectionsCombo.getSelectedIndex()).toString();
        if (selectedText.equalsIgnoreCase("LOCAL") ) {
          fileChooserButton.setText("Select a Folder");
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if (selectedText.equalsIgnoreCase("JSON")) {
          fileChooserButton.setText("Select a File");
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else if (selectedText.equalsIgnoreCase("SSH")) {
          fileChooserButton.setText("N/A");
          fileChooserButton.setEnabled(false);
          fc.setEnabled(false);
        }
      }
    });

    processButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
    
    controlPanel.add(connectionsListScrollPane);
    fileChooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stateCounter += 1;
        buildConnection(openFileChooser(fc), null, connectionsCombo.getItemAt(connectionsCombo.getSelectedIndex()).toString(), null);
        if (stateCounter == 2) {
          setHeaderLabel("Building the directory");
          app.buildDir();
        }
      }
    });
    
    controlPanel.add(fileChooserButton);
    mainFrame.add(controlPanel);
    mainFrame.add(processButton);
  }

  private void buildConnection(File file, String user, String type, Integer port) {
    setHeaderLabel("Building JSON from " + type + " source");
    Connection conn;
    try {
      conn = new Connection(file.getCanonicalPath(), user, App.InputType.valueOf(type), null);
      app.buildJSON(conn);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonIOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    setHeaderLabel("Choose the target");
  }

  public void setHeaderLabel(String headerLabel) {
    this.headerLabel.setText(headerLabel);
    this.headerLabel.paintImmediately(this.headerLabel.getVisibleRect());
  }
  
  private File openFileChooser(final JFileChooser fc) {
    fc.showOpenDialog(this);
    return fc.getSelectedFile();
  }

}
