package at.htl_leonding.musicnotesync.mainactivity.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import at.htl_leonding.musicnotesync.MainController;
import at.htl_leonding.musicnotesync.MoveFileActivity;
import at.htl_leonding.musicnotesync.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.R;
import at.htl_leonding.musicnotesync.db.contract.Directory;

/**
 * Created by hanne on 03.11.2016.
 */
public class NotesheetLongClickListener implements View.OnLongClickListener {
    private static final String TAG = NotesheetLongClickListener.class.getSimpleName();

    public static final int GET_TARGET_FOR_MOVE = 7;

    private MainController mController;
    private NotesheetArrayAdapter mAdapter;

    private final Activity mActivity;

    private Dialog mSelectFormatDialog;

    private static Directory sourceDir;
    public NotesheetLongClickListener(MainController controller, NotesheetArrayAdapter adapter, Activity activity) {
        mController = controller;
        mAdapter = adapter;
        mActivity = activity;
    }

    @Override
    public boolean onLongClick(final View view) {
        final Context context = view.getContext();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.context_menu_dialog, null);

        Button btnRenameFile = (Button) dialogView.findViewById(R.id.btnRenameFile);
        Button btnDeleteFile = (Button) dialogView.findViewById(R.id.btnDeleteFile);
        Button btnMoveFile = (Button) dialogView.findViewById(R.id.btnMoveFile);

        /*btnRenameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        btnRenameFile.setEnabled(false);

        btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = view.getTag();
                if (o instanceof Directory)
                    mController.getDF().delete((Directory)o);
                mAdapter.setDirectory(mAdapter.getCurrDir());
                mAdapter.notifyDataSetChanged();
                mSelectFormatDialog.dismiss();
            }
        });

        btnMoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = view.getTag();
                if (o instanceof Directory) {
                    sourceDir = (Directory)o;

                    Intent intent = new Intent(mActivity, MoveFileActivity.class);
                    mActivity.startActivityForResult(intent, GET_TARGET_FOR_MOVE);

                    //mController.getDF().move((Directory) o, mController.getDF().getRoot());

                }
                mSelectFormatDialog.dismiss();
                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setView(dialogView);
        builder.setTitle("Select Action");


        mSelectFormatDialog = builder.create();
        mSelectFormatDialog.show();
        return true;
    }

    public static Directory getSourceDir(){
        return sourceDir;
    }
}
