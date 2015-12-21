package be.uhasselt.ttui.battleshipevolved.client;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import be.uhasselt.ttui.battleshipevolved.Coordinate;
import be.uhasselt.ttui.battleshipevolved.ShipAircraftCarrier;
import be.uhasselt.ttui.battleshipevolved.ShipBattleship;
import be.uhasselt.ttui.battleshipevolved.ShipCruiser;
import be.uhasselt.ttui.battleshipevolved.ShipDecoy;
import be.uhasselt.ttui.battleshipevolved.ShipDestroyer;
import be.uhasselt.ttui.battleshipevolved.ShipMarineRadar;
import be.uhasselt.ttui.battleshipevolved.ShipMissileCommand;
import be.uhasselt.ttui.battleshipevolved.ShipPatrolBoat;

/**
 * Created by Reinaert on 2015-12-20.
 */
public class GridDrawer {
    private static final int NEEDED_VIEWPORT_COLUMNS = 18;
    private static final int NEEDED_VIEWPORT_ROWS = 12;
    private static final float NEEDED_VIEWPORT_RATIO =
            (float) NEEDED_VIEWPORT_COLUMNS / NEEDED_VIEWPORT_ROWS;

    private int mSquareSize;
    private int mOffsetTop, mOffsetLeft;

    private Coordinate mShipAircraftCarrierSize, mShipBattleshipSize, mShipCruiserSize,
            mShipDecoySize, mShipDestroyerSize, mShipMarineRadarSize,
            mShipMissileCommandSize, mShipPatrolBoatSize;

    private ImageView mImgShipAircraftCarrier, mImgShipBattleship, mImgShipCruiser,
            mImgShipDecoy, mImgShipDestroyer, mImgShipMarineRadar,
            mImgShipMissileCommand, mImgShipPatrolBoat;

    private boolean[] mShipOrientation;

    private int mRows;
    private int mColumns;
    private FrameLayout mGridLayout;
    private ImageView mGrid[][];
    private Activity mParent;

    public GridDrawer(int rows, int columns, FrameLayout grid, PlayActivity playActivity) {
        mRows = rows;
        mColumns = columns;
        mGridLayout = grid;
        mParent = playActivity;
        mGrid = new ImageView[mRows][mColumns];
        mOffsetTop = 0;
        mOffsetLeft = 0;

        calculateSquareSizeAndOffset();

        createGrid();

        loadShipSizes();
        loadShipImages();
        adjustShipImageSizes();
        setShipImagePositions();

        setImageLeft(mParent.findViewById(R.id.turnText), mSquareSize * 13);
        setImageLeft(mParent.findViewById(R.id.speechButton), mSquareSize * 13);
        setImageLeft(mParent.findViewById(R.id.onlineText), mSquareSize * 13);
        setImageLeft(mParent.findViewById(R.id.cooldownText), mSquareSize * 13);
    }

    private void adjustShipImageSizes() {
        adjustImageSize(mImgShipAircraftCarrier, mShipAircraftCarrierSize);
        adjustImageSize(mImgShipBattleship, mShipBattleshipSize);
        adjustImageSize(mImgShipCruiser, mShipCruiserSize);
        adjustImageSize(mImgShipDecoy, mShipDecoySize);
        adjustImageSize(mImgShipDestroyer, mShipDestroyerSize);
        adjustImageSize(mImgShipMarineRadar, mShipMarineRadarSize);
        adjustImageSize(mImgShipMissileCommand, mShipMissileCommandSize);
        adjustImageSize(mImgShipPatrolBoat, mShipPatrolBoatSize);
    }

    private void setShipImagePositions() {
        Bundle bundle = mParent.getIntent().getExtras();

        setShipImagePosition(mImgShipAircraftCarrier, bundle, "aircraftcarrier");
        setShipImagePosition(mImgShipBattleship, bundle, "battleship");
        setShipImagePosition(mImgShipCruiser, bundle, "cruiser");
        setShipImagePosition(mImgShipDestroyer, bundle, "decoy");
        setShipImagePosition(mImgShipMarineRadar, bundle, "destroyer");
        setShipImagePosition(mImgShipPatrolBoat, bundle, "marineradar");
        setShipImagePosition(mImgShipMissileCommand, bundle, "missilecommand");
        setShipImagePosition(mImgShipDecoy, bundle, "patrolboat");
    }

    private void setShipImagePosition(View ship, Bundle bundle, String name) {
        int left = bundle.getInt(name + "_left");
        int top = bundle.getInt(name + "_top");
        boolean vertical = bundle.getBoolean(name + "_vertical");

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ship.getLayoutParams();
        layoutParams.leftMargin = mOffsetLeft + Math.round((mSquareSize * left * 1.5f));
        layoutParams.topMargin =
                mOffsetTop + Math.round((mSquareSize * top * 1.5f) + mSquareSize / 2);
        ship.setLayoutParams(layoutParams);
        if (vertical) {
            ship.setRotation(90);
        }
        ship.bringToFront();
    }

