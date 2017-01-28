package at.htl_leonding.musicnotesync.server.facade;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.bluetooth.BluetoothActivity;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.listener.DownloadListener;
import at.htl_leonding.musicnotesync.server.listener.UploadListener;

/**
 * Created by michael on 12/30/16.
 */

public class NotesheetFacade {
//    private static final String SERVER_URL =
//            "http://vm91.htl-leonding.ac.at:8080/musicnotesyncserver/api/notesheet";
    private static final String SERVER_URL =
            "http://10.0.0.5:8080/musicnotesyncserver/api/notesheet";


    public NotesheetFacade(){

    }

    public void sendNotesheet(Context context,
                                 Notesheet notesheet,
                                 UploadListener... uploadListeners){
        File tmpFile = notesheet.getFile();

        if(tmpFile.exists() == false){
            tmpFile = new File(context.getFilesDir() + File.separator + notesheet.getFile());
            if(tmpFile.exists() == false){
                for (UploadListener listener : uploadListeners) {
                    listener.onUploadFinished(false, notesheet);
                }
            }
        }

        final File file = tmpFile;

        //Inspired by
        //http://stackoverflow.com/questions/1378920/how-can-i-make-a-multipart-form-data-post-request-using-java
        AsyncTask<Void, Void, Boolean> uploadTask =
                new UploadAsyncTask(notesheet, file, SERVER_URL, uploadListeners);

        uploadTask.execute();
    }

    public void downloadNotesheet(String uuid,
                                     String filename,
                                     DownloadListener... downloadListeners) {
        AsyncTask<Void, Void, Boolean> downloadTask =
                new DownloadAsyncTask(SERVER_URL, uuid, filename, downloadListeners);
        downloadTask.execute();
    }

}
