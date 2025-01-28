package Model.FluidTraversal;

import FlowNetwork.Vertex;

import java.util.HashSet;
import java.util.Set;

public class Junction implements Vertex<Component>{

    private final HashSet<Connector> connections;

    protected Junction(){
        this.connections = new HashSet<>();
    }

    protected void add(Connector connector){
        this.connections.add(connector);
    }

    protected void remove(Connector connector){
        this.connections.remove(connector);
    }

    protected void merge(Junction other){
        this.connections.addAll(other.connections);
        other.connections.addAll(this.connections);
    }

    @Override
    public Set<Component> getEdges(){
        HashSet<Component> connectionsTo = new HashSet<>();
        for(Connector connector : this.connections)
            connectionsTo.add(connector.getComponent());
        return connectionsTo;
    }

    @Override
    public Vertex<Component> vertexThrough(Component edge){
        if(!getEdges().contains(edge)) throw new RuntimeException("no such edge");
        if(edge.getInput().getJunction() == this) return edge.getOutput().getJunction();
        else return edge.getInput().getJunction();
    }


    public void giftConnections(Junction newJunction){
        for(Connector connector : this.connections){
            connector.connect(newJunction);
            newJunction.add(connector);
        }
    }

}
