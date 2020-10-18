package ltm.doanltm.dijkstra;

import javax.swing.JPanel;

import java.awt.*;
import java.util.HashMap;
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
    private HashMap<String, Integer> edges = new HashMap<>();

    public NodeGraph(String[] path, HashMap<String, Integer> edges) {
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
        graph.setAttribute("ui.stylesheet", "url('src/main/java/ltm/doanltm/dijkstra/graphstyle.css')");

        // Gender Edges & Nodes
        for (String edge : edges.keySet()) {
            Node a = graph.addNode(edge.charAt(0) + "");
            Node b = graph.addNode(edge.charAt(1) + "");
            Edge e = graph.addEdge(edge, a, b, true);
            e.setAttribute("ui.label", edges.get(edge));
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
