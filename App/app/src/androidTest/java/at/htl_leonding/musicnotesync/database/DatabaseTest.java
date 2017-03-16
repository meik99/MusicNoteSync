package at.htl_leonding.musicnotesync.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import at.htl_leonding.musicnotesync.infrastructure.database.DBHelper;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.contract.DirectoryImpl;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


/**
 * Created by michael on 06.07.16.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest{
    private static final String TAG = DatabaseTest.class.getSimpleName();

    @Test
    public void createDatabase_DatabaseCreated(){
        Context context = InstrumentationRegistry.getTargetContext();
        DirectoryContext df = new DirectoryContext(context);
        NotesheetContext nf = new NotesheetContext(context);
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();
        SQLiteDatabase dbWriteable = dbHelper.getWritableDatabase();

        assertNotNull(df);
        assertNotNull(nf);
        assertNotNull(dbHelper);
        assertNotNull(dbReadable);
        assertNotNull(dbWriteable);
    }

    @Test
    public void getRoot_RootDirectoryRetrieved(){
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Directory root = df.getRoot();

        assertNotNull(root);
        assertEquals(null, "ROOT", root.getName());
    }

    @Test
    public void findDirectoryById_DirectoryFound(){
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Directory root = df.getRoot();
        Directory rootById = df.findById(root.getId());

        assertNotNull(root);
        assertNotNull(rootById);
        assertEquals(root.getId(), rootById.getId());
    }

    @Test
    public void createDirectory_DirectoryCreated(){
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Directory newDir = df.create("Test Directory");
        Directory root = df.getRoot();
        long newId = newDir.getId();

        assertNotNull(newDir);
        assertNotNull(root);
        assertNotNull(newDir.getParent());
        assertEquals(root.getId(), newDir.getParent().getId());

        df.delete(newDir);
        newDir = df.findById(newDir.getId());

        assertNull(newDir);
    }

    @Test
    public void moveDirectory_DirectoryMoved(){
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Directory newDir = df.create("Test Directory");
        Directory referenceDir = null;
        Directory parentDir = df.create("Parent Directory");
        Directory root = df.getRoot();

        assertNotNull(newDir);
        assertNotNull(parentDir);
        assertNotNull(root);

        assertEquals(newDir.getParent().getId(), root.getId());
        assertEquals(parentDir.getParent().getId(), root.getId());

        newDir = df.move(newDir, parentDir);
        referenceDir = df.findById(newDir.getId());

        assertNotNull(newDir);
        assertNotNull(referenceDir);

        assertEquals(newDir.getParent().getId(), parentDir.getId());
    }

    @Test
    public void updateDirectory_directoryNameChanged(){
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Directory directory = df.create("Test Dir");

        Assert.assertNotNull(directory);
        Assert.assertEquals("Test Dir", directory.getName());

        DirectoryImpl updatedDirectory = new DirectoryImpl();

        updatedDirectory.fromDirectory(directory);
        updatedDirectory.setName("Test Directory");
        directory = df.rename(updatedDirectory);

        Assert.assertNotNull(directory);
        Assert.assertEquals("Test Directory", directory.getName());
    }
}
