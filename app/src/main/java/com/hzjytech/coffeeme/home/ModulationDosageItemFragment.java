package com.hzjytech.coffeeme.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hzjytech.coffeeme.R;

public class ModulationDosageItemFragment extends Fragment {
    private static final String IMGRESID = "imgResid";

    private int imgResid;


    public ModulationDosageItemFragment() {
        // Required empty public constructor
    }

    public static ModulationDosageItemFragment newInstance(int imgResid) {
        ModulationDosageItemFragment fragment = new ModulationDosageItemFragment();
        Bundle args = new Bundle();
        args.putInt(IMGRESID, imgResid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgResid = getArguments().getInt(IMGRESID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView imageView=new ImageView(getContext());
        imageView.setPadding(0,16,0,40);
        if(imgResid<1){
            imageView.setImageResource(R.drawable.img_cup);
        }else{
            imageView.setImageResource(imgResid);
        }
        return imageView;
    }

}
