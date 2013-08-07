package com.cvhci.bvsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.Mat;
import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

public class ViewActivity extends Activity {
	
	private static final String TAG = "ViewActivity";
	private CameraBridgeViewBase   mOpenCvCameraView;

	//Configfile for passing to native bvs framework
	private File  mConfigFile;
	//if only one config File exists, we close the app if back button is pressed
	private boolean oneConfig=false;
	//shared cv Mat initalized in java after opencvManager connected, address passing to native 
	private Mat callBackMat;

	protected float mScale = 0;

	//callback for opencv 
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
			public void onManagerConnected(int status) {
				switch (status) {
					case LoaderCallbackInterface.SUCCESS:
						{
							callBackMat= new Mat();
							System.loadLibrary("gnustl_shared");
       					    System.loadLibrary("BvsA");
             				System.loadLibrary("bvs_modules");
							Log.i(TAG, "OpenCV loaded successfully");
							Intent myIntent= getIntent(); // gets the previously created intent
							String configName = myIntent.getStringExtra("configFile"); 
							oneConfig = myIntent.getExtras().getBoolean("oneConfig");
							Log.i("ccccccccccccccccccccccccccconfigName", configName);
							try {
								// load config file from application resources

								InputStream is = getBaseContext().getAssets().open("conf/"+configName);
								//Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
								
								//InputStream is = getResources().openRawResource(R.raw.bvs);
								File configDir = getDir("config", Context.MODE_PRIVATE);
								mConfigFile = new File(configDir, configName);
								FileOutputStream os = new FileOutputStream(mConfigFile);

								byte[] buffer = new byte[4096];
								int bytesRead;
								while ((bytesRead = is.read(buffer)) != -1) {
									os.write(buffer, 0, bytesRead);
								}
								is.close();
								os.close();

								Log.i(TAG, "config loaded in java, call JNI");
								Log.i(TAG, "ADDRESS JAVA " + callBackMat.getNativeObjAddr());

								// call native function with config file, and mat address
								bvsAndroid("--bvs.config="+mConfigFile.getAbsolutePath(), callBackMat.getNativeObjAddr());

								configDir.delete();

							} catch (IOException e) {
								e.printStackTrace();
								Log.e(TAG,"Failed to load config. Exception thrown: " + e);
							}

						} break;
					default:
						{
							super.onManagerConnected(status);
						} break;
				}
			}
	};

	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			//layout for android screen
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_view);
			mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.view_activity_surface_view);
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
			
		}

	@Override
		public void onResume()
		{
			super.onResume();
			}

	@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			//finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	@Override
		protected void onDestroy() {
			super.onDestroy();
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	private void onClose() {
		// TODO Auto-generated method stub
		//finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && oneConfig ) {
		android.os.Process.killProcess(android.os.Process.myPid());
        return true;
    }
    return super.onKeyDown(keyCode, event);
	}

	public void drawToDisplay()
	{

		Log.i(TAG, "callBackFrom JNI");
		
		if(callBackMat.height() > 0)
		{
			
			// convert mat to bitmap, and draw to display
			Bitmap mCacheBitmap = Bitmap.createBitmap(callBackMat.cols(), callBackMat.rows(),
			            Bitmap.Config.ARGB_8888);
			boolean bmpValid = true;
	
			try {
				Utils.matToBitmap(callBackMat, mCacheBitmap);
				Log.i(TAG, "mat TO Bitmap done");
			} catch(Exception e) {
				//Log.e(TAG, "Mat type: " + callBackMat);
				Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
				Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
				bmpValid = false;
			}
			if (bmpValid && mCacheBitmap != null) {
				Canvas canvas = mOpenCvCameraView.getHolder().lockCanvas();
	
				if (canvas != null) {
					canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
					Log.d(TAG, "mStretch value: " + mScale);
	
					if (mScale != 0) {
						canvas.drawBitmap(mCacheBitmap, new Rect(0,0,mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
								new Rect((int)((canvas.getWidth() - mScale*mCacheBitmap.getWidth()) / 2),
									(int)((canvas.getHeight() - mScale*mCacheBitmap.getHeight()) / 2),
									(int)((canvas.getWidth() - mScale*mCacheBitmap.getWidth()) / 2 + mScale*mCacheBitmap.getWidth()),
									(int)((canvas.getHeight() - mScale*mCacheBitmap.getHeight()) / 2 + mScale*mCacheBitmap.getHeight())), null);
					} else {
						canvas.drawBitmap(mCacheBitmap, new Rect(0,0,mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
								new Rect((canvas.getWidth() - mCacheBitmap.getWidth()) / 2,
									(canvas.getHeight() - mCacheBitmap.getHeight()) / 2,
									(canvas.getWidth() - mCacheBitmap.getWidth()) / 2 + mCacheBitmap.getWidth(),
									(canvas.getHeight() - mCacheBitmap.getHeight()) / 2 + mCacheBitmap.getHeight()), null);
					}
	
	
					mOpenCvCameraView.getHolder().unlockCanvasAndPost(canvas);
				}
			} 
		}
	
	}

	public native void bvsAndroid(String inApkFilePath, long addrString);
    
}
