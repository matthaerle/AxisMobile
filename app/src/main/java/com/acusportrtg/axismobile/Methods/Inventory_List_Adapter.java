package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;
import com.acusportrtg.axismobile.R;

import java.util.ArrayList;

/**
 * Created by mhaerle on 4/14/2017.
 */

public class Inventory_List_Adapter extends ArrayAdapter<SendInventoryGroup> {
    private Context context;
    private ArrayList<SendInventoryGroup> groups;

    public Inventory_List_Adapter(Context context, ArrayList<SendInventoryGroup> groups) {
        super(context, 0, groups);
        this.context = context;
        this.groups = groups;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendInventoryGroup inv = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_inventory_group, parent, false);
        }
        TextView tv_Group_Name = (TextView)convertView.findViewById(R.id.txt_Group_Name);

        tv_Group_Name.setText(inv.getGroupName());

        return convertView;
    }
    public SendInventoryGroup getGroup(int position) {
        return groups.get(position);
    }
}
