package com.idtechdev.datamahasiswa.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idtechdev.datamahasiswa.R;
import com.idtechdev.datamahasiswa.adapters.RecyclerViewAdapter;
import com.idtechdev.datamahasiswa.models.Mhs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MhsActivity extends AppCompatActivity {

    private static final String TAG = MhsActivity.class.getSimpleName();
    private List<Mhs> listMhs = new ArrayList<>();
    private RecyclerView recyclerView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhs);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Data Mahasiswa");
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        recyclerView = findViewById(R.id.recycleview_id);
        getListMhs();

        FloatingActionButton addAction = findViewById(R.id.btn_add);
        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MhsActivity.this, MhsAddActivity.class);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
                startActivity(intent);
            }
        });
    }

    private void getListMhs() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String URL_MHS_READ = "https://data.didinstudio.com/mahasiswa-api/mhs";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MHS_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG,response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            if (status.equals("200") && error.equals("false")){
                                JSONArray jsonArray = jsonObject.getJSONArray("mhs");
                                for (int i=0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String strId_mhs = object.getString("id").trim();
                                    String strNim = object.getString("nim").trim();
                                    String strNama = object.getString("nama").trim();
                                    String strJurusan = object.getString("jurusan").trim();
                                    String strFoto = object.getString("foto").trim();
                                    Mhs mhs = new Mhs();
                                    mhs.setId_mhs(strId_mhs);
                                    mhs.setNim(strNim);
                                    mhs.setNama(strNama);
                                    mhs.setJurusan(strJurusan);
                                    mhs.setFoto(strFoto);
                                    listMhs.add(mhs);
                                    //Toast.makeText(MhsActivity.this, strNim+"\n"+strNama+"\n"+strFoto+"\n\n", Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(MhsActivity.this,"Size of Liste "+String.valueOf(listMhs.size()),Toast.LENGTH_SHORT).show();
                                //Toast.makeText(MhsActivity.this,listMhs.get(1).toString(),Toast.LENGTH_SHORT).show();
                                setuprecyclerview(listMhs);
                            } else {
                                Toast.makeText(MhsActivity.this, "Tidak dapat memuat data", Toast.LENGTH_SHORT).show();
                            }
                            //setuprecyclerview(listMhs);
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MhsActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MhsActivity.this, "Error "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setuprecyclerview(List<Mhs> listMhs){
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this, listMhs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }
}
