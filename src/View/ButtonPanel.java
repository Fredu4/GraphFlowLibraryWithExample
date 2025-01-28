package View;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel {

    private final JPanel buttonPanel;

    private final JButton calculateButton;
    private final JButton rotateButton;
    private final JButton deleteButton;
    private final JButton configButton;

    private final JButton newPipeButton;
    private final JButton newPumpButton;
    private final JButton linkButton;

    private final JButton exitModeButton;
    private final JButton clearBoardButton;


    public ButtonPanel(){
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS));

        this.newPipeButton = new JButton("Resistor");
        this.newPumpButton = new JButton("VoltageSource");
        this.linkButton = new JButton("Link");
        this.rotateButton = new JButton("Rotate");

        this.configButton = new JButton("Config");
        this.exitModeButton = new JButton("Exit Mode");

        this.calculateButton = new JButton("Calculate");
        this.deleteButton = new JButton("Delete");
        this.clearBoardButton = new JButton("Clear");

        this.buttonPanel.add(newPipeButton);
        this.buttonPanel.add(newPumpButton);
        this.buttonPanel.add(linkButton);
        this.buttonPanel.add(configButton);
        this.buttonPanel.add(calculateButton);
        this.buttonPanel.add(rotateButton);
        this.buttonPanel.add(deleteButton);
        this.buttonPanel.add(exitModeButton);
        this.buttonPanel.add(clearBoardButton);
    }

    protected JPanel getPanel(){
        return this.buttonPanel;
    }

    public JButton getCalculateButton(){
        return this.calculateButton;
    }
    public JButton getRotateButton(){
        return this.rotateButton;
    }
    public JButton getDeleteButton(){
        return this.deleteButton;
    }
    public JButton getNewPipeButton(){
        return this.newPipeButton;
    }
    public JButton getNewPumpButton(){
        return this.newPumpButton;
    }
    public JButton getLinkButton(){
        return this.linkButton;
    }
    public JButton getConfigButton(){
        return this.configButton;
    }
    public JButton getExitModeButton(){
        return this.exitModeButton;
    }
    public JButton getClearBoardButton(){
        return this.clearBoardButton;
    }

}
