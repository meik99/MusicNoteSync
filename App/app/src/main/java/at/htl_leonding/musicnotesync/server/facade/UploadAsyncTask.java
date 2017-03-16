package at.htl_leonding.musicnotesync.server.facade;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.listener.UploadListener;

/**
 * Created by michael on 1/15/17.
 */

public class UploadAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String FILE_TAG = "file";
    private static final String UUID_TAG = "uuid";

    private final UploadListener[] mUploadListeners;
    private final Notesheet mNotesheet;
    private final File mNotesheetFile;
    private final String mServerUrl;


    public UploadAsyncTask(Notesheet notesheet,
                           File notesheetFile,
                           String serverUrl,
                            UploadListener... uploadListeners){
        mNotesheet = notesheet;
        mNotesheetFile = notesheetFile;
        mServerUrl = serverUrl;
        mUploadListeners = uploadListeners;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("user");
        Part filePart = null;
        Part uuidPart = null;

        try {
            filePart = new FilePart(FILE_TAG, mNotesheetFile);
            uuidPart = new StringPart(UUID_TAG, mNotesheet.getUUID());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        MultipartEntity entity = new MultipartEntity(new Part[]{filePart, uuidPart});
        HttpPost post = new HttpPost(mServerUrl);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for (UploadListener listener :
                mUploadListeners) {
            listener.onUploadBegin();
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        for (UploadListener listener :
                mUploadListeners) {
            listener.onUploadFinished(success, mNotesheet);
        }
    }
}
