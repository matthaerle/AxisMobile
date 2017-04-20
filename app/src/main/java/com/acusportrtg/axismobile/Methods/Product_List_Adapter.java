package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.R;

import java.util.ArrayList;

/**
 * Created by mhaerle on 4/19/2017.
 */

public class Product_List_Adapter extends ArrayAdapter<SendProductView> {
    private Context context;
    private ArrayList<SendProductView> products;

    public Product_List_Adapter(Context context, ArrayList<SendProductView> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendProductView prod = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_view, parent, false);
        }
        TextView tv_product_upc = (TextView)convertView.findViewById(R.id.txt_upc_data);
        TextView tv_product_price = (TextView)convertView.findViewById(R.id.txt_prod_price);
        TextView tv_product_qoh = (TextView)convertView.findViewById(R.id.txt_qoh);
        TextView tv_product_min = (TextView)convertView.findViewById(R.id.txt_min);
        TextView tv_product_max = (TextView)convertView.findViewById(R.id.txt_max);
        TextView tv_product_desc = (TextView)convertView.findViewById(R.id.txt_prod_desc);

        tv_product_upc.setText(prod.getProductUPC());
        tv_product_qoh.setText(String.valueOf(prod.getPhysicalQoH()));
        tv_product_price.setText(prod.getPrice().toString());
        tv_product_min.setText(String.valueOf(prod.getMinLevel()));
        tv_product_max.setText(String.valueOf(prod.getMaxLevel()));
        tv_product_desc.setText(prod.getShortDescription());

        return convertView;
    }
    public SendProductView getProduct(int position) {
        return products.get(position);
    }
}
