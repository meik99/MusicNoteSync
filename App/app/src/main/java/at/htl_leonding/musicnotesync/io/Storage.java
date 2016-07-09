package at.htl_leonding.musicnotesync.io;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by michael on 09.07.16.
 */
public class Storage {
    private Context context;

    private final File INTERNAL_STORAGE;

    private static final String TAG = Storage.class.getSimpleName();

    public Storage(Context context){
        if(context == null) {
                throw new IllegalArgumentException("Argument 'context' must not be null!");
        }

        this.context = context;
        this.INTERNAL_STORAGE = this.context.getFilesDir();
    }

    /**
     * Copies a given file to a directory to the internal storage region.
     * If target directoy is null it will be copied to root directory.
     * If target file name is null, the name of the source file will be used.
     *
     * Root directoy is the data directory of the app.
     * E.g.: /data/at.htl_leonding.musicnotesync/files/
     * @param sourceFile File that shall be copied
     * @param targetDirectory Directory inside root directory where the file should be copied to.
     *                        Will be created if it doesn't exists
     * @param targetFileName Final name of the copy. If null, name of source file will be used.
     * @return Returns true if copying succeeded, else false
     */
    public boolean copyFileToInternalStorage(@NonNull File sourceFile,
                                             @Nullable String targetDirectory,
                                             @Nullable String targetFileName){

        if(sourceFile.exists() == false){
            return false;
        }

        //If targetDirectory already starts with a seperator it will be removed to prevent
        //errors
        if(targetDirectory.startsWith(File.pathSeparator)){
            targetDirectory.replaceFirst(File.pathSeparator, "");
        }

        if(targetDirectory == null) {
            targetDirectory = "";
        }
        if(targetFileName == null){
            targetFileName = sourceFile.getName();
        }

        File targetDir = new File(
                this.INTERNAL_STORAGE.getPath()
                + File.separator
                + targetDirectory);
        File targetFile = new File(
                targetDir.getPath()
                + File.separator
                + targetFileName);

        //Returns false if target directory creation fails
        if(targetDir.exists() == false){
            if(targetDir.mkdir() == false){
                return false;
            }
        }

        if(targetFile.exists() == true){
            return false;
        }

        try {
            this.copy(sourceFile, targetFile);
        } catch (IOException e) {
            Log.e(TAG, "copyFileToInternalStorage: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void copy(File sourceFile, File targetFile) throws IOException {
        //Create in and our streams
        InputStream in = new FileInputStream(sourceFile);
        OutputStream out = new FileOutputStream(targetFile);

        //Create buffer of one kilobyte
        byte buffer[] = new byte[1024];
        int len;
        //While the length that was read from the input stream is bigger than zero...
        while((len = in.read(buffer)) > 0){
            //...write the buffer to the output stream
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }
}
