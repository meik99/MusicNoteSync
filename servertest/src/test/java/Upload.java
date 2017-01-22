import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by michael on 12/7/16.
 */
public class Upload {
    @Test
    public void imageUpload() throws IOException {
        File file = new File("TallySheet_1.png");
        if(file.exists() == false){
            fail("File must exist");
        }

        //Inspired by
        //http://stackoverflow.com/questions/1378920/how-can-i-make-a-multipart-form-data-post-request-using-java
        //CloseableHttpClient httpClient;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder
                .addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        HttpPost post = new HttpPost("http://localhost:8080/musicnotesync/api/notesheet");
        post.setEntity(builder.build());
        HttpResponse response = httpClient.execute(post);
        HttpEntity result = response.getEntity();

        assertEquals(200, response.getStatusLine().getStatusCode());

        byte[] buffer = new byte[(int) result.getContentLength()];
        if(result.getContent() != null){
            result.getContent().read(buffer);

            String string = new String(buffer);
            long l = Long.parseLong(string);
            assertTrue(l > 0);
        }else{
            fail("InputStream must not be null");
        }
    }
}
