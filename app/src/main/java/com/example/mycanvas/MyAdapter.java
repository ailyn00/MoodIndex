package com.example.mycanvas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    Map<String , Object> stocklist;

    public MyAdapter(Context context, Map<String, Object> stocklist) {
        this.context = context;
        this.stocklist = stocklist;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        List<String> indexes = new ArrayList<String>(stocklist.keySet());
        String stock = indexes.get(position);
        holder.textView.setText(stock);


    }

    @Override
    public int getItemCount() {
        return stocklist.size();
    }

 public static class MyViewHolder extends RecyclerView.ViewHolder{
    TextView textView;
     public MyViewHolder(@NonNull View itemView) {

         super(itemView);
         textView = itemView.findViewById(R.id.stock);
     }
 }
}
