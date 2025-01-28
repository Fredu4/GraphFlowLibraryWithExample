package Model.FluidTraversal;

public class Connector{

    private final Component component;
    private Junction junction;

    public Connector(Component component){
        this.component = component;
    }

    protected void connect(Junction junction){
        this.junction = junction;
    }
    protected void disconnect(){
        this.junction = null;
    }

    public Component getComponent(){
        return component;
    }

    public Junction getJunction(){
        return junction;
    }
}
