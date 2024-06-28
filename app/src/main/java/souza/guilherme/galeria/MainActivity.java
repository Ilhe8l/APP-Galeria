package souza.guilherme.galeria;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;
    String currentPhotoPath;

    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cria uma lista de permissões necessárias e adiciona a permissão para usar a câmera
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions); // Verifica e solicita as permissões necessárias

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        // Obtém o diretório de armazenamento de fotos e lista todos os arquivos nesse diretório
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = dir.listFiles();

        // Adiciona o caminho absoluto de cada arquivo na lista de fotos
        for (int i = 0; i < files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }

        // Inicializa o adaptador com a lista de fotos
        mainAdapter = new MainAdapter(MainActivity.this, photos);

        // Configura o RecyclerView e define o adaptador
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        // Calcula o número de colunas para o GridLayoutManager com base na largura do item
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);

        // Configura o LayoutManager do RecyclerView como GridLayoutManager com o número de colunas calculado
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);


    }

    // Método para iniciar o processo de captura de uma foto
    private void dispatchTakePictureIntent() {
        File f = null;
        try {
            f = createImageFile(); // Cria um arquivo para armazenar a foto
        } catch (IOException e) {
            // Mostra uma mensagem de erro se o arquivo não puder ser criado
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_SHORT).show();
        }

        currentPhotoPath = f.getAbsolutePath(); // Armazena o caminho do arquivo

        if (f != null) {
            // Obtém a URI do arquivo usando o FileProvider
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "souza.guilherme.galeria.fileprovider", f);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Cria um Intent para capturar a imagem

            // Define a URI onde a foto será salva
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            startActivityForResult(i, RESULT_TAKE_PICTURE); // Inicia a Activity para capturar a foto
        }
    }

    // Método para criar um arquivo de imagem
    private File createImageFile() throws IOException {
        // Gera um nome de arquivo único usando a data e hora atual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Obtém o diretório de armazenamento
        File f = File.createTempFile(imageFileName, ".jpg", storageDir); // Cria um arquivo temporário
        return f;
    }

    // Método que recebe o resultado da Activity iniciada com startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                photos.add(currentPhotoPath); // Adiciona o caminho da foto à lista de fotos
                mainAdapter.notifyItemInserted(photos.size() - 1); // Notifica o adaptador para atualizar a RecyclerView
            } else {
                // Se o resultado não for OK, exclui o arquivo criado
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    // Método para verificar e solicitar permissões
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                permissionsNotGranted.add(permission); // Adiciona à lista as permissões não concedidas
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsNotGranted.size() > 0) {
                // Solicita as permissões não concedidas
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }

    // Método para verificar se uma permissão foi concedida
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    // Método que recebe o resultado da solicitação de permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if (requestCode == RESULT_REQUEST_PERMISSION) {
            for (String permission : permissions) {
                if (!hasPermission(permission)) {
                    permissionsRejected.add(permission); // Adiciona à lista as permissões rejeitadas
                }
            }
        }

        if (permissionsRejected.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    // Mostra um alerta explicando a necessidade das permissões
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Para usar esse app é preciso conceder essas permissões")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Solicita novamente as permissões rejeitadas
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                                }
                            }).create().show();
                }
            }
        }
    }

    // Método que cria o menu de opções
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu); // Infla o layout do menu
        return true;
    }

    // Método que lida com a seleção de itens no menu de opções
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera) {
            dispatchTakePictureIntent(); // Inicia o processo de captura de foto
            return true;
        }
        return super.onOptionsItemSelected(item); // Chama o método da superclasse para outros itens
    }

    // Método para iniciar a Activity de visualização da foto
    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath); // Adiciona o caminho da foto como extra
        startActivity(i); // Inicia a Activity
    }

}