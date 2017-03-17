package at.htl_leonding.musicnotesync.infrastructure.contract;

import android.database.Cursor;

import at.htl_leonding.musicnotesync.infrastructure.database.DirectoryChildsContract;

/**
 * Created by mrynkiewicz on 17/03/17.
 */

public class DirectoryChildImpl implements DirectoryChild {
    private long parentId;
    private long childId;

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public long getChildId() {
        return childId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public void fromCursor(Cursor cursor) {
        long parentId =
                cursor.getLong(
                        cursor.getColumnIndex(
                                DirectoryChildsContract.DirectoryChildsEntry.COLUMN_PARENT_ID));
        long childId =
                cursor.getLong(
                        cursor.getColumnIndex(
                                DirectoryChildsContract.DirectoryChildsEntry.COLUMN_CHILD_ID));

        setParentId(parentId);
        setChildId(childId);
    }
}
