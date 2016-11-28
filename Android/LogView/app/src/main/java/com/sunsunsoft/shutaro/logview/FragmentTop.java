package com.sunsunsoft.shutaro.logview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTop extends Fragment {
    public static final String TAG = "FragmentTop";

    private static final int LOG_MAX = 100;

    //private LogBufferList logBuf = new LogBufferList(LOG_MAX);
    private LogView logView;

    public FragmentTop() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);

        // Viewを追加
        logView = new LogView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(logView);

        return view;
    }
}
