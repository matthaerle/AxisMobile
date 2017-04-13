package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.R;

import java.util.ArrayList;

/**
 * Created by Matt on 4/11/2017.
 */

public class EmpSpinAdapter extends ArrayAdapter<GetEmployees> {
    private Context context;
    private ArrayList<GetEmployees> employees;

    public EmpSpinAdapter( Context context, int textViewResourceId, ArrayList<GetEmployees> employees) {
        super(context, textViewResourceId, employees);
        this.context = context;
        this.employees = employees;
    }

    public int getCount() {
        return employees.size();
    }

    public GetEmployees getEmp(int position) {
        return employees.get(position);
    }
    public long getEmpId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.singleSpinnerText,parent, false);
        }
        TextView userName = (TextView) convertView.findViewById(R.id.txtSpinner);

        userName.setTextColor(Color.BLACK);
        userName.setText(employees.get(position).getEmployeeNumber());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View currentView, ViewGroup parent) {
        if(currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.singleSpinnerText,parent, false);
        }
        TextView userName = (TextView) currentView.findViewById(R.id.txtSpinner);

        userName.setTextColor(Color.BLACK);
        userName.setText(employees.get(position).getEmployeeNumber());
        return currentView;
    }
}
