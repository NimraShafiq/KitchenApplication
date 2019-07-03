package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderHolder> {
    Order dataset;
    AdapterView.OnItemClickListener onItemClickListener;
    ProductOptionsOrderAdapter productOptionsOrderAdapter;
    LinearLayoutManager manager;

    public OrderProductAdapter(Order dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.order_product_item_layout, parent, false);
        return new OrderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHolder holder, int position) {
        holder.tvProductName.setText(dataset.productArrayList.get(position).p_name);
        holder.tvProductPrice.setText("Per unit price: "+dataset.productArrayList.get(position).p_price);
        holder.tvProductQuantity.setText(String.valueOf(dataset.productArrayList.get(position).qty));
        manager = new LinearLayoutManager(holder.itemView.getContext());
        holder.rvProductOptions.setLayoutManager(manager);

        productOptionsOrderAdapter = new ProductOptionsOrderAdapter(dataset.productArrayList.get(holder.getAdapterPosition()), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int innerPosition, long id) {
                onItemClickListener.onItemClick(parent, view, holder.getAdapterPosition(), id);
            }
        });
        holder.rvProductOptions.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        holder.rvProductOptions.setAdapter(productOptionsOrderAdapter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.productArrayList.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductQuantity;
        RecyclerView rvProductOptions;

        public OrderHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
           rvProductOptions=itemView.findViewById(R.id.rv_product_options);
        }
    }
}
