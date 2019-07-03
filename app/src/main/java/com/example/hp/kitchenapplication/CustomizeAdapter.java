package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomizeAdapter extends RecyclerView.Adapter<CustomizeAdapter.CustomizeHolder> {
    private ArrayList<ProductCustomize> dataset;
    AdapterView.OnItemClickListener onItemClickListener;
    LinearLayoutManager manager;
    ProductOptionsAdapter productOptionsAdapter;
    AdapterView.OnItemClickListener deleteClickListener;



    public CustomizeAdapter(ArrayList<ProductCustomize> dataset, AdapterView.OnItemClickListener onItemClickListener,AdapterView.OnItemClickListener deleteClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
        this.deleteClickListener=deleteClickListener;
    }

    @NonNull
    @Override
    public CustomizeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.customize_product_item_layout, parent, false);
        return new CustomizeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomizeHolder holder, int position) {
        holder.tvCustomizeName.setText(dataset.get(position).customize_name);
        //View v = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.product_options_item_layout, null);
        manager = new LinearLayoutManager(holder.itemView.getContext());
        holder.rvProductOptions.setLayoutManager(manager);
        final ProductCustomize currentCustomize = dataset.get(position);
        productOptionsAdapter = new ProductOptionsAdapter(holder.itemView.getContext(),
                dataset.get(holder.getAdapterPosition()), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int innerPosition, long id) {
                currentCustomize.selectedOption = currentCustomize.productOptionsArrayList.get(innerPosition);
                onItemClickListener.onItemClick(parent, view, innerPosition, id);
            }
        });
        holder.rvProductOptions.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        holder.rvProductOptions.setAdapter(productOptionsAdapter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0);
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

    public class CustomizeHolder extends RecyclerView.ViewHolder {
        TextView tvCustomizeName;
        RecyclerView rvProductOptions;
        Button btnDelete;


        public CustomizeHolder(View itemView) {
            super(itemView);
            tvCustomizeName = itemView.findViewById(R.id.tv_customize_name);
            rvProductOptions = itemView.findViewById(R.id.rv_product_options);
            btnDelete=itemView.findViewById(R.id.btn_delete);

        }
    }

}
