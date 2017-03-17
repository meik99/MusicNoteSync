package at.htl_leonding.musicnotesync.infrastructure.contract;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public interface DirectoryChild {
    long getParentId();
    long getChildId();
}
