package com.example.hirono_mayuko.redashclient2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hirono-mayuko on 2017/05/24.
 */

public class TablePageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_table_page, container, false);
        return rootView;
    }

    public static Fragment newInstance(){
        TablePageFragment f = new TablePageFragment();
        return f;
    }
}