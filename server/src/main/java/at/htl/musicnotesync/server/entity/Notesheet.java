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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @NotNull
    private String filepath;

    @Transient
    private File file;

    public Notesheet(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
