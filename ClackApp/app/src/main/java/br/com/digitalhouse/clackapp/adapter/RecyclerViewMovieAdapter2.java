package br.com.digitalhouse.clackapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import br.com.digitalhouse.clackapp.Elementos.Movie;
import br.com.digitalhouse.clackapp.R;

public class RecyclerViewMovieAdapter2 extends RecyclerView.Adapter<RecyclerViewMovieAdapter2.ViewHolder> {

    private List<Movie> movieList;
    private CardMovieClicado listener;

    public interface CardMovieClicado {
        void onMovieClicado(Movie movie);
    }

    public RecyclerViewMovieAdapter2(List<Movie> movieList){
        this.movieList = movieList;

    }

    @NonNull
    @Override
    public RecyclerViewMovieAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item_acao, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMovieAdapter2.ViewHolder viewHolder, int position) {
        Movie movie = movieList.get(position);
        viewHolder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.acao_id);

        }

        public void bind(Movie movie) {

            poster.setImageResource(movie.getPoster());


        }
    }
}
