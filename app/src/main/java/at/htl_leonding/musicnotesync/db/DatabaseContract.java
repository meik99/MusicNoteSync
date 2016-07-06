package at.htl_leonding.musicnotesync.db;

/**
 * Created by michael on 06.07.16.
 */
public final class DatabaseContract {
    private DatabaseContract(){}

    public static final String SEPERATOR = ",";
    public static final String DATABASE = "MUSICNOTESYNC";
    public static final int VERSION = 1;

    public static final String DROP =
            DirectoryChildsContract.DROP +
            NotesheetContract.DROP +
            DirectoryContract.DROP;

    public static final String CREATE =
            DirectoryContract.CREATE +
            NotesheetContract.CREATE +
            DirectoryChildsContract.CREATE;
}
