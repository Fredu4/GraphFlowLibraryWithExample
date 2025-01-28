package Controller;

import Controller.Exceptions.InvalidPlacement;
import DataStructures.FrozenStack;
import FlowNetwork.FlowNetwork;
import Model.FluidTraversal.*;
import Model.FluidTraversal.Component;
import View.ButtonPanel;

import View.Graphics.BoardGraphic;
import View.Graphics.ComponentGraphic;
import View.Graphics.Graphic;
import View.GraphingPanel;
import View.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class Controller {

    private final GraphingPanel graph;
    private final GraphingProperties properties;

    private final PlacementController placer;
    private final GraphicController grapher;
    private final Circuit circuit;

    private final HashMap<Point, Connector> inputs;
    private final HashMap<Connector, Connector> outputs;
    private final HashMap<Point, Double> componentValue;

    private boolean inPipeMode, inPumpMode, inLinkMode;

    private Point lastClick;

    public Controller(MainFrame mainFrame, GraphingProperties gProperties){
        this.graph = mainFrame.getGraphingPanel();
        this.properties = gProperties;
        this.placer = new PlacementController(gProperties);
        this.grapher = new GraphicController(25);

        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.componentValue = new HashMap<>();
        this.circuit = new Circuit();
        this.lastClick = null;

        setGraphingBackground(gProperties);
        this.addListeners(mainFrame.getButtonPanel());
    }

    private void addListeners(ButtonPanel bp){
        bp.getNewPipeButton().addActionListener(e -> this.enterPipeMode());
        bp.getNewPumpButton().addActionListener(e -> this.enterPumpMode());
        bp.getLinkButton().addActionListener(e -> this.enterLinkMode());
        bp.getCalculateButton().addActionListener(e -> this.calculate());
        bp.getConfigButton().addActionListener(e -> this.configureSelection());
        bp.getRotateButton().addActionListener(e -> this.rotateSelection());
        bp.getDeleteButton().addActionListener(e -> this.removeSelection());
        bp.getExitModeButton().addActionListener(e -> this.resetState());
        bp.getClearBoardButton().addActionListener(e -> this.clear());

        graph.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e){}
            @Override
            public void mousePressed(MouseEvent e){}
            @Override
            public void mouseReleased(MouseEvent e){
                onGraphClicked(e.getPoint());}
            @Override
            public void mouseEntered(MouseEvent e){}
            @Override
            public void mouseExited(MouseEvent e){}
        });
    }


    private void onGraphClicked(Point p){
        if(inPipeMode) placeResistor(p);
        if(inPumpMode) placeBattery(p);
        if(inLinkMode) placeWire(p);
        lastClick = p;
    }

    /* Graphic Placements */
    private void placeResistor(Point p){
        Point inPoint;
        try{inPoint = placer.placeComponent(p);}
        catch(InvalidPlacement e){return;}
        System.out.println("placed resistor");
        Resistor newResistor = circuit.newResistor();
        this.inputs.put(inPoint, newResistor.getInput());
        this.outputs.put(newResistor.getInput(), newResistor.getOutput());
        this.componentValue.put(inPoint, newResistor.getResistance());
        updateGraphics();
    }
    private void placeBattery(Point p){
        Point inPoint;
        try{inPoint = placer.placeComponent(p);}
        catch(InvalidPlacement e){return;}
        VoltageSource newVoltageSource = circuit.newVoltageSource();
        this.inputs.put(inPoint, newVoltageSource.getInput());
        this.outputs.put(newVoltageSource.getInput(), newVoltageSource.getOutput());
        this.componentValue.put(inPoint, newVoltageSource.getVoltage());
        updateGraphics();
    }
    private void placeWire(Point click){
        if(lastClick == null || !placer.isOpenWire(lastClick)) return;
        try{ placer.placeWire(lastClick, click); }
        catch(InvalidPlacement e){ return; }
        if(!placer.isOpenWire(click)) lastClick = null;
        updateGraphics();
    }


    /* CONFIGURE COMPONENT PROPERTIES */
    private double promptResistance(){
        double resistance;
        try{resistance = Double.parseDouble(JOptionPane.showInputDialog("Set pipe resistance"));}
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Please input a valid value!", "faulty user", JOptionPane.PLAIN_MESSAGE);
            return promptResistance();}
        return resistance;
    }
    private double promptPressure(){
        double pressure;
        try{pressure = Double.parseDouble(JOptionPane.showInputDialog("Set pump pressure"));}
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Please input a valid value!", "faulty user", JOptionPane.PLAIN_MESSAGE);
            return promptPressure();}
        return pressure;
    }
    private void configureSelection(){
        System.out.println("Entered Config");
        if(lastClick == null) return;
        Point selectionInpoint = placer.getComponentInput(lastClick);
        if(selectionInpoint == null) return;
        double input;
        switch(inputs.get(selectionInpoint).getComponent()){
            case Resistor resistor -> {
                input = promptResistance();
                circuit.setResistance(resistor, input);}
            case VoltageSource voltageSource -> {
                input = promptPressure();
                circuit.setVoltage(voltageSource, promptPressure());}
            default -> {System.out.println("Selection invalid");return;}}
        this.componentValue.put(selectionInpoint, input);
        updateGraphics();
    }

    private void rotateSelection(){
        if(lastClick == null) return;
        Point selectionInpoint = placer.getComponentInput(lastClick);
        if(!placer.rotateComponent(selectionInpoint)) return;
        System.out.println("Component rotated");
        lastClick = selectionInpoint;
        updateGraphics();
    }

    private void removeSelection(){
        if(lastClick == null) return;
        if(placer.hasComponent(lastClick)) this.removeComponent();
        if(placer.hasWires(lastClick)) placer.removeWires(lastClick);
        updateGraphics();
    }

    private void removeComponent(){
        if(lastClick == null) return;
        Point selectionInpoint = placer.getComponentInput(lastClick);
        try{placer.removeComponent(selectionInpoint);}
        catch(Exception e){return;}
        Connector c = inputs.get(selectionInpoint);
        this.inputs.remove(selectionInpoint);
        this.outputs.remove(c);
        circuit.remove(c.getComponent());
        System.out.println("Component removed");
        updateGraphics();
    }


    /* MODE HANDLING */
    private void enterPipeMode(){
        resetState();
        inPipeMode = true;
        System.out.println("Resistor Mode");
    }
    private void enterPumpMode(){
        resetState();
        inPumpMode = true;
        System.out.println("VoltageSource Mode");
    }
    private void enterLinkMode(){
        resetState();
        inLinkMode = true;
        lastClick = null;
        System.out.println("Link Mode");
    }
    private void resetState(){
        inPipeMode = false;
        inPumpMode = false;
        inLinkMode = false;
        System.out.println("State was reset");
    }


    /* CALCULATE FLOW THROUGH EACH COMPONENT */
    private void calculate(){
        // Add Components with connected Connectors to the same junction
        HashSet<HashSet<Point>> junctions = placer.getLinkedComponentBounds();
        for(HashSet<Point> junction : junctions){
            Junction newJunction = circuit.newJunction();
            for(Point cPoint : junction){
                if(inputs.containsKey(cPoint)) {
                    circuit.connect(inputs.get(cPoint), newJunction);
                    continue;}
                if(inputs.containsKey(placer.getComponentInput(cPoint))){
                    circuit.connect(outputs.get(inputs.get(placer.getComponentInput(cPoint))), newJunction);
                    continue;}
                throw new RuntimeException("Point without corresponding connector");}
        }
        // Create new FlowNetwork of those junctions
        Function<Component, Double> f = Component::getResistance;
        FlowNetwork<Component> network = new FlowNetwork<Component>(circuit.getJunctions(), f);
        HashMap<Component, Double> equivalentResistances = new HashMap<>();
        // Add the pumps as pressure difference sources
        for(VoltageSource voltageSource : circuit.getVoltageSources()){
            double equivalentResistance =
                    network.setPressure(voltageSource, voltageSource.getVoltage(), voltageSource.getOutput().getJunction());
            equivalentResistances.put(voltageSource, equivalentResistance);
        }
        // Prints the flow through each component to the terminal
        for(Component c : network.flowsThrough().keySet())
            System.out.println(c.getClass() + " : R(" + c.getResistance() + ") =  " + network.flowsThrough().get(c));
        equivalentResistances.forEach((c, r) -> System.out.println(c.toString() + " equivalent resistance = " + r));
    }


    private void clear(){
    }


    // PUSH MODEL TO VIEW
    private void updateGraphics(){
        ArrayList<Point[]> allComponentBounds = placer.getAllComponentBounds();
        ArrayList<Point[]> links = placer.getWireBounds();
        ArrayList<boolean[]> isLinker = new ArrayList<>();
        for(Point[] link : links){
            boolean[] b = new boolean[2];
            b[0] = placer.isOpenWire(link[0]);
            b[1] = placer.isOpenWire(link[1]);
            isLinker.add(b);}

        FrozenStack<Graphic> graphics = new FrozenStack<>();
        for(Point[] connectorPoints : allComponentBounds){
            double componentValue = this.componentValue.get(connectorPoints[0]);
            ComponentGraphic graphic = inputs.get(connectorPoints[0]).getComponent().getClass() == Resistor.class ?
                    grapher.getResistorGraphic(connectorPoints[0], connectorPoints[1], componentValue) :
                    grapher.getBatteryGraphic(connectorPoints[0], connectorPoints[1], componentValue);
            graphics.push(graphic);
        }
        for(int i = 0; i < links.size(); i++)
            graphics.push(grapher.getWireGraphic(links.get(i)[0], links.get(i)[1], isLinker.get(i)[0], isLinker.get(i)[1]));

        graphics.freeze();
        this.graph.updateGraphics(graphics);
    }


    private void setGraphingBackground(GraphingProperties gProperties){
        graph.setBoardGraphic(new BoardGraphic(gProperties.width, gProperties.height, 3, placer.getGridPoints()));

    }


}
