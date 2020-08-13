package com.hillarie.medbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<pojo> pojoList;
    private List<pojo> pojoListFiltered;
    ProgressDialog progressDialog;
    SpotsDialog pd;
    RequestQueue rQueue;
    JSONObject jsonObject;

    String GETTitle,GETBody;

    public class MyViewHolder extends RecyclerView.ViewHolder {


FloatingActionButton FabDelete,FabUpdate;
         TextView TVTitle, TVBody;


        public MyViewHolder(View view) {
            super(view);
            TVTitle = view.findViewById(R.id.tv_title);
            TVBody = view.findViewById(R.id.tv_body);
            FabUpdate = view.findViewById(R.id.fab_update);
            FabDelete = view.findViewById(R.id.fab_delete);




        }
    }


    public MainActivityAdapter(Context context, List<pojo> pojoList, AdapterListener listener) {
        this.context = context;
        this.pojoList = pojoList;
        this.pojoListFiltered = pojoList;
pd=new SpotsDialog(context);
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final pojo pojo = pojoListFiltered.get(position);
        holder.TVTitle.setText(pojo.getTitle());
        holder.TVBody.setText(pojo.getBody());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("DIALOG VIEW.");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("ID : "+pojo.getId() +"\n" +
                        "Title : "+pojo.getTitle() +"\n"+
                        "Body : "+pojo.getBody() +"\n"
                        );

                // Set the alert dialog yes button click listener
                builder.setPositiveButton("OKAY", (dialog, which) ->
                      dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.FabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  UpdateRecord(pojo.getId(),pojo.getBody(),pojo.getTitle());
            }

            private void UpdateRecord(int id, String body, String title) {


                EditText ETTitle,ETBody;
                Button BtnUpload;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alert = builder.create();
                alert.setIcon(R.mipmap.ic_launcher);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.dialog_addpost, null);

                ETTitle = view.findViewById(R.id.et_title);
                ETBody = view.findViewById(R.id.et_body);
                BtnUpload = view.findViewById(R.id.btn_upload);
                BtnUpload.setText("UPDATE");


                BtnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        GETTitle = ETTitle.getText().toString();
                        GETBody = ETBody.getText().toString();
                        if (GETTitle.isEmpty()) {
                            Toast.makeText(context, "Add Title", Toast.LENGTH_LONG).show();

                        }
                        else if (GETBody.isEmpty()) {
                            Toast.makeText(context, "Add Body", Toast.LENGTH_LONG).show();

                        }else {

                            uploaddata(GETTitle, GETBody,id);


                        }


                        //  alert.dismiss();
                    }

                    private void uploaddata(String Title, String Body, int id) {



                        pd = new SpotsDialog(context, R.style.ProgressUpdate);
                        pd.setCancelable(true);
                        pd.show();


                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("title", Title);
                            jsonObject.put("body", Body);
                            jsonObject.put("id", id);




                        } catch (JSONException e) {
                            Log.e("JSONObject Here", e.toString());
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, "https://jsonplaceholder.typicode.com/posts/1", jsonObject,
                                response -> {
                                    Log.e("RES", String.valueOf(response));
                                    rQueue.getCache().clear();
                                    pd.dismiss();
                                    try {
                                        //Check if user got logged in successfully
                                        if (!response.getBoolean("Error")) {
                                            Toast.makeText(context, response.getString("Message"), Toast.LENGTH_SHORT).show();


                                        } else {
                                            Toast.makeText(context, response.getString("Message"), Toast.LENGTH_SHORT).show();

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

                        rQueue = Volley.newRequestQueue(context);
                        rQueue.add(jsonObjectRequest);

                    }


                });

                alert.setView(view);
                alert.show();


            }
        });

        holder.FabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("MEDBOOK.");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("DO YOU WANT TO DELETE?");
                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton("Yes", (dialog, which) ->
                            DeleteRecord(pojo.getId()));

                    builder.setNegativeButton("No", (dialog, which) ->
                            dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();




            }



            private void DeleteRecord(int id) {



                pd = new SpotsDialog(context, R.style.ProgressDelete);
                pd.setCancelable(true);
                pd.show();


                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("id", id);





                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, "https://jsonplaceholder.typicode.com/posts/1", jsonObject,
                        response -> {
                            Log.e("RES", String.valueOf(response));
                            rQueue.getCache().clear();
                            pd.dismiss();
                            //Check if user got logged in successfully

                            Toast.makeText(context, "Record removed", Toast.LENGTH_SHORT).show();


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

                rQueue = Volley.newRequestQueue(context);
                rQueue.add(jsonObjectRequest);

            }
        });






    }



    @Override
    public int getItemCount() {
        return pojoListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    pojoListFiltered = pojoList;
                } else {
                    List<pojo> filteredList = new ArrayList<>();
                    for (pojo row : pojoList) {

                        // data match condition. this might differ depending on your requirement
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())
                                || row.getBody().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    pojoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = pojoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pojoListFiltered = (ArrayList<pojo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface AdapterListener {
        void onAdapterSelected(pojo pojo);
    }
}
