package com.example.florian.myapplication.Activities.MapsActivities.Releve.RelevesPopUp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.florian.myapplication.R;

public class PopUpLigne extends ReleveInfoPopup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_ligne);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_pop_up_ligne);
    }
}
