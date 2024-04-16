package com.ugb.calculadora;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class eliminarDatosServidor extends AsyncTask<String, Void, Boolean> {
    private OnDeleteListener onDeleteListener;
    HttpURLConnection httpURLConnection;

    public eliminarDatosServidor(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
        String _id = params[0];

            URL url = new URL(utilidades.urlMto+"/"+_id);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Basic "+ utilidades.credencialesCodificadas);

return true;
            //int responseCode = httpURLConnection.getResponseCode();
           // return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (onDeleteListener != null) {
            onDeleteListener.onDeleteComplete(success);
        }
    }

    public interface OnDeleteListener {
        void onDeleteComplete(boolean success);
    }
}
