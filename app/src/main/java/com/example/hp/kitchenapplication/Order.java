package com.example.hp.kitchenapplication;

import com.example.hp.kitchenapplication.ProductOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
            int order_id;
            int p_id;
            String p_name;
            String p_image;
            int k_id;
            int quantity;
            int p_price;
            String cat_name;
            int user_id;
            String user_name;
            int  order_time;
            String order_status;
            String payment_method;
            String contact_number;
            String delivery_address;
            int delivery_charges;
            String city;
            ArrayList<Product> productArrayList;
            ArrayList<ProductOptions> productOptionsArrayList;
}
