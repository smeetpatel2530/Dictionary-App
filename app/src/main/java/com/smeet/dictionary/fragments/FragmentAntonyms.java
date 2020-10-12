package com.smeet.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smeet.dictionary.R;
import com.smeet.dictionary.WordMeaningActivity;

public class FragmentAntonyms extends Fragment {
    public FragmentAntonyms(){
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition,container,false);



        Context context  = getActivity();
        TextView text = (TextView)view.findViewById(R.id.textViewD);

        String antonyms = ((WordMeaningActivity)context).antonyms;

        if(antonyms!=null){
            antonyms = antonyms.replaceAll(",",",\n");
            text.setText(antonyms);
        }


        if(antonyms==null){
            text.setText("No Antonyms found");
        }
        return view;
    }
}
