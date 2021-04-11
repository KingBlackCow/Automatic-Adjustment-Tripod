package org.tensorflow.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static android.widget.Toast.LENGTH_SHORT;
import static android.speech.SpeechRecognizer.*;

public abstract class CameraActivity extends Activity
    implements OnImageAvailableListener, Camera.PreviewCallback {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_BLE = Manifest.permission.BLUETOOTH;
  private static final String PERMISSION_BLE2 = Manifest.permission.BLUETOOTH_ADMIN;
  private static final String PERMISSION_VOICE = Manifest.permission.RECORD_AUDIO;
  private boolean debug = false;

  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;

  protected int previewWidth = 0;
  protected int previewHeight = 0;

  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private BluetoothSPP bt;
  double width;
  double height;
  static final int REQUEST_IMAGE_CAPTURE = 1;
  private SurfaceView mSurfaceView;
  private SurfaceHolder mSurfaceViewHolder;
  private Handler mHandler;
  public  int touch_X;
  public  int touch_Y;
  public  boolean flag_flash=true;
  Intent intent_voice;
  SpeechRecognizer mRecognizer;
  TextView textView;
  boolean speechbool = false;
  boolean speechbool2 = false;
  private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
  SoundPool soundpool;
  int tak;
  int left_up;
  int left;
  int left_bottom;
  int center;
  int center_up;
  int center_bottom;
  int right;
  int right_up;
  int right_bottom;
  int bluetooth_avilable;
  int bluetooth_notavilable;
  int yesmaster;
  int notunderstand;

    AudioManager mAudioManager;
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
   
    setContentView(R.layout.activity_camera);
    textView = (TextView)findViewById(R.id.textspeech);
      mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//    setContentView(R.layout.camera_connection_fragment_tracking);
    soundpool= new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    tak=soundpool.load(this,R.raw.cheese,1);
    left_up=soundpool.load(this,R.raw.left_up,1);
    left=soundpool.load(this,R.raw.left_center,1);
    left_bottom=soundpool.load(this,R.raw.left_bottom,1);
    right=soundpool.load(this,R.raw.right,1);
    right_bottom=soundpool.load(this,R.raw.right_bottom,1);
    right_up=soundpool.load(this,R.raw.right_up,1);
    center=soundpool.load(this,R.raw.center,1);
    center_up=soundpool.load(this,R.raw.center_up,1);
    center_bottom= soundpool.load(this,R.raw.center_bottom,1);
    bluetooth_avilable =soundpool.load(this,R.raw.bluetoothavailable,1);
    bluetooth_notavilable =soundpool.load(this,R.raw.bluetoothnotavailable,1);
    yesmaster=soundpool.load(this,R.raw.yesmaster,1);
    notunderstand=soundpool.load(this,R.raw.notunderstand,1);
    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.RECORD_AUDIO)) {

      } else {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
        );
      }
    }
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
    width = 480;
    height = 640 ;

    bt = new BluetoothSPP(this); //Initializing
    intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent_voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
    intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
    mRecognizer = createSpeechRecognizer(this);
    mRecognizer.setRecognitionListener(recognitionListener);
    mRecognizer.startListening(intent_voice);
    if (!bt.isBluetoothAvailable()) {
      Toast.makeText(getApplicationContext()
              , "Bluetooth is not available"
              , LENGTH_SHORT).show();
    }
    bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
      public void onDataReceived(byte[] data, String message) {
        Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
        setup(message);
      }
    });

    bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
      public void onDeviceConnected(String name, String address) {
          mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);

          mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8,
                  AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        Toast.makeText(getApplicationContext()
                , "Connected to " + name + "\n" + address
                , LENGTH_SHORT).show();
        soundpool.play(bluetooth_avilable,1,1,0,0,1);
      }

      public void onDeviceDisconnected() { //연결해제
        Toast.makeText(getApplicationContext()
                , "Connection lost", LENGTH_SHORT).show();
        soundpool.play(bluetooth_notavilable,1,1,0,0,1);
      }

      public void onDeviceConnectionFailed() { //연결실패
        Toast.makeText(getApplicationContext()
                , "Unable to connect", LENGTH_SHORT).show();
      }
    });

    Button btnConnect = (Button) findViewById(R.id.btnConnect); //연결시도
    btnConnect.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
          bt.disconnect();
        } else {
          Intent intent = new Intent(getApplicationContext(), DeviceList.class);
          startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }

      }
    });
    Button btnSend = (Button) findViewById(R.id.btnSend);
    btnSend.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Toast.makeText(getApplicationContext()
                , (int)(DetectorActivity.X*300/width)+","+(int)(DetectorActivity.Y*300/height)
                , LENGTH_SHORT).show();
        bt.send("5:"+(int)(DetectorActivity.X*300/width)+","+(int)(DetectorActivity.Y*300/height), true);
      }
    });

    Button btnSend2 = (Button) findViewById(R.id.button_2);
    btnSend2.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
