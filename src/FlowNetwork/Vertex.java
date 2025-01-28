package FlowNetwork;
import java.util.Set;

public interface Vertex<T>{

    /**
     * @return all edges that connect Vertex to other vertices
     */
    Set<T> getEdges();

    /**
     * @param edge an edge that the vertex inBoundsAsOpen
     * @return the vertex that this vertex is linked to through edge
     */
    Vertex<T> vertexThrough(T edge);

}
