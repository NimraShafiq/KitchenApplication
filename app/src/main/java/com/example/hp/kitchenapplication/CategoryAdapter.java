package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    ArrayList<Category> dataset;
    AdapterView.OnItemClickListener onItemClickListener;
    AdapterView.OnItemClickListener editClickListener;
    AdapterView.OnItemClickListener deleteClickListener;

    public CategoryAdapter(ArrayList<Category> dataset, AdapterView.OnItemClickListener onItemClickListener,AdapterView.OnItemClickListener editClickListener,AdapterView.OnItemClickListener deleteClickListener) {
        this.dataset = dataset;
        this.onItemClickListener =onItemClickListener;
        this.editClickListener=editClickListener;
        this.deleteClickListener=deleteClickListener;
    }
    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.category_item_layout,parent,false);
        return new CategoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, int position) {
        holder.tvCategoryName.setText(dataset.get(position).cat_name);
        holder.tvCategoryStatus.setText(""+dataset.get(position).cat_status);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null,holder.itemView,holder.getAdapterPosition(),0);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClickListener.onItemClick(null,holder.btnEdit,holder.getAdapterPosition(),0);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.onItemClick(null,holder.btnDelete,holder.getAdapterPosition(),0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvCategoryStatus;


        Button btnEdit;
        Button btnDelete;
        public CategoryHolder(View itemView) {
            super(itemView);
            tvCategoryName=itemView.findViewById(R.id.tv_cat_name);
            tvCategoryStatus=itemView.findViewById(R.id.tv_cat_status);
            btnEdit=itemView.findViewById(R.id.btn_edit);
            btnDelete=itemView.findViewById(R.id.btn_delete);
        }
    }
}