package com.example.eden62.GENSMobile.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eden62.GENSMobile.Activities.Historiques.Inventaires.HistoryRecensementActivity;
import com.example.eden62.GENSMobile.Activities.Historiques.Releves.HistoryReleveActivity;
import com.example.eden62.GENSMobile.Activities.Historiques.Saisies.HistorySaisiesActivity;
import com.example.eden62.GENSMobile.Database.CampagneDatabase.CampagneDAO;
import com.example.eden62.GENSMobile.Database.CampagneDatabase.Inventaire;
import com.example.eden62.GENSMobile.Database.ReleveDatabase.HistoryDao;
import com.example.eden62.GENSMobile.Database.SaisiesProtocoleDatabase.CampagneProtocolaire;
import com.example.eden62.GENSMobile.Database.SaisiesProtocoleDatabase.CampagneProtocolaireDao;
import com.example.eden62.GENSMobile.R;
import com.example.eden62.GENSMobile.Tools.MyHttpService;
import com.example.eden62.GENSMobile.Tools.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * Classe qui synchronise les relevés avec le serveur
 */
public class HttpActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout psswLayout,nbInvLayout, nbRelLayout, nbProtosLayout;
    private Button launchSyncInvs, launchSyncProtos, validPssw;
    private TextView txtJson, nbInvToSyncTxt, nbRelToSyncTxt, nbProtosToSyncTxt;
    private EditText psswText;
    private Snackbar snackbar;
    private CheckBox displayMdp;

    int nbInvToSync, nbRelToSync, nbProtosToSync;

    private String username,mdp;
    private long usrId;
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor editor;

    private MyHttpService httpService;

    private CampagneDAO campagneDao;
    private HistoryDao releveDao;
    private CampagneProtocolaireDao campagneProtocolaireDao;

    public static int END_OF_SYNC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        httpService = new MyHttpService(this);
        campagneDao = new CampagneDAO(this);
        releveDao = new HistoryDao(this);
        campagneProtocolaireDao = new CampagneProtocolaireDao(this);
        campagneDao.open();
        releveDao.open();
        campagneProtocolaireDao.open();
        prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = prefs.edit();

        makeView();

        if(prefs.getString("mdp","") != "")
            setViewAfterGoodConnection();

        username = prefs.getString("username","");
        usrId = Utils.getCurrUsrId(HttpActivity.this);
        setTxtNbDatas();

        snackbar = Snackbar.make(txtJson, "Requête en cours d'exécution",
                Snackbar.LENGTH_INDEFINITE);
    }

    /**
     * Initialise les élémentes de la view
     */
    private void makeView() {
        launchSyncInvs = (Button) findViewById(R.id.syncInvs);
        launchSyncProtos = (Button) findViewById(R.id.syncProtos);
        txtJson = (TextView) findViewById(R.id.tvJsonItem);
        validPssw = (Button) findViewById(R.id.validPssw);
        psswText = (EditText) findViewById(R.id.password);
        psswLayout = (LinearLayout) findViewById(R.id.passwordLayout);
        nbInvLayout = (LinearLayout) findViewById(R.id.invToSyncLayout);
        nbRelLayout = (LinearLayout) findViewById(R.id.relToSyncLayout);
        nbProtosLayout = (LinearLayout) findViewById(R.id.protoToSyncLayout);
        psswText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                psswText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        validPssw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isConnected(HttpActivity.this)) {
                    psswText.setError(getString(R.string.noConnection));
                    psswText.requestFocus();
                    return;
                }
                mdp = psswText.getText().toString();
                HttpActivityLoginTask task = new HttpActivityLoginTask(httpService.createConnectionRequest(username,mdp),httpService);
                task.execute((Void)null);
            }
        });
        launchSyncInvs.setOnClickListener(this);
        nbInvToSyncTxt = (TextView) findViewById(R.id.nbInvToSync);
        nbRelToSyncTxt = (TextView) findViewById(R.id.nbRelToSync);
        nbProtosToSyncTxt = (TextView) findViewById(R.id.nbProtoToSync);
        nbInvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HttpActivity.this, HistoryRecensementActivity.class);
                intent.putExtra("createCampagne",false);
                startActivity(intent);
            }
        });
        nbRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HttpActivity.this, HistoryReleveActivity.class);
                startActivity(intent);
            }
        });
        nbProtosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HttpActivity.this, HistorySaisiesActivity.class));
            }
        });
        displayMdp = (CheckBox) findViewById(R.id.displayMdp);
        displayMdp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    psswText.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    psswText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        launchSyncProtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isConnected(HttpActivity.this)) {
                    Snackbar.make(v, getString(R.string.noConnection), Snackbar.LENGTH_LONG).show();
                    return;
                }

                final ArrayList<CampagneProtocolaire> campagneProtoToSend = getCampagneProtoToSend();
                int currTotalCamp = campagneProtoToSend.size();
                boolean noCampToSend = currTotalCamp == 0;

                if(noCampToSend) {
                    Snackbar.make(txtJson, "Aucun inventaire à synchroniser", Snackbar.LENGTH_LONG).show();
                    return;
                }

                snackbar.show();
                Intent intent = new Intent(HttpActivity.this,SyncInvActivity.class);
                intent.putParcelableArrayListExtra("campagnesProtoToSend",campagneProtoToSend);
                intent.putExtra("mdp",mdp);
                startActivityForResult(intent,END_OF_SYNC);
            }
        });
    }

    private ArrayList<CampagneProtocolaire> getCampagneProtoToSend(){
        return campagneProtocolaireDao.getCampagneOfTheUsr(usrId);
    }

    /**
     * Récupère le nombre d'inventaires/relevés à synchroniser
     */
    private void getNbDataToSync(){
        nbInvToSync = getInventairesToSend().size();
        nbRelToSync = releveDao.getNbReleveOfTheUsr(usrId);
        nbProtosToSync = campagneProtocolaireDao.getNbCampagneOfTheUsr(usrId);
    }

    /**
     * Affecte le bon texte aux champs d'informations du nombre d'inventaires/relevés
     */
    protected void setTxtNbDatas(){
        getNbDataToSync();
        nbInvToSyncTxt.setText(nbInvToSync + " " + getString(R.string.invToSync));
        nbProtosToSyncTxt.setText(nbProtosToSync + " " + getString(R.string.protosToSync));
        nbRelToSyncTxt.setText(nbRelToSync + " " + getString(R.string.relToSync));
    }

    // Récupère les inventaires de l'utilisateur à envoyer au serveur
    private ArrayList<Inventaire> getInventairesToSend(){
        List<Inventaire> tmp =  campagneDao.getInventaireOfTheUsr(usrId);
        ArrayList<Inventaire> res = new ArrayList<>();
        for (Inventaire inv : tmp){
            if(inv.isToSync())
                res.add(inv);
        }
        return res;
    }

    @Override
    public void onClick(final View view) {
        if (!Utils.isConnected(HttpActivity.this)) {
            Snackbar.make(view, getString(R.string.noConnection), Snackbar.LENGTH_LONG).show();
            return;
        }

        final ArrayList<Inventaire> inventairesToSend = getInventairesToSend();
        int currTotalInv = inventairesToSend.size();
        boolean noInvToSend = currTotalInv == 0;

        if(noInvToSend) {
            Snackbar.make(txtJson, "Aucun inventaire à synchroniser", Snackbar.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous créer vos campagnes automatiquement ?");
        builder.setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HttpActivity.this, HistoryRecensementActivity.class);
                intent.putExtra("createCampagne",true);
                intent.putExtra("mdp",mdp);
                startActivity(intent);
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                snackbar.show();
                Intent intent = new Intent(HttpActivity.this,SyncInvActivity.class);
                intent.putParcelableArrayListExtra("inventairesToSend",inventairesToSend);
                intent.putExtra("mdp",mdp);
                dialogInterface.dismiss();
                startActivityForResult(intent,END_OF_SYNC);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == END_OF_SYNC)
            setTxtNbDatas();
    }

    private class HttpActivityLoginTask extends com.example.eden62.GENSMobile.Tools.AttemptLoginTask{

        public HttpActivityLoginTask(Request requete, MyHttpService httpService) {
            super(requete, httpService);
        }

        @Override
        protected void makeWrongJsonSnackbar() {
            Snackbar.make(txtJson, "Mauvaise forme de json",Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected void updateTxtJson(final String body) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtJson.setText(body);
                    snackbar.dismiss();
                }
            });
        }

        @Override
        protected void actionOnPostExecute(Boolean success) {
            if (success) {
                Snackbar.make(txtJson,"Connexion réussie",Snackbar.LENGTH_SHORT).show();
                if(!checkVersion(version)){
                    final int finalVersion = version;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editor.putInt("goodAppVersion", finalVersion);
                            editor.commit();
                            createWrongVersionDialog().show();
                        }
                    });
                }else {
                    editor.putString("mdp",mdp);
                    editor.commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewAfterGoodConnection();
                            InputMethodManager imm = (InputMethodManager) HttpActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(psswText.getWindowToken(), 0);
                        }
                    });
                }
            } else {
                psswText.setError(getString(R.string.mdpError));
                psswText.requestFocus();
            }
        }

        private boolean checkVersion(int servVersion){
            return Utils.getVerCode(HttpActivity.this) == servVersion;
        }
    }

    // Cache le layout password et affiche les infos de synchronisations et boutons
    private void setViewAfterGoodConnection(){
        psswLayout.setVisibility(View.GONE);
        setTxtNbDatas();
        nbInvLayout.setVisibility(View.VISIBLE);
        nbRelLayout.setVisibility(View.VISIBLE);
        nbProtosLayout.setVisibility(View.VISIBLE);
        launchSyncInvs.setVisibility(View.VISIBLE);
        launchSyncProtos.setVisibility(View.VISIBLE);
    }

    // Créé un message d'avertissement pour prévenir de la version obsolète de l'application
    private Dialog createWrongVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.updateApp);
        builder.setPositiveButton(getString(R.string.accord), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(HomeActivity.RESULT_CLOSE_ALL);
                finish();
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        campagneDao.close();
        releveDao.close();
        campagneProtocolaireDao.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTxtNbDatas();
    }
}
