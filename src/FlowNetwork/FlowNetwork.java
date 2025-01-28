package FlowNetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FlowNetwork<T>{

    private final HashMap<Edge, T> toObject;
    private final HashMap<T, Edge> toResistor;
    private final HashMap<Vertex<T>, Node> toNode;
    private final HashMap<T, Flow> flowSources;

    public FlowNetwork(Set<? extends Vertex<T>> graphUnknownType, Function<T, Double> resistanceOfObject){
        Set<Vertex<T>> graphKnownType = (Set<Vertex<T>>) graphUnknownType;
        this.toObject = new HashMap<>();
        this.toResistor = new HashMap<>();
        this.toNode = new HashMap<>();
        this.flowSources = new HashMap<>();
        cloneGraph(graphKnownType, resistanceOfObject);
    }

    //TODO: optimize cloning
    private void cloneGraph(Set<Vertex<T>> graph, Function<T, Double> function){
        HashSet<T> allEdges = new HashSet<>();
        for(Vertex<T> v : graph){ //creates new Junctions and collects all edges<T>
            toNode.put(v, new Node());
            allEdges.addAll(v.getEdges());
        }
        for(T edge : allEdges){
            Edge newEdge = new Edge(function.apply(edge));
            toObject.put(newEdge, edge);
            toResistor.put(edge, newEdge);
        }
        for(Vertex<T> v : graph) //Adds new edges to new vertices and connects them
            for(T edge : v.getEdges()){
                Edge resistor = toResistor.get(edge);
                if(resistor.in == null){
                    Node j1 = toNode.get(v);
                    Node j2 = toNode.get(v.vertexThrough(edge));
                    resistor.in = j1;
                    resistor.out = j2;
                    j1.add(resistor);
                    j2.add(resistor);
                }
            }
    }

    /**
     * Adds a new flow source to the network
     * @param source    The edge which generates the potential difference.
     * @param pressure  the potential difference between the vertices that are connected to the source edge
     * @param direction the vertex which has the positive pressure difference
     * @return the equivalent weight of the loops around the source
     */
    public double setPressure(T source, double pressure, Vertex<T> direction){
        if(flowSources.containsKey(source)) throw new RuntimeException("This edge is already pressure source!");
        Edge pressureSource = toResistor.get(source);
        Node directionNode = toNode.get(direction);
        if(pressureSource == null) throw new RuntimeException("This edge does not exist in network!");
        if(directionNode == null) throw new RuntimeException("This vertex does not exist in network!");
        Flow flow = new Flow(pressureSource, directionNode);
        flow.setPressure(pressure);
        this.flowSources.put(source, flow);
        return flow.getEquivalentResistance();
    }

    /**
     * @return the laminar flow through every edge in the graph
     */
    public HashMap<T, Double> flowsThrough(){
        HashMap<T, Double> flowsThrough = new HashMap<>();
        for(Flow flow : this.flowSources.values()){
            for(Edge edge : toResistor.values()){
                if(!flow.contains(edge)) continue;
                T asObject = toObject.get(edge);
                double currentFlow = flowsThrough.getOrDefault(asObject, 0.0);
                currentFlow += flow.flowThrough(edge);
                flowsThrough.put(asObject, currentFlow);
            }
        }
        flowsThrough.replaceAll((k, v) -> Math.abs(v));
        return flowsThrough;
    }


}
