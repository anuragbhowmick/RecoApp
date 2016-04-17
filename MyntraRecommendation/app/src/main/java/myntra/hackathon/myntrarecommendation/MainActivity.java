package myntra.hackathon.myntrarecommendation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import myntra.hackathon.myntrarecommendation.facebook.FacebookListActivity;

public class MainActivity extends AppCompatActivity {


    Button clickPhoto, chooseFile, recoButton, findFbFriends;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1;
    public static String URL = "Paste your URL here";
//    List<Uri> imageUri = new ArrayList<>();

    private int PICK_IMAGE_REQUEST = 1;
    private int CLICK_IMAGE_REQUEST = 2;
    //public static final String UPLOAD_URL = "http://simplifiedcoding.16mb.com/ImageUpload/upload.php";

//    public static final String UPLOAD_URL = "http://10.0.13.106:8080/products/Image"; // risabh


    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";

    private ImageView imageView;
    private Uri filePath;

    private Bitmap bitmap;
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;
    CallbackManager callbackManager;
//    ImageAdapter imageAdapter;
//    GridView gridview;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        findFbFriends = (Button) findViewById(R.id.findFBFriends);
        clickPhoto = (Button) findViewById(R.id.clickphoto);
        imageView = (ImageView) findViewById(R.id.imageView);
        chooseFile = (Button) findViewById(R.id.filechooser);
        recoButton = (Button) findViewById(R.id.uploadphoto);


//        gridview = (GridView) findViewById(R.id.gridView2);
//
//        imageUri = new ArrayList<>();
//        imageAdapter = new ImageAdapter(this,imageUri);
//        gridview.setAdapter(imageAdapter);


        findFbFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() == null) {
                    Toast.makeText(getApplication(), "You need to login to Facebook first", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this,FacebookListActivity.class);
                startActivity(intent);
            }
        });

        clickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickpic();
            }
        });

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        recoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecommendations();
            }
        });
        initializeFb();
        handleIntents();
    }

    public void initializeFb() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","user_friends","user_photos","user_likes"));
        LoginButton loginButton  = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("AB","access token " + loginResult.getAccessToken());
                Log.d("AB","curent access token " +  AccessToken.getCurrentAccessToken().getToken());
                // App code
            }

            @Override

            public void onCancel() {
                Log.d("AB","callbakc onCancel ");

                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("AB","callbakc onError ");
            }
        });

    }
    private void handleIntents() {
        Log.d("AB","handleIntents called ");
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
    }
    private void handleSendImage(Intent intent) {
        Log.d("AB","handleSendImage. Data " + intent.getDataString() + " extras " + intent.getExtras().toString());
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Log.d("AB","imageuri " + imageUri);
        }

    }

    private void handleSendMultipleImages(Intent intent) {
        Log.d("AB","handleSendMultipleImages. Data " + intent.getDataString() + " extras " + intent.getExtras().toString());
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        for(Uri uri : imageUris) {
            Log.d("AB","image uri " + uri);
        }

    }

    private void clickpic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CLICK_IMAGE_REQUEST);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void getRecommendations() {
        if(bitmap == null) {
            Toast.makeText(getApplicationContext(), "no image uploaded ", Toast.LENGTH_LONG).show();
            return;
        }
        RecommendationAsyncTask recommendationAsyncTask = new RecommendationAsyncTask();
        recommendationAsyncTask.execute(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AB","onActivityResult ");

//        gridview = (GridView) findViewById(R.id.gridView2);
//
//        imageUri = new ArrayList<>();
//        imageAdapter = new ImageAdapter(this,imageUri);
//        gridview.setAdapter(imageAdapter);
//
//            gridview.requestLayout();
//                imageAdapter = new ImageAdapter(this,imageUri);
//        gridview.setAdapter(imageAdapter);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d("AB",data.getDataString());
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageUri.add(0, filePath);
                //imageAdapter.setGridData(imageUri);
//                ((ImageAdapter)(gridview.getAdapter())).setGridData(imageUri);
                Picasso.with(getApplicationContext()).load(filePath).into(imageView);

//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CLICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null ) {
            Log.d("AB",data.getDataString());
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageUri.add(0, filePath);
//                imageAdapter.setGridData(imageUri);
//                ((ImageAdapter)(gridview.getAdapter())).setGridData(imageUri);
                Picasso.with(getApplicationContext()).load(filePath).into(imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        imageAdapter = new ImageAdapter(this,imageUri);
//        gridview.setAdapter(imageAdapter);
//    // TODO Auto-generated method stub super.onResume();
//    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    class RecommendationAsyncTask extends AsyncTask<Bitmap,Void,String>{

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, "Getting Recommendations", "Please wait...",true,true);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            loading.dismiss();
            if(response == null) {
                Log.d("AB","Null response while getting recommendations");
                Toast.makeText(getApplication(), "Recommendations not found. Please try again later", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d("AB","Recomedation response is " + response);
//            response = getSampleData();
            Intent intent = new Intent(MainActivity.this, GridViewActivity.class);
            intent.putExtra(Constants.RECOMMENDATION_KEY, response);

            //Start details activity
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }

        private String getSampleData() {
            try {
                JSONArray recoArray = new JSONArray();

                JSONObject jobj1 = new JSONObject();
                jobj1.put("productName","product");
                jobj1.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/assets/images/1003855/2015/10/12/11444654435993-Nike-Men-Tshirts-7631444654435703-1.jpg");

                JSONObject jobj2 = new JSONObject();
                jobj2.put("productName","product");
                jobj2.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/image/style/properties/605032/Nike-Men-Sports-Shoes_1_cbb03f2d3a5784e3cce4f7dfd976cc33.jpg");

                JSONObject jobj3 = new JSONObject();
                jobj3.put("productName","product");
                jobj3.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/image/style/properties/731299/Nike-Men-Charcoal-Grey-Air-Max-Muse-Casual-Shoes_1_d191db35cce486b900f9fc38639bdbb7.jpg");

                JSONObject jobj4 = new JSONObject();
                jobj4.put("productName","product");
                jobj4.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/assets/images/1003427/2015/12/15/11450160276958-Nike-Women-Black-Air-Zoom-Pegasus-32-Running-Shoes-4001450160276635-1.jpg");

                JSONObject jobj5 = new JSONObject();
                jobj5.put("productName","product");
                jobj5.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/image/style/properties/857397/Nike-Charcoal-Grey-Shorts_1_70103b36e51180a473ccce052344ccae.jpg");

                JSONObject jobj6 = new JSONObject();
                jobj6.put("productName","product");
                jobj6.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/assets/images/1110486/2016/1/28/11453978807761-Nike-Unisex-Blue--Black-Team-Training-Backpack-51453978807119-1.jpg");

                JSONObject jobj7 = new JSONObject();
                jobj7.put("productName","product");
                jobj7.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/image/style/properties/857754/Nike-Women-Tops_1_8d9edba02822501353d1d26d5aa5c495.jpg");

                JSONObject jobj8 = new JSONObject();
                jobj8.put("productName","product");
                jobj8.put("imageUrl","http://assets.myntassets.com/h_240,q_95,w_180/v1/assets/images/1110561/2016/1/23/11453546089711-Nike-Navy-As-Hyperspeed-Training-Jacket-6961453546089000-1.jpg");

                recoArray.put(jobj1);
                recoArray.put(jobj2);
                recoArray.put(jobj3);
                recoArray.put(jobj4);
                recoArray.put(jobj5);
                recoArray.put(jobj6);
                recoArray.put(jobj7);
                recoArray.put(jobj8);

                return recoArray.toString();
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            String uploadImage = getStringImage(bitmap);
            Log.d("AB","String object " + uploadImage);
            String result=null;

            try {
                JSONObject imageObject = new JSONObject();
                imageObject.put(Constants.RECOM_RAW_IMAGE_KEY, uploadImage);
                result = rh.sendPostRequest(Constants.UPLOAD_URL,imageObject.toString());
                Log.d("AB","doInBackground . response " + result);
            } catch(Exception e) {
            }
            return result;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
