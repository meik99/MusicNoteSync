package at.htl_leonding.musicnotesync.db.contract;

import java.io.File;
import java.io.Serializable;

/**
 * Created by michael on 11.08.16.
 */
public interface Notesheet extends Entity{
    String getPath();
    String getName();
    String getUUID();
    Directory getParent();
    File getFile();
    String getMetadata();
}
