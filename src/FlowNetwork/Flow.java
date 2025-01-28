package FlowNetwork;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

    /*
    Represents a discrete flow through a graph. Main functionality is the ability to calculate the flow through
    a specific edge of the graph. To instantiate a Flow you need the edge which is the source of the flow
    and the Node to which the flow flows.

    When instantiated the GraphParser parses the Graph into a flow in the form of a list of all valid paths and
    a transition graph of how a discrete flow element can move from Node to Node.

    To calculate the flow through each it uses the loop rule:
        The sum of all (Edge weight * flow through Edge) in a path must equal the pressure.
    To create a generalized Flow we call the pressure 1. These equations are not enough to calculate a unique solution so
    it must also utilize the junction rule:
        The sum of all incoming flows in a node must equal the outgoing flows.

    For every valid path the GraphParser found we can generate the loop equations and using the transition graph we
    can generate the junction equations. Then using the Jama library it solves the system of equations by Gaussian
    elimination and stores the general flows in the map flowsThroughEdge. By then setting a pressure, one can calculate
    the specific flow through the edge by multiplying the general flow with the pressure.

    The direction of the flow is also considered. If the flow through an edge passes in the direction Edge.in -> Edge.out
    the flow is set to positive, otherwise negative.

    The GraphParser is abstracted into its own class purely for readabilities’ sake.
     */
/**
 * @author Fredrik Simonsen
 * @version 1.0
 * @since 4.12.2024
 */

public class Flow {

    private static class GraphParser {
        /*
        This class parses a graph into a tree structure. And extracts all possible paths from root to leaf, as well as
        a transition graph that describes the direction of how a flow flows through the graph.

        When instantiated as TreeNode(Edge source, Node direction) it "removes" the source edge from the graph and calls
        the Node that the source Edge connects the direction Node to "finalNode". A TreeNode, root, is then created from
        the direction Node. Root then spawns children by creating child treeNodes from the Nodes that the root graph node
        is connected to. The children then also grow with the constraint that they cannot spawn a child that it has as
        an ancestor.

        Thus, all acyclic paths through the graph are traversed. If a child has a path to the finalNode the parent adds
        the child's path(to the finalNode) to its own paths. The root shall then contain all valid acyclic paths to the
        finalNode.

        Simultaneously states corresponding to all visited graph Nodes are created. If a TreeNode has a child with a
        valid path to finalNode it adds the transition self.node -> child.node to the state corresponding to its own
        graph Node.
         */
        private class TreeNode{
            State<Edge> state;
            Node vertex;
            TreeNode parent;
            boolean hasPath = false;
            ArrayList<ArrayList<Edge>> paths;

            //Constructs root
            private TreeNode(Edge source, Node direction){
                this.vertex = direction;
                this.parent = null;
                this.state = new State<Edge>(nextStateNumber++);
                this.paths = new ArrayList<>();
                vertexToState.put(vertex, this.state);
                for(Edge transition : direction.getEdges()){
                    if(transition == source) continue;
                    Node nextVertex = direction.vertexThrough(transition);
                    TreeNode child = new TreeNode(this, nextVertex);
                    if(!child.hasPath) continue;
                    allEdges.add(transition);
                    directionOf.put(transition, transition.out == nextVertex ? 1 : -1);
                    this.state.addTransition(transition, child.state);
                    for(ArrayList<Edge> path : child.paths) path.addFirst(transition);
                    this.paths.addAll(child.paths);
                    this.hasPath = true;
                }
            }

            //Constructs child
            private TreeNode(TreeNode parent, Node vertex){
                this.vertex = vertex;
                this.parent = parent;
                this.state = vertexToState.getOrDefault(this.vertex, new State<Edge>(nextStateNumber++));
                vertexToState.put(this.vertex, this.state);
                this.paths = new ArrayList<>();
                if(vertex == finalNode){
                    this.paths.add(new ArrayList<>());
                    this.hasPath = true;
                    return;
                }
                for(Edge transition : vertex.getEdges()){
                    Node nextVertex = vertex.vertexThrough(transition);
                    if(inAncestors(nextVertex)) continue;
                    TreeNode child = new TreeNode(this, nextVertex);
                    if(!child.hasPath) continue;
                    this.state.addTransition(transition, child.state);
                    allEdges.add(transition);
                    directionOf.put(transition, transition.out == nextVertex ? 1 : -1);
                    for(ArrayList<Edge> path : child.paths) path.addFirst(transition);
                    this.paths.addAll(child.paths);
                    this.hasPath = true;
                }
            }
            private boolean inAncestors(Node vertex){
                if(vertex == this.vertex) return true;
                if(this.parent == null) return false;
                return this.parent.inAncestors(vertex);
            }
        }

        private int nextStateNumber = 0;
        private final Node finalNode;
        private final HashMap<Node, State<Edge>> vertexToState;
        private final ArrayList<State<Edge>> states;
        private final HashSet<Edge> allEdges;
        private final HashMap<Edge, Integer> directionOf;
        private final ArrayList<ArrayList<Edge>> allPaths;

