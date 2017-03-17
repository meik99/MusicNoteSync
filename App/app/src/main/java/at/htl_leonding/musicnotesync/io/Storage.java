package at.htl_leonding.musicnotesync.io;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michael on 09.07.16.
 */
public class Storage {
    private Context context;

    private final File INTERNAL_STORAGE;
    private final File EXTERNAL_STORAGE;

    private static final String TAG = Storage.class.getSimpleName();

    public Storage(Context context){
        if(context == null) {
                throw new IllegalArgumentException("Argument 'context' must not be null!");
        }

        this.context = context;
        this.INTERNAL_STORAGE = this.context.getFilesDir();
        this.EXTERNAL_STORAGE = this.context.getExternalFilesDir(null);
    }

    /**
     * Creates a temporary file.
     * Can be used to store a picture taken by
     * a camera-intent
     * @param filename Filename of created file. If null, 'tmp_file' will be used
     * @param extension Extension for created file. If null, no extension will be added
     * @return Returns created file
     * @throws IOException Throws if File cannot be created for whatever reason
     */
    public static File createTemporaryStorageFile(
            Context context,
            @Nullable String filename,
            @Nullable String extension) throws IOException {
        filename = filename == null ? "tmp_file" : filename;
        extension = extension == null ? "" : extension;
        File image = null;
        Uri uri = null;

        File storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                filename,
                extension,
                storageDir
        );
        return image;
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
     * @return Returns target file if succeeded, else null
     */
    public File copyFileToInternalStorage(@NonNull File sourceFile,
                                             @Nullable String targetDirectory,
                                             @Nullable String targetFileName){

        if(sourceFile.exists() == false){
            return null;
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
                return null;
            }
        }

        if(targetFile.exists() == true){
            return null;
        }

        this.copy(sourceFile, targetFile);

        return targetFile;
    }

    private void copy(File sourceFile, File targetFile){
        //Create in and our streams
        try {
            FileInputStream in = null;
                in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(targetFile);

            //Create buffer of one kilobyte
            byte buffer[] = new byte[1024];
            int len = in.read(buffer);

            if(len > -1){
                out.write(buffer);
            }

            //While the input stream is avaiable
            while((len = in.read(buffer)) > -1){
                //...write the buffer to the output stream
                out.write(buffer);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<File> getDirectoryContent(File directory){
        List<File> result = new LinkedList<>();
        File[] files = directory != null ? directory.listFiles() :
                                            EXTERNAL_STORAGE.listFiles();

        for(File file : files){
            result.add(file);
        }

        return result;
    }

    public String getCameraDirectory() {
        return INTERNAL_STORAGE.getPath() + File.separator + "camera" + File.separator;
    }
}
