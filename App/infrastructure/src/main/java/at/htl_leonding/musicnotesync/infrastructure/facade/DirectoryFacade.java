package at.htl_leonding.musicnotesync.infrastructure.facade;

import android.content.Context;

import java.util.Collection;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryChild;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryChildImpl;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryImpl;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryChildContext;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public class DirectoryFacade {
    private DirectoryContext directoryContext;
    private DirectoryChildContext directoryChildContext;

    public DirectoryFacade(Context context){
        directoryContext = new DirectoryContext(context);
        directoryChildContext = new DirectoryChildContext(context);
    }

    public Directory getRootDirectory() {
        return directoryContext.getRoot();
    }

    public Directory create(String folderName, Directory currentDirectory) {
        DirectoryImpl directory = new DirectoryImpl();
        Directory result = null;
        directory.setName(folderName);

        result = directoryContext.create(directory);
        directoryChildContext.create(result, currentDirectory);

        return result;
    }

    public List<Directory> findByDirectory(Directory parentDirectory) {
        return directoryContext.getChildren(parentDirectory);
    }

    public Directory update(Directory directory) {
        return directoryContext.update(directory);
    }

    public Directory delete(Directory directory) {
        return directoryContext.delete(directory);
    }

    public Directory getParent(Directory directory) {
        return directoryContext.getParent(directory);
    }

    public Directory findById(long id) {
        return directoryContext.findById(id);
    }

    public Directory move(Directory sourceDirectory, Directory targetDirectory) {
        DirectoryChildImpl directoryChild = new DirectoryChildImpl();

        List<DirectoryChild> directoryChildren = directoryChildContext.findAll();

        for (DirectoryChild item :
                directoryChildren) {
            if(item.getChildId() == sourceDirectory.getId()){
                directoryChild.setChildId(sourceDirectory.getId());
                directoryChild.setParentId(targetDirectory.getId());

                directoryChildContext.update(item, directoryChild);
                return findById(sourceDirectory.getId());
            }
        }

        return sourceDirectory;
    }
}
