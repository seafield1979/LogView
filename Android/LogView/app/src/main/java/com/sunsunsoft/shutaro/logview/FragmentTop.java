package com.sunsunsoft.shutaro.logview;


import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTop extends Fragment {
    public static final String TAG = "FragmentTop";

    private TopView topView;


    public FragmentTop() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);

        // Viewを追加
        topView = new TopView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(topView);

        return view;
    }


}
