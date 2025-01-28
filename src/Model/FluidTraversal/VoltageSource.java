package Model.FluidTraversal;

public class VoltageSource extends Component {

    private double voltage;

    protected VoltageSource(double voltage){
        super(0);
        this.voltage = voltage;
    }

    public double getVoltage(){
        return voltage;
    }

    protected void setVoltage(double voltage){
        this.voltage = voltage;
    }
}
