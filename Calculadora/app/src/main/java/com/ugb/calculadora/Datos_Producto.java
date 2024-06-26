package com.ugb.calculadora;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Datos_Producto extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    FloatingActionButton btnRegresar;
    String accion="nuevo", id="", urlCompletaImg="",rev="",idProducto="";
    Intent tomarFotoIntent,cargarFotoIntent;
    ImageView img;
    utilidades utls;
    detectarInternet di;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_producto);

        utls = new utilidades();

        img = findViewById(R.id.imgProducto);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoProducto();
            }
        });

        btnRegresar = findViewById(R.id.btnRegresarListaProductos);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regresarListaProductos();
            }
        });
        btn = findViewById(R.id.btnGuardarProductos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tempVal = findViewById(R.id.txtnombre);
                    String nombre = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtdescripcion);
                    String descripcion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtMarca);
                    String marca = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPresentacion);
                    String presentacion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPrecio);
                    String precio = tempVal.getText().toString();

                    if (!(nombre.equals("") || marca.equals("") || descripcion.equals("") || presentacion.equals("") || precio.equals(""))) {
                    //Guardar en el server
                        String[] datos = new String[]{idProducto, nombre, descripcion, marca, presentacion, precio, urlCompletaImg,id,rev};

                        try {
                            di = new detectarInternet(getApplicationContext());
                            if(di.hayConexionInternet()) {
                                guardarDatosServidor(datos);
                            }
                            else {
                                guardarDatosSQLite(datos);
                            }
                            mostrarMsg("Producto Registrado con Exito.");
                            regresarListaProductos();

                        }catch(Exception e){mostrarMsg("Error al detectar si hay conexion "+e.getMessage());}


                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Hay campos vacíos", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){mostrarMsg("Error al guardar: "+e.getMessage());}
            }

        });
        mostrarDatosProductos();
    }
    private void guardarDatosServidor(String[] datos){
        try {
            JSONObject datosProductos = new JSONObject();
            if (accion.equals("modificar") && datos[7].length() > 0 && datos[8].length() > 0) {
                datosProductos.put("_id", datos[7]);
                datosProductos.put("_rev", datos[8]);

            }
            datosProductos.put("idProducto", datos[0]);
            datosProductos.put("nombre", datos[1]);
            datosProductos.put("descripcion", datos[2]);
            datosProductos.put("marca", datos[3]);
            datosProductos.put("presentacion", datos[4]);
            datosProductos.put("precio", datos[5]);
            datosProductos.put("urlCompletaImg", datos[6]);

            String respuesta = "";
            enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
            respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();
            JSONObject respuestaJSONObject = new JSONObject(respuesta);

            if (respuestaJSONObject.getBoolean("ok")) {
                id = respuestaJSONObject.getString("id");
                rev = respuestaJSONObject.getString("rev");
            } else {
                respuesta = "Error al guardar en servidor: " + respuesta;
            }
        }catch (Exception e) {
            mostrarMsg("Error en server: "+e.getMessage());
        }

    }
    private void guardarDatosSQLite(String[] datos){
        try {
            DB db = new DB(getApplicationContext(), "", null, 1);
            String respuesta = "";

            respuesta = db.administrar_Productos(accion, datos);

        }catch (Exception e){mostrarMsg("Error SQLite"+e.getMessage());}

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{

            if( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                img.setImageBitmap(imagenBitmap);
            }else{
                mostrarMsg("Se cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar la camara: "+ e.getMessage());
        }
    }
    private void cargarFotoProducto(){
        cargarFotoIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

      if( cargarFotoIntent.resolveActivity(getPackageManager())!=null ){
            File fotoProducto = null;
            try{

                fotoProducto = crearImagenProducto();
                if( fotoProducto!=null ){
                    Uri uriFotoProducto = FileProvider.getUriForFile(Datos_Producto.this, "com.ugb.calculadora.fileprovider", fotoProducto);
                    cargarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                    startActivityForResult(cargarFotoIntent, 1);
                }else{
                    mostrarMsg("NO pude tomar la foto");
                }
            }catch (Exception e){
                mostrarMsg("Error al tomar la foto: "+ e.getMessage());
            }
        }
    }


    private void tomarFotoProducto(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if( tomarFotoIntent.resolveActivity(getPackageManager())!=null ){
            File fotoProducto = null;
            try{

                fotoProducto = crearImagenProducto();
                if( fotoProducto!=null ){
                    Uri uriFotoProducto = FileProvider.getUriForFile(Datos_Producto.this, "com.ugb.calculadora.fileprovider", fotoProducto);
                    tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                    startActivityForResult(tomarFotoIntent, 1);
                }else{
                    mostrarMsg("NO pude tomar la foto");
                }
            }catch (Exception e){
                mostrarMsg("Error al tomar la foto: "+ e.getMessage());
            }
        }else{
            mostrarMsg("No se selecciono una foto...");
        }
    }
    private File crearImagenProducto() throws Exception{

        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "imagen_"+ fechaHoraMs +"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( !dirAlmacenamiento.exists() ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void regresarListaProductos(){
        Intent abrirVentana = new Intent(getApplicationContext(), Lista_Productos.class);
        startActivity(abrirVentana);
    }
    private void mostrarDatosProductos(){
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if( accion.equals("modificar") ){
                JSONObject jsonObject = new JSONObject(parametros.getString("tienda_online")).getJSONObject("value");
                id=jsonObject.getString("_id");
                rev =jsonObject.getString("_rev");
                idProducto=jsonObject.getString("idProducto");

                tempVal = findViewById(R.id.txtnombre);
                tempVal.setText(jsonObject.getString("nombre"));

                tempVal = findViewById(R.id.txtdescripcion);
                tempVal.setText(jsonObject.getString("descripcion"));

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(jsonObject.getString("marca"));

                tempVal = findViewById(R.id.txtPresentacion);
                tempVal.setText(jsonObject.getString("presentacion"));

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(jsonObject.getString("precio"));

                urlCompletaImg = jsonObject.getString("urlCompletaImg");
                Bitmap bitmap = BitmapFactory.decodeFile(urlCompletaImg);
                img.setImageBitmap(bitmap);
            }else
                idProducto=utls.generarIdUnico();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al mostrar los datos: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}