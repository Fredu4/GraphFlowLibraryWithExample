package FlowNetwork;

public class Edge {
    Node in;
    Node out;
    double weight;
    protected Edge(double weight){
        this.weight = weight;
    }
    @Override
    public String toString(){
        return "R(" + (int) weight + ")";
    }
}
