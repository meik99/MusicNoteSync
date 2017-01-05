package at.htl.musicnotesync.server.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;

/**
 * Created by michael on 12/7/16.
 */
@Entity
public class Notesheet implements Serializable{
    @Column
    @NotNull
    @Id
    private String filepath;

    @Transient
    private File file;

    public Notesheet(){
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
        this.file = new File(this.filepath);
    }

    public File getFile() {
        return file;
    }
}
