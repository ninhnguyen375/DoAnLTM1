/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ltm.doanltm.dijkstra;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FileChooserUI;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTextField;

/**
 *
 * @author ninhn
 */
public class MainDijkstra extends JPanel {

    // --- Right side
    // Main panel
    JPanel rightSidePanel = new JPanel();
    // Input group panel
    JPanel panelGroupInputEdge = new JPanel(new GridBagLayout());
    JXTextField inputEdge = new JXTextField("Edge");
    JXTextField inputEdgeWeight = new JXTextField("Weight");
    JButton enterButton = new JXButton("ADD");
    // Upload file panel
    JPanel panelUploadFile = new JPanel();
    JButton uploadFileButton = new JButton("Upload File");
    // --- End Right side
    // Left side
    JPanel nodeGraphPanel = new JPanel();

    private static String[] path = {};
    private static HashMap<String, Integer> edges = new HashMap<>();

    public MainDijkstra() {
        init();
    }

    private void init() {
        NodeGraph nodeGraph = new NodeGraph(path, edges);

        // --- RIGHT SIDE >>START
        
        // ----- panelGroupInputEdge >>START
        panelGroupInputEdge.setBorder(BorderFactory.createTitledBorder("Add An Edge"));
        panelGroupInputEdge.setPreferredSize(new Dimension(Constant.addNodeFormWidth - 20, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        // full width
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // x: col, y: row
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelGroupInputEdge.add(new JLabel("Edge:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelGroupInputEdge.add(inputEdge, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelGroupInputEdge.add(new JLabel("Weight:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelGroupInputEdge.add(inputEdgeWeight, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelGroupInputEdge.add(enterButton, gbc);
        // ----- panelGroupInputEdge >>END
        
        // ----- panelUploadFile >>START
        panelUploadFile.setBorder(BorderFactory.createTitledBorder("Upload File"));
        panelUploadFile.setPreferredSize(new Dimension(Constant.addNodeFormWidth - 20, 60));
        
        panelUploadFile.add(uploadFileButton);
        // ----- panelUploadFile >>END
        
        rightSidePanel.setPreferredSize(new Dimension(Constant.addNodeFormWidth, Constant.mainHeight));
        rightSidePanel.add(panelGroupInputEdge);
        rightSidePanel.add(panelUploadFile);

        // Events
        enterButton.addActionListener((ActionEvent arg0) -> {
            String edgeValue = inputEdge.getText();
            String edgeWeightValue = inputEdgeWeight.getText();

            if (validateEdgeValue(edgeValue, edgeWeightValue)) {
                nodeGraphPanel.removeAll();

                edges.put(edgeValue, Integer.parseInt(edgeWeightValue));

                NodeGraph newNodeGraph = new NodeGraph(path, edges);
                nodeGraphPanel.add(newNodeGraph);

                //clear inputs
                inputEdge.setText("");
                inputEdgeWeight.setText("");

                this.validate();
            }
        });
        uploadFileButton.addActionListener((e) -> {
            // create an object of JFileChooser class 
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
  
            // invoke the showsOpenDialog function to show the save dialog 
            int r = chooser.showOpenDialog(null); 
  
            // if the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) 
  
            { 
                // set the label to the path of the selected file 
                System.out.println(chooser.getSelectedFile().getAbsolutePath());
            } 
            // if the user cancelled the operation 
            else
                System.out.println("The user cancelled the operation");
            
        });
        // --- RIGHT SIDE >>END

        nodeGraphPanel.add(nodeGraph);

        this.setLayout(new GridBagLayout());
        this.setSize(Constant.mainWidth, Constant.mainHeight);

        this.add(nodeGraphPanel, new GridBagConstraints());
        this.add(rightSidePanel, new GridBagConstraints());
    }

    private boolean validateEdgeValue(String edge, String edgeWeight) {
        // is not blank ("   ")
        if (edgeWeight.isBlank() || edge.isBlank()) {
            JOptionPane.showMessageDialog(null, "Please filled the form.");
            return false;
        }

        // is type of "AB" (length == 2)
        if (edge.length() != 2) {
            JOptionPane.showMessageDialog(null, "Invalid Edge value. Value must be XY (2 chars)");
            return false;
        }

        // is a number
        try {
            Integer.parseInt(edgeWeight);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please input edge weight by a number");
            return false;
        }

        return true;
    }
}
