package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.Camera;
import es.uva.tfg.hector.SkyWalkerApp.business.MapPoint;
import es.uva.tfg.hector.SkyWalkerApp.business.OrientationSensor;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;
import es.uva.tfg.hector.SkyWalkerApp.services.Vector2D;
import es.uva.tfg.hector.SkyWalkerApp.services.Vector3D;

/**
 * Overlay view controller.
 * Handles the drawing on screen, and the points updating.
 * @author Héctor Del Campo Pando.
 */
public class OverlayView implements Observer{

    /**
     * Camera used in the underlying preview.
     */
    private Camera camera;  //TODO probably gonna delete this -just for quick development-

    /**
     * View where objects will be displayed.
     */
    private TextureView view;

    /**
     * {@link PointOfInterest} to be displayed.
     */
    private List<PointOfInterest> points;

    /**
     * Position of the user.
     */
    private PointOfInterest mySelf;

    /**
     * Thread that will manage listInUse points.
     */
    private PainterThread thread;

    /**
     * Connection thread.
     */
    private Thread connectionThread;

    /**
     * The orientation's sensor that will indicate where the user is aiming.
     */
    private OrientationSensor orientationSensor;

    private Activity activity;  //TODO probably gonna delete this -just for development purposes-

    /**
     * Listener for the given {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        /**
         * Indicates whether surface was already destroyed or not.
         */
        private volatile boolean destroyed = true;

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

            if (!destroyed) {
                return;
            }

            destroyed = false;

            orientationSensor.registerEvents();
            thread = new PainterThread();
            thread.start();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

            if (destroyed) {
                return true;
            }

