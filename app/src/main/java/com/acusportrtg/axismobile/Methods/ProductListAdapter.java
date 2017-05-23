package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.acusportrtg.axismobile.R.id.chk_active;

/**
 * Created by mhaerle on 4/19/2017.
 */

public class ProductListAdapter extends ArrayAdapter<SendProductView> {
    private Context context;
    private ArrayList<SendProductView> products;
    private Boolean multiProduct;

    public ProductListAdapter(Context context, ArrayList<SendProductView> products, Boolean multiProduct) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.multiProduct = multiProduct;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendProductView prod = getItem(position);
        if(convertView == null) {
            if (multiProduct)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_search_multi_product_cell, parent, false);
            else
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

        if(!multiProduct){
            CheckBox chk_is_active = (CheckBox)convertView.findViewById(R.id.chk_active);
            CheckBox chk_is_firearm = (CheckBox)convertView.findViewById(R.id.chk_is_firearm);
            CheckBox chk_is_serialized = (CheckBox)convertView.findViewById(R.id.chk_is_serialized);
            CheckBox chk_is_stock = (CheckBox)convertView.findViewById(R.id.chk_is_stock);

            chk_is_active.setChecked(prod.getIsActive());
            chk_is_firearm.setChecked(prod.getIsFirearm());
            chk_is_serialized.setChecked(prod.getIsSerialized());
            chk_is_stock.setChecked(prod.getIsStockItem());
        }


        tv_product_upc.setText(prod.getProductUPC());
        tv_product_qoh.setText(String.valueOf(prod.getPhysicalQoH()));
        if(prod.getPhysicalQoH() < 0){
            tv_product_qoh.setTextColor(Color.parseColor("#c0392b"));
        }
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
