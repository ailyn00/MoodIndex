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
    ArrayList<String> favStocks;

    public MyAdapter(){

    }

    public MyAdapter(Context context, ArrayList<String> favStocks) {
        this.context = context;
        this.favStocks = favStocks;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        String stock = favStocks.get(position);
        holder.textView.setText(stock);
    }

    @Override
    public int getItemCount() {
        if(favStocks != null){
            return favStocks.size();
        } else {
            return 0;
        }
    }

 public static class MyViewHolder extends RecyclerView.ViewHolder{
    TextView textView;
     public MyViewHolder(@NonNull View itemView) {

         super(itemView);
         textView = itemView.findViewById(R.id.stock);
     }
 }
}
