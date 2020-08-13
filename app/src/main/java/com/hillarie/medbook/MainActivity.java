package com.hillarie.medbook;

/**
 * Created by hilla on 13/08/2020.
 */

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity implements MainActivityAdapter.AdapterListener  {
    private List<pojo> pojoList;
    private MainActivityAdapter mAdapter;
    private SearchView searchView;


    SpotsDialog pd;


    FloatingActionButton FabAddPost;

    JSONObject jsonObject;
    RequestQueue rQueue;
    public Button BtnUpload;

    RecyclerView recyclerView;

    String GetTitle, GetBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FabAddPost = findViewById(R.id.fab_addpost);
        recyclerView = findViewById(R.id.rv);
        pojoList = new ArrayList<>();
        mAdapter = new MainActivityAdapter(this, pojoList, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        data();


        FabAddPost.setOnClickListener(view -> {

            DialogAddStuff();
        });
    }

    private void DialogAddStuff() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("MEDBOOK.");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("DO YOU WANT TO ADD?");
        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", (dialog, which) ->
                UploadObje());

        builder.setNegativeButton("No", (dialog, which) ->
                dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void UploadObje() {


        EditText ETTitle,ETBody;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alert = builder.create();
        alert.setIcon(R.mipmap.ic_launcher);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.dialog_addpost, null);

        ETTitle = view.findViewById(R.id.et_title);
        ETBody = view.findViewById(R.id.et_body);
        BtnUpload = view.findViewById(R.id.btn_upload);


        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GetTitle = ETTitle.getText().toString();
                GetBody = ETBody.getText().toString();
                if (GetTitle.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Add Title", Toast.LENGTH_LONG).show();

                }
               else if (GetBody.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Add Body", Toast.LENGTH_LONG).show();

                }else {

                    uploaddata(GetTitle, GetBody);


                }


                //  alert.dismiss();
            }

            private void uploaddata(String Title, String Body) {



                pd = new SpotsDialog(MainActivity.this, R.style.ProgressUpload);
                pd.setCancelable(true);
                pd.show();


                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("title", Title);
                    jsonObject.put("body", Body);
                    jsonObject.put("userId", 1);




                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://jsonplaceholder.typicode.com/posts", jsonObject,
                        response -> {
                            Log.e("RES", String.valueOf(response));
                            rQueue.getCache().clear();
                            pd.dismiss();
                            try {
                                //DIALOGRESPONSE
                                if (response.getInt("id")>0){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("POST RESPONSE.");
                                    builder.setIcon(R.mipmap.ic_launcher);
                                    builder.setMessage("ID : "+response.getString("id"));
                                    // Set the alert dialog yes button click listener
                                    builder.setPositiveButton("OKAY", (dialog, which) ->
                                            data());

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "failed to post", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ERROR", volleyError.toString());

                    }
                }){




                    //This is for Headers If You Needed
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-type", "application/json; charset=UTF-8");
                        return params;
                    }
                };

                rQueue = Volley.newRequestQueue(MainActivity.this);
                rQueue.add(jsonObjectRequest);

            }


        });

        alert.setView(view);
        alert.show();


    }





    /**
     * fetches json by making http calls
     */
    private void data() {

        pd = new SpotsDialog(MainActivity.this, R.style.ProgressLoading);
        pd.setCancelable(true);
        pd.show();

        JsonArrayRequest request = new JsonArrayRequest("https://jsonplaceholder.typicode.com/posts",
                response -> {
                    Log.d("Data", String.valueOf(response));
                    pd.dismiss();
                    if (response == null) {
                        Toast.makeText(getApplicationContext(), "Couldn't fetch any data! Please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    List<pojo> items = new Gson().fromJson(response.toString(), new TypeToken<List<pojo>>() {
                    }.getType());

                    // adding contacts to contacts list
                    pojoList.clear();
                    pojoList.addAll(items);


                    // refreshing recycler view
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }, error -> {
               pd.dismiss();

            // error in getting json
        });


        com.hillarie.medbook.Volley.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_refresh) {
            data();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on background button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onAdapterSelected(pojo pojo) {

    }


}
