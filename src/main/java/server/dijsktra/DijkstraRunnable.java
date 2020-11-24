package server.dijsktra;

import com.google.gson.Gson;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DijkstraRunnable implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();
    private ArrayList<MEdge> edges = new ArrayList<>();
    boolean isValid = true;
    private final String dijkstraFilePath = "src/main/java/server/serverfile.txt";

    public DijkstraRunnable(Socket socket) throws IOException {
        this.socket = socket;
    }

    public String getStringEdgesFromFile() throws FileNotFoundException {
        File file = new File(dijkstraFilePath);
        String line;
        ArrayList<MEdge> newEdges = new ArrayList<>();

        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name())) {
            while (sc.hasNextLine()) {
                line = sc.nextLine();

                // validate line
                String[] items = line.split("-");
                if (items.length != 3 || items[0].isBlank() || items[1].isBlank() || items[2].isBlank()) {
                    return "error: invalid file(each line must be 'src-dest-weight')";
                }

                String src = items[0];
                String dest = items[1];
                int weight = 0;
                try {
                    weight = Integer.parseInt(items[2]);
                } catch (Exception e) {
                    return "error: invalid file(weight must be a number)";
                }

                newEdges.add(new MEdge(src, dest, weight));
            }
        }

        return gson.toJson(newEdges);
    }

    public boolean validateGraph(String startNode, String endNode) {
        // Init graph
        GraphBuilder<String, Integer> g = GraphBuilder.<String, Integer>create();
        // Connect edge to graph
        for (int i = 0; i < edges.size(); i++) {
            MEdge edge = edges.get(i);
            g.connect(edge.getSrc()).to(edge.getDest()).withEdge(edge.getWeight());
        }

        HipsterGraph<String, Integer> graph = g.createUndirectedGraph();

        Iterable<String> vertices = graph.vertices();
        String mainNode = startNode.isBlank() ? edges.get(0).getSrc() : startNode;

        // Create the search problem from mainNode
        SearchProblem p = GraphSearchProblem
                .startingFrom(mainNode)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        // Validate if has endNode
        if (!endNode.isBlank()) {
            Algorithm.SearchResult result = Hipster.createDijkstra(p).search(endNode);
            List<String> path = (List<String>) result.getOptimalPaths().get(0);
            if (!path.contains(endNode)) {
                isValid = false;
                return false;
            }
        } else {
            // Valid graph is each node has path from mainNode to destNode
            vertices.forEach((destNode) -> {
                // Search the shortest path from "A" to "C"
                Algorithm.SearchResult result = Hipster.createDijkstra(p).search(destNode);
                List<String> path = (List<String>) result.getOptimalPaths().get(0);
                if (!path.contains(destNode)) {
                    isValid = false;
                    return;
                }
            });
        }

        return isValid;
    }

    private List<String> getShortestPath(String startNode, String endNode) {
        try {
            // Init graph
            GraphBuilder<String, Integer> g = GraphBuilder.<String, Integer>create();
            // Connect edge to graph
            for (int i = 0; i < edges.size(); i++) {
                MEdge edge = edges.get(i);
                g.connect(edge.getSrc()).to(edge.getDest()).withEdge(edge.getWeight());
            }

            HipsterGraph<String, Integer> graph = g.createUndirectedGraph();

            SearchProblem p = GraphSearchProblem
                    .startingFrom(startNode)
                    .in(graph)
                    .takeCostsFromEdges()
                    .build();

            // Search the shortest path from "A" to "C"
            Algorithm.SearchResult result = Hipster.createDijkstra(p).search(endNode);
            List<String> path = (List<String>) result.getOptimalPaths().get(0);
            System.out.println(path);
            return path;
        } catch (Exception e) {
            System.out.println("Get shortest path fail");
            return null;
        }
    }

    private void applyEdgesFromJsonString(String string) {
        List list = gson.fromJson(string, List.class);
        ArrayList<MEdge> newEdges = new ArrayList<>();

        for (Object objEdge : list) {
            MEdge edge = gson.fromJson(gson.toJson(objEdge), MEdge.class);
            newEdges.add(edge);
        }

        this.edges = newEdges;
    }

    @Override
    @SuppressWarnings("ConvertToTryWithResources")
    public void run() {
        try {
            System.out.println("New client Connected");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String input = in.readUTF();

                // get-shortest-path-A-B
                if (input.contains("get-shortest-path")) {
                    String startNode = input.split("-")[3];
                    String endNode = input.split("-")[4];
                    
                    String jsonString = in.readUTF();
                    applyEdgesFromJsonString(jsonString);
                    
                    if (validateGraph(startNode, endNode)) {
                        List<String> result = getShortestPath(startNode, endNode);
                        if (result == null) {
                            out.writeUTF("error: func err.");
                            out.flush();
                        } else {
                            out.writeUTF(gson.toJson(result));
                            out.flush();
                        }
                    } else {
                        out.writeUTF("error: Your nodes invalid or does not have a path.");
                    }
                }

                if (input.equals("bye")) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
