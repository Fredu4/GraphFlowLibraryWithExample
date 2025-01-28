package View;

import javax.swing.*;
import java.awt.*;

public class MainFrame{

    private final int width;
    private final int height;
    private final int buttonPanelHeight = 100;

    private final JFrame mainFrame;
    private final ButtonPanel buttonPanel;
    private final GraphingPanel graphingPanel;

    public MainFrame(int width, int height){
        this.width = width;
        this.height = height;
        this.mainFrame = new JFrame();
        mainFrame.setTitle("Flow Demo");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(this.width, this. height);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setLocationRelativeTo(null);

        this.buttonPanel = new ButtonPanel();
        this.graphingPanel = new GraphingPanel(this.width, this.height - buttonPanelHeight);

        mainFrame.add(buttonPanel.getPanel(), BorderLayout.NORTH);
        mainFrame.add(graphingPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }



    public GraphingPanel getGraphingPanel(){
        return graphingPanel;
    }
    public ButtonPanel getButtonPanel(){
        return buttonPanel;
    }



}
