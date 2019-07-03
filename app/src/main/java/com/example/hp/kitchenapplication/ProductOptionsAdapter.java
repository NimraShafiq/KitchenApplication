package com.example.hp.kitchenapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProductOptionsAdapter extends RecyclerView.Adapter<ProductOptionsAdapter.ProductOptionsHolder> {
    ProductCustomize dataset;
    AdapterView.OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;
    Context context;

    public ProductOptionsAdapter(Context context, ProductCustomize dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductOptionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.product_options_item_layout, parent, false);
        return new ProductOptionsHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull final ProductOptionsHolder holder, final int position) {

        holder.tvOptionName.setText(dataset.productOptionsArrayList.get(position).option_name);
        holder.tvOptionPrice.setText("+Rs. " + dataset.productOptionsArrayList.get(position).option_price);
    }

    @Override
    public int getItemCount() {
        return dataset.productOptionsArrayList.size();
    }

    public class ProductOptionsHolder extends RecyclerView.ViewHolder {
        TextView tvOptionName;
        TextView tvOptionPrice;
        RelativeLayout rlProductOption;

        public ProductOptionsHolder(View itemView) {
            super(itemView);
            tvOptionName = itemView.findViewById(R.id.tv_customize_option_name);
            tvOptionPrice = itemView.findViewById(R.id.tv_customize_option_price);
            rlProductOption = itemView.findViewById(R.id.rl_ProductOption);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            };
            itemView.setOnClickListener(onClickListener);
            tvOptionName.setOnClickListener(onClickListener);
            rlProductOption.setOnClickListener(onClickListener);
        }
    }

}
