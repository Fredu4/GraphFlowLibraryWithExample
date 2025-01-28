package FlowNetwork;

import java.util.HashMap;
import java.util.HashSet;


public class TransitionTree<T> {

    private class TreeNode{
        Vertex<T> vertex;
        State<T> state;
        TreeNode parent;
        HashSet<TreeNode> children;
        boolean hasPath = false;

        //Constructor for root
        private TreeNode(T source, Vertex<T> direction){
            this.vertex = direction;
            this.state = new State<T>(nextStateNumber++);
            vertexToState.put(vertex, state);
            for(T newTransition : direction.getEdges()){
                if(newTransition == source) continue;
                TreeNode child = new TreeNode(this, direction.vertexThrough(newTransition));
                if(!child.hasPath) continue;
                this.state.addTransition(newTransition, child.state);
            }
        }
        //Constructor for children
        private TreeNode(TreeNode parent, Vertex<T> vertex){
            this.vertex = vertex;
            this.parent = parent;
            this.state = vertexToState.getOrDefault(vertex, new State<T>(nextStateNumber++));
            vertexToState.put(vertex, state);
            if(vertex == finalVertex){
                this.hasPath = true;
                return;
            }
            for(T newTransition : vertex.getEdges()){
                Vertex<T> nextVertex = vertex.vertexThrough(newTransition);
                if(inAncestors(nextVertex)) continue;
                TreeNode child = new TreeNode(this, nextVertex);
                if(!child.hasPath) continue;
                this.state.addTransition(newTransition, child.state);
                this.hasPath = true;
            }
        }

        private boolean inAncestors(Vertex<T> vertex){
            if(vertex == this.vertex) return true;
            if(this.parent == null) return false;
            return this.parent.inAncestors(vertex);
        }
    }
    private final TreeNode root;
    private final Vertex<T> finalVertex;
    private final HashMap<Vertex<T>, State<T>> vertexToState;
    private int nextStateNumber = 0;

    public TransitionTree(T root, Vertex<T> direction) {
        this.vertexToState = new HashMap<Vertex<T>, State<T>>();
        this.root = new TreeNode(root, direction);
        this.finalVertex = direction.vertexThrough(root);
    }


}
