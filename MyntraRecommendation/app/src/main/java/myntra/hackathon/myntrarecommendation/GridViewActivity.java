package myntra.hackathon.myntrarecommendation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GridViewActivity extends ActionBarActivity {
    private static final String TAG = GridViewActivity.class.getSimpleName();

    private GridView mGridView;
    private ProgressBar mProgressBar;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://javatechig.com/?json=get_recent_posts&count=45";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getTitle()).
                        putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });

        handleIntent();

        //Start download
//        new AsyncHttpTask().execute(FEED_URL);
//        mProgressBar.setVisibility(View.VISIBLE);
    }


    public void handleIntent() {
        Intent intent = getIntent();
        if(intent != null) {
            String recommendations = intent.getStringExtra(Constants.RECOMMENDATION_KEY);
            Log.d("AB", "recommedation object " + recommendations);
            parseResponse(recommendations);
        }
    }

/*    public void parseResponse(String recommendation) {
        Log.d("AB","parsing response received");
        try {
            JSONObject recomObject = new JSONObject(recommendation);
            JSONArray jImageArray = recomObject.getJSONArray(Constants.RECOM_IMAGES_KEY);

            GridItem item;
            for(int i = 0 ; i < jImageArray.length() ; i++) {
                item = new GridItem();
                item.setTitle("name");
                item.setImage(jImageArray.getString(i));
                mGridData.add(item);
            }
            mGridAdapter.setGridData(mGridData);
        } catch (Exception e) {

        }

    }*/

    public void parseResponse(String recommendation) {
        try {
            JSONArray jArray = new JSONArray(recommendation);
            GridItem item;
            for(int i = 0 ; i < jArray.length() ; i++) {
                JSONObject recoObject = jArray.getJSONObject(i);
                String name = recoObject.getString("productName");
                String image = recoObject.getString("imageUrl");
                item = new GridItem();
                item.setTitle(name);
                item.setImage(image);
                mGridData.add(item);
            }
            mGridAdapter.setGridData(mGridData);
        } catch (Exception e) {

        }

    }

/*    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        RequestHandler rh = new RequestHandler();
        @Override
        protected Integer doInBackground(String... params) {
            String uri = params[0];
            String response = rh.sendGetRequest(uri);
            if(response == null) {
                return 0;
            }
            parseResult(response);
            return 1;

*//*            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;*//*
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    *//**
     * Parsing the feed results and get the list
     *
     * @param result
     *//*
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                item = new GridItem();
                item.setTitle(title);
                JSONArray attachments = post.getJSONArray("attachments");
                if (null != attachments && attachments.length() > 0) {
                    JSONObject attachment = attachments.getJSONObject(0);
                    if (attachment != null)
                        item.setImage(attachment.getString("url"));
                }
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}