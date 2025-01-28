import Controller.Controller;
import FlowNetwork.FlowNetwork;
import Model.FluidTraversal.*;
import View.MainFrame;
import Controller.GraphingProperties;

import javax.swing.*;
import java.util.function.Function;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main{

    public static void main(String[] args){

        System.setProperty("sun.java2d.uiScale", "1.0");
       MainFrame mainFrame = new MainFrame(1400, 1000);
        GraphingProperties properties = new GraphingProperties(
                1400, 900, 50, 2, 0);
       Controller controller = new Controller(mainFrame, properties);

    }

}