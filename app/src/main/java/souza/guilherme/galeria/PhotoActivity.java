package souza.guilherme.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    // Obtém a referência para a Toolbar pelo ID e a define como a ActionBar da Activity
    Toolbar toolbar = findViewById(R.id.tbPhoto);
    setSupportActionBar(toolbar);

    // Obtém a referência para a ActionBar e habilita o botão "up" para navegação
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    // Obtém o Intent que iniciou a Activity e extrai o caminho da foto
    Intent i = getIntent();
    photoPath = i.getStringExtra("photo_path");

    // Converte o caminho da foto em um Bitmap e define esse Bitmap em um ImageView
    Bitmap bitmap = Util.getBitmap(photoPath);
    ImageView imPhoto = findViewById(R.id.imPhoto);
    imPhoto.setImageBitmap(bitmap);
    }

    // Método que lida com a seleção de itens no menu de opções
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Verifica se o item selecionado é o de compartilhar
        if (item.getItemId() == R.id.opShare) {
            sharePhoto(); // Chama o método para compartilhar a foto
            return true;
        }
        return super.onOptionsItemSelected(item); // Chama o método da superclasse
    }

    // Método que cria o menu de opções
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu); // Chama o método da superclasse
        MenuInflater inflater = getMenuInflater(); // Obtém o inflador de menus
        inflater.inflate(R.menu.photo_activity_tb, menu); // Infla o layout do menu
        return true; // Indica que o menu foi criado com sucesso
    }

    // Método para compartilhar a foto
    void sharePhoto() {
        // Cria uma URI para a foto usando o FileProvider
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this, "souza.guilherme.galeria.fileprovider", new File(photoPath));

        // Cria um Intent de envio e adiciona a URI da foto como extra
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        i.setType("image/jpeg"); // Define o tipo MIME como imagem JPEG

        // Inicia uma Activity
        startActivity(i);
    }

}