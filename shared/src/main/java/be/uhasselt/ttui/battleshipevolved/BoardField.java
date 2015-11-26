package be.uhasselt.ttui.battleshipevolved;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Graphical version of a field for on a board
 * @Author Arno Stienaers
 */
public class BoardField extends JPanel implements Observer {
    private JLabel[][] mGrid;
    private BufferedImage[] mStatusImages;

    public BoardField() {
        loadImages();
        loadGrid();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof CoordinateStatus) {
            colourCoordinate((CoordinateStatus)arg);
        } else if (arg instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) arg)
                if (obj instanceof CoordinateStatus)
                    colourCoordinate((CoordinateStatus) obj);
        }
    }

    private void loadImages() {
        mStatusImages = new BufferedImage[4];
        try {
            //mStatusImages[0] = ImageIO.read(new File("./resources/Cloud.jpg"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadGrid() {
        mGrid = new JLabel[Field.ROWS][Field.COLUMNS];
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < Field.ROWS; i++) {
            c.gridy = i;
            for (int j = 0; j < Field.COLUMNS; j++) {
                JPanel panel = new JPanel();
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panel.setPreferredSize(new Dimension(25, 25));
                c.gridx = j;
                JLabel label = new JLabel();//new ImageIcon(mStatusImages[0]));
                panel.add(label);
                this.add(panel, c);
                mGrid[i][j] = label;
            }
        }
    }

    private void colourCoordinate(CoordinateStatus cState) {
        switch (cState.getStatus()) {
            case FOG:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.GRAY);
                break;
            case HIT:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.RED);
                break;
            case MISSED:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.BLUE);
                break;
            case BOAT:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.BLACK);
                break;
        }
    }
}
