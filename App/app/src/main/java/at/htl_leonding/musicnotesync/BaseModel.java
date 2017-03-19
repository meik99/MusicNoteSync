package at.htl_leonding.musicnotesync;

import java.util.List;

import at.htl_leonding.musicnotesync.blt.BltRepository;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;

/**
 * Created by michael on 3/19/17.
 */

public class BaseModel {
    private Directory activeDirectory;
    private String[] currentConnections;

    public BaseModel(){}

    public Directory getActiveDirectory() {
        return activeDirectory;
    }

    public void setActiveDirectory(Directory activeDirectory) {
        this.activeDirectory = activeDirectory;
    }

    public void setCurrentConnections(String[] currentConnections) {
        this.currentConnections = currentConnections;
    }

    public String[] getCurrentConnections() {
        return currentConnections;
    }
}
