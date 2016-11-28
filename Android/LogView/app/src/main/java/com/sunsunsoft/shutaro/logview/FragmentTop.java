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
public class FragmentTop extends Fragment implements OnClickListener{
    public static final String TAG = "FragmentTop";

    private static final int LOG_MAX = 100;

    private static final int[] buttonIds = {
            R.id.button,
            R.id.button2,
            R.id.button3,
            R.id.button4
    };

    //private LogBufferList logBuf = new LogBufferList(LOG_MAX);
    private LogView logView;

    private boolean logTypeSwitch = false;

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

        // Set listener
        for (int id : buttonIds) {
            view.findViewById(id).setOnClickListener(this);
        }

        return view;
    }

    public void onClick(View view) {
        LogBase log = null;
        switch(view.getId()) {
            case R.id.button:
                logView.getLogBuf().addPointLog(LogId.Log1, System.nanoTime())
                        .dispLog();

                break;
            case R.id.button2:
                logView.getLogBuf().addTextLog(LogId.Log1, System.nanoTime(), "button2")
                        .dispLog();
                break;
            case R.id.button3: {
                LogAreaType areaType = logTypeSwitch ? LogAreaType.End : LogAreaType.Start;
                logView.getLogBuf().addAreaLog(LogId.Log1, areaType, System.nanoTime())
                        .dispLog();
            }
                break;
            case R.id.button4:
                logView.getLogBuf().showAllLog();
                break;
        }
    }
}
