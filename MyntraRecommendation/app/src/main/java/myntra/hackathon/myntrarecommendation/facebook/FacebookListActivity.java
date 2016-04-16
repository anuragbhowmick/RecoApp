package myntra.hackathon.myntrarecommendation.facebook;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.restfb.types.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import myntra.hackathon.myntrarecommendation.Constants;
import myntra.hackathon.myntrarecommendation.GridViewActivity;
import myntra.hackathon.myntrarecommendation.R;
import myntra.hackathon.myntrarecommendation.RequestHandler;

/**
 * Created by anurag on 4/16/16.
 */
public class FacebookListActivity extends ListActivity {

    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter;
    List<User> fbFriendsList = new ArrayList<>();
    String token = "CAAGo85yJfPgBAKEZCnbDGiiqnbRg8ass7C517EgjoMZCTl7ZBQhTDCeCNyxLEUUoZBA0yOfABMYn1Ktm10iEdNfxOuEIbApvOM5e2DIqBqbgsD5KcLsThdCzvCcsEdUadbd4EjCNqsOLZANUYu8ivxFRZCJlaML0y9Hn57Iggkcz9sGXrgPEQAJfQdS0ZB1JOBm5hYs6f3iJAZDZD";
    RestFBClient restFBClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_list_view);
        Log.d("AB", "FacebookListActivity.onCreate");
        restFBClient = new RestFBClientImpl(token);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        setListAdapter(adapter);
        FacebookUsers fbInfo = new FacebookUsers();
        fbInfo.execute();
        this.getListView().setOnItemClickListener(mMessageClickedHandler);
    }

    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Log.d("AB","On item cliecked at postion " + position);
            User user = fbFriendsList.get(position);
            if(user == null) {
                Log.e("AB","No user found");
            }
            RecommendationAsyncTask recommendationAsyncTask = new RecommendationAsyncTask();
            recommendationAsyncTask.execute(user);
        }
    };


    class FacebookUsers extends AsyncTask<String,Void,List<User>> {

        @Override
        protected List<User> doInBackground(String... params) {
            Log.d("AB","FacebookInfo.doInBackground");
            List<User> friendsList = restFBClient.getFriendList();
            return friendsList;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            Log.d("AB", "FacebookInfo. onPostExecute");
            for(User user : users) {
                listItems.add(user.getName());
            }
            fbFriendsList = users;
            adapter.notifyDataSetChanged();
        }
    }

    class RecommendationAsyncTask extends AsyncTask<User,Void,String> {

        ProgressDialog loading;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(FacebookListActivity.this, "Getting Recommendations", "Please wait...",true,true);
        }

        @Override
        protected String doInBackground(User... params) {
            Log.d("AB","FacebookInfo.doInBackground");
            String response = null;
            List<String> profilePictures = restFBClient.getProfilePhotosUrl(params[0]);
            try {
                JSONObject imageObject = new JSONObject();
                JSONArray imageUrlArray = new JSONArray();

                for(String image : profilePictures) {
                    imageUrlArray.put(image);
                }
                imageObject.put(Constants.REQUEST_IMAGEURLS_KEY,imageUrlArray);
                response = rh.sendPostRequest(Constants.UPLOAD_URL,imageObject.toString());
                Log.d("AB","doInBackground . response " + response);
            } catch(Exception e) {
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d("AB", "FacebookInfo. onPostExecute");
            loading.dismiss();
            if(response == null) {
                Log.d("AB","Null response while getting recommendations");
                return;
            }
            Log.d("AB", "Recomedation response is " + response);
            Intent intent = new Intent(FacebookListActivity.this, GridViewActivity.class);
            intent.putExtra(Constants.RECOMMENDATION_KEY, response);
            startActivity(intent);
        }
    }

}
