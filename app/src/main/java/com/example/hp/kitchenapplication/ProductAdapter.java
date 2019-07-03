package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    ArrayList<Product> dataset;
    AdapterView.OnItemClickListener onItemClickListener;
    AdapterView.OnItemClickListener editClickListener;
    AdapterView.OnItemClickListener deleteClickListener;

    public ProductAdapter(ArrayList<Product> dataset,  AdapterView.OnItemClickListener onItemClickListener,AdapterView.OnItemClickListener editClickListener,AdapterView.OnItemClickListener deleteClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
        this.editClickListener=editClickListener;
        this.deleteClickListener=deleteClickListener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.product_item_layout,parent,false);
        return new ProductHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHolder holder, int position) {
       /* if(!dataset.get(position).p_image.isEmpty()) {
            Picasso.with(holder.itemView.getContext())
                    .load(dataset.get(position).p_image)
                    .into(holder.ivProductImage);
        }*/
        holder.tvProductName.setText(dataset.get(position).p_name);
        holder.tvProductPrice.setText("Rs. "+dataset.get(position).p_price);
        holder.tvProductDesc.setText(dataset.get(position).p_desc);
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

    public class ProductHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductDesc;
        Button btnEdit;
        Button btnDelete;
        public ProductHolder(View itemView) {
            super(itemView);
            tvProductName=itemView.findViewById(R.id.tv_product_name);
            tvProductPrice=itemView.findViewById(R.id.tv_product_price);
            tvProductDesc=itemView.findViewById(R.id.tv_product_desc);
            btnEdit=itemView.findViewById(R.id.btn_edit);
            btnDelete=itemView.findViewById(R.id.btn_delete);


        }
    }
}
