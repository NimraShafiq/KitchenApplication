package com.example.hp.kitchenapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    ArrayList<Order> dataset;
    AdapterView.OnItemClickListener onItemClickListener;

    public OrderAdapter(ArrayList<Order> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.order_item_layout, parent, false);
        return new OrderHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHolder holder, int position) {
        holder.tvUserName.setText(dataset.get(position).user_name);

        holder.tvDeliveryAddress.setText(dataset.get(position).delivery_address);
        holder.tvContactNumber.setText(dataset.get(position).contact_number);
        String dateAsText = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss")
                .format(new Date(dataset.get(position).order_time * 1000L));
        holder.tvOrderTime.setText(dateAsText);
        holder.tvOrderStatus.setText(String.valueOf(dataset.get(position).order_status));
        int amount = 0;
        int sum=0;
        for(int j=0;j<dataset.get(position).productArrayList.size();j++) {
            amount = dataset.get(position).productArrayList.get(j).p_price;
            for (int i = 0; i < dataset.get(position).productArrayList.get(j).productOptionsArrayList.size(); i++) {
                amount = amount + dataset.get(position).productArrayList.get(j).productOptionsArrayList.get(i).option_price;
            }
            sum  = sum + (amount * dataset.get(position).productArrayList.get(j).qty);
        }
        holder.tvAmount.setText("Rs ." + sum);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvAmount;
        TextView tvDeliveryAddress;
        TextView tvContactNumber;
        TextView tvOrderTime;
        TextView tvOrderStatus;

        public OrderHolder(View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDeliveryAddress = itemView.findViewById(R.id.tv_delivery_address);
            tvContactNumber = itemView.findViewById(R.id.tv_contact_number);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}
