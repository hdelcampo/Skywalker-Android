package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Hector Del Campo Pando on 18/07/2016.
 */
public class OverlayView implements Observer{

    private static final String TAG = "Overlay View";

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
    private PointOfInterest myPosition;

    /**
     * Thread that will manage drawing points.
     */
    private PainterThread thread;

    /**
     * The orientation's sensor that will indicate where the user is aiming.
     */
    private OrientationSensor orientationSensor;

    private Activity activity;  //TODO probably gonna delete this -just for development purposes-

    /**
     * Listener for the given {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            orientationSensor.registerEvents();
            thread = new PainterThread();
            thread.start();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            thread.interrupt();
            //When preview is paused the camera device must be freed
            orientationSensor.unregisterEvents();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    public OverlayView(TextureView view, Activity activity, Camera camera){
        this.activity = activity;
        this.view = view;
        view.setOpaque(false);

        orientationSensor = OrientationSensor.createSensor(activity,
                                                    activity.getWindowManager().getDefaultDisplay());
        orientationSensor.addObserver(this);

        this.camera = camera;

        points = PointOfInterest.getPoints();

        view.setSurfaceTextureListener(textureListener);

    }

    public void updateDegrees(){
        TextView degreeText = (TextView)activity.findViewById(R.id.zRotation);
        degreeText.setText("Azimuth " + String.valueOf(orientationSensor.getAzimuth()));

        degreeText = (TextView)activity.findViewById(R.id.yRotation);
        degreeText.setText("Roll " + String.valueOf(orientationSensor.getRoll()));

        degreeText = (TextView)activity.findViewById(R.id.xRotation);
        degreeText.setText("Pitch " + String.valueOf(orientationSensor.getPitch()));
    }

    @Override
    public void update(Observable observable, Object o) {
        updateDegrees();
    }

    public List<PointOfInterest> getActivePoints() {
        return points;
    }

    public void display(ArrayList<String> toShow) {
        List<PointOfInterest> allPoints = PointOfInterest.getPoints();
        points.clear();
        for (String one: toShow) {
            for (PointOfInterest point: allPoints) {
                if (point.getID().equals(one)) {
                    points.add(point);
                }
            }
        }
    }

    /**
     * Inner class extending {@link Thread}, this will handle all drawing tasks, as well as
     * deciding if a {@code PointOfInterest} must be shown or not.
     */
    private class PainterThread extends Thread{


        private volatile boolean run = false;

        /**
         * Waiting time  in milliseconds to start rewriting points.
         */
        private static final long SLEEP_TIME = 15;

        /**
         * Constants for {@code PointOfInterest} indicator.
         */
        private static final int CIRCLE_BORDER_SIZE = 10;

        private static final float TEXT_SIZE = 40f;

        final int ARC_LENGTH = 50;
        final int ARC_SIZE = 360 / 4;

        /**
         * Constants for in sight drawing.
         */
        private static final int INSIGHT_ICON = android.R.drawable.arrow_down_float;

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            Canvas canvas;

