package server.dijsktra;

import com.google.gson.Gson;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Dijkstra {

    private final Gson gson = new Gson();
    private ArrayList<MEdge> edges = new ArrayList<>();
    boolean isValid = true;

    public Dijkstra(String edges) throws IOException {
        applyEdgesFromJsonString(edges);
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

    public List<String> getShortestPath(String startNode, String endNode) {
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

    public void applyEdgesFromJsonString(String string) {
        List list = gson.fromJson(string, List.class);
        ArrayList<MEdge> newEdges = new ArrayList<>();

        for (Object objEdge : list) {
            MEdge edge = gson.fromJson(gson.toJson(objEdge), MEdge.class);
            newEdges.add(edge);
        }

        this.edges = newEdges;
    }
}
