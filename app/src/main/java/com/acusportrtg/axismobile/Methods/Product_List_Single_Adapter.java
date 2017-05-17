package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by mhaerle on 4/19/2017.
 */

public class Product_List_Single_Adapter extends ArrayAdapter<SendProductView> {
    private Context context;
    private ArrayList<SendProductView> products;

    public Product_List_Single_Adapter(Context context, ArrayList<SendProductView> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendProductView prod = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_single_product_cell, parent, false);
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        String pattern = "###,###,###";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);
        TextView tv_product_upc = (TextView)convertView.findViewById(R.id.txt_upc_data);
        TextView tv_product_price = (TextView)convertView.findViewById(R.id.txt_prod_price);
        TextView tv_product_qoh = (TextView)convertView.findViewById(R.id.txt_qoh);
        TextView tv_product_min = (TextView)convertView.findViewById(R.id.txt_min);
        TextView tv_product_max = (TextView)convertView.findViewById(R.id.txt_max);
        TextView tv_product_desc = (TextView)convertView.findViewById(R.id.txt_prod_desc);
        TextView txt_qty_on_order_data = (TextView)convertView.findViewById(R.id.txt_qty_on_order_data);
        TextView txt_qty_committed_data = (TextView)convertView.findViewById(R.id.txt_qty_committed_data);
        TextView txt_department_data = (TextView)convertView.findViewById(R.id.txt_department_data);
        TextView txt_manufacture_data = (TextView)convertView.findViewById(R.id.txt_manufacture_data);

        tv_product_upc.setText(prod.getProductUPC());
        tv_product_qoh.setText(String.valueOf(prod.getPhysicalQoH()));
        tv_product_price.setText(nf.format(prod.getPrice()));
        tv_product_min.setText(df.format(prod.getMinLevel()));
        tv_product_max.setText(df.format(prod.getMaxLevel()));
        tv_product_desc.setText(prod.getShortDescription());
        txt_qty_on_order_data.setText(df.format(prod.getQtyOnOrder()));
        txt_qty_committed_data.setText(df.format(prod.getQtyCommitted()));
        txt_department_data.setText(prod.getDepartment());
        txt_manufacture_data.setText(prod.getManufacture());

        return convertView;
    }
    public SendProductView getProduct(int position) {
        return products.get(position);
    }
}
