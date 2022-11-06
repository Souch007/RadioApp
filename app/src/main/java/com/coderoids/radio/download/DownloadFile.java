package com.coderoids.radio.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<String, Integer, String> {
    private String podcastURL;
    public DownloadFile(String podcastURl){
        this.podcastURL = podcastURl;
    }
    @Override
    protected String doInBackground(String... url) {
        int count;
        try {
            URL _url = new URL(podcastURL);
            URLConnection conexion = _url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream input = new BufferedInputStream(_url.openStream());
            String relativePath = Environment.getExternalStorageDirectory().getPath() + "/DownloadPodcastM.mp3";
            OutputStream output = new FileOutputStream(relativePath);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //Log.d("Downloading", values+"");
    }
}
