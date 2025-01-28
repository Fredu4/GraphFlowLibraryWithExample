package Model.FluidTraversal;

import java.util.HashSet;

public class Circuit {

    private final HashSet<Resistor> resistors;
    private final HashSet<VoltageSource> voltageSources;
    private final HashSet<Junction> junctions;

    public Circuit(){
        resistors = new HashSet<>();
        voltageSources = new HashSet<>();
        junctions = new HashSet<>();
    }

    public Resistor newResistor(){
        Resistor newResistor = new Resistor(5);
        resistors.add(newResistor);
        return newResistor;
    }

    public VoltageSource newVoltageSource(){
        VoltageSource newVoltageSource = new VoltageSource(5);
        voltageSources.add(newVoltageSource);
        return newVoltageSource;
    }

    public Junction newJunction(){
        Junction newJunction = new Junction();
        junctions.add(newJunction);
        return newJunction;
    }

    public void connect(Connector connector, Junction junction){
        if(!junctions.contains(junction)) throw new RuntimeException("Unknown junction");
        connector.connect(junction);
        junction.add(connector);
    }


    public void remove(Component component){
        if(component instanceof Resistor) this.resistors.remove((Resistor) component);
        else voltageSources.remove((VoltageSource) component);
        for(Junction j : junctions){
            j.remove(component.getInput());
            j.remove(component.getOutput());}
    }
    public void remove(Resistor resistor){
        resistors.remove(resistor);
    }
    public void remove(VoltageSource voltageSource){
        voltageSources.remove(voltageSource);
    }
    public void remove(Junction junction){
        junctions.remove(junction);
    }

    public void setResistance(Resistor resistor, double resistance){
        if(resistors.contains(resistor)) resistor.setResistance(resistance);
        else throw new RuntimeException("Unknown resistor");
    }

    public void setVoltage(VoltageSource voltageSource, double voltage){
        if(voltageSources.contains(voltageSource)) voltageSource.setVoltage(voltage);
        else throw new RuntimeException("Unknown voltage source");
    }


    public HashSet<Junction> getJunctions(){
        return this.junctions;
    }

    public HashSet<Resistor> getResistors(){
        return this.resistors;
    }
    public HashSet<VoltageSource> getVoltageSources(){
        return this.voltageSources;
    }

    public void disconnectAll(){
        for(Resistor p : resistors){
            p.getInput().disconnect();
            p.getOutput().disconnect(); }
        for(VoltageSource p : voltageSources){
            p.getInput().disconnect();
            p.getOutput().disconnect(); }
        junctions.clear();
    }


    public void printJunctions(){
        System.out.println("Junction amount: " + junctions.size());
        for(Junction j : junctions){
            System.out.println("this junction contains " + j.getEdges().size() + " edges:");
            for(Component c : j.getEdges())
                System.out.print(c.toString() + ", ");
            System.out.println();
        }
    }





}
