package be.uhasselt.ttui.battleshipevolved;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Graphical frame that contains each field.
 * @Author Arno Stienaers
 */
public class Board extends JFrame implements Observer {
    private BoardField[] mFields;
    private JLabel[] mPlayerLabels;
    private Audio mAudio;

    //TODO: Aangenomen dat het aantal players 4 is, moet weggewerkt worden.
    public Board(Player[] players) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        mAudio = new Audio();
        JPanel background = new JPanel();
        mFields = new BoardField[players.length];
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.BLACK);
        mPlayerLabels = new JLabel[4];
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        Font font = new Font("Sans-Serif", Font.BOLD, 24);

        for (int i = 0; i < players.length / 2; i++) {
            JLabel playerName = new JLabel("Player " + (i + 1));
            playerName.setForeground(Color.WHITE);
            playerName.setFont(font);
            playerName.setHorizontalAlignment(JLabel.CENTER);
            mPlayerLabels[i] = playerName;
            c.gridx = i;
            c.gridy = 0;
            grid.add(playerName, c);

            mFields[i] = new BoardField();
            c.gridx = i;
            c.gridy = 1;
            grid.add(mFields[i], c);
            players[i].getField().addObserver(mFields[i]);
            players[i].getField().addObserver(mAudio);
        }
        for (int i = 0; i < players.length / 2; i++) {
            JLabel playerName = new JLabel("Player " + (i + 3));
            playerName.setForeground(Color.WHITE);
            playerName.setFont(font);
            playerName.setHorizontalAlignment(JLabel.CENTER);
            mPlayerLabels[i + 2] = playerName;
            c.gridx = 1-i;
            c.gridy = 2;
            grid.add(playerName, c);

            mFields[i + 2] = new BoardField();
            c.gridx = 1-i;
            c.gridy = 3;
            grid.add(mFields[i + 2], c);
            players[i + 2].getField().addObserver(mFields[i + 2]);
            players[i + 2].getField().addObserver(mAudio);
        }
        background.add(grid);
        background.setBackground(Color.black);
        //TODO: Player 1 always starts!
        mPlayerLabels[0].setForeground(Color.GREEN);

        this.getContentPane().add(background);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Player) {
            Player p = (Player)arg;
            for (int i = 0; i < mFields.length; i++) {
                mPlayerLabels[i].setForeground(Color.WHITE);
            }
            mPlayerLabels[p.getID()].setForeground(Color.GREEN);
        }
    }
}
