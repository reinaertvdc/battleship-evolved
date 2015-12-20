package be.uhasselt.ttui.battleshipevolved.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import be.uhasselt.ttui.battleshipevolved.Coordinate;
import be.uhasselt.ttui.battleshipevolved.Field;
import be.uhasselt.ttui.battleshipevolved.ShipAircraftCarrier;
import be.uhasselt.ttui.battleshipevolved.ShipBattleship;
import be.uhasselt.ttui.battleshipevolved.ShipCruiser;
import be.uhasselt.ttui.battleshipevolved.ShipDecoy;
import be.uhasselt.ttui.battleshipevolved.ShipDestroyer;
import be.uhasselt.ttui.battleshipevolved.ShipMarineRadar;
import be.uhasselt.ttui.battleshipevolved.ShipMissileCommand;
import be.uhasselt.ttui.battleshipevolved.ShipPatrolBoat;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlaceShipsActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private FrameLayout mLayout;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

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

    private Coordinate mShipAircraftCarrierPos, mShipBattleshipPos, mShipCruiserPos,
                       mShipDecoyPos, mShipDestroyerPos, mShipMarineRadarPos,
                       mShipMissileCommandPos, mShipPatrolBoatPos;

    private boolean[] mShipOrientation;

    private boolean[] mShipIsPlaced;

    private Button mFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_place_ships);
        mContentView = findViewById(R.id.fullscreen_content);
        mLayout = (FrameLayout) findViewById(R.id.place_ships_field);
        mOffsetTop = 0;
        mOffsetLeft = 0;
        mShipOrientation = new boolean[8];
        mShipIsPlaced = new boolean[]{false, false, false, false, false, false, false, false};

        doBinding();

        calculateSquareSizeAndOffset();

        drawField();

        loadShipSizes();
        loadShipImages();
        adjustShipImageSizes();
        setShipImagePositions();
        createShipImageOnTouchListeners();

        mFinish = new Button(this);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(mSquareSize * 5, mSquareSize * 2);
        layoutParams.leftMargin = mOffsetLeft + mSquareSize / 2;
        layoutParams.topMargin = mOffsetTop + mSquareSize * 5;
        mFinish.setLayoutParams(layoutParams);
        mFinish.setBackgroundColor(Color.rgb(0, 128, 0));
        mFinish.setText("Ready");
        mFinish.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    onFinished();
                }
                return true;
            }
        });
    }

    private void createShipImageOnTouchListeners() {
        mShipAircraftCarrierPos = new Coordinate(0, 0);
        mShipBattleshipPos = new Coordinate(0, 0);
        mShipCruiserPos = new Coordinate(0, 0);
        mShipDecoyPos = new Coordinate(0, 0);
        mShipDestroyerPos = new Coordinate(0, 0);
        mShipMarineRadarPos = new Coordinate(0, 0);
        mShipMissileCommandPos = new Coordinate(0, 0);
        mShipPatrolBoatPos = new Coordinate(0, 0);
        createOnTouchListener(mImgShipAircraftCarrier, mShipAircraftCarrierSize,
                mShipAircraftCarrierPos, 0);
        createOnTouchListener(mImgShipBattleship, mShipBattleshipSize,
                mShipBattleshipPos, 1);
        createOnTouchListener(mImgShipCruiser, mShipCruiserSize,
                mShipCruiserPos, 2);
        createOnTouchListener(mImgShipDecoy, mShipDecoySize,
                mShipDecoyPos, 3);
        createOnTouchListener(mImgShipDestroyer, mShipDestroyerSize,
                mShipDestroyerPos, 4);
        createOnTouchListener(mImgShipMarineRadar, mShipMarineRadarSize,
                mShipMarineRadarPos, 5);
        createOnTouchListener(mImgShipMissileCommand, mShipMissileCommandSize,
                mShipMissileCommandPos, 6);
        createOnTouchListener(mImgShipPatrolBoat, mShipPatrolBoatSize,
                mShipPatrolBoatPos, 7);
    }

    private void createOnTouchListener(final ImageView image, final Coordinate size,
                                       final Coordinate position, final int orientation) {
        image.setOnTouchListener(new View.OnTouchListener() {
            private static final int INVALID_POINTER_ID = -1;
            private PointF mInitialImagePosition = null;
            private PointF mDragOffset;
            private PointF mInitRotationPosPointer1;
            private PointF mInitRotationPosPointer2;
            private int mPointerID1 = INVALID_POINTER_ID;
            private int mPointerID2 = INVALID_POINTER_ID;
            private int mOrientation = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mLayout.removeView(mFinish);
                        if (mInitialImagePosition == null) {
                            mInitialImagePosition = getImagePosition(image);
                        }
                        mShipIsPlaced[orientation] = false;
                        mPointerID1 = event.getPointerId(event.getActionIndex());
                        PointF rotatedDragOffset = getPointerPositionRelative(event, mPointerID1);
                        mDragOffset = getUnrotatedPosition(rotatedDragOffset, mOrientation, size);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mPointerID2 = event.getPointerId(event.getActionIndex());
                        if (mPointerID1 != INVALID_POINTER_ID) {
                            mInitRotationPosPointer1 =
                                    getPointerPositionRelative(event, mPointerID1);
                        }
                        mInitRotationPosPointer2 = getPointerPositionRelative(event, mPointerID2);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mPointerID1 != INVALID_POINTER_ID &&
                                mPointerID2 == INVALID_POINTER_ID) {
                            image.setX(event.getRawX() - mDragOffset.x);
                            image.setY(event.getRawY() - mDragOffset.y);
                        } else if (mPointerID1 != INVALID_POINTER_ID) {
                            PointF currPosPointer1 = getPointerPositionRelative(event, mPointerID1);
                            PointF currPosPointer2 = getPointerPositionRelative(event, mPointerID2);
                            float initRotationPosAngle =
                                    getAngle(mInitRotationPosPointer1, mInitRotationPosPointer2);
                            float currAngle = getAngle(currPosPointer1, currPosPointer2);
                            float rotationAngle =
                                    (float) Math.toDegrees(initRotationPosAngle - currAngle) % 360;
                            if (rotationAngle < 0) rotationAngle += 360;
                            image.setRotation(image.getRotation() - rotationAngle);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mPointerID1 = INVALID_POINTER_ID;
                        PointF imagePosition = getImagePosition(image);
                        if (isOnField(imagePosition, mOrientation, size)) {
                            Coordinate fieldPosition =
                                    getPositionOnField(imagePosition, mOrientation, size);
                            if (fieldPosition != null &&
                                    isWithinField(fieldPosition, size, mOrientation) &&
                                    !overlapsWithExistingShip(fieldPosition, size, orientation)) {
                                if ((mOrientation == 90 || mOrientation == 270) &&
                                        size.getColumn() % 2 == 0 && size.getRow() == 1) {
                                    image.setX(mOffsetLeft
                                            + Math.round((imagePosition.x - mOffsetLeft)
                                            / mSquareSize + 0.5) * mSquareSize
                                            - Math.round(mSquareSize * 0.5));
                                    image.setY(mOffsetTop
                                            + Math.round((imagePosition.y - mOffsetTop)
                                            / mSquareSize + 0.5) * mSquareSize
                                            - Math.round(mSquareSize * 0.5));
                                } else {
                                    image.setX(mOffsetLeft
                                            + Math.round((imagePosition.x - mOffsetLeft)
                                            / mSquareSize) * mSquareSize);
                                    image.setY(mOffsetTop
                                            + Math.round((imagePosition.y - mOffsetTop)
                                            / mSquareSize) * mSquareSize);
                                }
                                position.set(fieldPosition);
                                mShipOrientation[orientation]
                                        = mOrientation == 90 || mOrientation == 270;
                                mShipIsPlaced[orientation] = true;
                                checkIfFinished();
                            } else {
                                mOrientation = 0;
                                mShipOrientation[orientation] = false;
                                image.setRotation(mOrientation);
                                image.setX(mInitialImagePosition.x);
                                image.setY(mInitialImagePosition.y);
                                mShipIsPlaced[orientation] = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mPointerID1 = INVALID_POINTER_ID;
                        mPointerID2 = INVALID_POINTER_ID;
                        float rotation = image.getRotation();
                        while (rotation < 0) rotation += 360;
                        mOrientation = (Math.round(rotation / 90) * 90) % 360;
                        image.setRotation(mOrientation);
                        System.out.println(mOrientation);
                        mShipOrientation[orientation]
                                = mOrientation == 90 || mOrientation == 270;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mPointerID1 = INVALID_POINTER_ID;
                        mPointerID2 = INVALID_POINTER_ID;
                        break;
                }
                return true;
            }
        });
    }

    private void checkIfFinished() {
        for (boolean isPlaced : mShipIsPlaced) {
            if (!isPlaced) {
                mLayout.removeView(mFinish);
                return;
            }
        }
        mLayout.addView(mFinish);
    }

    private void onFinished() {
        placeShip("aircraftcarrier", mShipAircraftCarrierPos, mShipOrientation[0]);
        placeShip("battleship", mShipBattleshipPos, mShipOrientation[1]);
        placeShip("cruiser", mShipCruiserPos, mShipOrientation[2]);
        placeShip("decoy", mShipDecoyPos, mShipOrientation[3]);
        placeShip("destroyer", mShipDestroyerPos, mShipOrientation[4]);
        placeShip("marineradar", mShipMarineRadarPos, mShipOrientation[5]);
        placeShip("missilecommand", mShipMissileCommandPos, mShipOrientation[6]);
        placeShip("patrolboat", mShipPatrolBoatPos, mShipOrientation[7]);
        mBoundService.sendMessage("end placement");
        startActivity(new Intent(PlaceShipsActivity.this, Play.class));
    }

    private void placeShip(String name, Coordinate position, boolean vertical) {
        mBoundService.sendMessage("place " + name + " "
                + (char) ('A' + position.getRow()) + position.getColumn() + " "
                + (vertical ? "vertical" : "horizontal"));
    }

    private boolean overlapsWithExistingShip(
            Coordinate position, Coordinate size, int orientation) {
        return overlapsWithShip(mShipAircraftCarrierPos, mShipAircraftCarrierSize, 0,
                position, size, orientation) ||
                overlapsWithShip(mShipBattleshipPos, mShipBattleshipSize, 1,
                        position, size, orientation) ||
                overlapsWithShip(mShipCruiserPos, mShipCruiserSize, 2,
                        position, size, orientation) ||
                overlapsWithShip(mShipDecoyPos, mShipDecoySize, 3,
                        position, size, orientation) ||
                overlapsWithShip(mShipDestroyerPos, mShipDestroyerSize, 4,
                        position, size, orientation) ||
                overlapsWithShip(mShipMarineRadarPos, mShipMarineRadarSize, 5,
                        position, size, orientation) ||
                overlapsWithShip(mShipMissileCommandPos, mShipMissileCommandSize, 6,
                        position, size, orientation) ||
                overlapsWithShip(mShipPatrolBoatPos, mShipPatrolBoatSize, 7,
                        position, size, orientation);
    }

    private boolean overlapsWithShip(
            Coordinate shipPosition, Coordinate shipSize, int shipOrientation,
            Coordinate position, Coordinate size, int orientation) {
        if (!mShipIsPlaced[shipOrientation] || shipOrientation == orientation) {
            return false;
        } else {
            int shipWidthMin = shipPosition.getColumn();
            int shipWidthMax = shipPosition.getColumn() + (mShipOrientation[shipOrientation]
                    ? shipSize.getRow() : shipSize.getColumn()) - 1;
            int shipHeightMin = shipPosition.getRow();
            int shipHeightMax = shipPosition.getRow() + (mShipOrientation[shipOrientation]
                    ? shipSize.getColumn() : shipSize.getRow()) - 1;
            int widthMin = position.getColumn();
            int widthMax = position.getColumn() + (mShipOrientation[orientation]
                    ? size.getRow() : size.getColumn()) - 1;
            int heightMin = position.getRow();
            int heightMax = position.getRow() + (mShipOrientation[orientation]
                    ? size.getColumn() : size.getRow()) - 1;
            return !(shipWidthMin > widthMax || shipWidthMax < widthMin ||
                    shipHeightMin > heightMax || shipHeightMax < heightMin);
        }
    }

    private boolean isWithinField(Coordinate position, Coordinate size, int orientation) {
        if (orientation == 0 || orientation == 180) {
            return position.getColumn() + size.getColumn() <= Field.COLUMNS &&
                    position.getRow() + size.getRow() <= Field.ROWS;
        } else {
            return position.getColumn() + size.getRow() <= Field.COLUMNS &&
                    position.getRow() + size.getColumn() <= Field.ROWS;
        }
    }

    private Coordinate getPositionOnField(PointF imagePosition, int orientation, Coordinate size) {
        PointF rotatedPosition = getRotatedPosition(imagePosition, orientation, size);
        int column = Math.round((rotatedPosition.x - mOffsetLeft) / mSquareSize) - 7;
        int row = Math.round((rotatedPosition.y - mOffsetTop) / mSquareSize) - 1;
        try {
            return new Coordinate(row, column);
        } catch(Exception e) {
            return null;
        }
    }

    private boolean isOnField(PointF position, int orientation, Coordinate size) {
        position = getRotatedPosition(position, orientation, size);
        if (orientation == 0 || orientation == 180) {
            if (position.x >= mOffsetLeft + mSquareSize * 6 - size.getColumn() * mSquareSize &&
                    position.x < mOffsetLeft + mSquareSize * 18 &&
                    position.y >= mOffsetTop - size.getRow() * mSquareSize &&
                    position.y < mOffsetTop + mSquareSize * 11) {
                return true;
            }
        } else {
            if (position.x >= mOffsetLeft + mSquareSize * 6 - size.getRow() * mSquareSize &&
                    position.x < mOffsetLeft + mSquareSize * 18 &&
                    position.y >= mOffsetTop - size.getColumn() * mSquareSize &&
                    position.y < mOffsetTop + mSquareSize * 11) {
                return true;
            }
        }
        return false;
    }

    private PointF getImagePosition(ImageView image) {
        return new PointF(image.getX(), image.getY());
    }

    private PointF getRotatedPosition(PointF position, int orientation, Coordinate size) {
        PointF rotatedPosition = new PointF(position.x, position.y);
        if (orientation == 90 || orientation == 270) {
            rotatedPosition.x += (size.getColumn() - size.getRow()) / 2f * mSquareSize;
            rotatedPosition.y -= (size.getColumn() - size.getRow()) / 2f * mSquareSize;
        }
        return rotatedPosition;
    }

    private PointF getUnrotatedPosition(PointF position, int orientation, Coordinate size) {
        PointF result = new PointF();
        if (orientation == 90) {
            result.x = ((size.getColumn() - size.getRow()) / 2f + size.getRow()) * mSquareSize
                    - position.y;
            result.y = position.x - (size.getColumn() - size.getRow()) / 2f * mSquareSize;
        } else if (orientation == 180) {
            result.x = (size.getColumn() * mSquareSize) - position.x;
            result.y = (size.getRow() * mSquareSize) - position.y;
        } else if (orientation == 270) {
            result.x = (size.getColumn() - size.getRow()) / 2f * mSquareSize + position.y;
            result.y = ((size.getColumn() - size.getRow()) / 2f + size.getRow()) * mSquareSize
                    - position.x;
        } else {
            result.set(position.x, position.y);
        }
        return result;
    }

    private float getAngle(PointF point1, PointF point2) {
        PointF vector = new PointF(point2.x - point1.x, point2.y - point1.y);
        return (float) Math.atan2(vector.y, vector.x);
    }

    private PointF getPointerPositionRelative(MotionEvent event, int pointerID) {
        float posX = event.getX(event.findPointerIndex(pointerID));
        float posY = event.getY(event.findPointerIndex(pointerID));
        return new PointF(posX, posY);
    }

    private void drawField() {
        Drawable fieldTileImg =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.field_tile);
        Drawable edgeTileImg =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.edge_tile);

        for (int i = 0; i < NEEDED_VIEWPORT_ROWS; i++) {
            for (int j = 6; j < NEEDED_VIEWPORT_COLUMNS; j++) {
                ImageView fieldTile = new ImageView(this);
                fieldTile.setLayoutParams(new FrameLayout.LayoutParams(mSquareSize, mSquareSize));
                if (i == 0 || i == NEEDED_VIEWPORT_ROWS - 1 ||
                        j == 6 || j == NEEDED_VIEWPORT_COLUMNS - 1) {
                    fieldTile.setImageDrawable(edgeTileImg);
                } else {
                    fieldTile.setImageDrawable(fieldTileImg);
                }
                setImagePosition(fieldTile, mSquareSize * j, mSquareSize * i);
                mLayout.addView(fieldTile);
            }
        }
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

    private void loadShipImages() {
        mImgShipAircraftCarrier = (ImageView) findViewById(R.id.ship_aircraft_carrier);
        mImgShipBattleship = (ImageView) findViewById(R.id.ship_battleship);
        mImgShipCruiser = (ImageView) findViewById(R.id.ship_cruiser);
        mImgShipDecoy = (ImageView) findViewById(R.id.ship_decoy);
        mImgShipDestroyer = (ImageView) findViewById(R.id.ship_destroyer);
        mImgShipMarineRadar = (ImageView) findViewById(R.id.ship_marine_radar);
        mImgShipMissileCommand = (ImageView) findViewById(R.id.ship_missile_command);
        mImgShipPatrolBoat = (ImageView) findViewById(R.id.ship_patrol_boat);
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

    private void adjustImageSize(ImageView image, Coordinate size) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.height = Math.round(mSquareSize * size.getRow());
        layoutParams.width = Math.round(mSquareSize * size.getColumn());
        image.setLayoutParams(layoutParams);
    }

    private void setShipImagePositions() {
        Point mImgShipAircraftCarrierPos = new Point();
        Point mImgShipBattleshipPos = new Point();
        Point mImgShipCruiserPos = new Point();
        Point mImgShipDestroyerPos = new Point();
        Point mImgShipMarineRadarPos = new Point();
        Point mImgShipPatrolBoatPos = new Point();
        Point mImgShipMissileCommandPos = new Point();
        Point mImgShipDecoyPos = new Point();
        setShipImagePosition(mImgShipAircraftCarrier, mImgShipAircraftCarrierPos, 0, 0);
        setShipImagePosition(mImgShipBattleship, mImgShipBattleshipPos, 1, 0);
        setShipImagePosition(mImgShipCruiser, mImgShipCruiserPos, 2, 0);
        setShipImagePosition(mImgShipDestroyer, mImgShipDestroyerPos, 3, 0);
        setShipImagePosition(mImgShipMarineRadar, mImgShipMarineRadarPos, 4, 0);
        setShipImagePosition(mImgShipPatrolBoat, mImgShipPatrolBoatPos, 5, 0);
        setShipImagePosition(mImgShipMissileCommand, mImgShipMissileCommandPos, 6, 0);
        setShipImagePosition(mImgShipDecoy, mImgShipDecoyPos, 6, 2);
    }

    private Point getViewportResolution() {
        Point viewportResolution = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            getWindowManager().getDefaultDisplay().getSize(viewportResolution);
        } else {
            //noinspection deprecation
            viewportResolution.set(getWindowManager().getDefaultDisplay().getWidth(),
                    getWindowManager().getDefaultDisplay().getHeight());
        }
        return viewportResolution;
    }

    private void setShipImagePosition(ImageView image, Point position, int top, int left) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.leftMargin = mOffsetLeft + Math.round((mSquareSize * left * 1.5f));
        layoutParams.topMargin =
                mOffsetTop + Math.round((mSquareSize * top * 1.5f) + mSquareSize / 2);
        position.set(layoutParams.leftMargin, layoutParams.topMargin);
        image.setLayoutParams(layoutParams);
        image.bringToFront();
    }

    private void setImagePosition(ImageView image, int left, int top) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.leftMargin = mOffsetLeft + left;
        layoutParams.topMargin = mOffsetTop + top;
        image.setLayoutParams(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbinding();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    //functions for binding to the connectionThread service
    private boolean isBound = false;
    private ConnectionThread mBoundService = null;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((ConnectionThread.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };
    private void doBinding() {
        bindService(new Intent(this, ConnectionThread.class), mConn, Context.BIND_AUTO_CREATE);
        isBound = true;
    }
    private void doUnbinding() {
        if (isBound) {
            unbindService(mConn);
            isBound = false;
        }
    }
    //end functions for binding
}
