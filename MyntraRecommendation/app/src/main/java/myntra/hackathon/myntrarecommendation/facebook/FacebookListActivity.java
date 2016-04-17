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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
//    String token = "CAAGo85yJfPgBAKEZCnbDGiiqnbRg8ass7C517EgjoMZCTl7ZBQhTDCeCNyxLEUUoZBA0yOfABMYn1Ktm10iEdNfxOuEIbApvOM5e2DIqBqbgsD5KcLsThdCzvCcsEdUadbd4EjCNqsOLZANUYu8ivxFRZCJlaML0y9Hn57Iggkcz9sGXrgPEQAJfQdS0ZB1JOBm5hYs6f3iJAZDZD";
    RestFBClient restFBClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_list_view);
        Log.d("AB", "FacebookListActivity.onCreate");
        if(AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        restFBClient = new RestFBClientImpl(AccessToken.getCurrentAccessToken().getToken());

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        setListAdapter(adapter);
        this.getListView().setOnItemClickListener(mMessageClickedHandler);
        FacebookUsers fbInfo = new FacebookUsers();
        fbInfo.execute();
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
            Log.d("AB", "FacebookInfo.doInBackground");
            List<User> friendsList = new ArrayList<>();
            try {
                friendsList = restFBClient.getFriendList();
            } catch (Throwable t) {
                 Log.d("AB","Exception while getting user lists " + t);
            }
            return friendsList;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            Log.d("AB", "FacebookInfo. onPostExecute");
            try {
                for (User user : users) {
                    Log.d("AB", "user id " + user.getId() + " name " + user.getName());
                    listItems.add(user.getName());
                }
                fbFriendsList = users;
                adapter.notifyDataSetChanged();
            } catch (Throwable t) {
                Log.d("AB","onPostExecute" + t);
            }
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
            Log.d("AB", "FacebookInfo.doInBackground");
            String response = null;
            try {
                User user = params[0];
                List<String> profilePictures = restFBClient.getProfilePhotosUrl(user);
                JSONObject imageObject = new JSONObject();
                JSONArray imageUrlArray = new JSONArray();

                for (String image : profilePictures) {
                    Log.d("AB", "user " + user.getName() + " gender " + user.getGender() + " image " + image);
                    imageUrlArray.put(image);
                }
                imageObject.put(Constants.REQUEST_IMAGEURLS_KEY, imageUrlArray);
                String gender = (user.getGender().compareToIgnoreCase("male")==0) ? "men":"women";
                imageObject.put(Constants.REQUEST_GENDER, gender);
                response = rh.sendPostRequest(Constants.UPLOAD_URL, imageObject.toString());
                Log.d("AB", "doInBackground . response " + response);
            } catch (Throwable t) {
                Log.e("AB","Exception in doInBackground " + t);
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
                Toast.makeText(getApplication(), "Recommendations not found. Please try again later", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d("AB", "Recomedation response is " + response);
            Intent intent = new Intent(FacebookListActivity.this, GridViewActivity.class);
            intent.putExtra(Constants.RECOMMENDATION_KEY, response);
            startActivity(intent);
        }
    }

/*    public void getFriendsList() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("AB","getFriendsList response " + response.toString() + " " + response.getJSONArray());
                    }
                }
        ).executeAsync();
    }

    public void getProfilePictures() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("AB","getFriendsList response " + response.toString() + " " + response.getJSONArray());
                    }
                }
        ).executeAsync();
    }*/
}
