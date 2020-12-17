package client.dijkstra;

import client.Client;
import com.google.gson.Gson;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTextField;

public class MainDijkstra extends JPanel {

    private final Gson gson = new Gson();
    private String[] path = {};
    private final String[] emptyPath = {};
    private ArrayList<MEdge> edges = new ArrayList<>();
    boolean isValid = true;

    // --- Left side
    public JPanel nodeGraphPanel = new JPanel();

    // --- Right side
    public JPanel rightSidePanel = new JPanel();
    // Add node form
    public JPanel panelGroupInputEdge = new JPanel(new GridBagLayout());
    public JXTextField inputSrcNode = new JXTextField("Source Node");
    public JXTextField inputDestNode = new JXTextField("Dest Node");
    public JXTextField inputEdgeWeight = new JXTextField("Weight");
    public JButton enterButton = new JXButton("ADD");
    // Upload file form
    public JPanel panelUploadFile = new JPanel();
    public JLabel fileNameLabel = new JLabel();
    public JButton chooseFileButton = new JButton("Choose File");
    // Find shortest path form
    public JPanel panelFindPath = new JPanel(new GridBagLayout());
    public JXTextField inputStartNode = new JXTextField("start");
    public JXTextField inputEndNode = new JXTextField("end");
    public JButton findPathButton = new JButton("Find shortest path");
    // Export image button
    public JButton exportImageButton = new JButton("Export to Image");
    // Clear graph
    public JButton clearGraphButton = new JButton("Clear Graph");

    public MainDijkstra() {
        init();
    }

