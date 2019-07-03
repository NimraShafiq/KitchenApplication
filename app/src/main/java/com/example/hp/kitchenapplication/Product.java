package com.example.hp.kitchenapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    int cat_id;
    int p_id;
    String p_name;
    String p_desc;
    int p_price;
    String p_image;
    int p_status;
    int qty;
    ArrayList<ProductOptions> productOptionsArrayList;
}
