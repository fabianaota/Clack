package br.com.digitalhouse.clackapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.digitalhouse.clackapp.adapter.RecyclerViewPesquisaFilmeAdapter;
import br.com.digitalhouse.clackapp.interfaces.CardMovieClicado;
import br.com.digitalhouse.clackapp.interfaces.ServiceListener;
import br.com.digitalhouse.clackapp.model.Movie;
import br.com.digitalhouse.clackapp.R;
import br.com.digitalhouse.clackapp.model.MovieResponse;
import br.com.digitalhouse.clackapp.model.dao.MovieDAO;


/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment implements CardMovieClicado,ServiceListener {
    private RecyclerView recyclerView;
    private RecyclerViewPesquisaFilmeAdapter adapter;
    private SearchView editSearch;
    private List<Movie> movieList = new ArrayList<>();


    public PesquisaFragment() {
        // Required empty public constructor
    }

    @Override // O que devo alterar aqui?
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
        //movieList = createMovieList();
        setupRecyclerView(view);

        editSearch = view.findViewById(R.id.search_id);
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callMovieService(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callMovieService(newText);
                //Toast.makeText(getContext(), newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return view;
    }


    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_id);

        adapter = new RecyclerViewPesquisaFilmeAdapter(movieList, this);
        recyclerView.setAdapter(adapter);
        int columns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), columns));

    }

    @Override
    public void onMovieClicado(Movie movie) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_main_id, new DetailFragment());
        transaction.commit();


    }

    private void callMovieService(String query){
        MovieDAO dao = new MovieDAO();
        dao.getSearchList(this, query);
    }

    @Override
    public void onSuccess(Object object) {
        MovieResponse response = (MovieResponse) object;
        List<Movie> movieList = response.getResults();
        adapter.setMovieList(movieList);

    }

    @Override
    public void onError(Throwable throwable) {

    }
}



