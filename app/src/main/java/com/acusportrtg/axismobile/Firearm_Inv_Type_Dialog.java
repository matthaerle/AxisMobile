package com.acusportrtg.axismobile;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;


/**
 * Created by jpederson on 4/28/2017.
 */

public class Firearm_Inv_Type_Dialog extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    private Button btn_nfa,btn_non_nfa,btn_gunsmithing;
    private String firearmType;

    /*static Firearm_Inv_Type_Dialog newInstance(int num) {
        Firearm_Inv_Type_Dialog f = new Firearm_Inv_Type_Dialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        return f;
    }*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        View v = inflater.inflate(R.layout.firearm_inv_type_dialog, container, false);
        Activity activity = getActivity();

        btn_nfa = (Button)v.findViewById(R.id.btn_nfa);
        btn_non_nfa = (Button)v.findViewById(R.id.btn_non_nfa);
        btn_gunsmithing = (Button)v.findViewById(R.id.btn_gunsmithing);

        btn_nfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firearmType = "NFA";
                onButtonPressed(firearmType);
                getActivity().getFragmentManager().beginTransaction().remove(Firearm_Inv_Type_Dialog.this).commit();
            }
        });
        btn_non_nfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firearmType = "NON-NFA";
                onButtonPressed(firearmType);
                getActivity().getFragmentManager().beginTransaction().remove(Firearm_Inv_Type_Dialog.this).commit();
            }
        });
        btn_gunsmithing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firearmType = "GUNSMITHING";
                onButtonPressed(firearmType);
                getActivity().getFragmentManager().beginTransaction().remove(Firearm_Inv_Type_Dialog.this).commit();
            }
        });


        return v;
    }
    public void onButtonPressed(String firearmType) {
        if (mListener != null) {
            mListener.onFragmentInteraction(firearmType);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String firearmType);
    }

}
