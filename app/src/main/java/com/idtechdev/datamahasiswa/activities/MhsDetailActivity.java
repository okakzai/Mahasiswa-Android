package com.idtechdev.datamahasiswa.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.idtechdev.datamahasiswa.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MhsDetailActivity extends AppCompatActivity {

    private static final String TAG = MhsDetailActivity.class.getSimpleName();
    private EditText nim, nama, jurusan;
    private Button btn_hapus;
    private Bitmap bitmap;
    CircleImageView foto_profil;
    SessionManager sessionManager;
    private static String URL_UPDATE = "https://data.didinstudio.com/mahasiswa-api/mhs/update";
    String getId;
    private Menu action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mhs_detail);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Data Mahasiswa");

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        getId = getIntent().getStringExtra("id");

        nim = findViewById(R.id.nim);
        nama = findViewById(R.id.nama);
        jurusan = findViewById(R.id.jurusan);
        Button btn_foto = findViewById(R.id.btn_foto);
        btn_hapus = findViewById(R.id.btn_hapus);
        foto_profil = findViewById(R.id.foto_profil);
        nim.setEnabled(false);
        nama.setEnabled(false);
        jurusan.setEnabled(false);

        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
        btn_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusMhs();
            }
        });
        getUserDetail();
    }

    private void hapusMhs() {
        btn_hapus.setVisibility(View.GONE);

        String URL_DELETE = "https://data.didinstudio.com/mahasiswa-api/mhs/delete";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            String message = jsonObject.getString("message");

                            if (status.equals("200") && error.equals("false")){
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        MhsDetailActivity.this.startActivity(new Intent(MhsDetailActivity.this, MhsActivity.class));
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                btn_hapus.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MhsDetailActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();

                            btn_hapus.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MhsDetailActivity.this, "Error! " + error.toString(), Toast.LENGTH_SHORT).show();

                        btn_hapus.setVisibility(View.VISIBLE);
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
                params.put("id",getId);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("Image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                foto_profil.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }

            UploadFoto(getId, getStringImage(bitmap));
        }
    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }

    private void UploadFoto(final String getId, final String foto) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            String message = jsonObject.getString("message");
                            if (status.equals("200") && error.equals("false")){
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MhsDetailActivity.this, MhsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MhsDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MhsDetailActivity.this, "Cek " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                params.put("foto", foto);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getUserDetail(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String URL_READ = "https://data.didinstudio.com/mahasiswa-api/mhs/id";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            if (status.equals("200") && error.equals("false")){
                                JSONArray jsonArray = jsonObject.getJSONArray("mhs");
                                for (int i=0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String strNim = object.getString("nim").trim();
                                    String strNama = object.getString("nama").trim();
                                    String strJurusan = object.getString("jurusan").trim();
                                    String strFoto = object.getString("foto").trim();
                                    Picasso.get().load(strFoto).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(foto_profil);
                                    nim.setText(strNim);
                                    nama.setText(strNama);
                                    jurusan.setText(strJurusan);

                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MhsDetailActivity.this, "Data tidak tersedia", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MhsDetailActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MhsDetailActivity.this, "Error "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", getId);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action, menu);
        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                nim.setEnabled(true);
                nama.setEnabled(true);
                jurusan.setEnabled(true);
                btn_hapus.setVisibility(View.GONE);
                nim.setFocusableInTouchMode(true);
                nama.setFocusableInTouchMode(true);
                jurusan.setFocusableInTouchMode(true);
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.showSoftInput(nim, InputMethodManager.SHOW_IMPLICIT);
                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);
                return true;

            case R.id.menu_save:
                Update();
                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);
                nim.setFocusableInTouchMode(false);
                nama.setFocusableInTouchMode(false);
                jurusan.setFocusableInTouchMode(false);
                nim.setFocusable(false);
                nama.setFocusable(false);
                jurusan.setFocusable(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Update() {
        final String nim = this.nim.getText().toString().trim();
        final String nama = this.nama.getText().toString().trim();
        final String jurusan = this.jurusan.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Update...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String error = jsonObject.getString("error");
                            String message = jsonObject.getString("message");

                            if (status.equals("200") && error.equals("false")){
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MhsDetailActivity.this, MhsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MhsDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MhsDetailActivity.this, "JSONException "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MhsDetailActivity.this, "onResponError "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nim", nim);
                params.put("nama", nama);
                params.put("jurusan", jurusan);
                params.put("id", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
