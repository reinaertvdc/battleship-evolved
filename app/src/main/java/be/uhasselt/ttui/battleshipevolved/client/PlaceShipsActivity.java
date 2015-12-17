package be.uhasselt.ttui.battleshipevolved.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
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
    int viewportWidth;
    int viewportHeight;
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

    private static final float NEEDED_VIEWPORT_RATIO = 18 / 12;

    private final Drawable mFieldTile =
            ContextCompat.getDrawable(getApplicationContext(), R.drawable.field_tile);
    private final Drawable mEdgeTile =
            ContextCompat.getDrawable(getApplicationContext(), R.drawable.edge_tile);

    private Point mViewportSize;
    private boolean mLandscapeMode;
    private float mSquareSize;
    private int mTopOffset;
    private int mLeftOffset;

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

        doBinding();

        setContentView(R.layout.activity_place_ships);

        mContentView = findViewById(R.id.fullscreen_content);

        private float mSquareSize;
        private int mTopOffset;
        private int mLeftOffset;

        mViewportSize = getViewportSize();
        mLandscapeMode = mViewportSize.x >= mViewportSize.y;
        mSquareSize = calculateSquareSize();

        loadShipSizes();
        loadShipImages();
        adjustShipImageSizes();
        setShipImagePositions();
        createShipImageOnTouchListeners();
    }

    private void createShipImageOnTouchListeners() {
        createOnTouchListener(mImgShipAircraftCarrier, mImgShipAircraftCarrierPos, new Point());
        createOnTouchListener(mImgShipBattleship, mImgShipBattleshipPos, new Point());
        createOnTouchListener(mImgShipCruiser, mImgShipCruiserPos, new Point());
        createOnTouchListener(mImgShipDecoy, mImgShipDecoyPos, new Point());
        createOnTouchListener(mImgShipDestroyer, mImgShipDestroyerPos, new Point());
        createOnTouchListener(mImgShipMarineRadar, mImgShipMarineRadarPos, new Point());
        createOnTouchListener(mImgShipMissileCommand, mImgShipMissileCommandPos, new Point());
        createOnTouchListener(mImgShipPatrolBoat, mImgShipPatrolBoatPos, new Point());
    }

    private void createOnTouchListener(final ImageView image, final Point start,
                                       final Point position) {
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) image.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        position.set((int) event.getRawX(), (int) event.getRawY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Point movement = new Point((int) event.getRawX() - position.x,
                                                   (int) event.getRawY() - position.y);
                        layoutParams.leftMargin += movement.x;
                        layoutParams.topMargin += movement.y;
                        image.setLayoutParams(layoutParams);
                        position.set((int) event.getRawX(), (int) event.getRawY());
                        break;
                    default:
                        layoutParams.leftMargin = start.x;
                        layoutParams.topMargin = start.y;
                        image.setLayoutParams(layoutParams);
                        //mBoundService.sendMessage("shoot 2 E4");
                        break;
                }
                return true;
            }
        });
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

    private Point getViewportSize() {
        Point viewportSize = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            getWindowManager().getDefaultDisplay().getSize(viewportSize);
        } else {
            //noinspection deprecation
            viewportWidth = getWindowManager().getDefaultDisplay().getWidth();
            //noinspection deprecation
            viewportHeight = getWindowManager().getDefaultDisplay().getHeight();
            viewportSize.set(viewportWidth, viewportHeight);
        }
        return viewportSize;
    }

    private int calculateSquareSize() {
        float viewportRatio;
        if (mLandscapeMode) {
            viewportRatio = mViewportSize.x / mViewportSize.y;
            if (viewportRatio > NEEDED_VIEWPORT_RATIO) {
                mLeftOffset = (int) (mViewportSize.x * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2);
            } else {
                mTopOffset = (int) (mViewportSize.y * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2);
            }
        } else {
            viewportRatio = mViewportSize.y / mViewportSize.x;
            if (viewportRatio > NEEDED_VIEWPORT_RATIO) {
                mTopOffset = (int) (mViewportSize.x * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2);
            } else {
                mLeftOffset = (int) (mViewportSize.y * (viewportRatio - NEEDED_VIEWPORT_RATIO) / 2);
            }
        }


        //return Math.min(mViewportSize.x, mViewportSize.y) / SQUARES_IN_FIELD;
    }

    private void setShipImagePosition(ImageView image, Point position, int top, int left) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.topMargin = Math.round((mSquareSize * top * 1.5f) + mSquareSize / 2);
        layoutParams.leftMargin = Math.round((mSquareSize * left * 1.5f) + mSquareSize / 2);
        position.set(layoutParams.leftMargin, layoutParams.topMargin);
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
