package com.example.android.mdc.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mdc.R;

public class slide_3 extends Fragment {
    TextView slide_title, slide_desc;
    private slide_1.OnFragmentInteractionListener mListener;
    public slide_3() {}

    public static slide_3 newInstance() {
        return new slide_3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_slide_3, container, false);
        Typeface robotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        slide_title  = (TextView) v.findViewById(R.id.slide_3_title);
        slide_desc  = (TextView) v.findViewById(R.id.slide_3_desc);
        slide_title.setTypeface(robotoLight);
        slide_desc.setTypeface(robotoLight);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof slide_1.OnFragmentInteractionListener) {
            mListener = (slide_1.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
