package es.uva.tfg.hector.tfg;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity {

    private final static String TAG = "Camera2testJ";
    private Size mPreviewSize;

    private TextureView mTextureView;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private CameraCharacteristics characteristics;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextureView = (TextureView)findViewById(R.id.textura);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        initSensor();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub  
        super.onResume();
        sManager.registerListener( sEvent, sensorAc, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener( sEvent, sensorMf, SensorManager.SENSOR_DELAY_NORMAL);
        Log.e(TAG, "onResume");
    }

    private void openCamera() {

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera E");
        try {
            String cameraId = manager.getCameraIdList()[0];
            characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            // TODO Auto-generated method stub  
            Log.e(TAG, "onSurfaceTextureAvailable, width="+width+",height="+height);
            openCamera();
            showFOV();
            transformImage(width, height);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            // TODO Auto-generated method stub  
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub  
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub  
           // Log.e(TAG, "onSurfaceTextureUpdated");
        }

    };

    private void transformImage(int width, int height) {
        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(),
                    (float)height / mPreviewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub  
            Log.e(TAG, "onOpened");
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub  
            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub  
            Log.e(TAG, "onError");
        }

    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub  
        Log.e(TAG, "onPause");
        super.onPause();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        if (null != sManager){
            sManager.unregisterListener(sEvent);
        }
    }

    protected void startPreview() {
        // TODO Auto-generated method stub  
        if(null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            Log.e(TAG, "startPreview fail, return");
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // TODO Auto-generated method stub  
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // TODO Auto-generated method stub  
                    Toast.makeText(MainActivity.this, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        // TODO Auto-generated method stub  
        if(null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
    }

    private SensorManager sManager;
    private Sensor sensorAc;
    private Sensor sensorMf;

    // Gravity rotational data
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

    // azimuth, pitch and roll
    private float azimuth;
    private float pitch;
    private float roll;

    private SensorEventListener2 sEvent = new SensorEventListener2(){

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = sensorEvent.values.clone();
                    break;
            }

            if (mags != null && accels != null) {
                gravity = new float[9];
                magnetic = new float[9];
                SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
                float[] outGravity = new float[9];
                SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity);
                SensorManager.getOrientation(outGravity, values);

                azimuth = values[0] * 57.2957795f;
                pitch =values[1] * 57.2957795f;
                roll = values[2] * 57.2957795f;
                mags = null;
                accels = null;
            }

            TextView zText = (TextView) findViewById(R.id.zRotation);
            TextView yText = (TextView) findViewById(R.id.yRotation);
            TextView xText = (TextView) findViewById(R.id.xRotation);

            zText.setText("Azimuth = " + String.valueOf(azimuth));
            yText.setText("Pitch = " + String.valueOf(pitch));
            xText.setText("Roll = " + String.valueOf(roll));

            if((Math.abs(azimuth) <= (fov/2))){
                ((TextView)findViewById(R.id.indicador)).setEnabled(true);
            } else {
                ((TextView)findViewById(R.id.indicador)).setEnabled(false);
            }

            View column = (View)findViewById(R.id.column);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            Resources resources = getApplicationContext().getResources();
            int resourceId = resources.getIdentifier("navigation_bar_width", "dimen", "android");
            if (resourceId > 0) {
                width += resources.getDimensionPixelSize(resourceId);
            }

            int pos = width/2 - (int)(azimuth * (width/fov));
            if(azimuth >= fov/2) {
                column.setX(0);
            } else if(azimuth <= -fov/2){
                column.setX(width - 10);
            } else {
                column.setX(pos);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onFlushCompleted(Sensor sensor) {
        }
    };

    private void initSensor(){
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAc = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMf = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sManager.registerListener( sEvent, sensorAc, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener( sEvent, sensorMf, SensorManager.SENSOR_DELAY_NORMAL);
    }

    float fov;

    private void showFOV(){
        SizeF sSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        ((TextView)findViewById(R.id.Size)).setText("Size = " + sSize.toString());

        float fLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
        ((TextView)findViewById(R.id.fLength)).setText("fLength = " + String.valueOf(fLength));

        fov = (float) (2 * Math.atan(sSize.getWidth()/(2*fLength)));
        fov = (float) Math.toDegrees(fov);
        ((TextView)findViewById(R.id.FOV)).setText("FOV = " + String.valueOf(fov));
    }
}  