    private void loadShipImages() {
        mImgShipAircraftCarrier = (ImageView) mParent.findViewById(R.id.ship_aircraft_carrier);
        mImgShipBattleship = (ImageView) mParent.findViewById(R.id.ship_battleship);
        mImgShipCruiser = (ImageView) mParent.findViewById(R.id.ship_cruiser);
        mImgShipDecoy = (ImageView) mParent.findViewById(R.id.ship_decoy);
        mImgShipDestroyer = (ImageView) mParent.findViewById(R.id.ship_destroyer);
        mImgShipMarineRadar = (ImageView) mParent.findViewById(R.id.ship_marine_radar);
        mImgShipMissileCommand = (ImageView) mParent.findViewById(R.id.ship_missile_command);
        mImgShipPatrolBoat = (ImageView) mParent.findViewById(R.id.ship_patrol_boat);
    }

    private void loadShipSizes() {
        mShipAircraftCarrierSize = new ShipAircraftCarrier().getSize();
        mShipBattleshipSize = new ShipBattleship().getSize();
        mShipCruiserSize = new ShipCruiser().getSize();
        mShipDecoySize = new ShipDecoy().getSize();
        mShipDestroyerSize = new ShipDestroyer().getSize();
        mShipMarineRadarSize = new ShipMarineRadar().getSize();
        mShipMissileCommandSize = new ShipMissileCommand().getSize();
        mShipPatrolBoatSize = new ShipPatrolBoat().getSize();
    }

    private void createGrid() {
        Drawable fieldTileImg =
                ContextCompat.getDrawable(mParent.getApplicationContext(), R.drawable.field_tile);

        for (int i = 0; i < mRows + 2; i++) {
            for (int j = 0; j < mColumns + 2; j++) {
                View fieldTile;
                if (i == 0 || i == 11 || j == 0 || j == 11) {
                    fieldTile = new TextView(mParent);
                    fieldTile.setBackgroundColor(0xFF000000);
                    ((TextView)fieldTile).setGravity(Gravity.CENTER);
                    if ((i != 0 && i != 11) || (j != 0 && j != 11)) {
                        if (i == 0 || i == 11) {
                            ((TextView) fieldTile).setText("" + j);
                        } else {
                            ((TextView) fieldTile).setText("" + (char)('A' + i - 1));
                        }
                    }
                    ((TextView)fieldTile).setTextColor(0xFFFFFFFF);
                } else {
                    fieldTile = new ImageView(mParent);
                    ((ImageView)fieldTile).setImageDrawable(fieldTileImg);
                    mGrid[j - 1][i - 1] = (ImageView)fieldTile;
                }
                fieldTile.setLayoutParams(new FrameLayout.LayoutParams(mSquareSize, mSquareSize));
                setImagePosition(fieldTile, mSquareSize * j, mSquareSize * i);
                mGridLayout.addView(fieldTile);
            }
        }
    }

    public void setDamaged(int row, int column) {
        Drawable hitTile =
                ContextCompat.getDrawable(mParent.getApplicationContext(), R.drawable.hit_tile);
        try {
            mGrid[column][row].setImageDrawable(hitTile);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Out of grid bounds.");
        }
    }

    public void setWater(int row, int column) {
        Drawable waterTile =
                ContextCompat.getDrawable(mParent.getApplicationContext(), R.drawable.water_tile);
        try {
            mGrid[column][row].setImageDrawable(waterTile);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Out of grid bounds.");
        }
    }

    private void setImagePosition(View image, int left, int top) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.leftMargin = mOffsetLeft + left;
        layoutParams.topMargin = mOffsetTop + top;
        image.setLayoutParams(layoutParams);
    }

    private void setImageLeft(View image, int left) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.leftMargin = mOffsetLeft + left;
        image.setLayoutParams(layoutParams);
    }

    private void calculateSquareSizeAndOffset() {
        Point viewportResolution = getViewportResolution();
        float viewportRatio = viewportResolution.x / (float) viewportResolution.y;
        if (viewportRatio > NEEDED_VIEWPORT_RATIO) {
            mSquareSize = Math.round(viewportResolution.y / (float)NEEDED_VIEWPORT_ROWS);
            mOffsetLeft +=
                    Math.round(viewportResolution.x * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2f);
        } else {
            mSquareSize = Math.round(viewportResolution.x / (float)NEEDED_VIEWPORT_COLUMNS);
            mOffsetTop +=
                    Math.round(viewportResolution.y * (NEEDED_VIEWPORT_RATIO - viewportRatio) / 2f);
        }
    }

    private Point getViewportResolution() {
        Point viewportResolution = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            mParent.getWindowManager().getDefaultDisplay().getSize(viewportResolution);
        } else {
            //noinspection deprecation
            viewportResolution.set(mParent.getWindowManager().getDefaultDisplay().getWidth(),
                    mParent.getWindowManager().getDefaultDisplay().getHeight());
        }
        return viewportResolution;
    }

    private void adjustImageSize(ImageView image, Coordinate size) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.height = Math.round(mSquareSize * size.getRow());
        layoutParams.width = Math.round(mSquareSize * size.getColumn());
        image.setLayoutParams(layoutParams);
    }
}
