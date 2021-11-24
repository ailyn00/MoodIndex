package com.example.mycanvas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> favStocks;
    private OnNoteListener mOnNoteListener;

    public MyAdapter() {

    }

    public MyAdapter(Context context, ArrayList<String> favStocks, OnNoteListener onNoteListener) {
        this.context = context;
        this.favStocks = favStocks;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new MyViewHolder(v, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        String stock = favStocks.get(position);
        holder.textView.setText(stock);
    }

    @Override
    public int getItemCount() {
        if (favStocks != null) {
            return favStocks.size();
        } else {
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        OnNoteListener onNoteListener;
        Button delFavStockBtn;

        int position;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {

            super(itemView);
            textView = itemView.findViewById(R.id.stock);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

            delFavStockBtn = itemView.findViewById(R.id.delFavStockBtn);
            delFavStockBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.delFavStockBtn:
                    onNoteListener.delBtnClicked(getAdapterPosition());
                    break;
                default:
                    onNoteListener.oNoteClick(getAdapterPosition());
            }
        }
    }

    public interface OnNoteListener {
        void oNoteClick(int position);
        void delBtnClicked(int position);
    }
}
