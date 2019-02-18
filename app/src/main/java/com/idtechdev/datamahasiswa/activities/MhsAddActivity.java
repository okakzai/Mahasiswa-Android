package com.idtechdev.datamahasiswa.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idtechdev.datamahasiswa.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MhsAddActivity extends AppCompatActivity {

    private EditText nim , nama, jurusan;
    private Button btn_add_mhs;
    private ProgressBar loading;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhs_add);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Data Mahasiswa");
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        loading = findViewById(R.id.loading);
        nim = findViewById(R.id.nim);
        nama = findViewById(R.id.nama);
        jurusan = findViewById(R.id.jurusan);
        btn_add_mhs = findViewById(R.id.btn_add_mhs);
        Button btn_back = findViewById(R.id.btn_back);

        btn_add_mhs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMhs();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MhsAddActivity.this, MhsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void AddMhs() {
        final String nim = this.nim.getText().toString().trim();
        final String nama = this.nama.getText().toString().trim();
        final String jurusan = this.jurusan.getText().toString().trim();

        if (nim.matches("")){
            Toast.makeText(this,"Anda belum mengisi nim",Toast.LENGTH_SHORT).show();
            return;
        }
        if (nama.matches("")){
            Toast.makeText(this,"Anda belum mengisi nama",Toast.LENGTH_SHORT).show();
            return;
        }
        if (jurusan.matches("")){
            Toast.makeText(this,"Anda belum mengisi program studi",Toast.LENGTH_SHORT).show();
            return;
        }

        loading.setVisibility(View.VISIBLE);
        btn_add_mhs.setVisibility(View.GONE);

        String URL_REGISTER = "https://data.didinstudio.com/mahasiswa-api/mhs/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            String message = jsonObject.getString("message");

                            if (status.equals("200") && error.equals("false")){
                                Toast.makeText(MhsAddActivity.this, message, Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MhsAddActivity.this, MhsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(MhsAddActivity.this, message, Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_add_mhs.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MhsAddActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_add_mhs.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MhsAddActivity.this, "Error! " + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_add_mhs.setVisibility(View.VISIBLE);
                    }
                })

        {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nim",nim);
                params.put("nama",nama);
                params.put("jurusan",jurusan);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
