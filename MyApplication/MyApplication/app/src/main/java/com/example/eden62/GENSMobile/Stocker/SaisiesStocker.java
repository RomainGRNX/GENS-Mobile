package com.example.eden62.GENSMobile.Stocker;

import android.content.Context;

import com.example.eden62.GENSMobile.Database.SaisiesProtocoleDatabase.CampagneProtocolaireDao;
import com.example.eden62.GENSMobile.Database.SaisiesProtocoleDatabase.CampagneProtocolaire;

import java.util.ArrayList;

public class SaisiesStocker extends StockCheckedItems<CampagneProtocolaire,CampagneProtocolaireDao> {
    public SaisiesStocker(Context ctx) {
        super(new ArrayList<CampagneProtocolaire>(), new CampagneProtocolaireDao(ctx));
    }
}
