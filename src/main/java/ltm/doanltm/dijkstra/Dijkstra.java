/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ltm.doanltm.dijkstra;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ninhn
 */
public class Dijkstra {

    private ArrayList<MEdge> edges = new ArrayList<>();
    private boolean isValid = true;

    public Dijkstra(ArrayList<MEdge> edges) {
        this.edges = edges;
    }

    public boolean validateGraph() {
        // Init graph
        GraphBuilder<String, Integer> g = GraphBuilder.<String, Integer>create();
        // Connect edge to graph
        for (int i = 0; i < edges.size(); i++) {
            MEdge edge = edges.get(i);
            g.connect(edge.getSrc()).to(edge.getDest()).withEdge(edge.getWeight());
        }

        HipsterGraph<String, Integer> graph = g.createUndirectedGraph();

        // Valid graph is each node has path from mainNode to destNode
        Iterable<String> vertices = graph.vertices();
        String mainNode = edges.get(0).getSrc(); // select any node from edges

        vertices.forEach((destNode) -> {
        // Create the search problem
            SearchProblem p = GraphSearchProblem
                    .startingFrom(mainNode)
                    .in(graph)
                    .takeCostsFromEdges()
                    .build();

            // Search the shortest path from "A" to "C"
            Algorithm.SearchResult result = Hipster.createDijkstra(p).search(destNode);
            List<String> path = (List<String>) result.getOptimalPaths().get(0);
            if(!path.contains(destNode)) {
                isValid = false;
                return;
            }
        });
        
        return isValid;
    }

    public static void main(String[] args) {
        ArrayList<MEdge> edges = new ArrayList<>();
        
        edges.add(new MEdge("A", "B", 2));
        edges.add(new MEdge("B", "C", 2));
        
        Dijkstra d = new Dijkstra(edges);
        System.out.println(d.validateGraph());
    }
}
