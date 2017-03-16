package at.htl_leonding.musicnotesync.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.infrastructure.database.context.DirectoryContext;
import at.htl_leonding.musicnotesync.infrastructure.database.context.NotesheetContext;
import at.htl_leonding.musicnotesync.infrastructure.contract.NotesheetImpl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by michael on 01.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class NotesheetTest {
    @Test
    public void createNotesheet_notesheetCreated(){
        NotesheetContext nf = new NotesheetContext(InstrumentationRegistry.getTargetContext());
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        Notesheet inserted = null;

        assertNotNull(nf);
        assertNotNull(df);

        Directory dir = df.getRoot();

        assertNotNull(dir);
        assertEquals("ROOT", dir.getName());

        inserted = nf.insert(dir, "Test_File.jpg");

        assertTrue(inserted != null);

        nf.delete(nf.findById(inserted.getId()));
    }

    @Test
    public void moveNotesheet_notesheetMoved(){
        NotesheetContext nf = new NotesheetContext(InstrumentationRegistry.getTargetContext());
        DirectoryContext df = new DirectoryContext(InstrumentationRegistry.getTargetContext());
        String dirName = "New Directory";
        String fileName = "doesntmatter.jpg";
        Directory dir = df.create(dirName);
        Directory root = df.getRoot();

        assertNotNull(dir);
        assertEquals(dirName, dir.getName());
        assertEquals(root.getId(), dir.getParent().getId());

        Notesheet notesheet = nf.insert(root, fileName);

        assertNotNull(notesheet);
        assertEquals(fileName, notesheet.getName());
        assertEquals(root.getId(), notesheet.getParent().getId());

        notesheet = nf.move(notesheet, dir);

        assertNotNull(notesheet);
        assertEquals(fileName, notesheet.getName());
        assertEquals(dir.getId(), notesheet.getParent().getId());
    }

    @Test
    public void deleteNotesheet_notesheetDeleted(){
        NotesheetContext nf = new NotesheetContext(InstrumentationRegistry.getTargetContext());
        Notesheet notesheet =  nf.insert(null, "to delete");
        long id = notesheet.getId();

        assertNotNull(notesheet);
        assertEquals(id, notesheet.getId());

        nf.delete(notesheet);

        notesheet = nf.findById(id);

        assertNull(notesheet);
    }

   @Test
    public void updateNotesheet_notesheetUpdated(){
       NotesheetContext nf = new NotesheetContext(InstrumentationRegistry.getTargetContext());
       long id = nf.insert(null, "Test.png").getId();
       Notesheet notesheet = nf.findById(id);

       assertNotNull(notesheet);
       assertEquals("Test.png", notesheet.getName());

       NotesheetImpl updatedNotesheet = new NotesheetImpl(notesheet.getUUID());
       updatedNotesheet.fromNotesheet(notesheet);
       updatedNotesheet.setName("Tested.png");

       notesheet = nf.update(updatedNotesheet);

       assertNotNull(notesheet);
       assertEquals("Tested.png", notesheet.getName());
   }
}
