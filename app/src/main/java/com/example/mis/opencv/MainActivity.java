package com.example.mis.opencv;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Sources: https://github.com/opencv/opencv/blob/master/samples/android/face-detection/src/org/opencv/samples/facedetect/FdActivity.java
// and https://docs.opencv.org/3.4.1/d7/d8b/tutorial_py_face_detection.html
// and https://github.com/crankdaworld/Android-OpenCV-FaceDetectionwithEyes/blob/master/OpenCV-Android-FaceDetect-Eye/FdEye/src/org/opencv/samples/fd/FdView.java

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private static final Scalar NOSE_COLOR = new Scalar(255, 0, 0, 255);

    private CameraBridgeViewBase mOpenCvCameraView;
    private CascadeClassifier    faceDetector;
    private CascadeClassifier    noseDetector;
    private float                mRelativeFaceSize   = 0.2f;
    private int                  mAbsoluteFaceSize   = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    initDetection();
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

    }

    public void onCameraViewStopped() {

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        return drawRedNose(inputFrame.rgba(), inputFrame.gray());
    }


    public Mat drawRedNose(Mat rgba, Mat gray) {

        MatOfRect faces = new MatOfRect();
        MatOfRect noses = new MatOfRect();

        if (mAbsoluteFaceSize == 0) {
            int height = gray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        faceDetector.detectMultiScale(gray, faces, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();

        if (facesArray.length > 0) {

            for (Rect face : facesArray) {

                Rect roi = new Rect((int) face.tl().x, (int) (face.tl().y), face.width, (face.height));
                Mat croppedGray = gray.submat(roi);
                Mat croppedRGB = rgba.submat(roi);

                noseDetector.detectMultiScale(croppedGray, noses, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

                Rect[] nosesArray = noses.toArray();
                    for (Rect nose : nosesArray) {
                        Point center = new Point(nose.x + nose.width * 0.5, nose.y + nose.height * 0.33);
                        int radius = (int) (nose.width * 0.26);
                        Imgproc.circle(croppedRGB, center, radius, NOSE_COLOR, -1);
                    }
                }
            }
        return rgba;
    }

    public void initDetection(){

        faceDetector = new CascadeClassifier(initAssetFile("haarcascade_frontalface_default.xml"));
        noseDetector = new CascadeClassifier(initAssetFile("haarcascade_mcs_nose.xml"));

    }

    public String initAssetFile(String filename)  {
        File file = new File(getFilesDir(), filename);
        if (!file.exists()) try {
            InputStream is = getAssets().open(filename);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data); os.write(data); is.close(); os.close();
        } catch (IOException e) { e.printStackTrace(); }
        Log.d(TAG,"prepared local file: "+filename);
        return file.getAbsolutePath();
    }
}
