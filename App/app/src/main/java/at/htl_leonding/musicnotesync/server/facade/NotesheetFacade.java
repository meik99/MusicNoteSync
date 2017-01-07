package at.htl_leonding.musicnotesync.server.facade;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPostHC4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.request.RequestCode;

/**
 * Created by michael on 12/30/16.
 */

public class NotesheetFacade {
    private static final String SERVER_URL = "http://10.0.2.2:8080/musicnotesync/api/notesheet";
    private static final String FILE_TAG = "file";
    private static final String UUID_TAG = "uuid";

    private final Context mContext;

    public NotesheetFacade(Context context){
        mContext = context;
    }

    public boolean sendNotesheet(Notesheet notesheet){
        File tmpFile = notesheet.getFile();
        final String uuid = notesheet.getUUID();

        if(tmpFile.exists() == false){
            tmpFile = new File(mContext.getFilesDir() + File.separator + notesheet.getFile());
            if(tmpFile.exists() == false){
                return false;
            }
        }

        final File file = tmpFile;

        //Inspired by
        //http://stackoverflow.com/questions/1378920/how-can-i-make-a-multipart-form-data-post-request-using-java
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... param) {

                AndroidHttpClient httpClient = AndroidHttpClient.newInstance("user");
                Part filePart = null;
                Part uuidPart = null;
                try {
                    filePart = new FilePart(FILE_TAG, file);
                    uuidPart = new StringPart(UUID_TAG, uuid);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                MultipartEntity entity = new MultipartEntity(new Part[]{filePart, uuidPart});
                HttpPostHC4 post = new HttpPostHC4(SERVER_URL);
                post.setEntity(entity);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    return true;
                }else{
                    return false;
                }
            }
        };

        try {
            asyncTask.execute();
            return asyncTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
}
