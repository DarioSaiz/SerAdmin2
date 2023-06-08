package com.example.seradmin.Recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seradmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorListado extends RecyclerView.Adapter<AdaptadorListado.ViewHolder> {

    private ArrayList<Cliente> perfilesArrayList;

    private ArrayList<Cliente> perfilesArrayList2;

    public interface ItemClickListener {
        void onClick(View view, int position, Cliente cliente);
    }

    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    private RecyclerViewClickListener listener;

    public AdaptadorListado(ArrayList<Cliente> dataSet) {
        perfilesArrayList = dataSet;
        //this.listener = listener;
        //almacena una copia de la lista en perfilesList2.
        perfilesArrayList2 = new ArrayList<>(perfilesArrayList);
    }

    public AdaptadorListado() {

    }

    public void filtrado(final String nombreCliente) {
        int longitud = nombreCliente.length();
        if (longitud == 0) {
            perfilesArrayList.clear();
            perfilesArrayList.addAll(perfilesArrayList2);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Cliente> collecion = perfilesArrayList.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(nombreCliente.toLowerCase()))
                        .collect(Collectors.toList());
                perfilesArrayList.clear();
                perfilesArrayList.addAll(collecion);
            } else {
                for (Cliente c : perfilesArrayList2) {
                    if (c.getNombre().toLowerCase().contains(nombreCliente.toLowerCase())) {
                        perfilesArrayList.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nomClienteCajaPerfiles1;
        //private final TextView letraNombre;
        private final ImageView imagenCajaPerfiles;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            nomClienteCajaPerfiles1 = (TextView) v.findViewById(R.id.nomClienteCajaPerfiles1);
            //letraNombre = (TextView) v.findViewById(R.id.Letra);
            imagenCajaPerfiles = (ImageView) v.findViewById(R.id.imagenCajaPerfiles);

        }


        public TextView getNomCliente() {
            return nomClienteCajaPerfiles1;
        }

//        public TextView getLetraNom() {
//            return letraNombre;
//        }

        public ImageView getImagenPerfil() {
            return imagenCajaPerfiles;
        }

        public void onClick(View view) {
            // Si tengo un manejador de evento lo propago con el índice
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition(), perfilesArrayList.get(getAdapterPosition()));
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.caja_perfiles, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//      String letraActual = perfilesList.get(position).getLetra();
//      String letraSiguiente = "";
        holder.getNomCliente().setText(perfilesArrayList.get(position).getNombre() + " "  + perfilesArrayList.get(position).getApellidos());
        //holder.getLetraNom().setText(perfilesArrayList.get(position).getLetra());
        //holder.getImagenPerfil().setImageResource(perfilesList.get(position).getImagen());
//      if (letraActual != letraSiguiente) {
//          holder.getLetraNom().setText(perfilesList.get(position).getLetra());
//      }
//      letraSiguiente = perfilesList.get(position).getLetra();
    }

    @Override
    public int getItemCount() {
        //return 1;
        return perfilesArrayList.size();
    }

}