    private void init() {
        NodeGraph nodeGraph = new NodeGraph(path, edges);

        // --- RIGHT SIDE >>START
        // ----- panelGroupInputEdge >>START
        panelGroupInputEdge.setBorder(BorderFactory.createTitledBorder("Add An Edge"));
        panelGroupInputEdge.setPreferredSize(new Dimension(Constant.addNodeFormWidth - 20, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        // full width
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // x: col, y: row
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelGroupInputEdge.add(new JLabel("Source Node:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelGroupInputEdge.add(inputSrcNode, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelGroupInputEdge.add(new JLabel("Dest Node:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelGroupInputEdge.add(inputDestNode, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelGroupInputEdge.add(new JLabel("Weight:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelGroupInputEdge.add(inputEdgeWeight, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelGroupInputEdge.add(enterButton, gbc);
        // ----- panelGroupInputEdge >>END

        // ----- panelUploadFile >>START
        panelUploadFile.setBorder(BorderFactory.createTitledBorder("Upload File"));
        panelUploadFile.setPreferredSize(new Dimension(Constant.addNodeFormWidth - 20, 60));

        fileNameLabel.setPreferredSize(new Dimension(150, 23));
        panelUploadFile.add(fileNameLabel);
        panelUploadFile.add(chooseFileButton);
        // ----- panelUploadFile >>END

        // ----- panelFindPath >>START
        panelFindPath.setBorder(BorderFactory.createTitledBorder("Find Shortest Path"));
        panelFindPath.setPreferredSize(new Dimension(Constant.addNodeFormWidth - 20, 100));
        GridBagConstraints fc = new GridBagConstraints();

        // full width
        fc.weightx = 1;
        fc.fill = GridBagConstraints.HORIZONTAL;

        // x: col, y: row
        fc.gridx = 0;
        fc.gridy = 0;
        panelFindPath.add(new JLabel("Start Node:"), fc);
        fc.gridx = 1;
        fc.gridy = 0;
        panelFindPath.add(inputStartNode, fc);
        fc.gridx = 0;
        fc.gridy = 1;
        panelFindPath.add(new JLabel("End Node:"), fc);
        fc.gridx = 1;
        fc.gridy = 1;
        panelFindPath.add(inputEndNode, fc);
        fc.gridx = 0;
        fc.gridy = 2;
        fc.gridwidth = 2;
        panelFindPath.add(findPathButton, fc);
        // ----- panelFindPath >>END

        rightSidePanel.setPreferredSize(new Dimension(Constant.addNodeFormWidth, Constant.mainHeight));
        rightSidePanel.add(panelGroupInputEdge);
        rightSidePanel.add(panelUploadFile);
        rightSidePanel.add(panelFindPath);
        rightSidePanel.add(exportImageButton);
        rightSidePanel.add(clearGraphButton);

        // Events
        enterButton.addActionListener((e) -> {
            handleClickAddEdgeButton();
        });
        chooseFileButton.addActionListener((e) -> {
            handleClickChooseFileButton();
        });
        findPathButton.addActionListener((e) -> {
            handleClickFindPathButton();
        });
        exportImageButton.addActionListener((e) -> {
            handleClickExportImage();
        });
        clearGraphButton.addActionListener((e) -> {
            handleClearGraphClick();
        });
        // --- RIGHT SIDE >>END

        nodeGraphPanel.add(nodeGraph);

        this.setLayout(new GridBagLayout());
        this.setSize(Constant.mainWidth, Constant.mainHeight);

        this.add(nodeGraphPanel, new GridBagConstraints());
        this.add(rightSidePanel, new GridBagConstraints());
    }

    private void makePanelImage(Component panel, int width, int height, String pathname) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        try {
            ImageIO.write(image, "png", new File(pathname));
            JOptionPane.showMessageDialog(null, "Saved.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Make image fail");
            System.out.println(e);
        }
    }

    private void handleClickExportImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Specify a file to save");
        chooser.setSelectedFile(new File("fileToSave.png"));
        
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            makePanelImage(
                    nodeGraphPanel,
                    Constant.nodeGraphWidth,
                    Constant.mainHeight,
                    chooser.getSelectedFile().getPath());
            
        }
    }

    private MEdge validateEdgeValue(String src, String dest, String weight) {
        if (src.isBlank() || dest.isBlank() || weight.isBlank()) {
            JOptionPane.showMessageDialog(null, "Please filled the form.");
            return null;
        }
        
        

        // same value
        if (src.equals(dest)) {
            JOptionPane.showMessageDialog(null, "Can not put same nodes.");
            return null;
        }

        // is a number
        try {
            Integer.parseInt(weight);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please input edge weight by a number");
            return null;
        }
        
        if (Integer.parseInt(weight) < 1) {
            JOptionPane.showMessageDialog(null, "Must be >= 1.");
            return null;
        }

        return new MEdge(src, dest, Integer.parseInt(weight));
    }

    private void handleClickAddEdgeButton() {
        String src = inputSrcNode.getText();
        String dest = inputDestNode.getText();
        String weight = inputEdgeWeight.getText();

        MEdge edge = validateEdgeValue(src, dest, weight);

        if (edge != null) {
            ArrayList<MEdge> tmpEdges = (ArrayList<MEdge>) edges.clone();

            tmpEdges.add(edge);
            if (!validateGraph(tmpEdges)) {
                JOptionPane.showMessageDialog(null, "error: graph invalid or has nodes out of graph");
            } else {
                this.edges.add(edge);

                //clear inputs
                inputSrcNode.setText("");
                inputDestNode.setText("");
                inputEdgeWeight.setText("");

                regenerateGraph();
            }
        }
    }

    private void handleClickChooseFileButton() {
        JFileChooser chooser = new JFileChooser(new File(Constant.testFilesPath));

        int r = chooser.showOpenDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            String uploadFilePath = chooser.getSelectedFile().getAbsolutePath();

            try {
                applyEdgesFromFile(uploadFilePath);

                // regenerate graph layout
                regenerateGraph();
            } catch (FileNotFoundException ex) {
                System.out.println("Upload fail");
                System.out.println(ex);
            }

            fileNameLabel.setText(chooser.getSelectedFile().getName());
        } else {
            System.out.println("The user cancelled the operation");
        }
    }

    public void applyEdgesFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        String line;
        ArrayList<MEdge> newEdges = new ArrayList<>();

        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name())) {
            while (sc.hasNextLine()) {
                line = sc.nextLine();

                // validate line
                String[] items = line.split("-");
                if (items.length != 3 || items[0].isBlank() || items[1].isBlank() || items[2].isBlank()) {
                    JOptionPane.showMessageDialog(null, "error: invalid file(each line must be 'src-dest-weight')");
                }

                String src = items[0];
                String dest = items[1];
                int weight = 0;
                try {
                    weight = Integer.parseInt(items[2]);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "error: invalid file(weight must be a number)");
                    // reset graph
                    this.edges = new ArrayList<>();
                    this.path = emptyPath;
                    return;
                }

                newEdges.add(new MEdge(src, dest, weight));
            }
        }

