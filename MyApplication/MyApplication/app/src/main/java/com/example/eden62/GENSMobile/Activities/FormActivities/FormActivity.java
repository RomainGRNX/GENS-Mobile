package com.example.eden62.GENSMobile.Activities.FormActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eden62.GENSMobile.Activities.MapsActivities.ShowInvRelActivity;
import com.example.eden62.GENSMobile.Database.CampagneDatabase.CampagneDAO;
import com.example.eden62.GENSMobile.Database.CampagneDatabase.Inventaire;
import com.example.eden62.GENSMobile.Database.LoadingDatabase.TaxUsrDAO;
import com.example.eden62.GENSMobile.R;
import com.example.eden62.GENSMobile.Tools.LoadingMapDialog;
import com.example.eden62.GENSMobile.Tools.Utils;

/**
 * Activité Formulaire qui sert de base aux 4 formulaires différents
 */
public abstract class FormActivity extends AppCompatActivity {

    protected int typeTaxon;

    protected TextView nomfr, nomlatin, date;
    protected String nomFrString, nomLatinString;
    protected EditText nombre,remarques;
    protected Button valider,modif,visualise, decNombreButton, incNombreButton;
    protected ImageButton delete;
    protected RelativeLayout buttonsModifLayout;

    protected CampagneDAO campagneDao;
    protected TaxUsrDAO taxDao;

    protected long usrId,ref_taxon;
    protected double lat,lon;
    protected String dat, heure, remarquesTxt;
    protected int nb, nv_taxon;
    protected Inventaire consultedInv;
    protected Intent callerIntent;

    public static LoadingMapDialog lmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView();

        openDatabase();

        initFields();

        callerIntent = getIntent();
        consultedInv = callerIntent.getParcelableExtra("selectedInv");

