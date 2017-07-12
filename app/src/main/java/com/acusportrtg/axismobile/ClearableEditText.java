package com.acusportrtg.axismobile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.acusportrtg.axismobile.R;

public class ClearableEditText extends RelativeLayout
{
    LayoutInflater inflater = null;
    EditText edit_text;
    Button btn_clear;

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initViews();
    }
    public ClearableEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initViews();
    }
    public ClearableEditText(Context context)
    {
        super(context);

        initViews();
    }
    void initViews()
    {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_clearable_edit_text, this, true);
        edit_text = (EditText) findViewById(R.id.clearable_edit);
        btn_clear = (Button) findViewById(R.id.clearable_button_clear);
        btn_clear.setVisibility(RelativeLayout.INVISIBLE);
        clearText();
        showHideClearButton();
    }
    void clearText()
    {
        btn_clear.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edit_text.setText("");
            }
        });
    }
    void showHideClearButton()
    {
        edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideClearButton();
                    edit_text.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindowToken(), 0);
                }
                else{
                    if (edit_text.getText().length() > 0) {
                        btn_clear.setVisibility(RelativeLayout.VISIBLE);
                        edit_text.setTextColor(Color.parseColor("#2980b9"));
                        edit_text.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        });

        edit_text.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0) {
                    btn_clear.setVisibility(RelativeLayout.VISIBLE);
                    edit_text.setTextColor(Color.parseColor("#2980b9"));
                    edit_text.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                }
                else
                    btn_clear.setVisibility(RelativeLayout.INVISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                if(edit_text.getText().toString().trim().length() == 0){
                    edit_text.setTextColor(Color.parseColor("#95a5a6"));
                    edit_text.getBackground().setColorFilter(Color.parseColor("#95a5a6"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
    }

    public void hideClearButton(){
        btn_clear.setVisibility(RelativeLayout.INVISIBLE);
    }

    public void positiveFeedback(){
        edit_text.getBackground().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_ATOP);
        edit_text.setTextColor(Color.parseColor("#27ae60"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_text.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                edit_text.setTextColor(Color.parseColor("#2980b9"));
                edit_text.setText("");
            }
        }, 1000);
    }

    public void negativeFeedback(){
        edit_text.getBackground().setColorFilter(Color.parseColor("#c0392b"), PorterDuff.Mode.SRC_ATOP);
        edit_text.setTextColor(Color.parseColor("#c0392b"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_text.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                edit_text.setTextColor(Color.parseColor("#2980b9"));
            }
        }, 1000);
    }

    public Editable getText()
    {
        Editable text = edit_text.getText();
        return text;
    }

    public void SetInputTypeDecimal(){
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void SetInputTypeText(){
        edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setText(String s){
        edit_text.setText(s);
    }

    public void SetHint(String s){
        edit_text.setHint(s);
    }

}