package com.sarthakdoshi.textonimageexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sarthakdoshi.textonimage.TextOnImage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Uri passImageUri;
    Button takeImage,processImage;
    EditText addTextEditText;
    private static int IMAGE_REQ = 44;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        takeImage = (Button) findViewById(R.id.takeImage);
        processImage = (Button) findViewById(R.id.processImage);
        addTextEditText = (EditText) findViewById(R.id.addTextEditText);

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //take image from the gallery
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_REQ);
            }
        });
        processImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passImageUri == null)
                {
                    Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }else
                {
                    if(TextUtils.isEmpty(addTextEditText.getText().toString()))
                    {
                        addTextEditText.setError("Enter the text");
                    }else
                    {
                        String text = addTextEditText.getText().toString();
                        addTextEditText.setText("");
                        addTextOnImage(text);
                    }
                }



            }
        });


    }

    private void addTextOnImage(String text) {
        //pass the data to add it in image
        Intent intent = new Intent(MainActivity.this,TextOnImage.class);
        Bundle bundle = new Bundle();
        bundle.putString(TextOnImage.IMAGE_IN_URI,passImageUri.toString()); //image uri
        bundle.putString(TextOnImage.TEXT_COLOR,"#27ceb8");                 //initial color of the text
        bundle.putFloat(TextOnImage.TEXT_FONT_SIZE,20.0f);                  //initial text size
        bundle.putString(TextOnImage.TEXT_TO_WRITE,text);                   //text to be add in the image
        intent.putExtras(bundle);
        startActivityForResult(intent, TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE); //start activity for the result
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_REQ)
        {
            if(resultCode == RESULT_OK)
            {

                passImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), passImageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE)
        {
            if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_OK_CODE)
            {
                Uri resultImageUri = Uri.parse(data.getStringExtra(TextOnImage.IMAGE_OUT_URI));

                try {
                  Bitmap  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultImageUri);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(resultCode == TextOnImage.TEXT_ON_IMAGE_RESULT_FAILED_CODE)
            {
                String errorInfo = data.getStringExtra(TextOnImage.IMAGE_OUT_ERROR);
                Log.d("MainActivity", "onActivityResult: "+errorInfo);
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
