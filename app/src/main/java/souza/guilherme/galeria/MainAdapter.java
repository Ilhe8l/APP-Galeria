package souza.guilherme.galeria;



import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos){
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item da lista
        // Usado para ler o arquivo xml de layout do item e então criar os elementos de interface propriamente ditos
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }
    // Método que retorna a quantidade de itens no adaptador
    @Override
    public int getItemCount() {
        return photos.size(); // Retorna o tamanho da lista de fotos
    }

    // Método que vincula os dados do item à view holder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        // Obtém a referência do ImageView no item da RecyclerView
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);

        // Obtém as dimensões do item da RecyclerView a partir dos recursos
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);

        // Carrega o bitmap da foto na posição especificada com as dimensões obtidas
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);
        imPhoto.setImageBitmap(bitmap); // Define o bitmap no ImageView

        // Define um listener para o clique no ImageView
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia a atividade de visualização da foto ao clicar na imagem
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }

}
