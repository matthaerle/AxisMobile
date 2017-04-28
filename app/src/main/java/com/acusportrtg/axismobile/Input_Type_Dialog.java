package com.acusportrtg.axismobile;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by jpederson on 4/28/2017.
 */

public class Input_Type_Dialog extends DialogFragment {

    static Input_Type_Dialog newInstance(int num) {
        Input_Type_Dialog f = new Input_Type_Dialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_type_dialog, container, false);


        return v;
    }

}
