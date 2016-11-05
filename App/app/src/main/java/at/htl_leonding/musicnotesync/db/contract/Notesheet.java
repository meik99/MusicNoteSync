package at.htl_leonding.musicnotesync.db.contract;

import java.io.File;

/**
 * Created by michael on 11.08.16.
 */
public interface Notesheet {
    String getPath();
    String getName();
    String getUUID();
    Directory getParent();
    long getId();
    File getFile();
}