        if(isConsultationForm()) {
            setFormNotModifiable();
            buttonsModifLayout.setVisibility(View.VISIBLE);
            valider.setVisibility(View.GONE);
            setFieldsFromConsultedInv();
        } else
            setAutoGeneratedFields();
    }

    /**
     * Vérifie si on est dans un formulaire de consultation ou de création d'inventaire
     *
     * @return <code>True</code> si on consulte un inventaire, <code>false</code> sinon
     */
    protected boolean isConsultationForm(){
        return consultedInv != null;
    }

    /**
     * Vérifie si le champ dénombrement est vide
     */
    protected boolean isEmptyDenombrement(){
        return nombre.getText().toString().isEmpty();
    }

    /**
     * Rend ce formulaire non modifiable
     */
    protected void setFormNotModifiable(){
        changeFieldsStates(false);
    }

    /**
     * Rend ce formulaire modifiable
     */
    protected void setFormModifiable(){
        changeFieldsStates(true);
    }

    /**
     * Change l'état de ce formulaire en fonction du paramêtre enabled
     * @param enabled Si vaut <code>true</code>, le formulaire est modifiable, si vaut <code>false</code>, le formulaire n'est
     *                pas modifiable
     */
    protected void changeFieldsStates(boolean enabled){
        nombre.setEnabled(enabled);
        remarques.setEnabled(enabled);

        incNombreButton.setEnabled(enabled);
        decNombreButton.setEnabled(enabled);
    }

    /**
     * Permet d'enlever le focus actuel de l'activité
     */
    protected void clearFocus(){
        getCurrentFocus().clearFocus();
    }

    /**
     * Récupère les informations de l'inventaire consulté via l'historique et les ajoute à ce formulaire
     */
    protected void setFieldsFromConsultedInv(){
        nomfr.setText(consultedInv.getNomFr());
        nomlatin.setText(consultedInv.getNomLatin());
        date.setText(Utils.printDateWithYearIn2Digit(consultedInv.getDate()));
        nb = consultedInv.getNombre();
        if(nb > 0)
            nombre.setText(nb + "");
        remarques.setText(consultedInv.getRemarques());
    }

    /**
     * Défini un layout personalisé pour chaque activité
     */
    protected abstract void setView();

    /**
     * Initialise les bases de données et les ouvres pour permettre la modification
     */
    protected void openDatabase(){
        campagneDao = new CampagneDAO(this);
        taxDao = new TaxUsrDAO(this);

        campagneDao.open();
        taxDao.open();
    }

    /**
     * Ferme les deux bases de données
     */
    protected void closeDatabase(){
        campagneDao.close();
        taxDao.close();
    }

    /**
     * Créé un objet {@link Inventaire} en utilisant les données fournies par l'utilisateur en utilisant la méthode
     * spécifique de chaque formulaire
     *
     * @return L'objet {@link Inventaire} prêt à être inséré dans la base
     * @see FormActivity#createPersonalInventaire()
     */
    protected Inventaire createInventaireFromFields(){
        setStockedValuesFromUsrInput();
        return createPersonalInventaire();
    }

    /**
     * Créé un objet {@link Inventaire} spécifique au formulaire
     *
     * @return L'objet {@link Inventaire} prêt à être inséré dans la base
     * @see FormActivity#createInventaireFromFields()
     */
    protected abstract Inventaire createPersonalInventaire();

    /**
     * Récupère l'id de l'utilisateur courant grâce aux {@link SharedPreferences}
     */
    protected void setUsrId(){
        usrId = Utils.getCurrUsrId(this);
    }

    /**
     * Récupère les données fournies par l'utilisateur et affecte les attributs de la classe appellante en conséquence
     */
    protected void setStockedValuesFromUsrInput(){
        if(!isConsultationForm()) {
            setUsrId();
            ref_taxon = taxDao.getRefTaxon(new String[]{nomLatinString, nomFrString});
        }
        nb = getDenombrement();
        remarquesTxt = remarques.getText().toString();
    }



    /**
     * Initialise les views de ce formulaire
     */
    protected void initFields(){
        lmd = new LoadingMapDialog(this);
        nomfr = (TextView) findViewById(R.id.nomFr);
        nomlatin = (TextView) findViewById(R.id.nomLatin);
        date = (TextView) findViewById(R.id.date);
        nombre = (EditText) findViewById(R.id.nombre);
        remarques = (EditText) findViewById(R.id.remarques);
        buttonsModifLayout = (RelativeLayout) findViewById(R.id.buttonsLayout);
        decNombreButton = (Button) findViewById(R.id.decDenombrement);
        incNombreButton = (Button) findViewById(R.id.incDenombrement);
        modif = (Button) findViewById(R.id.modifier);
        visualise = (Button) findViewById(R.id.visualiserInv);
        delete = (ImageButton) findViewById(R.id.deleteInv);
        valider = (Button) findViewById(R.id.validerForm);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formIsValidable()){
                    if(isConsultationForm()) {
                        modifConsultedInventaire();
                        campagneDao.modifInventaire(consultedInv);
                    }
                    else {
                        Inventaire inv = createInventaireFromFields();
                        campagneDao.insertInventaire(inv);
                    }
                    finish();
                } else{
                    actionWhenFormNotValidable();
                }
            }
        });

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFormModifiable();
                buttonsModifLayout.setVisibility(View.GONE);
                valider.setVisibility(View.VISIBLE);
            }
        });

        visualise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lmd.show(true);
                Intent intent = new Intent(FormActivity.this, ShowInvRelActivity.class);
                intent.putExtra("inv",consultedInv);
                startActivity(intent);
            }
        });

        decNombreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decNombre();
            }
        });

        incNombreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incNombre();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                campagneDao.deleteInventaire(consultedInv.get_id());
                finish();
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
            }
        });

        nombre.addTextChangedListener(new UnsetError());
        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nb = getDenombrement();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        remarques.setOnFocusChangeListener(new MyFocusChangeListener());
    }

    /**
     * Modifie l'inventaire en utilisant les nouvelles données saisient par l'utilisateur
     */
    protected void modifConsultedInventaire() {
        setStockedValuesFromUsrInput();
        consultedInv.setNombre(nb);
        consultedInv.setRemarques(remarquesTxt);
    }

    /**
     * Action réalisée lorque le formulaire soumis par l'utilisateur ne respecte pas les conditions voulues
     */
    protected void actionWhenFormNotValidable(){
        if(!checkNombreValid())
            nombre.setError(getString(R.string.error_edit_nombre));
    }

    /**
     * Prédicat qui permet de dire si le formulaire soumis par l'utilisateur est valable
     *
     * @return <code>True</code> si le formulaire soumis par l'utilisateur est valable, <code>false</code> sinon
     */
    protected boolean formIsValidable(){
        return checkNombreValid();
    }

    /**
     * Prédicat qui vérifie si le dénombrement soumis par l'utilisateur est valide. En effet, il ne peut pas mettre 0 car
     * cela signifie qu'il y a une présence de cette espèce dans le code.
     *
     * @return <code>True</code> si le dénombrement est valide, <code>false</code> sinon
     */
    protected boolean checkNombreValid(){
        return isEmptyDenombrement() || (getDenombrement() > 0);
    }

    /**
     * Affecte les champs qui sont fournis par le système
     */
    protected void setAutoGeneratedFields(){
        lat = callerIntent.getDoubleExtra("latitude",0.0);
        lon = callerIntent.getDoubleExtra("longitude",0.0);
        dat = Utils.getDate();
        heure = callerIntent.getStringExtra("heure");
        nomFrString = callerIntent.getStringExtra("nomfr");
        nomLatinString = callerIntent.getStringExtra("nomlatin");
        nv_taxon = taxDao.getNiveau(new String[]{nomLatinString,nomFrString});

        nomfr.setText(nomFrString);
        nomlatin.setText(nomLatinString);
        date.setText(dat);
    }

    /**
     * Ferme les bases de données
     * @see AppCompatActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();
    }

    /**
     * Retourne le dénombrement fourni par l'utilisateur si le champ n'est pas vide, sinon 0
     *
     * @return Dénombrement inséré par l'utilisateur
     */
    protected int getDenombrement() {
        int nb = 0;
        try{
            nb = Integer.parseInt(nombre.getText().toString());
        }catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        return nb;
    }

    /**
     * Décrémente la valeure contenue dans un EditText, utile pour les différents dénombrements
     *
     * @param et L'editText à changer
     * @param nb La valeur à modifier
     * @return La valeur décrémenté de 1
     */
    protected int decreaseDecompteEditText(EditText et, int nb){
        if(nb > 0) {
            String newText = "";
            nb--;
            if(nb > 0)
                newText += nb;
            et.setText(newText);
        }
        return nb;
    }

    /**
     * Verifie que la variable de valeur i à été initialisée
     *
     * @param i La valeur de la variable à vérifié
     * @return <code>True</code> si la variable n'a pas été initialisée, <code>false</code> si oui
     */
    public boolean isNull(int i){
        return i == 0;
    }

    /**
     * Incrémente le dénombrement total de 1 si le dénombrement à déjà été renseigné par l'utilisateur et effectue une
     * modification supplémentaire via {@link #specialFormModif(int)}
     */
    protected void incNombre(){
        if(isNull(nb))
            nb = getDenombrement();

        int nbBeforeAdjust = nb;

        nb = specialFormModif(nb);
        boolean wasCoherentNombreBeforeAdjust = nbBeforeAdjust == nb;

        // On incrémente le dénombrement seulement si celui-ci n'a pas été modifié pour être égale au total de genre
        if(wasCoherentNombreBeforeAdjust)
            nb++;
        nombre.setText(nb + "");
    }

    /**
     * Effectue une modification spéciale du dénombrement selon le type de formulaire
     *
     * @param nb La valeur sur laquelle effectuer la modification
     * @return La valeur modifiée
     */
    protected int specialFormModif(int nb){
        return nb;
    }

    /**
     * Décrémente le dénombrement total de 1
     */
    protected void decNombre(){
        nb = decreaseDecompteEditText(nombre,nb);
    }

    /**
     * Permet d'enlever l'erreur du champ dénombrement si celui ci est modifié
     */
    public class UnsetError implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        /**
         * Enlève l'erreur sur le champ dénombrement
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { nombre.setError(null); }

        @Override
        public void afterTextChanged(Editable editable) { }
    }

    /**
     * Action réalisé lorsque les EditText perdent le focus. Cela permet de définir quand l'utilisateur a fini son input
     * et de cacher le clavier en conséquence
     */
    public class MyFocusChangeListener implements View.OnFocusChangeListener{

        /**
         * Ferme le clavier lorsque le focus est perdu
         */
        @Override
        public void onFocusChange(View view, boolean b) {
            if(!b)
                Utils.hideKeyboard(getApplicationContext(),view);
        }
    }
}
