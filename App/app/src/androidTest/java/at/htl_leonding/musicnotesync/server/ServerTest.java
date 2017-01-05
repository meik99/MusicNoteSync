package at.htl_leonding.musicnotesync.server;

import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.server.facade.NotesheetFacade;

import static org.junit.Assert.fail;

/**
 * Created by michael on 1/4/17.
 */
@RunWith(AndroidJUnit4.class)
public class ServerTest {
    NotesheetFacade serverFacade;
    at.htl_leonding.musicnotesync.db.facade.NotesheetFacade dbFacade;

//    @Before
//    public void setupFacades(){
//        serverFacade = new NotesheetFacade();
//        dbFacade =
//                new at.
//                        htl_leonding.
//                        musicnotesync.
//                        db.
//                        facade.
//                        NotesheetFacade(InstrumentationRegistry.getTargetContext());
//    }
//
//    @After
//    public void teardownFacades(){
//        dbFacade = null;
//        serverFacade = null;
//    }

    @Test
    public void uploadNotesheet(){
//        Notesheet notesheet = dbFacade.findById(0);
        File file = new File("./");
        fail();
    }
}
