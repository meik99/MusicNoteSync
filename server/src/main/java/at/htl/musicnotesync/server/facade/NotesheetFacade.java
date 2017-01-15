package at.htl.musicnotesync.server.facade;

import at.htl.musicnotesync.server.entity.Notesheet;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by michael on 12/7/16.
 */
@Stateless
public class NotesheetFacade {
    @PersistenceContext(unitName = "mysqlPU")
    EntityManager entityManager;

    public NotesheetFacade() {
    }

    public long save(String filename, InputStream inputStream) {
        File file = new File(filename);

        if(file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -2;
        } catch (IOException e) {
            e.printStackTrace();
            return -3;
        }

//        Notesheet notesheet = new Notesheet();
//        notesheet.setFilepath(filename);
//
//
//        Notesheet result = entityManager.merge(notesheet);

        return 1;
    }
}
