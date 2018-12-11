package br.com.digitalhouse.clackapp;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.digitalhouse.clackapp.database.PreferenceReaderContract;
import br.com.digitalhouse.clackapp.database.PreferenceReaderDbHelper;
import br.com.digitalhouse.clackapp.model.Preference;
import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class PreferenceActivity extends AppCompatActivity {

    private static final String TAG = "PreferenceActivity";
    private TextView textViewHelloPref;
    private CheckBox checkBoxAcao;
    private CheckBox checkBoxAnimacao;
    private CheckBox checkBoxAventura;
    private CheckBox checkBoxCinemaTv;
    private CheckBox checkBoxComedia;
    private CheckBox checkBoxCrime;
    private CheckBox checkBoxDocumentario;
    private CheckBox checkBoxDrama;
    private CheckBox checkBoxFamilia;
    private CheckBox checkBoxFantasia;
    private CheckBox checkBoxFaroeste;
    private CheckBox checkBoxFiccaoCientifica;
    private CheckBox checkBoxGuerra;
    private CheckBox checkBoxHistoria;
    private CheckBox checkBoxMisterio;
    private CheckBox checkBoxMusica;
    private CheckBox checkBoxRomance;
    private CheckBox checkBoxTerror;
    private CheckBox checkBoxThriller;
    private FloatingActionButton floatingActionButton;
    private List<CheckBox> checkBoxListAll = new ArrayList<>();
    private ArrayList<String> checkBoxListChecked = new ArrayList<>();
    private Preference preference;
    private Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private CircleImageView imagePreference;
    private StorageReference storageReference;
    private StorageReference imageReference;
    private FirebaseStorage storage;
    private PreferenceReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setupIds();
        getCheckBoxListAll();

        mDbHelper = new PreferenceReaderDbHelper(getApplicationContext());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxListChecked.clear();
                for (CheckBox checkBox : checkBoxListAll) {
                    if (checkBox.isChecked()) {
                        checkBoxListChecked.add(checkBox.getText().toString());
                    }
                }
                if (checkBoxListChecked.size() == 4) {
                    savePreference();
                    cadastrarPreferencesSQL();
                    intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtras(bundleHome());
                    startActivity(intent);
                } else {
                    Toast.makeText(PreferenceActivity.this, "Selecione 4 gêneros preferidos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            textViewHelloPref.setText("Olá  " + user.getDisplayName() + "!");
            if (user.getPhotoUrl() == null) {
                imageReference = storageReference.child("users/").child(mAuth.getUid());
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).into(imagePreference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            } else {
                Picasso.get().load(user.getPhotoUrl()).into(imagePreference);
            }
        }

            loadPreferences();
            exibirPreferencesSQL();

        }


    public void setupIds () {
        textViewHelloPref = findViewById(R.id.textView_hello_pref_id);
        Typeface myCustomFontLogo = Typeface.createFromAsset(getAssets(), "fonts/LuckiestGuy-Regular.ttf");
        textViewHelloPref.setTypeface(myCustomFontLogo);
        imagePreference = findViewById(R.id.imageView_profile_id);
        checkBoxAcao = findViewById(R.id.checkbox_acao_id);
        checkBoxAnimacao = findViewById(R.id.checkbox_animacao_id);
        checkBoxAventura = findViewById(R.id.checkbox_aventura_id);
        checkBoxCinemaTv = findViewById(R.id.checkbox_cinematv_id);
        checkBoxComedia = findViewById(R.id.checkbox_comedia_id);
        checkBoxCrime = findViewById(R.id.checkbox_crime_id);
        checkBoxDocumentario = findViewById(R.id.checkbox_documentario_id);
        checkBoxDrama = findViewById(R.id.checkbox_drama_id);
        checkBoxFamilia = findViewById(R.id.checkbox_familia_id);
        checkBoxFantasia = findViewById(R.id.checkbox_fantasia_id);
        checkBoxFaroeste = findViewById(R.id.checkbox_faroeste_id);
        checkBoxFiccaoCientifica = findViewById(R.id.checkbox_ficcaocientifica_id);
        checkBoxGuerra = findViewById(R.id.checkbox_guerra_id);
        checkBoxHistoria = findViewById(R.id.checkbox_historia_id);
        checkBoxMisterio = findViewById(R.id.checkbox_misterio_id);
        checkBoxMusica = findViewById(R.id.checkbox_musica_id);
        checkBoxRomance = findViewById(R.id.checkbox_romance_id);
        checkBoxTerror = findViewById(R.id.checkbox_terror_id);
        checkBoxThriller = findViewById(R.id.checkbox_thriller_id);
        floatingActionButton = findViewById(R.id.fab_save_id);
    }

    public List<CheckBox> getCheckBoxListAll () {
        checkBoxListAll.add(checkBoxAcao);
        checkBoxListAll.add(checkBoxAnimacao);
        checkBoxListAll.add(checkBoxAventura);
        checkBoxListAll.add(checkBoxCinemaTv);
        checkBoxListAll.add(checkBoxComedia);
        checkBoxListAll.add(checkBoxCrime);
        checkBoxListAll.add(checkBoxDocumentario);
        checkBoxListAll.add(checkBoxDrama);
        checkBoxListAll.add(checkBoxFamilia);
        checkBoxListAll.add(checkBoxFantasia);
        checkBoxListAll.add(checkBoxFaroeste);
        checkBoxListAll.add(checkBoxFiccaoCientifica);
        checkBoxListAll.add(checkBoxGuerra);
        checkBoxListAll.add(checkBoxHistoria);
        checkBoxListAll.add(checkBoxMisterio);
        checkBoxListAll.add(checkBoxMusica);
        checkBoxListAll.add(checkBoxRomance);
        checkBoxListAll.add(checkBoxTerror);
        checkBoxListAll.add(checkBoxThriller);
        return checkBoxListAll;
    }

    public void savePreference () {
        preference = new Preference();
        preference.setPreferenciaSelecionada1(checkBoxListChecked.get(0));
        preference.setPreferenciaSelecionada2(checkBoxListChecked.get(1));
        preference.setPreferenciaSelecionada3(checkBoxListChecked.get(2));
        preference.setPreferenciaSelecionada4(checkBoxListChecked.get(3));
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("preferences/" + mAuth.getUid());
        myRef.setValue(preference);
    }

    public void cadastrarPreferencesSQL () {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE1, checkBoxListChecked.get(0));
        values.put(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE2, checkBoxListChecked.get(1));
        values.put(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE3, checkBoxListChecked.get(2));
        values.put(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE4, checkBoxListChecked.get(3));

        long newRowId = db.insert(PreferenceReaderContract.PreferenceEntry.TABLE_NAME, null, values);

        exibirPreferencesSQL();
    }

    public void exibirPreferencesSQL () {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE1,
                PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE2,
                PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE3,
                PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE4
        };

        String sortOrder =
                PreferenceReaderContract.PreferenceEntry._ID + " ASC";

        Cursor cursor = db.query(
                PreferenceReaderContract.PreferenceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Preference> checkedPreferencesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(PreferenceReaderContract.PreferenceEntry._ID));

            String preferencia1 = cursor.getString(
                    cursor.getColumnIndexOrThrow(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE1));

            String preferencia2 = cursor.getString(
                    cursor.getColumnIndexOrThrow(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE2));

            String preferencia3 = cursor.getString(
                    cursor.getColumnIndexOrThrow(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE3));

            String preferencia4 = cursor.getString(
                    cursor.getColumnIndexOrThrow(PreferenceReaderContract.PreferenceEntry.COLUMN_NAME_PREFERENCE4));

            Preference preference = new Preference();
            preference.setId(id);
            preference.setPreferenciaSelecionada1(preferencia1);
            preference.setPreferenciaSelecionada2(preferencia2);
            preference.setPreferenciaSelecionada3(preferencia3);
            preference.setPreferenciaSelecionada4(preferencia4);

            checkedPreferencesList.add(preference);

        }
        cursor.close();

    }

    public Bundle bundleHome () {
        Bundle bundleHome = new Bundle();
        bundleHome.putStringArrayList("checados", checkBoxListChecked);
        return bundleHome;
    }

    public void loadPreferences () {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference("preferences/" + mAuth.getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //tenta atribuir preferencia do firebase
                    Preference preference = dataSnapshot.getValue(Preference.class);
                    Log.d(TAG, "Value is: " + preference);
                    if (preference != null) {//se existir, define as preferencias
                        setChecked(preference.getPreferenciaSelecionada1());
                        setChecked(preference.getPreferenciaSelecionada2());
                        setChecked(preference.getPreferenciaSelecionada3());
                        setChecked(preference.getPreferenciaSelecionada4());
                        //floatingActionButton.callOnClick();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception ex) {

        }
    }

    // TODO ir para preferência depois do sucesso do upload da imagem

    public void setChecked (String genre){
        switch (genre){
        case"Ação":
        checkBoxAcao.setChecked(true);
        break;
        case"Animação":
        checkBoxAnimacao.setChecked(true);
        break;
        case"Aventura":
        checkBoxAventura.setChecked(true);
        break;
        case"Cinema TV":
        checkBoxCinemaTv.setChecked(true);
        break;
        case"Comédia":
        checkBoxComedia.setChecked(true);
        break;
        case"Crime":
        checkBoxCrime.setChecked(true);
        break;
        case"Documentário":
        checkBoxDocumentario.setChecked(true);
        break;
        case"Drama":
        checkBoxDrama.setChecked(true);
        break;
        case"Família":
        checkBoxFamilia.setChecked(true);
        break;
        case"Fantasia":
        checkBoxFantasia.setChecked(true);
        break;
        case"Faroeste":
        checkBoxFaroeste.setChecked(true);
        break;
        case"Ficção Científica":
        checkBoxFiccaoCientifica.setChecked(true);
        break;
        case"Guerra":
        checkBoxGuerra.setChecked(true);
        break;
        case"História":
        checkBoxHistoria.setChecked(true);
        break;
        case"Mistério":
        checkBoxMisterio.setChecked(true);
        break;
        case"Música":
        checkBoxMusica.setChecked(true);
        break;
        case"Romance":
        checkBoxRomance.setChecked(true);
        break;
        case"Terror":
        checkBoxTerror.setChecked(true);
        break;
        case"Thriller":
        checkBoxThriller.setChecked(true);
        break;
        }

    }
}