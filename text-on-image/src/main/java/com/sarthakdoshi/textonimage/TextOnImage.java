package com.sarthakdoshi.textonimage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextOnImage extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {


    public static String IMAGE_IN_URI = "imageInURI";
    public static String TEXT_TO_WRITE = "sourceText";
    public static String TEXT_FONT_SIZE = "textFontSize";
    public static String TEXT_COLOR = "textColor";
    public static String IMAGE_OUT_URI = "imageOutURI";
    public static String IMAGE_OUT_ERROR = "imageOutError";

    public static int TEXT_ON_IMAGE_RESULT_OK_CODE = 1;
    public static int TEXT_ON_IMAGE_RESULT_FAILED_CODE = -1;
    public static int TEXT_ON_IMAGE_REQUEST_CODE = 4;


    private static String TAG = TextOnImage.class.getSimpleName();
    private Uri imageInUri,imageOutUri;
    private String saveDir="/tmp/";
    private String textToWrite = "",textColor="#ffffff";
    private float textFontSize;
    private TextView  addTextView;
    private String errorAny = "";
    private ImageView sourceImageView;
    private RelativeLayout workingLayout,baseLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private ProgressDialog progressDialog;
    private RotationGestureDetector mRotationGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup the scale detection and rotation detection for the textview
        scaleGestureDetector = new ScaleGestureDetector(TextOnImage.this,new simpleOnScaleGestureListener());
        mRotationGestureDetector = new RotationGestureDetector(TextOnImage.this);
        extractBundle();
        uiSetup();
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        //set the rotation of the text view
        float angle = rotationDetector.getAngle();
        addTextView.setRotation(angle);
    }

    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //change the font size of the text on pinch
            float size = addTextView.getTextSize();
            float factor = detector.getScaleFactor();
            float product = size*factor;
            addTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);
            size = addTextView.getTextSize();
            return true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        mRotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void extractBundle()
    {   //extract the data from previous activity
        Bundle bundle = getIntent().getExtras();
        imageInUri = Uri.parse(bundle.getString(IMAGE_IN_URI));
        textToWrite = bundle.getString(TEXT_TO_WRITE);
        textFontSize = bundle.getFloat(TEXT_FONT_SIZE);
        textColor = bundle.getString(TEXT_COLOR);
    }

    private void uiSetup()
    {
        //show progress dialog
        progressDialog = new ProgressDialog(TextOnImage.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //setup action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Text");
        }

        try {
            //get the image bitmap
            Bitmap bitmapForImageView = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageInUri);
            int width = 0;
            int height =0;
            //resize the views as per the image size
            if(bitmapForImageView.getWidth()>bitmapForImageView.getHeight())
            {
                width = 1280;
                height = 720;
            }else if(bitmapForImageView.getWidth()<bitmapForImageView.getHeight())
            {
                width = 720;
                height=1280;
            }else
            {
                width = 600;
                height = 600;
            }

            //create the layouts
            //base layout
            baseLayout = new RelativeLayout(TextOnImage.this);
            RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            baseLayout.setBackgroundColor(Color.parseColor("#000000"));

            //working layout
            workingLayout = new RelativeLayout(TextOnImage.this);
            RelativeLayout.LayoutParams workingLayoutParams = new RelativeLayout.LayoutParams(width,height);
            workingLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            workingLayout.setLayoutParams(workingLayoutParams);

            //image view
            sourceImageView = new ImageView(TextOnImage.this);
            RelativeLayout.LayoutParams sourceImageParams = new RelativeLayout.LayoutParams(width,height);
            sourceImageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            sourceImageView.setLayoutParams(sourceImageParams);

            //textview
            addTextView = new TextView(TextOnImage.this);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addTextView.setLayoutParams(textViewParams);

            //add views to working layout
            workingLayout.addView(sourceImageView);
            workingLayout.addView(addTextView);

            //add view to base layout
            baseLayout.addView(workingLayout);

            //set content view
            setContentView(baseLayout,baseLayoutParams);
            
            sourceImageView.setImageBitmap(bitmapForImageView);
            workingLayout.setDrawingCacheEnabled(true);
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        } catch (IOException e) {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            e.printStackTrace();
        }

        //setup the text view
        addTextView.setText(textToWrite);
        addTextView.setTextSize(textFontSize);
        addTextView.setTextColor(Color.parseColor(textColor));
        addTextView.setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0, lastY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               switch (motionEvent.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        lastX = motionEvent.getX();
                        lastY = motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = motionEvent.getX() - lastX;
                        float dy = motionEvent.getY() - lastY;
                        float finalX = view.getX() + dx;
                        float finalY = view.getY() + dy + view.getHeight();
                        view.setX(finalX);
                        view.setY(finalY);
                        break;
                }

                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }else if(item.getItemId() == R.id.setTextButton)
        {
            progressDialog.setMessage("Adding Text");
            progressDialog.show();

            //set the text
            boolean doneSetting = setTextFinal();
            if(doneSetting)
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_URI,imageOutUri.toString());
                setResult(TEXT_ON_IMAGE_RESULT_OK_CODE,intent);
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                finish();

            }else
            {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_OUT_ERROR,errorAny);
                setResult(TEXT_ON_IMAGE_RESULT_FAILED_CODE,intent);
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                finish();

            }
            return  true;
        }else if(item.getItemId() == R.id.setColor)
        {
            ColorPickerDialogBuilder.with(TextOnImage.this,R.style.AppTheme_ColorPicker)
                        .setTitle("Choose Color")
                        .initialColor(Color.parseColor(textColor))
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(20)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int i) {
                            addTextView.setTextColor(i);
                            }
                        })
                        .setPositiveButton("Ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                addTextView.setTextColor(i);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).build().show();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

    }

    private boolean setTextFinal()
    {
        addTextView.setOnTouchListener(null);
        boolean toBeReturn = false;
        workingLayout.buildDrawingCache();
        toBeReturn = saveFile(Bitmap.createBitmap(workingLayout.getDrawingCache()),"temp.jpg");
        return toBeReturn;
    }

    private boolean saveFile(Bitmap sourceImageBitmap,String fileName)
    {
        boolean result = false;
        String path = getApplicationInfo().dataDir + saveDir;
        File pathFile = new File(path);
        pathFile.mkdirs();
        File imageFile = new File(path,fileName);
        if(imageFile.exists())
        {
            imageFile.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);

            sourceImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            result = true;
        } catch (Exception e) {
            errorAny = e.getMessage();
            result =false;
            e.printStackTrace();
        }
        imageOutUri = Uri.fromFile(imageFile);
        return result;
    }

}