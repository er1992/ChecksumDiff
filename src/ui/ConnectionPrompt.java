package ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javafx.stage.FileChooser;
import pojo.Connection;

public class ConnectionPrompt extends JFrame {

  private enum StatusType {
    INSTRUCTION, INFO
  }
  
  private static final long serialVersionUID = 3932350688147644588L;
  private JFrame mainFrame;
  private JLabel headerLabel;
  private App app;
  private int stateCounter;
  private File lastBrowsedDir;

  public ConnectionPrompt(App app) {
    this.app = app;
    this.stateCounter = 0;
    lastBrowsedDir = null;
  }

  public void initUI() {
    mainFrame = new JFrame("Connection Prompt");
    mainFrame.setSize(400, 200);
    mainFrame.setLayout(new GridLayout(4, 1));
    mainFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {
        System.exit(0);
      }
    });

    headerLabel = new JLabel("", JLabel.CENTER);
    headerLabel.setFont(new Font("Serif", Font.BOLD, 15));

    setHeaderLabel("Choose the source", StatusType.INSTRUCTION);

    mainFrame.add(headerLabel);
    generateConnectionPromptRow("Process source connection and proceed");
    generateConnectionPromptRow("Process target connection and proceed");
    
    JButton fileExemptionChooserButton = new JButton("Add an exempted folder");
    fileExemptionChooserButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (lastBrowsedDir != null) {
          fc.setCurrentDirectory(lastBrowsedDir);
        }
        File selectedFile = openFileChooser(fc);
        if (selectedFile == null) {
          return;
        }
        lastBrowsedDir = selectedFile.getParentFile();
        try {
          app.addFileExemption(selectedFile.getCanonicalPath());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    
    mainFrame.setVisible(true);
    mainFrame.add(fileExemptionChooserButton);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void generateConnectionPromptRow(String processButtonText) {
    final JButton fileChooserButton = new JButton("Select a Folder");
    JPanel connectionPanel = new JPanel();
    final DefaultComboBoxModel connectionNames = new DefaultComboBoxModel();
    final JComboBox connectionsCombo = new JComboBox(connectionNames);
    JScrollPane connectionsListScrollPane = new JScrollPane(connectionsCombo);
    final JFileChooser fc = new JFileChooser();
    
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    connectionPanel.setLayout(new FlowLayout());
    
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
          fileChooserButton.setEnabled(true);
        } else if (selectedText.equalsIgnoreCase("JSON")) {
          fileChooserButton.setText("Select a File");
          fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooserButton.setEnabled(true);
        } else if (selectedText.equalsIgnoreCase("SSH")) {
          fileChooserButton.setText("N/A");
          fileChooserButton.setEnabled(false);
          fc.setEnabled(false);
        }
      }
    });
    connectionPanel.add(connectionsListScrollPane);
    
    fileChooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lastBrowsedDir != null) {
          fc.setCurrentDirectory(lastBrowsedDir);
        }
        File selectedFile = openFileChooser(fc);
        if (selectedFile == null) {
          return;
        }
        lastBrowsedDir = selectedFile.getParentFile();
        stateCounter += 1;
        ( (JButton) e.getSource()).setEnabled(false);
        ( (JButton) e.getSource()).paintImmediately( ((JButton) e.getSource()).getVisibleRect() );
        connectionsCombo.setEnabled(false);
        connectionsCombo.paintImmediately(connectionsCombo.getVisibleRect());
        buildConnection(selectedFile, null, connectionsCombo.getItemAt(connectionsCombo.getSelectedIndex()).toString(), null);
        if (stateCounter == 2) {
          setHeaderLabel("Building the directory");
          app.buildDir();
          setHeaderLabel("Deploy folder has been created");
        }
      }
    });
    connectionPanel.add(fileChooserButton);
        
    mainFrame.add(connectionPanel);
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
    setHeaderLabel("Choose the target", StatusType.INSTRUCTION);
  }

  public void setHeaderLabel(String headerLabelText, StatusType status) {
    if (status == StatusType.INFO) {
      headerLabel.setForeground(Color.BLACK);
    } else if (status == StatusType.INSTRUCTION) {
      headerLabel.setForeground(Color.RED);
    }
    this.headerLabel.setText(headerLabelText);
    this.headerLabel.paintImmediately(this.headerLabel.getVisibleRect());
  }
  
  public void setHeaderLabel(String headerLabelText) {
    this.setHeaderLabel(headerLabelText, StatusType.INFO);
  }
  
  private File openFileChooser(final JFileChooser fc) {
    fc.showOpenDialog(this);
    return fc.getSelectedFile();
  }

}
