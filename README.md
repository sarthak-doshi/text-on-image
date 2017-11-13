Android Text On Image
=======

Simple Library for to add the text on 

![Crop](https://github.com/SarthakDoshi04/text-on-image/blob/master/art/working.gif?raw=true  width="720" height="1280")

## Usage
1. Add it in your root build.gradle at the end of repositories:

 ```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 ```

2. Include the library

 ```
 compile 'com.github.sarthakdoshi04:text-on-image:0.1.0'
 ```

Add permissions to manifest

 ```
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 ```

### Using Activity

2. Add `TextOnImage` into your AndroidManifest.xml
 ```xml
 <activity android:name="com.sarthakdoshi.textonimage.TextOnImage"
   android:theme="@style/Base.Theme.AppCompat"/> <!-- optional (needed if default theme has no action bar) -->
 ```

3. Start `TextOnImage Activity` using builder pattern from your activity
 ```java
    //pass the data to add it in image
        Intent intent = new Intent(MainActivity.this,TextOnImage.class);
        Bundle bundle = new Bundle();
        bundle.putString(TextOnImage.IMAGE_IN_URI,passImageUri.toString()); //image uri
        bundle.putString(TextOnImage.TEXT_COLOR,"#27ceb8");                 //initial color of the text
        bundle.putFloat(TextOnImage.TEXT_FONT_SIZE,20.0f);                  //initial text size
        bundle.putString(TextOnImage.TEXT_TO_WRITE,text);                   //text to be add in the image
        intent.putExtras(bundle);
        startActivityForResult(intent, TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE); //start activity for the result

 ```

4. Override `onActivityResult` method in your activity to get crop result
 ```java
 @Override
 public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        
 }
 ```

## Features
- Built-in `TextOnImage Activity`.
- Pinch to change the font size.
- Move the text over the image.
- Rotate the text using gesture.
- Get the output Image on OnActivtyResult.
- More..
 
## Dependance
  [QuadFlask - colorpicker](https://github.com/QuadFlask/colorpicker)
  
## To do

* Add Undo - Redo Support 
* Add Font Support
  
## License

Copyright 2017, Sarthak Doshi.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the   License.
You may obtain a copy of the License in the LICENSE file, or at:

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS   IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
