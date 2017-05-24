package com.example.hirono_mayuko.redashclient2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hirono-mayuko on 2017/05/24.
 */

public class TableLastPage extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.table_last_page, container, false);
        return rootView;
    }

    public static Fragment newInstance(){
        TableLastPage f = new TableLastPage();
        return f;
    }
}
