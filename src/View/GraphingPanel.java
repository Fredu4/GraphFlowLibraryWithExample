package View;

import DataStructures.FrozenStack;
import View.Graphics.BoardGraphic;
import View.Graphics.Graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphingPanel extends JPanel implements ActionListener{

    private BoardGraphic boardGraphic;
    private FrozenStack<Graphic> graphics;

    public GraphingPanel(int width, int preferredHeight){
        this.setPreferredSize(new Dimension(width, preferredHeight));
        this.setBackground(Color.lightGray);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        if(boardGraphic == null) return;
        boardGraphic.paint(g);

        if(graphics == null) return;
        while(!graphics.isEmpty()) graphics.pop().paint(g);
    }

    public void setBoardGraphic(BoardGraphic boardGraphic){
        this.boardGraphic = boardGraphic;
    }

    public void updateGraphics(FrozenStack<Graphic> graphics){
        this.graphics = graphics;
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e){
        //Should only repaint when asked by Controller
    }

}