            destroyed = true;

            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            orientationSensor.unregisterEvents();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }

    };

    public OverlayView(TextureView view, Activity activity, Camera camera){
        this.activity = activity;
        this.view = view;
        this.camera = camera;

        view.setOpaque(false);

        orientationSensor = new OrientationSensor(activity.getBaseContext());
        orientationSensor.addObserver(this);

        points = PointOfInterest.getPoints();
        mySelf = PointOfInterest.getSelf();

        mySelf.setX(0.5f);
        mySelf.setY(0.5f);
        mySelf.setZ(0);

        view.setSurfaceTextureListener(textureListener);

    }

    /**
     * Forces a texture available call.
     */
    public void start () {
        if (view.isAvailable()) {
            textureListener.onSurfaceTextureAvailable(view.getSurfaceTexture(), view.getWidth(), view.getHeight());
        }

        if (!ServerFacade.getInstance(activity.getApplicationContext()).isDemo()) {
            connectionThread = new ConnectionThread();
            connectionThread.start();
        }
    }

    /**
     * Forces a texture destroyed call.
     */
    public void stop () {
        if (view.isAvailable()) {
            textureListener.onSurfaceTextureDestroyed(view.getSurfaceTexture());
        }

        if (connectionThread != null) {
            connectionThread.interrupt();
        }
    }

    public void updateDegrees(){
        Vector3D orientationVector = orientationSensor.getOrientationVector();
        TextView degreeText = (TextView)activity.findViewById(R.id.zRotation);
        degreeText.setText("X: " + String.valueOf(orientationVector.getX()));

        degreeText = (TextView)activity.findViewById(R.id.yRotation);
        degreeText.setText("Y: " + String.valueOf(orientationVector.getY()));

        degreeText = (TextView)activity.findViewById(R.id.xRotation);
        degreeText.setText("Z: " + String.valueOf(orientationVector.getZ()));
    }

    @Override
    public void update(Observable observable, Object o) {
        updateDegrees();
    }

    public List<PointOfInterest> getActivePoints() {
        return new ArrayList<>(points);
    }

    public void display(List<PointOfInterest> toShow) {
        synchronized (points) {
            points.clear();
            points.addAll(toShow);
        }
    }

    /**
     * Inner class extending {@link Thread}, this will handle all listInUse tasks, as well as
     * deciding if a {@code PointOfInterest} must be shown or not.
     */
    private class PainterThread extends Thread {

        /**
         * Thread's running state.
         */
        private volatile boolean running = true;

        /**
         * Waiting time  in milliseconds to start rewriting points.
         */
        private static final long SLEEP_TIME = (long)(OrientationSensor.SENSOR_DELAY*0.001);

        /**
         * Global constants
         */
        private static final float TEXT_SIZE = 70f;
        private static final float STROKE_WIDTH = 1f;
        private static final boolean ANTI_ALIAS_ENABLED = true;
        private static final int TEXT_COLOR = Color.WHITE;
        private static final int TEXT_BORDER_COLOR = Color.BLACK;

        /**
         * Constants for out of sight listInUse.
         */
        private final static int OUT_OF_SIGHT_ICON = R.drawable.out_of_sight_icon;
        private final static int OUT_OF_SIGHT_ICON_ANGLE_OFFSET = 90;
        private final static float OUT_OF_SIGHT_ICON_SCALE = 1.5f;
        private final static float MARGIN = 0.9f;

        /**
         * Constants for in sight listInUse.
         */
        private static final int INSIGHT_ICON = R.drawable.in_sight_icon;
        private static final float IN_SIGHT_ICON_SCALE = 0.8f;

        @Override
        public void run() {
            Canvas canvas;

            while(running){

                canvas = view.lockCanvas();

                /*
                 * Set Background to transparent, also clear all previous draws,
                 *  this must be do just once.
                 */
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                synchronized (points) {

                    for(PointOfInterest point : points) {

                        if (point.isUndefined()) {
                            continue;
                        }

                        final Vector2D vectorToPoint =
                        new Vector2D(
                                point.getX() - mySelf.getX(),
                                point.getY() - mySelf.getY()
                        );

                        // Same position, skip.
                        if (vectorToPoint.getY() == 0 && vectorToPoint.getX() == 0) {
                            continue;
                        }

                        vectorToPoint.normalize();

                        final Vector3D orientationVector =
                                orientationSensor.getOrientationVector();

                        if(inSight(vectorToPoint, orientationVector)){
                            drawPoint(point, vectorToPoint, orientationVector, canvas);
                        } else {
                            drawIndicator(point, vectorToPoint, orientationVector, canvas);
                        }

                    }

                }

                view.unlockCanvasAndPost(canvas);

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
        }

        /**
         * Decide whether a {@code PointOfInterest} is in sight or not.
         * @param vectorToPoint Direction vector from mySelf to the Point.
         * @return True if the {@code PointOfInterest} is in sight, false otherwise.
         */
        private boolean inSight(Vector2D vectorToPoint, Vector3D orientationVector) {

            float fovWidth, fovHeight;

            if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                fovWidth = camera.getFOVHeight();
                fovHeight = camera.getFOVWidth();
            } else {
                fovWidth = camera.getFOVWidth();
                fovHeight = camera.getFOVHeight();
            }

            // Horizontal
            final Vector2D orientationOnMap = new Vector2D(orientationVector.getX(), orientationVector.getY());
            float horizontalTheta = (float) orientationOnMap.angle(vectorToPoint);

            //Vertical
            final double verticalTheta = Math.abs(-90.0*orientationVector.getZ());

            return ( horizontalTheta <= fovWidth/2 &&
                     verticalTheta <= fovHeight/2 );

        }

        /**
         * Shows an indicator for points out of sight.
         * @param point to indicate.
         * @param canvas where to draw.
         */
        private void drawIndicator(PointOfInterest point, Vector2D vectorToPoint,
                                   Vector3D orientationVector, Canvas canvas) {

            // Horizontal
            final Vector2D orientationOnMap = new Vector2D(orientationVector.getX(), orientationVector.getY());
            float x = (float) orientationOnMap.angleWithSign(vectorToPoint) / 180;

            //Vertical
            float y = (float) orientationVector.getZ();

            final double angle = Vector2D.getAngle(x, y);

            /*
             * So once we get angle, we must remap it to coordinates. There are 2 kinds:
             *  -Left screen and down screen, they go in reverse coordinates system
             *  -Up and right screen are "normal" cases. However, right screen is special due to [0,360) angles
             *
             * Once we get corrected angle, we start coordinates from size*margin, and we multiply current angle to
             * size of the "rect", size of rect is size of height or weight subtracting twice the margin.
             */
            if (0 <= angle && angle <= 45 ||
                    315 <= angle && angle <= 360) {
                x = view.getWidth()*MARGIN;
                double correctedAngle;

                // As angle can be > 315, correct it
                if (315 <= angle && angle <= 360) {
                    correctedAngle = Math.abs(360-angle-45);
                } else {
                    correctedAngle = angle + 45;
                }

                y = (float) (view.getHeight()*(1 - MARGIN) + ((correctedAngle/(45*2))) * view.getHeight()*(1- (1 - MARGIN) * 2));   //Decimal part from div by 45 angles

            } else if (45 < angle && angle <= 135) {
                final double correctedAngle =  135 - angle;
                x = (float) (view.getWidth()*(1 - MARGIN) + (correctedAngle/(45*2)) * view.getWidth()*(1- (1 - MARGIN) * 2));
                y = view.getHeight()*MARGIN;
            } else if (135 < angle && angle <= 225) {
                final double correctedAngle = 225 - angle;
                x = view.getWidth()*(1-MARGIN);
                y = (float) (view.getHeight()*(1 - MARGIN) + (correctedAngle/(45*2)) * view.getHeight()*(1- (1 - MARGIN) * 2));
            } else {
                final double correctedAngle = angle - 225;
                x = (float) (view.getWidth()*(1 - MARGIN) + (correctedAngle/(45*2)) * view.getWidth()*(1- (1 - MARGIN) * 2));
                y = view.getHeight()*(1-MARGIN);
            }

            drawIcon(canvas, x, y, OUT_OF_SIGHT_ICON, (float)angle + OUT_OF_SIGHT_ICON_ANGLE_OFFSET, OUT_OF_SIGHT_ICON_SCALE);
            drawText(canvas, new String[]{point.getName()}, x + 17.5f, y + 15);

        }

        /**
         * Draws the given {@code PointOfInterest} in the corresponding position.</br>
         * Notice that canvas should be set to transparent and all previous draws must be removed,
         * just once,  before calling this method.
         * @param point to be drawn.
         * @param canvas to draw on.
         */
        private void drawPoint(PointOfInterest point, Vector2D vectorToPoint,
                               Vector3D orientationVector, Canvas canvas) {

            /*
             * First get the actual position on screen.
             */
            float fovWidth, fovHeight;

            if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                fovWidth = camera.getFOVHeight();
                fovHeight = camera.getFOVWidth();
            } else {
                fovWidth = camera.getFOVWidth();
                fovHeight = camera.getFOVHeight();
            }

            // Horizontal
            final Vector2D orientationOnMap =
                    new Vector2D(orientationVector.getX(), orientationVector.getY());
            final double horizontalTheta = orientationOnMap.angleWithSign(vectorToPoint);

            //Vertical
            final double verticalTheta = -90.0*orientationVector.getZ();

            final float x = (float) (view.getWidth()/2 + horizontalTheta*view.getWidth()/fovWidth),
                        y = (float) (view.getHeight()/2 - verticalTheta*view.getHeight()/fovHeight);

            drawIcon(canvas, x, y, INSIGHT_ICON, 0, IN_SIGHT_ICON_SCALE);

            drawText(canvas, new String[]{point.getName(), "50"}, x + IN_SIGHT_ICON_SCALE*17.5f, y + IN_SIGHT_ICON_SCALE*17.5f);

        }

        /**
         * Draws a icon to show positioning.
         * @param canvas where to draw
         * @param x abscissa
         * @param y ordinate
         * @param angle angle to rotate icon
         */
        private void drawIcon(final Canvas canvas, final float x, final float y, final int icon, final float angle, final float scale) {

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            matrix.postScale(scale, scale);

            final Bitmap original = BitmapFactory.decodeResource(activity.getResources(), icon);
            final Bitmap iconBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

            final Paint paint = new Paint();
            canvas.drawBitmap(
                    iconBitmap,
                    x - (iconBitmap.getWidth() / 2),
                    y - (iconBitmap.getHeight() / 2),
                    paint);

        }

        /**
         * Draws the given texts, one below others.
         * @param canvas where to draw
         * @param texts to draw
         * @param x abscissa
         * @param y ordinate
         */
        private void drawText(final Canvas canvas, final String[] texts , final float x, final float y) {

            final Paint paint = new Paint();
            final Paint border = new Paint();
            final float textSize = view.getHeight() < view.getWidth() ? TEXT_SIZE*view.getHeight()/1080 : TEXT_SIZE*view.getWidth()/1080;

            paint.setAntiAlias(ANTI_ALIAS_ENABLED);
            paint.setTextSize(textSize);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(TEXT_COLOR);

            border.setAntiAlias(ANTI_ALIAS_ENABLED);
            border.setTextSize(textSize);
            border.setStyle(Paint.Style.STROKE);
            border.setStrokeWidth(STROKE_WIDTH);
            border.setColor(TEXT_BORDER_COLOR);

            for(int i = 0; i < texts.length; i++) {
                canvas.drawText(texts[i], x, y + textSize*i, paint);
                canvas.drawText(texts[i], x, y + textSize*i, border);
            }

        }

    }

    /**
     * Thread that handles points updating from server.
     */
    private class ConnectionThread extends Thread {

        private static final long SLEEP_TIME = 250; //milliseconds

        private volatile boolean running = true;

        @Override
        public void run() {

            while (running) {

                // Update mySelf
                ServerFacade.getInstance(activity.getApplicationContext()).
                        getLastPosition(new ServerFacade.OnServerResponse<MapPoint>() {
                            @Override
                            public void onSuccess(MapPoint newPosition) {
                                mySelf.setX(newPosition.getX());
                                mySelf.setY(newPosition.getY());
                                mySelf.setZ(newPosition.getZ());
                            }

                            @Override
                            public void onError(ServerFacade.Errors error) {
                            }
                        }, mySelf);

                // Update all other points
                final List<PointOfInterest> points = OverlayView.this.points;
                for (PointOfInterest point : points) {
                    ServerFacade.getInstance(activity.getApplicationContext()).
                            getLastPosition(new ServerFacade.OnServerResponse<MapPoint>() {
                                @Override
                                public void onSuccess(MapPoint newPosition) {
                                    for (PointOfInterest point: points) {
                                        if (point.equals(newPosition)) {
                                            point.setX(newPosition.getX());
                                            point.setY(newPosition.getY());
                                            point.setZ(newPosition.getZ());
                                        }
                                    }

                                }

                                @Override
                                public void onError(ServerFacade.Errors error) {
                                }
                            }, point);
                }

                try {
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
        }


    }
}
