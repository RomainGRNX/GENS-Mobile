package com.example.florian.myapplication.Activities.FormActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.florian.myapplication.Database.CampagneDatabase.CampagneDAO;
import com.example.florian.myapplication.Database.CampagneDatabase.Inventaire;
import com.example.florian.myapplication.Database.LoadingDatabase.TaxUsrDAO;
import com.example.florian.myapplication.R;
import com.example.florian.myapplication.Tools.Utils;

/**
 * Activité Formulaire qui sert de base aux 4 formulaires différents
 */
public abstract class FormActivity extends AppCompatActivity {

    protected int typeTaxon;

    protected TextView nomfr, nomlatin, date;
    protected String nomFrString, nomLatinString;
    protected EditText nombre;
    protected CampagneDAO campagneDao;
    protected TaxUsrDAO taxDao;

    protected Button valider;

    protected long usrId,ref_taxon;
    protected double lat,lon;
    protected String dat;
    protected int nb;

    /**
     * Initialise les champs commun au différents formulaires
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView();

        initFields();
        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
            }
        });
        nombre.addTextChangedListener(new UnsetError());

        openDatabase();

        valider = (Button) findViewById(R.id.validerForm);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formIsValidable()){
                    Inventaire inv = createInventaireFromFields();
                    campagneDao.insertInventaire(inv);
                    FormActivity.this.finish();
                } else{
                    actionWhenFormNotValidable();
                }
            }
        });

        setAutoGeneratedFields();
    }

    /**
     * Permet d'enlever le focus actuel de l'activité
     */
    protected void clearFocus(){
        getCurrentFocus().clearFocus();
    }



    /**
     * Permet d'obtenir un layout personalisé pour chaque activité
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
     * Créé un objet {@link Inventaire} en utilisant les données fournies par l'utilisateur en utilisant la méthode spécifique de chaque formulaire
     * @return L'objet {@link Inventaire} prêt à être inséré dans la base
     * @see FormActivity#createPersonalInventaire()
     */
    protected Inventaire createInventaireFromFields(){
        setValuesFromUsrInput();
        return createPersonalInventaire();
    }

    /**
     * Créé un objet {@link Inventaire} spécifique au formulaire
     * @return L'objet {@link Inventaire} prêt à être inséré dans la base
     * @see FormActivity#createInventaireFromFields()
     */
    protected abstract Inventaire createPersonalInventaire();

    /**
     * Récupère l'id de l'utilisateur courant grâce aux {@link SharedPreferences}
     */
    protected void setUsrId(){
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        usrId = loginPreferences.getLong("usrId",0);
    }

    /**
     * Récupère les données fournies par l'utilisateur et affecte les attributs de la classe appellante en conséquence
     */
    protected void setValuesFromUsrInput(){
        setUsrId();
        ref_taxon = taxDao.getRefTaxon(new String[]{nomLatinString,nomFrString});
        try{
            nb = getDenombrement();
        } catch (NumberFormatException e) {
            nb = 0;
        }
    }

    /**
     * Récupère les différentes View présente dans le layout de base
     */
    protected void initFields(){
        nomfr = (TextView) findViewById(R.id.nomFr);
        nomlatin = (TextView) findViewById(R.id.nomLatin);
        date = (TextView) findViewById(R.id.date);
        nombre = (EditText) findViewById(R.id.nombre);
    }

    /**
     * Action réalisée lorque le formulaire soumis par l'utilisateur ne respecte pas les conditions voulues
     */
    protected void actionWhenFormNotValidable(){
        if(!checkNombreValid()){
            nombre.setError(getString(R.string.error_edit_nombre));
        }
    }

    /**
     * Prédicat qui permet de dire si le formulaire soumis par l'utilisateur est valable
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
        String nombreText = nombre.getText().toString();
        int nombre;
        try{
            nombre = getDenombrement();
            return (nombre > 0);
        } catch(Exception e){
            return nombreText.isEmpty();
        }
    }

    /**
     * Affecte les champs qui sont fournis par le système
     */
    protected void setAutoGeneratedFields(){
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("latitude",0.0);
        lon = intent.getDoubleExtra("longitude",0.0);
        dat = Utils.getDate();
        nomFrString = intent.getStringExtra("nomfr");
        nomLatinString = intent.getStringExtra("nomlatin");

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
     * Retourne le dénombrement fourni par l'utilisateur si le champ n'est pas vide, sinon une exception
     * @return Dénombrement inséré par l'utilisateur
     * @throws NumberFormatException
     */
    protected int getDenombrement() throws NumberFormatException{
        return Integer.parseInt(nombre.getText().toString());
    }

    /**
     * Permet d'enlever l'erreur du champ dénombrement si celui ci est modifié
     */
    public class UnsetError implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        /**
         * Enlève l'erreur sur le champ dénombrement
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            nombre.setError(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * Action réalisé lorsque les EditText perdent le focus. Cela permet de définir quand l'utilisateur a fini son input
     */
    public class MyFocusChangeListener implements View.OnFocusChangeListener{

        /**
         * Ferme le clavier lorsque le focus est perdu
         */
        @Override
        public void onFocusChange(View view, boolean b) {
            if(!b){
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
    }
}
