package at.htl_leonding.musicnotesync.server.facade;

import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Base64InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;

/**
 * Created by michael on 12/30/16.
 */

public class NotesheetFacade {
    private static final String SERVER_URL = "http://localhost:8080/musicnotesync/api/notesheet";

    public NotesheetFacade(){

    }

    public boolean sendNotesheet(Notesheet notesheet){
        File file = notesheet.getFile();
        String uuid = notesheet.getUUID();

        if(file.exists() == false){
            return false;
        }

        //Inspired by
        //http://stackoverflow.com/questions/1378920/how-can-i-make-a-multipart-form-data-post-request-using-java
        //CloseableHttpClient httpClient;

        HttpClient httpClient = AndroidHttpClient.newInstance("user");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder
                .addBinaryBody("file", file)
                .addTextBody("uuid", uuid);


        HttpPost post = new HttpPost(SERVER_URL);
        post.setEntity(builder.build());
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity result = response.getEntity();


        byte[] buffer = new byte[(int) result.getContentLength()];
        try {
            if(result.getContent() != null) {
                result.getContent().read(buffer);

                String string = new String(buffer);
                long l = Long.parseLong(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
