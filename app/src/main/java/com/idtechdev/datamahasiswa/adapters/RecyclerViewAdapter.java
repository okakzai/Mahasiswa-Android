package com.idtechdev.datamahasiswa.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.idtechdev.datamahasiswa.R;
import com.idtechdev.datamahasiswa.activities.MhsDetailActivity;
import com.idtechdev.datamahasiswa.models.Mhs;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Mhs> mData;
    private RequestOptions options;

    public RecyclerViewAdapter(Context mContext, List<Mhs> mData) {
        this.mContext = mContext;
        this.mData = mData;
        options = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.mhs_row_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myviewHolder, @SuppressLint("RecyclerView") final int i) {
        myviewHolder.id_mhs.setText(mData.get(i).getId_mhs());
        myviewHolder.nim.setText(mData.get(i).getNim());
        myviewHolder.nama.setText(mData.get(i).getNama());
        myviewHolder.jurusan.setText(mData.get(i).getJurusan());

        Glide.with(mContext).load(mData.get(i).getFoto()).apply(options).into(myviewHolder.foto);
        //myviewHolder.
        myviewHolder .linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MhsDetailActivity.class);
                intent.putExtra("id", mData.get(i).getId_mhs());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView id_mhs, nim, nama, jurusan;
        ImageView foto;
        LinearLayout linearLayout;

        MyViewHolder(View itemView){
            super(itemView);
            id_mhs = itemView.findViewById(R.id.id_mhs);
            nim = itemView.findViewById(R.id.nim);
            nama = itemView.findViewById(R.id.nama);
            jurusan = itemView.findViewById(R.id.jurusan);
            foto = itemView.findViewById(R.id.thumbnail);
            linearLayout = itemView.findViewById(R.id.item_mhs_layout);
        }
    }
}
