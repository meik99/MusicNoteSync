package at.htl_leonding.musicnotesync;

import android.database.SQLException;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import at.htl_leonding.musicnotesync.db.DBHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Created by michael on 06.07.16.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private static final String TAG = DatabaseTest.class.getSimpleName();

    @Test
    public void createDatabase_DatabaseCreated(){
        try {
        }catch(SQLException ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void dropDatabase_DatabaseDropped(){
        DBHelper dbHelper = null;

        try {
        }catch(SQLException ex){
            fail(ex.getMessage());
        }

        assertNotNull(dbHelper);

        try{
            dbHelper.dropDatabase();
        }catch (SQLException ex){
            fail(ex.getMessage());
        }

    }
}
