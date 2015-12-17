package be.uhasselt.ttui.battleshipevolved;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Graphical frame that contains each field.
 * @Author Arno Stienaers
 */
public class Board extends JFrame implements Observer {
    BoardField[] mFields;

    //TODO: Aangenomen dat het aantal players 4 is, moet weggewerkt worden.
    public Board(Player[] players) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setUndecorated(true);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        JPanel background = new JPanel();
        mFields = new BoardField[players.length];
        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < players.length / 2; i++) {
            mFields[i] = new BoardField();
            mFields[i].setPreferredSize(mFields[i].getPreferredSize());
            c.gridx = i;
            c.gridy = 0;
            grid.add(mFields[i], c);
            players[i].getField().addObserver(mFields[i]);
        }
        for (int i = 0; i < players.length / 2; i++) {
            mFields[i + 2] = new BoardField();
            c.gridx = 1-i;
            c.gridy = 1;
            grid.add(mFields[i + 2], c);
            players[i + 2].getField().addObserver(mFields[i + 2]);
        }
        background.add(grid);
        background.setBackground(Color.black);

        this.getContentPane().add(background);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Player) {
            Player p = (Player)arg;
            for (int i = 0; i < mFields.length; i++) {
                //mFields[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                mFields[i].setVisible(true);
            }
            //mFields[p.getID()].setBorder(BorderFactory.createLineBorder(Color.RED));
            //mFields[p.getID()].setVisible(false);
        }
    }
}
