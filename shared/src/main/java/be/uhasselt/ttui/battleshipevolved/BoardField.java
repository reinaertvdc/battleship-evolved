package be.uhasselt.ttui.battleshipevolved;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
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
    private JPanel[][] mGrid;
    private static int MAX = 35;
    private static int PREF = 23;
    private static int MIN = 23;
    //private BufferedImage[] mStatusImages;

    public BoardField() {
        //loadImages();
        this.setLayout(new GridBagLayout());
        loadGrid();
        loadUI();
        loadCorners();
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

    /*private void loadImages() {
        mStatusImages = new BufferedImage[4];
        try {
            File file = new File(new File(Thread.currentThread().getContextClassLoader().getResource("").toURI()), "/resources/Cloud.jpg");
            mStatusImages[0] = ImageIO.read(file);
            System.out.println("Image is loaded!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }*/

    private void loadGrid() {
        mGrid = new JPanel[Field.ROWS][Field.COLUMNS];
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < Field.ROWS; i++) {
            c.gridy = i + 1;
            for (int j = 0; j < Field.COLUMNS; j++) {
                JPanel panel = new JPanel();
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panel.setMaximumSize(new Dimension(MAX, MAX));
                panel.setPreferredSize(new Dimension(PREF, PREF));
                panel.setMinimumSize(new Dimension(MIN, MIN));
                c.gridx = j + 1;
                //JLabel label = new JLabel(new ImageIcon(mStatusImages[0]));
                //panel.add(label);
                panel.setBackground(Color.LIGHT_GRAY);
                this.add(panel, c);
                mGrid[i][j] = panel;
            }
        }
    }

    private void loadUI() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        Font normalFont = new Font("Sans-Serif", Font.BOLD, 12);
        Font upsidedownFont = new Font("Sans-Serif", Font.BOLD, -12);
        //TODO: Display Coordinates upsidedown.

        for (int i = 0; i < Field.ROWS; i++) {
            c.gridx = 0;
            JPanel normalPanel = new JPanel();
            normalPanel.setMaximumSize(new Dimension(MAX, MAX));
            normalPanel.setPreferredSize(new Dimension(PREF, PREF));
            normalPanel.setMinimumSize(new Dimension(MIN, MIN));
            c.gridy = i + 1;
            JLabel normalLabel = new JLabel("" + (char)(i + 'A'));
            normalLabel.setFont(normalFont);
            normalLabel.setForeground(Color.WHITE);
            normalPanel.add(normalLabel);
            normalPanel.setBackground(Color.BLACK);
            this.add(normalPanel, c);

//            c.gridx = Field.ROWS + 1;
//            JPanel upsidedownPanel = new JPanel();
//            upsidedownPanel.setMaximumSize(new Dimension(MAX, MAX));
//            upsidedownPanel.setPreferredSize(new Dimension(PREF, PREF));
//            upsidedownPanel.setMinimumSize(new Dimension(MIN, MIN));
//            c.gridy = i + 1;
//            JLabel upsidedownLabel = new JLabel("" + (char)(i + 'A'));
//            //upsidedownLabel.setFont(upsidedownFont);
//            upsidedownLabel.setForeground(Color.WHITE);
//            upsidedownPanel.add(upsidedownLabel);
//            upsidedownPanel.setBackground(Color.BLACK);
//            this.add(upsidedownPanel, c);
        }

        for (int j = 0; j < Field.COLUMNS; j++) {
            c.gridy = 0;
            JPanel normalPanel = new JPanel();
            normalPanel.setMaximumSize(new Dimension(MAX, MAX));
            normalPanel.setPreferredSize(new Dimension(PREF, PREF));
            normalPanel.setMinimumSize(new Dimension(MIN, MIN));
            c.gridx = j + 1;
            JLabel normalLabel = new JLabel("" + (j + 1));
            normalLabel.setFont(normalFont);
            normalLabel.setForeground(Color.WHITE);
            normalPanel.add(normalLabel);
            normalPanel.setBackground(Color.BLACK);
            this.add(normalPanel, c);

//            c.gridy = Field.COLUMNS + 1;
//            JPanel upsidedownPanel = new JPanel();
//            upsidedownPanel.setMaximumSize(new Dimension(MAX, MAX));
//            upsidedownPanel.setPreferredSize(new Dimension(PREF, PREF));
//            upsidedownPanel.setMinimumSize(new Dimension(MIN, MIN));
//            c.gridx = j + 1;
//            JLabel upsidedownLabel = new JLabel("" + (j + 1));
//            //upsidedownLabel.setFont(upsidedownFont);
//            upsidedownLabel.setForeground(Color.WHITE);
//            upsidedownPanel.add(upsidedownLabel);
//            upsidedownPanel.setBackground(Color.BLACK);
//            this.add(upsidedownPanel, c);
        }
    }

    private void loadCorners() {
        GridBagConstraints c = new GridBagConstraints();
        //for (int i = 0; i < Field.ROWS + 2; i += Field.ROWS + 1){
            //for (int j = 0; j < Field.COLUMNS + 2; j += Field.COLUMNS + 1){
                JPanel corner = new JPanel();
                corner.setMaximumSize(new Dimension(MAX, MAX));
                corner.setPreferredSize(new Dimension(PREF, PREF));
                corner.setMinimumSize(new Dimension(MIN, MIN));
                corner.setBackground(Color.BLACK);
                c.gridx = 0;
                c.gridy = 0;
                this.add(corner, c);
            //}
        //}
    }

    private void colourCoordinate(CoordinateStatus cState) {
        switch (cState.getStatus()) {
            case FOG:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.LIGHT_GRAY);
                break;
            case HIT:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.RED);
                break;
            case MISSED:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.BLUE);
                break;
            case BOAT:
                mGrid[cState.getRow()][cState.getColumn()].setBackground(Color.DARK_GRAY);
                break;
        }
    }
}
