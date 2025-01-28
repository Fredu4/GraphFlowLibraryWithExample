package FlowNetwork;

import java.util.HashSet;

public class Node implements Vertex<Edge> {

    private final HashSet<Edge> edges;

    protected Node(){
        edges = new HashSet<>();
    }

    protected void add(Edge edge){
        edges.add(edge);
    }

    @Override
    public HashSet<Edge> getEdges(){
        return this.edges;
    }

    @Override
    public Node vertexThrough(Edge edge) {
        return edge.in == this ? edge.out : edge.in;
    }
}