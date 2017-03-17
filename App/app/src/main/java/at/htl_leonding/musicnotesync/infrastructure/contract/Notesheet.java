package at.htl_leonding.musicnotesync.infrastructure.contract;

import java.io.File;

/**
 * Created by michael on 11.08.16.
 */
public interface Notesheet extends Entity{
    String getPath();
    String getName();
    String getUUID();
    long getParentId();
    //File getFile();
    String getMetadata();
}
