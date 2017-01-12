package com.example.hakon.movieavailablenotifier;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private ListView lv_relevant;
    private ListView lv_favorite;
    private List<String> relevantMovies;
    private List<String> favoriteMovies;
    private List<String> newRelevantMovies;
    private  ArrayAdapter<String> favoriteArrayAdapter;
    private  ArrayAdapter<String> relevantArrayAdapter;
    private String favoriteMovie;
    private String username;
    private boolean showRelevantMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleUIlist();
//                doGetWhatsNewRequest("http://hawkon.eu:5000/whats-new/" + username);

//                  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        username = "hawkon";
        showRelevantMovies = true;
        lv_relevant = (ListView) findViewById(R.id.my_list_view_id);
        lv_favorite = (ListView) findViewById(R.id.my_list_view_id_2);
        lv_favorite.setVisibility(View.GONE);

        relevantMovies = new ArrayList<String>();
        favoriteMovies = new ArrayList<String>();
        newRelevantMovies = new ArrayList<String>();

        doRelevantMoviesRequest("http://hawkon.eu:5000/movies-coming-soon/" + username);
        doGetFavoriteMoviesRequest("http://hawkon.eu:5000/user/" + username);


        favoriteArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                favoriteMovies );

        relevantArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                relevantMovies );

        lv_relevant.setAdapter(relevantArrayAdapter);
        lv_favorite.setAdapter(favoriteArrayAdapter);
        lv_relevant.setOnItemClickListener(this);
//        lv_favorite.setOnItemClickListener(this);
        // TODO make onclik for favorites as well


    }

    private void toggleUIlist() {
        if (showRelevantMovies) {
            lv_relevant.setVisibility(View.GONE);
            lv_favorite.setVisibility(View.VISIBLE);
        }
        else {
            lv_favorite.setVisibility(View.GONE);
            lv_relevant.setVisibility(View.VISIBLE);
        }
        showRelevantMovies = !showRelevantMovies;
    }

    private void doGetFavoriteMoviesRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                favoriteMovies.add(jsonArray.getString(i));
                            }
                            favoriteArrayAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json", "error dude");
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void doRelevantMoviesRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                relevantMovies.add(jsonArray.getString(i));
                            }
                            relevantArrayAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json", "error dude");
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void doGetWhatsNewRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            newRelevantMovies = new ArrayList<String >();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                newRelevantMovies.add(jsonArray.getString(i));
                            }
                            //TODO make notifications!
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json", "error dude");
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void doSetFavoriteRequest(final String movieTitle) {
        RequestQueue queue = Volley.newRequestQueue(this);
        favoriteMovie = movieTitle;
        String url = "http://hawkon.eu:5000/favorite";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        favoriteMovies.add(movieTitle);
                        relevantMovies.remove(favoriteMovie);
                        relevantArrayAdapter.notifyDataSetChanged();
                        favoriteArrayAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json", "error bro");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("movie", favoriteMovie);

                return params;
            }};

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
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

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        doSetFavoriteRequest(relevantMovies.get(position));
    }


}
