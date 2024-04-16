package com.ugb.calculadora;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class adaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<productos> datosProductosArrayList;
    productos misProductos;
    LayoutInflater layoutInflater;

    public adaptadorImagenes(Context context, ArrayList<productos> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }
    @Override
    public int getCount() {
        return datosProductosArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return datosProductosArrayList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        try{
            misProductos = datosProductosArrayList.get(i);


            TextView tempVal = itemView.findViewById(R.id.lblNombre);
            tempVal.setText(misProductos.getNombre());


            tempVal = itemView.findViewById(R.id.lblMarca);
            tempVal.setText("Marca: " +misProductos.getMarca());

            tempVal = itemView.findViewById(R.id.lblDescripcion);
            tempVal.setText(misProductos.getDescripcion());

            tempVal = itemView.findViewById(R.id.lblCodigo);
            tempVal.setText("Cod: "+misProductos.getCodigoProducto());

            tempVal = itemView.findViewById(R.id.lblPresentacion);
            tempVal.setText(misProductos.presentacion);

            tempVal = itemView.findViewById(R.id.lblPrecio);
            tempVal.setText("$"+misProductos.getPrecio());

            ImageView imgView = itemView.findViewById(R.id.imgFoto);
            Bitmap bitmap = BitmapFactory.decodeFile(misProductos.getFoto());
            imgView.setImageBitmap(bitmap);

        }catch (Exception e){
            Toast.makeText(context, "Error al mostrar datos: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}