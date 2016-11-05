package at.htl_leonding.musicnotesync.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import at.htl_leonding.musicnotesync.db.contract.Directory;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.db.facade.DirectoryImpl;
import at.htl_leonding.musicnotesync.db.facade.NotesheetFacade;
import at.htl_leonding.musicnotesync.db.facade.NotesheetImpl;

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
        NotesheetFacade nf = new NotesheetFacade(InstrumentationRegistry.getTargetContext());
        DirectoryFacade df = new DirectoryFacade(InstrumentationRegistry.getTargetContext());
        long inserted = -1;

        assertNotNull(nf);
        assertNotNull(df);

        Directory dir = df.getRoot();

        assertNotNull(dir);
        assertEquals("ROOT", dir.getName());

        inserted = nf.insert(dir, "Test_File.jpg");

        assertTrue(inserted > -1);

        nf.delete(nf.findById(inserted));
    }

    @Test
    public void moveNotesheet_notesheetMoved(){
        NotesheetFacade nf = new NotesheetFacade(InstrumentationRegistry.getTargetContext());
        DirectoryFacade df = new DirectoryFacade(InstrumentationRegistry.getTargetContext());
        String dirName = "New Directory";
        String fileName = "doesntmatter.jpg";
        Directory dir = df.create(dirName);
        Directory root = df.getRoot();

        assertNotNull(dir);
        assertEquals(dirName, dir.getName());
        assertEquals(root.getId(), dir.getParent().getId());

        Notesheet notesheet = nf.findById(
                nf.insert(root, fileName)
        );

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
        NotesheetFacade nf = new NotesheetFacade(InstrumentationRegistry.getTargetContext());
        long id = nf.insert(null, "to delete");
        Notesheet notesheet = nf.findById(id);

        assertNotNull(notesheet);
        assertEquals(id, notesheet.getId());

        nf.delete(notesheet);

        notesheet = nf.findById(id);

        assertNull(notesheet);
    }

   @Test
    public void updateNotesheet_notesheetUpdated(){
       NotesheetFacade nf = new NotesheetFacade(InstrumentationRegistry.getTargetContext());
       long id = nf.insert(null, "Test.png");
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
