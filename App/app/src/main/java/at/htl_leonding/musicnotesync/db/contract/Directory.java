package at.htl_leonding.musicnotesync.db.contract;

import java.util.List;

/**
 * Created by michael on 11.08.16.
 */
public interface Directory extends Entity{;
    String getName();
    List<Directory> getChildren();
    List<Notesheet> getNotesheets();
    Directory getParent();
}
