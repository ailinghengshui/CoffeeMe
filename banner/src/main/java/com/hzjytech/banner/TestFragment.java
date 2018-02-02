package com.hzjytech.banner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    private static final  String KEY_CONTENT="TestFragment:Content";
    private String mContent="";

    public static TestFragment newInstance(String content){
        TestFragment fragment=new TestFragment();
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<20;i++){
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length()-1);
        fragment.mContent=builder.toString();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((savedInstanceState!=null)&&savedInstanceState.containsKey(KEY_CONTENT)){
            mContent=savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT,mContent);
    }

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TextView textView=new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setText(mContent);
        textView.setTextSize(20 * getResources().getDisplayMetrics().density);
        textView.setPadding(20, 20, 20, 20);

        LinearLayout layout=new LinearLayout(getActivity());
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(textView);

        return layout;
    }

}