            while(run){

                canvas = view.lockCanvas();

                /*
                 * Set Background to transparent, also clear all previous draws,
                 *  this must be do just once.
                 */
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                for(PointOfInterest point : points){
                    if(inSight(point)){
                        drawPoint(point, canvas);
                    } else {
                        drawIndicator(point, canvas);
                    }
                }

                view.unlockCanvasAndPost(canvas);

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e){
                    //TODO
                }
            }
        }

        /**
         * Shows an indicator for points out of sight.
         * @param point to indicate.
         * @param canvas where to draw.
         */
        private void drawIndicator(PointOfInterest point, Canvas canvas) {
            final int borderSize = view.getHeight() < view.getWidth() ?
                            CIRCLE_BORDER_SIZE*view.getHeight()/1080 : CIRCLE_BORDER_SIZE*view.getWidth()/1080;

            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);

            final int x_center = view.getWidth() / 2,
                    y_center = view.getHeight() / 2;

            float x = -(orientationSensor.getAzimuth() - point.getX()) / 180,
                y = (orientationSensor.getPitch() - point.getZ()) / 90;

            final int arcLength = view.getHeight() < view.getWidth() ? ARC_LENGTH*view.getHeight()/1080 : ARC_LENGTH*view.getWidth()/1080;

            RectF oval = new RectF(x_center - arcLength,
                    y_center - arcLength,
                    x_center + arcLength,
                    y_center + arcLength);

            float angle = getAngle(x, y) - ARC_SIZE / 2;

            canvas.drawArc(oval, angle, ARC_SIZE, false, paint);

            final float textSize = view.getHeight() < view.getWidth() ? TEXT_SIZE*view.getHeight()/1080 : TEXT_SIZE*view.getWidth()/1080;
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);
            canvas.drawText(point.getID(), (float) (x_center + arcLength *Math.cos(Math.toRadians(angle))),
                    (float) (y_center + arcLength *Math.sin(Math.toRadians(angle))), paint);
        }

        /**
         * Retrieves angle in degrees.
         * @param x the abscissa coordinate.
         * @param y the ordinate coordinate.
         * @return the angle
         */
        private float getAngle(float x, float y){
            //TODO To Vector class
            float angle = 0;

            if( false ){
                angle = 0;
            } else {
                angle = (float) Math.toDegrees(Math.atan2(-y, x));
            }

            return angle;
        }

        @Override
        public synchronized void start() {
            run = true;
            super.start();
        }

        @Override
        public void interrupt() {
            run = false;
            super.interrupt();
        }

        /**
         * Decide whether a {@code PointOfInterest} is in sight or not.
         * @param point to decide on.
         * @return True if the {@code PointOfInterest} is in sight, false otherwise.
         */
        private boolean inSight(PointOfInterest point){
            float fovWidth, fovHeight;

            if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                fovWidth = camera.getFOVHeight();
                fovHeight = camera.getFOVWidth();
            } else {
                fovWidth = camera.getFOVWidth();
                fovHeight = camera.getFOVHeight();
            }

            return (Math.abs(orientationSensor.getAzimuth() - point.getX()) <= fovWidth/2 &&
                    Math.abs(orientationSensor.getPitch() - point.getZ()) <= fovHeight/2); //TODO stub
        }

        /**
         * Draws the given {@code PointOfInterest} in the corresponding position.</br>
         * Notice that canvas should be set to transparent and all previous draws must be removed,
         * just once,  before calling this method.
         * @param point to be drawn.
         * @param canvas to draw on.
         */
        private void drawPoint(PointOfInterest point, Canvas canvas){
            //TODO stub for demo
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

            final float x = view.getWidth()/2 - (orientationSensor.getAzimuth() - point.getX())*view.getWidth()/fovWidth,
                        y = view.getHeight()/2 - (orientationSensor.getPitch() - point.getZ())*view.getHeight()/fovHeight;

            final float radius = view.getHeight() < view.getWidth() ? 50f*view.getHeight()/1080 : 50f*view.getWidth()/1080;

            drawIcon(canvas, x, y);

            drawText(canvas, new String[]{point.getID(), "50"}, x + radius, y + radius);
        }

        /**
         * Draws a icon to show positioning.
         * @param canvas where to draw
         * @param x abscissa
         * @param y ordinate
         */
        private void drawIcon(Canvas canvas, float x, float y) {

            final Paint paint = new Paint();
            final Bitmap b = BitmapFactory.decodeResource(activity.getResources(), INSIGHT_ICON);
            canvas.drawBitmap(b, x, y, paint);

        }

        /**
         * Draws the given texts, one below others.
         * @param canvas where to draw
         * @param texts to draw
         * @param x abscissa
         * @param y ordinate
         */
        private void drawText(Canvas canvas, String[] texts ,float x, float y) {

            final Paint paint = new Paint();
            final float textSize = view.getHeight() < view.getWidth() ? TEXT_SIZE*view.getHeight()/1080 : TEXT_SIZE*view.getWidth()/1080;

            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);

            for(int i = 0; i < texts.length; i++) {
                canvas.drawText(texts[i], x , y + textSize*i, paint);
            }

        }
    }
}
