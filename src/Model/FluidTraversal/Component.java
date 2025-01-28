package Model.FluidTraversal;

public abstract class Component {

    private Connector input;
    private Connector output;

    private double resistance;

    public Component(double resistance){
        this.input = new Connector(this);
        this.output = new Connector(this);
        this.resistance = resistance;
    }

    public double getResistance(){
        return resistance;
    }

    protected void setResistance(double resistance){
        this.resistance = resistance;
    }

    public Connector getInput(){
        return this.input;
    }

    public Connector getOutput(){
        return this.output;
    }

}
