package be.uhasselt.ttui.battleshipevolved.client;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

    private static final int SQUARES_IN_FIELD = 12;

    private Point mViewportSize;

    private float mSquareSize;

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

        setContentView(R.layout.activity_place_ships);

        mContentView = findViewById(R.id.fullscreen_content);

        mViewportSize = getViewportSize();
        mSquareSize = calculateSquareSize();

        loadShipSizes();
        loadShipImages();
        adjustShipImageSizes();
        setShipImagePositions();
        createShipImageOnTouchListeners();

        //FrameLayout fl = (FrameLayout) findViewById(R.id.place_ships_field);
        //fl.addView(ship1);

        /*blueSquare.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public  boolean onTouch(View v, MotionEvent event) {
                System.out.print("Touch event: ");
                LayoutParams layoutParams = (LayoutParams) blueSquare.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("ACTION_MOVE");
                        int xCord = (int)event.getRawX();
                        int yCord = (int)event.getRawY();
                        if (xCord > viewportWidth) {xCord = viewportWidth;}
                        if (yCord > viewportHeight) {yCord = viewportHeight;}
                        layoutParams.leftMargin = xCord - 25;
                        layoutParams.topMargin = yCord - 75;
                        blueSquare.setLayoutParams(layoutParams);
                        break;
                    default:
                        System.out.println("UNKNOWN");
                        break;
                }
                return true;
            }
        });*/
    }

    private void createShipImageOnTouchListeners() {
        mImgShipAircraftCarrierPos = new Point();
        mImgShipBattleshipPos = new Point();
        mImgShipCruiserPos = new Point();
        mImgShipDecoyPos = new Point();
        mImgShipDestroyerPos = new Point();
        mImgShipMarineRadarPos = new Point();
        mImgShipMissileCommandPos = new Point();
        mImgShipPatrolBoatPos = new Point();
        createOnTouchListener(mImgShipAircraftCarrier, mImgShipAircraftCarrierPos);
        createOnTouchListener(mImgShipBattleship, mImgShipBattleshipPos);
        createOnTouchListener(mImgShipCruiser, mImgShipCruiserPos);
        createOnTouchListener(mImgShipDecoy, mImgShipDecoyPos);
        createOnTouchListener(mImgShipDestroyer, mImgShipDestroyerPos);
        createOnTouchListener(mImgShipMarineRadar, mImgShipMarineRadarPos);
        createOnTouchListener(mImgShipMissileCommand, mImgShipMissileCommandPos);
        createOnTouchListener(mImgShipPatrolBoat, mImgShipPatrolBoatPos);
    }

    private void createOnTouchListener(final ImageView image, final Point position) {
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
        setShipImagePosition(mImgShipAircraftCarrier, 0, 0);
        setShipImagePosition(mImgShipBattleship, 1, 0);
        setShipImagePosition(mImgShipCruiser, 2, 0);
        setShipImagePosition(mImgShipDestroyer, 3, 0);
        setShipImagePosition(mImgShipMarineRadar, 4, 0);
        setShipImagePosition(mImgShipPatrolBoat, 5, 0);
        setShipImagePosition(mImgShipMissileCommand, 6, 0);
        setShipImagePosition(mImgShipDecoy, 6, 2);

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
        return Math.min(mViewportSize.x, mViewportSize.y) / SQUARES_IN_FIELD;
    }

    private void setShipImagePosition(ImageView image, int top, int left) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
        layoutParams.topMargin = Math.round((mSquareSize * top * 1.5f) + mSquareSize / 2);
        layoutParams.leftMargin = Math.round((mSquareSize * left * 1.5f) + mSquareSize / 2);
        image.setLayoutParams(layoutParams);
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
}
