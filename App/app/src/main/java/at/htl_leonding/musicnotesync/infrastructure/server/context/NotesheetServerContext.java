package at.htl_leonding.musicnotesync.infrastructure.server.context;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;

/**
 * Created by mrynkiewicz on 15/03/17.
 */

public class NotesheetServerContext {
    private static int MEGABYTE = 1024000;
    private static final String PROTOCOL = "http";
//    private static final String HOST =
//            "vm91.htl-leonding.ac.at";
    private static final String HOST =
            "192.168.1.239";
    private static int PORT = 8080;
    private static final String PATH = "/musicnotesyncserver/api/notesheet";
    private final Context mContext;
    //    private static final String HOST =
    //            "http://10.0.0.6:8080/musicnotesyncserver/api/notesheet";
    //    private static final String HOST =
    //            "http://192.168.0.10:8080/musicnotesyncserver/api/notesheet";

    public NotesheetServerContext(Context context){
        mContext = context;
    }

    //Download notesheet & add to database
    //Upload notesheet
    public void upload(Notesheet notesheet){
        if(notesheet != null) {
            AsyncTask<Notesheet, Void, Void> task = new AsyncTask<Notesheet, Void, Void>() {
                @Override
                protected Void doInBackground(Notesheet[] params) {
                    try {
                        OutputStream outputStream;
                        FileInputStream fileInputStream;
                        byte[] buffer;
                        int read = -1;
                        URL serverUrl = new URL(PROTOCOL, HOST, PORT, PATH);
                        File file = new File(mContext.getFilesDir().getPath() +
                                File.separator +
                                params[0].getFile().getPath());
                        String uuid = params[0].getUUID();
                        HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();

                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/octet-stream");
                        connection.setRequestProperty("Content-Length", "" + file.length());
                        connection.setRequestProperty("filename", uuid);
                        connection.setFixedLengthStreamingMode(file.length());

                        buffer = new byte[MEGABYTE];
                        fileInputStream = new FileInputStream(file);
                        outputStream = connection.getOutputStream();

                        while ((read = fileInputStream.read(buffer)) > -1) {
                            outputStream.write(buffer, 0, read);
                        }


                        outputStream.close();
                        fileInputStream.close();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            task.execute(notesheet);
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public Notesheet download(String uuid){
        if(uuid == null){
            return null;
        }



        return null;
    }
}