        if (!validateGraph(newEdges)) {
            JOptionPane.showMessageDialog(null, "error: graph invalid or has nodes out of graph");
            // reset graph
            this.edges = new ArrayList<>();
            this.path = emptyPath;
        } else {
            this.edges = newEdges;
        }
    }

    public boolean validateGraph(ArrayList<MEdge> data) {
        isValid = true;

        // Init graph
        GraphBuilder<String, Integer> g = GraphBuilder.<String, Integer>create();
        // Connect edge to graph
        for (int i = 0; i < data.size(); i++) {
            MEdge edge = data.get(i);
            g.connect(edge.getSrc()).to(edge.getDest()).withEdge(edge.getWeight());
        }

        HipsterGraph<String, Integer> graph = g.createUndirectedGraph();

        // Valid graph is each node has path from mainNode to destNode
        Iterable<String> vertices = graph.vertices();
        String mainNode = data.get(0).getSrc(); // select any node from edges

        vertices.forEach((destNode) -> {
            // Create the search problem
            SearchProblem p = GraphSearchProblem
                    .startingFrom(mainNode)
                    .in(graph)
                    .takeCostsFromEdges()
                    .build();

            // Search the shortest path from "A" to "C"
            Algorithm.SearchResult result = Hipster.createDijkstra(p).search(destNode);
            List<String> optimalPath = (List<String>) result.getOptimalPaths().get(0);
            if (!optimalPath.contains(destNode)) {
                isValid = false;
            }
        });

        return isValid;
    }

    private void handleClickFindPathButton() {
        try {
            String startNode = inputStartNode.getText();
            String endNode = inputEndNode.getText();

            if (startNode.isBlank() || endNode.isBlank()) {
                JOptionPane.showMessageDialog(null, "Please filled the form.");
                return;
            }

            // call get shortest path
            Client.socketSend("get-shortest-path-" + startNode + "-" + endNode);
            // then send the list current edges
            Client.socketSend(gson.toJson(this.edges));

            String result = Client.socketReadLine();

            if (result.contains("error")) {
                JOptionPane.showMessageDialog(null, result);
                this.path = this.emptyPath;
                regenerateGraph();
            } else {
                String[] shortestPath = gson.fromJson(result, String[].class);
                this.path = shortestPath;
                regenerateGraph();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Not have any path");
            this.path = this.emptyPath;
            regenerateGraph();
            System.out.println(ex);
        }
    }

    private void regenerateGraph() {
        nodeGraphPanel.removeAll();

        NodeGraph newNodeGraph = new NodeGraph(path, edges);
        nodeGraphPanel.add(newNodeGraph);

        this.validate();
    }

    private void handleClearGraphClick() {
        nodeGraphPanel.removeAll();

        // empty path & edges
        path = emptyPath;
        edges = new ArrayList<>();

        NodeGraph newNodeGraph = new NodeGraph(path, edges);
        nodeGraphPanel.add(newNodeGraph);

        this.validate();
    }
}
