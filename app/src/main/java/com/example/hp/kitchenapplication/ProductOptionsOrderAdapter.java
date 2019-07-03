package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductOptionsOrderAdapter extends RecyclerView.Adapter<ProductOptionsOrderAdapter.ProductOptionsOrderHolder> {
        Product dataset;
        AdapterView.OnItemClickListener onItemClickListener;

        public ProductOptionsOrderAdapter(Product dataset, AdapterView.OnItemClickListener onItemClickListener) {
            this.dataset = dataset;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ProductOptionsOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.product_options_order_layout, parent, false);
            return new ProductOptionsOrderHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductOptionsOrderHolder holder, int position) {
            holder.tvOptionName.setText(dataset.productOptionsArrayList.get(position).option_name);
            Log.i("mytag", dataset.productOptionsArrayList.get(position).option_name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.productOptionsArrayList.size();
        }

        public class ProductOptionsOrderHolder extends RecyclerView.ViewHolder {
            TextView tvOptionName;

            public ProductOptionsOrderHolder(View itemView) {
                super(itemView);
                tvOptionName = itemView.findViewById(R.id.tv_option_name);
            }
        }
    }


