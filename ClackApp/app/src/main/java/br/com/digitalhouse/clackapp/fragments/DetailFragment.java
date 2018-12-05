package br.com.digitalhouse.clackapp.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.digitalhouse.clackapp.R;
import br.com.digitalhouse.clackapp.database.FilmesFavoritosContract;
import br.com.digitalhouse.clackapp.database.FilmesFavoritosDbHelper;
import br.com.digitalhouse.clackapp.interfaces.UpdateMovies;
import br.com.digitalhouse.clackapp.model.Movie;
import br.com.digitalhouse.clackapp.service.RetrofitService;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    public static final String MOVIE = "MOVIE";
    private ImageView imagemPost;
    private ImageView share;
    private Movie movie;
    private ImageView favoritarFilme;
    private ImageButton botaoFechar;
    private FilmesFavoritosDbHelper mDbHelper;
    private  SQLiteDatabase db;
    private UpdateMovies listenerUpdate;

    public static DetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putSerializable(MOVIE, movie);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenerUpdate = (UpdateMovies) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        mDbHelper = new FilmesFavoritosDbHelper(getContext());
        db = mDbHelper.getWritableDatabase();


        imagemPost = view.findViewById(R.id.imagem_act_id);
        share = view.findViewById(R.id.image_compartilhar);
        botaoFechar = view.findViewById(R.id.botao_fechar_id);
       // botaoSalva = view.findViewById(R.id.)
        favoritarFilme = view.findViewById(R.id.favoritar_film);
        favoritarFilme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarFilmeLocal(movie);
            }
        });


        botaoFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });





        share.setOnClickListener(new View.OnClickListener()
       {
            @Override
            public void onClick(View v) {
                onShareClicado(movie);
            }
        });

        movie = (Movie) getArguments().getSerializable(MOVIE);

        String titulo = movie.getNome();
        TextView tituloText = view.findViewById(R.id.titulo_act_id);
        Typeface myCustomFontLogo = Typeface.createFromAsset(getContext().getAssets(), "fonts/LuckiestGuy-Regular.ttf");
        tituloText.setTypeface(myCustomFontLogo);
        tituloText.setText(titulo);


        String descricao = movie.getSinopse();
        TextView descricaoText = view.findViewById(R.id.sinopse_act_id);
        descricaoText.setText(descricao);




        String poster = movie.getPoster();
        Picasso.get().load(RetrofitService.BASE_IMAGE_URL + poster).into(imagemPost);


        Date data = movie.getData();
        TextView dataText = view.findViewById(R.id.textView_data_id);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = formatter.format(data);
        dataText.setText("Data de lançamento: " + dataFormatada);

        float nota = movie.getNota();
        TextView notaText = view.findViewById(R.id.textView_nota_id);
        notaText.setText("Nota média: " + nota);

        return view;

    }

    private void salvarFilmeLocal(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_NAME_TITLE, movie.getNome());
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_DATE, movie.getData().toString());
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_POSTER, movie.getPoster());
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_NOTA, movie.getNota());
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_GENERO, movie.getGeneros().toString());
        values.put(FilmesFavoritosContract.FilmesFavoritosEntry.COLUMN_SINOPSE,movie.getSinopse());
        long newRowId = db.insert(FilmesFavoritosContract.FilmesFavoritosEntry.TABLE_NAME, null, values);
        Toast.makeText(getContext(),"Filme salvo!",Toast.LENGTH_SHORT).show();
        listenerUpdate.updateMovies();
    }



    public void onShareClicado(Movie movie){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, movie.getNome());
        share.putExtra(Intent.EXTRA_TEXT, "https://image.tmdb.org/t/p/w500" + movie.getPoster());
        startActivity(Intent.createChooser(share, movie.getPoster()));
    }


}
