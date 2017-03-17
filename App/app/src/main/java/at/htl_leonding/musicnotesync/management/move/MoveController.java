package at.htl_leonding.musicnotesync.management.move;

import android.app.Activity;

import at.htl_leonding.musicnotesync.mainactivity.adapter.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.infrastructure.contract.Directory;
import at.htl_leonding.musicnotesync.infrastructure.contract.Notesheet;
import at.htl_leonding.musicnotesync.management.move.listener.NotesheetClickListener;
import at.htl_leonding.musicnotesync.request.RequestCode;

/**
 * Created by michael on 11/29/16.
 */

public class MoveController {
    private MoveActivity mMoveActivity;
    private MoveModel mMoveModel;

    protected MoveController(MoveActivity moveActivity){
        long id;
        String _class;

        mMoveActivity = moveActivity;
        mMoveModel = new MoveModel();
        mMoveModel.createFacades(mMoveActivity);

        id = mMoveActivity.getIntent().getLongExtra("ObjectId", -1);
        _class = mMoveActivity.getIntent().getStringExtra("ObjectClass");

        if(id != -1){
            if(_class.equals("Notesheet")){
               mMoveModel.setSelectedObject(
                       mMoveModel.getNotesheetFacade().findById(id)
               );
           }else if(_class.equals("Directory")){
                mMoveModel.setSelectedObject(
                        mMoveModel.getDirectoryFacade().findById(id)
                );
            }
        }

        mMoveModel.setNotesheetClickListener(new NotesheetClickListener(this));
        mMoveModel.setNotesheetArrayAdapter(new NotesheetArrayAdapter(
                mMoveModel.getNotesheetClickListener(), null));
        openDirectory(mMoveModel.getDirectoryFacade().getRoot());

    }

    public NotesheetArrayAdapter getNotesheetArrayAdapter() {
        return mMoveModel.getNotesheetArrayAdapter();
    }

    public void moveObjectToDirectory() {
        Object notesheetObject = mMoveModel.getSelectedObject();

        if(notesheetObject instanceof Notesheet){
            mMoveModel.getNotesheetFacade().move((Notesheet)notesheetObject,
                    mMoveModel.getCurrentDirectory());
        }else if(notesheetObject instanceof Directory){
            mMoveModel.getDirectoryFacade().move((Directory)notesheetObject,
                    mMoveModel.getCurrentDirectory());
        }
        mMoveActivity.setResult(Activity.RESULT_OK);
        mMoveActivity.finishActivity(RequestCode.MOVE_ITEM_REQUEST_CODE);
    }

    public void openDirectory(Directory directory) {
        directory = directory == null ? mMoveModel.getDirectoryFacade().getRoot() : directory;

        mMoveModel.getNotesheetArrayAdapter().setNotesheetObjects(
                mMoveModel.getNotesheetObjects(directory)
        );
        mMoveModel.setCurrentDirectory(directory);
    }

    public String getSelectedFileName() {
        String fileName = "n/a";
        Object notesheetObject = this.mMoveModel.getSelectedObject();
        if(notesheetObject instanceof Notesheet){
            fileName = ((Notesheet)notesheetObject).getName();
        }else if(notesheetObject instanceof Directory){
            fileName = ((Directory)notesheetObject).getName();
        }
        return fileName;
    }
}