        private GraphParser(Edge source, Node direction) {
            this.vertexToState = new HashMap<>();
            this.allEdges = new HashSet<>();
            this.directionOf = new HashMap<>();
            this.finalNode = direction.vertexThrough(source);
            TreeNode root = new TreeNode(source, direction);
            this.states = new ArrayList<>(vertexToState.values());
            State<Edge> rootState = root.state;
            states.remove(rootState);
            states.addFirst(rootState);
            State<Edge> finalState = vertexToState.get(finalNode);
            states.remove(finalState);
            states.add(finalState);
            for(int i = 0; i < states.size(); i++){
                states.get(i).setStateNumber(i);
            }
            this.allPaths = root.paths;
        }
        private LADFA<Edge> toAutomata(){
            return new LADFA<Edge>(states, 0, vertexToState.get(finalNode).getStateNumber());
        }
    }

    private final HashSet<Edge> edgesInFlow;
    private final HashMap<Edge, Integer> directionOf;
    private final LADFA<Edge> automata;
    private final ArrayList<ArrayList<Edge>> paths;
    private final HashMap<Edge, Double> flowThroughEdge;
    private double pressure;
    private final double equivalentResistance;

    protected Flow(Edge source, Node direction){
        GraphParser factory = new GraphParser(source, direction);
        this.automata = factory.toAutomata();
        this.paths = factory.allPaths;
        this.edgesInFlow = factory.allEdges;
        this.directionOf = factory.directionOf;
        this.flowThroughEdge = new HashMap<>();
        this.calculateDiscreteCurrents();
        this.pressure = 1;
        //adding the source
        this.edgesInFlow.add(source);
        double sourceFlow = 0;
        State<Edge> sourceState = automata.getStates().get(automata.getStart()[0]);
        for(State<Edge> nextState : sourceState.nextStates())
            for(Edge transition : sourceState.transitionsBetween(nextState))
                sourceFlow += flowThroughEdge.get(transition);
        flowThroughEdge.put(source, sourceFlow);

        this.equivalentResistance = new EvaluableRegEx<Edge>(automata, e -> e.weight).evaluate();
    }

    protected void setPressure(double pressure){
        this.pressure = pressure;
    }

    protected double getEquivalentResistance(){
        return equivalentResistance;
    }

    // Kirchhoff junction rule: flow in = out, for every junction
    private HashMap<HashSet<Edge>, HashSet<Edge>> junctionEquations(){
        HashMap<HashSet<Edge>, HashSet<Edge>> equations = new HashMap<>();
        for(State<Edge> state : this.automata.getStates()){
            if(state.previousStates().isEmpty() || state.nextStates().isEmpty()) continue;
            HashSet<Edge> previous = new HashSet<>(state.previousStates().keySet());
            HashSet<Edge> next = new HashSet<>(state.getTransitions().keySet());
            equations.put(previous, next);
        }
        return equations;
    }

    // Creates a system of equations using loop rules and junction rules
    // Using the Jama library it then solves the equations and stores the
    // flow through every resistance in this.flowsThrough
    private void calculateDiscreteCurrents(){
        HashMap<Edge, Integer> matrixPosition = new HashMap<>();
        for(Edge e : edgesInFlow) matrixPosition.put(e, matrixPosition.size());
        int variableAmount = edgesInFlow.size();
        ArrayList<double[]> leftHandSide = new ArrayList<>();
        ArrayList<double[]> rightHandSide = new ArrayList<>();
        //Add all loop rule equations
        for(ArrayList<Edge> flow : paths){
            double[] equation = new double[variableAmount];
            Arrays.fill(equation, 0);
            for(Edge e : flow)
                equation[matrixPosition.get(e)] = e.weight;
            leftHandSide.add(equation);
            rightHandSide.add(new double[]{1});
        }
        //Add all junction rule equations
       HashMap<HashSet<Edge>, HashSet<Edge>> junctionEquations = junctionEquations();
        for(HashSet<Edge> incoming : junctionEquations.keySet()){
            double[] equation = new double[variableAmount];
            Arrays.fill(equation, 0);
            for(Edge e : incoming) equation[matrixPosition.get(e)] = 1;
            for(Edge e : junctionEquations.get(incoming)) equation[matrixPosition.get(e)] = -1;
            leftHandSide.add(equation);
            rightHandSide.add(new double[]{0});
        }
        // create and solve matrix
        Matrix lhs = new Matrix(leftHandSide.toArray(new double[leftHandSide.size()][]));
        Matrix rhs = new Matrix(rightHandSide.toArray(new double[rightHandSide.size()][]));
        Matrix currents = lhs.solve(rhs);
        for(Edge e : edgesInFlow){
            double flow = currents.get(matrixPosition.get(e), 0) * (this.directionOf.get(e));
            this.flowThroughEdge.put(e, flow);
        }
    }

    public boolean contains(Edge e){
        return this.edgesInFlow.contains(e);
    }

    public double flowThrough(Edge e){
        return this.flowThroughEdge.get(e) * pressure;
    }


}
