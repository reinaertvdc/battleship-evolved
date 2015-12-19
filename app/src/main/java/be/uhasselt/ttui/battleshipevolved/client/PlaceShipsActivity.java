package be.uhasselt.ttui.battleshipevolved.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
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
import android.widget.FrameLayout;
import android.widget.ImageView;

import be.uhasselt.ttui.battleshipevolved.Coordinate;
import be.uhasselt.ttui.battleshipevolved.Ship;
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
    private int mOffsetTop = 0, mOffsetLeft = 0;

    private Coordinate mShipAircraftCarrierSize, mShipBattleshipSize, mShipCruiserSize,
                       mShipDecoySize, mShipDestroyerSize, mShipMarineRadarSize,
                       mShipMissileCommandSize, mShipPatrolBoatSize;

    private ImageView mImgShipAircraftCarrier, mImgShipBattleship, mImgShipCruiser,
                      mImgShipDecoy, mImgShipDestroyer, mImgShipMarineRadar,
                      mImgShipMissileCommand, mImgShipPatrolBoat;

    private Point mImgShipAircraftCarrierPos, mImgShipBattleshipPos, mImgShipCruiserPos,
                  mImgShipDecoyPos, mImgShipDestroyerPos, mImgShipMarineRadarPos,
                  mImgShipMissileCommandPos, mImgShipPatrolBoatPos;

    private Coordinate mShipAircraftCarrierPos, mShipBattleshipPos, mShipCruiserPos,
                       mShipDecoyPos, mShipDestroyerPos, mShipMarineRadarPos,
                       mShipMissileCommandPos, mShipPatrolBoatPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_place_ships);
        mContentView = findViewById(R.id.fullscreen_content);
        mLayout = (FrameLayout) findViewById(R.id.place_ships_field);

        doBinding();

        calculateSquareSizeAndOffset();

        drawField();

        loadShipSizes();
        loadShipImages();
        adjustShipImageSizes();
        setShipImagePositions();
        createShipImageOnTouchListeners();
    }

    private void createShipImageOnTouchListeners() {
        createOnTouchListener(mImgShipAircraftCarrier, mShipAircraftCarrierSize);
        createOnTouchListener(mImgShipBattleship, mShipBattleshipSize);
        createOnTouchListener(mImgShipCruiser, mShipCruiserSize);
        createOnTouchListener(mImgShipDecoy, mShipDecoySize);
        createOnTouchListener(mImgShipDestroyer, mShipDestroyerSize);
        createOnTouchListener(mImgShipMarineRadar, mShipMarineRadarSize);
        createOnTouchListener(mImgShipMissileCommand, mShipMissileCommandSize);
        createOnTouchListener(mImgShipPatrolBoat, mShipPatrolBoatSize);
    }

    private void createOnTouchListener(final ImageView image, final Coordinate size) {
        image.setOnTouchListener(new View.OnTouchListener() {
            private static final int INVALID_POINTER_ID = -1;
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
                        mPointerID1 = event.getPointerId(event.getActionIndex());
                        mDragOffset = getPointerPositionRelative(event, mPointerID1);
                        if (mOrientation == 90) {
                            float originalDragOffsetX = mDragOffset.x;
                            mDragOffset.x =
                                    ((size.getColumn() - size.getRow()) / 2f + size.getRow())
                                            * mSquareSize - mDragOffset.y;
                            mDragOffset.y = originalDragOffsetX
                                    - (size.getColumn() - size.getRow()) / 2f * mSquareSize;
                        } else if (mOrientation == 180) {
                            mDragOffset.x = (size.getColumn() * mSquareSize) - mDragOffset.x;
                            mDragOffset.y = (size.getRow() * mSquareSize) - mDragOffset.y;
                        } else if (mOrientation == 270) {
                            float originalDragOffsetX = mDragOffset.x;
                            mDragOffset.x = (size.getColumn() - size.getRow()) / 2f
                                            * mSquareSize + mDragOffset.y;
                            mDragOffset.y =  ((size.getColumn() - size.getRow()) / 2f
                                            + size.getRow()) * mSquareSize - originalDragOffsetX;
                        }
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
                            if (rotationAngle < -180.f) rotationAngle += 360.0f;
                            if (rotationAngle > 180.f) rotationAngle -= 360.0f;
                            image.setRotation(image.getRotation() - rotationAngle);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mPointerID1 = INVALID_POINTER_ID;
                        if (image.getX() >= mSquareSize * 7) {
                            image.setX(mOffsetLeft + (Math.round(image.getX() / mSquareSize) - 1)
                                    * mSquareSize);
                            image.setY(mOffsetTop + Math.round(image.getY() / mSquareSize)
                                    * mSquareSize);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mPointerID1 = INVALID_POINTER_ID;
                        mPointerID2 = INVALID_POINTER_ID;
                        float rotation = image.getRotation();
                        mOrientation = Math.round(rotation / 90) * 90;
                        image.setRotation(mOrientation);
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
        System.out.println(viewportResolution);
        float viewportRatio = (float) viewportResolution.x / viewportResolution.y;
        System.out.println(viewportRatio);
        if (viewportRatio > NEEDED_VIEWPORT_RATIO) {
            System.out.println("1");
            mSquareSize = viewportResolution.y / NEEDED_VIEWPORT_ROWS;
            mOffsetLeft = (int)(viewportResolution.x * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2);
        } else {
            System.out.println("2");
            mSquareSize = viewportResolution.x / NEEDED_VIEWPORT_COLUMNS;
            mOffsetTop = (int)(viewportResolution.y * (NEEDED_VIEWPORT_RATIO - viewportRatio) / 2);
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
        mImgShipAircraftCarrierPos = new Point();
        mImgShipBattleshipPos = new Point();
        mImgShipCruiserPos = new Point();
        mImgShipDestroyerPos = new Point();
        mImgShipMarineRadarPos = new Point();
        mImgShipPatrolBoatPos = new Point();
        mImgShipMissileCommandPos = new Point();
        mImgShipDecoyPos = new Point();
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
        layoutParams.leftMargin =
                mOffsetLeft + Math.round((mSquareSize * left * 1.5f) + mSquareSize / 2);
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