//          Toast.makeText(getApplicationContext()
//                    , "현재높이는"+DetectorActivity.Y_height
//                    , LENGTH_SHORT).show();
//        bt.send("5:"+(Classifier.Recognition.location.left+Classifier.Recognition.location.right)/2+","+Classifier.Recognition.location.bottom, true);
        }
    });
//    Button btnSend3 = (Button) findViewById(R.id.button_3);
//    btnSend3.setOnClickListener(new View.OnClickListener() {
//      public void onClick(View v) {
//        int tmp_X=(int)(DetectorActivity.X*300/width)-DetectorActivity.X_inBox;
//        int tmp_Y=(int)(DetectorActivity.Y*300/width)-DetectorActivity.Y_inBox;
//
//        Toast.makeText(getApplicationContext()
//                , tmp_X+","+tmp_Y
//                , LENGTH_SHORT).show();
//        //bt.send("5:"+(Classifier.Recognition.location.left+Classifier.Recognition.location.right)/2+","+Classifier.Recognition.location.bottom, true);
//      }
//    });



    Button capture = (Button) findViewById(R.id.button_main_capture); //연결시도
    capture.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        soundpool.play(tak,1,1,0,0,1);
        ImageCap();

      }
    });
    //JS
    Button btn_gall;
    btn_gall = (Button)findViewById(R.id.gall);
    btn_gall.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent pickerIntent = new Intent(Intent.ACTION_PICK);

        pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickerIntent, 100);
        galleryAddPic(Environment.getExternalStorageDirectory() + "/DCIM/Camera");

      }
    });
  }

  private RecognitionListener recognitionListener = new RecognitionListener() {
    @Override
    public void onReadyForSpeech(Bundle bundle) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int i) {
        if(mAudioManager.isMusicActive() == false)
        {

            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);

        }
      mRecognizer.destroy();
      mRecognizer.setRecognitionListener(recognitionListener);



      mRecognizer.startListening(intent_voice);

    }


    @Override
    public void onResults(Bundle bundle) {
      String key = "";
      key = RESULTS_RECOGNITION;
      ArrayList<String> mResult = bundle.getStringArrayList(key);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
      String[] rs = new String[mResult.size()];
      mResult.toArray(rs);
      if(rs[0].contains("촬영")||rs[0].contains("촬")||rs[0].contains("찰")||rs[0].contains("차")||rs[0].contains("자료")||rs[0].contains("김치"))
      {
          ImageCap();
          soundpool.play(tak,1,1,0,0,1);
      }
      else if(rs[0].contains("왼")||rs[0].contains("좌")||rs[0].contains("자"))
      {
        if(rs[0].contains("아래")||rs[0].contains("하"))
        {
          setup("7");
        }
        else if(rs[0].contains("위")||rs[0].contains("상"))
        {
          setup("1");
        }
        else {
          setup("4");
        }

      }
      else if(rs[0].contains("가운")||rs[0].contains("중앙")||rs[0].contains("가우")||rs[0].contains("중"))
      {
        if(rs[0].contains("아래")||rs[0].contains("하")||rs[0].contains("래"))
        {
          setup("8");
        }
        else if(rs[0].contains("위")||rs[0].contains("상"))
        {
          setup("2");
        }
        else {
          setup("5");
        }
      }
      else if(rs[0].contains("오른")||rs[0].contains("우"))
      {
        if(rs[0].contains("아래")||rs[0].contains("하"))
        {
          setup("9");
        }
        else if(rs[0].contains("위")||rs[0].contains("상"))
        {
          setup("3");
        }
        else {
          setup("6");
        }
      }
      else if(rs[0].contains("지정"))
      {
        setup("12");
      }
      else if(rs[0].contains("초기")||rs[0].contains("원")||rs[0].contains("제자"))
      {
        setup("13");
      }
      else if(rs[0].contains("현재")||rs[0].contains("지금"))
      {
        current_location();
      }
      else if(rs[0].contains("멀티"))
      {
        soundpool.play(yesmaster,1,1,0,0,1);
      }


      textView.setText(rs[0]);
      mRecognizer.stopListening();
      onError(1);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }
  };
  public void current_location() {
    if(DetectorActivity.X_inBox<100)
    {
      if(DetectorActivity.Y_inBox2<100)
      {
        soundpool.play(left_up,1,1,0,0,1);
      }
      else if(DetectorActivity.Y_inBox2<200)
      {
        soundpool.play(left,1,1,0,0,1);
      }
      else
      {
        soundpool.play(left_bottom,1,1,0,0,1);
      }
    }
    else if(DetectorActivity.X_inBox>100&&DetectorActivity.X_inBox<=200)
    {
      if(DetectorActivity.Y_inBox2<100)
      {
        soundpool.play(center_up,1,1,0,0,1);
      }
      else if(DetectorActivity.Y_inBox2<200)
      {
        soundpool.play(center,1,1,0,0,1);
      }
      else
      {
        soundpool.play(center_bottom,1,1,0,0,1);
      }
    }
    else if(DetectorActivity.X_inBox>200)
    {
      if(DetectorActivity.Y_inBox2<100)
      {
        soundpool.play(right_up,1,1,0,0,1);
      }
      else if(DetectorActivity.Y_inBox2<200)
      {
        soundpool.play(right,1,1,0,0,1);
      }
      else
      {
        soundpool.play(right_bottom,1,1,0,0,1);
      }
    }


  }
  public void setup(String message) {
    Button btnSend = (Button) findViewById(R.id.btnSend);
    btnSend.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        bt.send("1400,700", true);
      }
    });
    double X=TensorFlowObjectDetectionAPIModel.width_1;
    double Y=TensorFlowObjectDetectionAPIModel.height_1;
    if(message.equals("11"))
    {
      current_location();
    }

    else if(message.equals("12"))
    {
      boolean te = DetectorActivity.inbool;
      boolean te2 = DetectorActivity.outbool;
      if(te2 == true && te == false)
      {
        setFragment2();
      }
      int tmp_X=(int)( X*300 / 1000)-(int)(DetectorActivity.X_inBox) ;
      int tmp_Y=(int)( Y*300 / 950)-(int)(DetectorActivity.Y_inBox2);

      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }

    else if(message.equals("13"))
    {
//      int tmp_Y=DetectorActivity.Y_height;
//      Toast.makeText(getApplicationContext()
//              ,tmp_Y
//              , LENGTH_SHORT).show();
    }

    else if(message.equals("0"))
    {
      ImageCap();
      soundpool.play(tak,1,0,0,0,1);
    }
    else if(message.equals("1"))
    {
      int tmp_X=50-DetectorActivity.X_inBox;
      int tmp_Y=100-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "1번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("2"))
    {
      int tmp_X=150-DetectorActivity.X_inBox;
      int tmp_Y=100-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "2번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }

    else if(message.equals("3"))
    {
      int tmp_X=250-DetectorActivity.X_inBox;
      int tmp_Y=100-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "3번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);

    }
    else if(message.equals("4"))
    {
      int tmp_X=50-DetectorActivity.X_inBox;
      int tmp_Y=180-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "4번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("5"))
    {
      int tmp_X=150-DetectorActivity.X_inBox;
      int tmp_Y=180-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "5번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("6"))
    {
      int tmp_X=250-DetectorActivity.X_inBox;
      int tmp_Y=180-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "6번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("7"))
    {
      int tmp_X=50-DetectorActivity.X_inBox;
      int tmp_Y=270-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "7번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("8"))
    {
      int tmp_X=150-DetectorActivity.X_inBox;
      int tmp_Y=270-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "8번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }
    else if(message.equals("9"))
    {
      int tmp_X=250-DetectorActivity.X_inBox;
      int tmp_Y=270-DetectorActivity.Y_inBox;
      Toast.makeText(getApplicationContext()
              , "9번모드 전송완료"
              , LENGTH_SHORT).show();
      bt.send(message+":"+tmp_X+","+tmp_Y, true);
    }

//


//    btnSend.setOnClickListener(new View.OnClickListener() {
//      public void onClick(View v) {
////        double X2=TensorFlowObjectDetectionAPIModel.width_1;
////        double Y2=TensorFlowObjectDetectionAPIModel.height_1;
//        Toast.makeText(getApplicationContext()
//                , "5"+":"+"100"+","+"300"
//                , LENGTH_SHORT).show();
//               bt.send("5"+":"+"100"+","+"300", true);
//
//      }
//    });
  }



  public void onActivityResult(int requestCode2, int resultCode, Intent data) {
    super.onActivityResult(requestCode2, resultCode, data);
    if (requestCode2 == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if (resultCode == Activity.RESULT_OK)
        bt.connect(data);
    } else if (requestCode2 == BluetoothState.REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        setup("ActivityResult");
      } else {
        Toast.makeText(getApplicationContext()
                , "Bluetooth was not enabled."
                , LENGTH_SHORT).show();
        finish();
      }
    }
  }
  private byte[] lastPreviewFrame;

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /**
   * Callback for android.hardware.Camera API
   */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    lastPreviewFrame = bytes;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /**
   * Callback for Camera2 API
   */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    //We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
    if (!bt.isBluetoothEnabled()) { //
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
    } else {
      if (!bt.isServiceAvailable()) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);

      }
    }


  }

  @Override
  public void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }
//
//  @Override
//  public synchronized void onPause() {
//    LOGGER.d("onPause " + this);
//
//    if (!isFinishing()) {
//      LOGGER.d("Requesting finish");
//      finish();
//    }
//
//    handlerThread.quitSafely();
//    try {
//      handlerThread.join();
//      handlerThread = null;
//      handler = null;
//    } catch (final InterruptedException e) {
//      LOGGER.e(e, "Exception!");
//    }
//
//    super.onPause();
//  }
//
//  @Override
//  public void onStop() {
//    LOGGER.d("onStop " + this);
//    super.onStop();
//  }
//
//  @Override
//  public  void onDestroy() {
//    LOGGER.d("onDestroy " + this);
//    super.onDestroy();
//
//  }

  protected  void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED
              && grantResults[2] == PackageManager.PERMISSION_GRANTED
       &&grantResults[3] == PackageManager.PERMISSION_GRANTED
              ) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
          checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED &&
              checkSelfPermission(PERMISSION_BLE) == PackageManager.PERMISSION_GRANTED &&
              checkSelfPermission(PERMISSION_BLE2) == PackageManager.PERMISSION_GRANTED ;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) ||
          shouldShowRequestPermissionRationale(PERMISSION_STORAGE) ||
              shouldShowRequestPermissionRationale(PERMISSION_BLE) ||
              shouldShowRequestPermissionRationale(PERMISSION_BLE2)
      ) {

//        Toast.makeText(CameraActivity.this,
//            "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA, PERMISSION_STORAGE, PERMISSION_BLE ,PERMISSION_BLE2}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
            || isHardwareLevelSupported(characteristics, 
                                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();
    if (cameraId == null) {
//      Toast.makeText(this, "No Camera Detected", Toast.LENGTH_SHORT).show();
      finish();
    }

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.container, fragment)
        .commit();
  }
  protected void setFragment2() {
    String cameraId = chooseCamera();
    if (cameraId == null) {
//      Toast.makeText(this, "No Camera Detected", Toast.LENGTH_SHORT).show();
      finish();
    }

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivity.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera("2");
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager()
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit();
  }
  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  public boolean isDebug() {
    return debug;
  }

  public void requestRender() {
    final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
    if (overlay != null) {
      overlay.postInvalidate();

    }
  }

  public void addCallback(final OverlayView.DrawCallback callback) {
    final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
    if (overlay != null) {
      overlay.addCallback(callback);
    }
  }

  public void onSetDebug(final boolean debug) {}

//  @Override
//  public boolean onKeyDown(final int keyCode, final KeyEvent event) {
//    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
//            || keyCode == KeyEvent.KEYCODE_BUTTON_L1 || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//      debug = !debug;
//      requestRender();
//      onSetDebug(debug);
//      return true;
//    }
//    return super.onKeyDown(keyCode, event);
//  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }
  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  public static int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
    int degrees = 0;

    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }

    return result;
  }









  protected  abstract  void galleryAddPic(String s);
  protected  abstract  void ImageCap();
  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);
  protected abstract int getLayoutId();
  protected abstract Size getDesiredPreviewFrameSize();
}


