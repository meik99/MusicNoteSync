package at.htl_leonding.musicnotesync.management.move;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.contract.Entity;
import at.htl_leonding.musicnotesync.infrastructure.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.infrastructure.facade.NotesheetFacade;
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

    private NotesheetFacade notesheetFacade;
    private DirectoryFacade directoryFacade;

    protected MoveController(MoveActivity moveActivity){
        long id;
        String _class;

        mMoveActivity = moveActivity;
        mMoveModel = new MoveModel();

        notesheetFacade = new NotesheetFacade(moveActivity);
        directoryFacade = new DirectoryFacade(moveActivity);

        id = mMoveActivity.getIntent().getLongExtra("ObjectId", -1);
        _class = mMoveActivity.getIntent().getStringExtra("ObjectClass");

        if(id != -1){
            if(_class.equals("Notesheet")){
               mMoveModel.setSelectedObject(
                       notesheetFacade.findById(id)
               );
           }else if(_class.equals("Directory")){
                mMoveModel.setSelectedObject(
                        directoryFacade.findById(id)
                );
            }
        }

        mMoveModel.setNotesheetClickListener(new NotesheetClickListener(this));
        mMoveModel.setNotesheetArrayAdapter(new NotesheetArrayAdapter(
                mMoveModel.getNotesheetClickListener(), null));

        openDirectory(directoryFacade.getRootDirectory());

    }

    public NotesheetArrayAdapter getNotesheetArrayAdapter() {
        return mMoveModel.getNotesheetArrayAdapter();
    }

    public void moveObjectToDirectory() {
        Object notesheetObject = mMoveModel.getSelectedObject();

        if(notesheetObject instanceof Notesheet){
            notesheetFacade.move(
                    (Notesheet)notesheetObject,
                    mMoveModel.getCurrentDirectory());
        }else if(notesheetObject instanceof Directory){
            directoryFacade.move(
                    (Directory)notesheetObject,
                    mMoveModel.getCurrentDirectory());
        }
        mMoveActivity.setResult(Activity.RESULT_OK);
        mMoveActivity.finishActivity(RequestCode.MOVE_ITEM_REQUEST_CODE);
    }

    public void openDirectory(Directory directory) {
        directory = directory == null ? directoryFacade.getRootDirectory() : directory;

        List<Entity> entities = new ArrayList<>();
        List<Directory> directories = directoryFacade.findByDirectory(directory);

        for (Directory item :
                directories) {
            if(
                    (mMoveModel.getSelectedObject() instanceof Directory &&
                    ((Directory)mMoveModel.getSelectedObject()).getId() != item.getId())
                            || mMoveModel.getSelectedObject() instanceof Notesheet){
                entities.add(item);
            }
        }

        mMoveModel.getNotesheetArrayAdapter().setNotesheetObjects(
                entities
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

    public boolean goToDirectoryParent() {
        boolean currentDirectoryIsRoot =
                mMoveModel.getCurrentDirectory().getId() ==
                        directoryFacade.getRootDirectory().getId();

        if(currentDirectoryIsRoot == false){
            mMoveModel.setCurrentDirectory(
                    directoryFacade.getParent(
                            mMoveModel.getCurrentDirectory())
            );

            openDirectory(mMoveModel.getCurrentDirectory());
        }

        return !currentDirectoryIsRoot;
    }
}
