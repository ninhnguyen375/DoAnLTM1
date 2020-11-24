package client.dijkstra;

import client.dijkstra.Constant;
import javax.swing.JPanel;

import java.awt.*;
import java.util.ArrayList;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.*;

public class NodeGraph extends JPanel {

    JPanel panel = new JPanel(new GridLayout()) {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Constant.nodeGraphWidth, Constant.mainHeight);
        }
    };
    private String[] path = {};
    private ArrayList<MEdge> edges = new ArrayList<>();

    public NodeGraph(String[] path, ArrayList<MEdge> edges) {
        this.path = path;
        this.edges = edges;

        init();
    }

    private void init() {
        // Using swing for library GraphStream
        System.setProperty("org.graphstream.ui", "swing");

        // Create new Graph
        Graph graph = new SingleGraph("Node Graph", false, true);

        // Apply file graphstyle.css to graph
        graph.setAttribute("ui.stylesheet", "url('src/main/java/client/dijkstra/graphstyle.css')");

        // Gender Edges & Nodes
        for (MEdge edge : edges) {
            Node a = graph.addNode(edge.getSrc());
            Node b = graph.addNode(edge.getDest());
            Edge e = graph.addEdge(edge.getSrc() + edge.getDest(), a, b, true);
            e.setAttribute("ui.label", edge.getWeight());
        }

        // Add label for each Node
        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }

        // Active shorted path with color red
        for (int i = 0; i < path.length; i++) {
            String nodeId = path[i];
            String nextNodeId = "";
            try {
                nextNodeId = path[i + 1];
            } catch (Exception e) {
            }
            // active node
            graph.getNode(nodeId).setAttribute("ui.class", "active");

            // active edge
            if (!nextNodeId.isEmpty()) {
                graph.getEdge(nodeId + nextNodeId).setAttribute("ui.class", "active");
            }
        }

        SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

        panel.add(viewPanel);

        this.add(panel);
    }
}
