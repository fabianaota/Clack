package br.com.digitalhouse.clackapp.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.digitalhouse.clackapp.Elementos.FilmeFavorito;
import br.com.digitalhouse.clackapp.R;

public class RecyclerviewFavoritosAdapter extends RecyclerView.Adapter<RecyclerviewFavoritosAdapter.ViewHolder> {
    private List<FilmeFavorito> filmeFavoritosList;

    public RecyclerviewFavoritosAdapter(List<FilmeFavorito> filmeFavoritosList) {
        this.filmeFavoritosList = filmeFavoritosList;
    }

    public List<FilmeFavorito> getFilmeFavoritosList() {
        return filmeFavoritosList;
    }

    public void setFilmeFavoritosList(List<FilmeFavorito> filmeFavoritosList) {
        this.filmeFavoritosList = filmeFavoritosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.modelo_recyclerview_favoritos, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FilmeFavorito filmeFavoritos = filmeFavoritosList.get(i);
        viewHolder.bind(filmeFavoritos);

    }

    @Override
    public int getItemCount() {
        return filmeFavoritosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView sinopse;
        private ImageView imagemView;

        @SuppressLint("ResourceType")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.TituloPrimeiroFilme_id);
            sinopse = itemView.findViewById(R.id.sinopsePrimeiroFile_id);
            imagemView = itemView.findViewById(R.id.primeiro_filme_id);
        }

        public void bind(final FilmeFavorito filmeFavoritos) {
            titulo.setText(filmeFavoritos.getTitulo());
            sinopse.setText(filmeFavoritos.getSinopse());

            imagemView.setImageResource(filmeFavoritos.getImageView());
        }
    }
}
