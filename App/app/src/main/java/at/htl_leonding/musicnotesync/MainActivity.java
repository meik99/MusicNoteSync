package at.htl_leonding.musicnotesync;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.List;

import at.htl_leonding.musicnotesync.adapter.NotesheetArrayAdapter;
import at.htl_leonding.musicnotesync.db.contract.Notesheet;
import at.htl_leonding.musicnotesync.db.facade.DirectoryFacade;
import at.htl_leonding.musicnotesync.helper.intent.CameraIntentHelper;
import at.htl_leonding.musicnotesync.io.Storage;
import at.htl_leonding.musicnotesync.mainactivity.listener.FabOnClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView noteSheetRecyclerView;
    private NotesheetArrayAdapter adapter;
    private DirectoryFacade dirFacade;

    private MainController controller;
    private File mPhotoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        controller = new MainController(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(controller.getFabListener());

        dirFacade = new DirectoryFacade(this);
        adapter = new NotesheetArrayAdapter(dirFacade.getRoot().getNotesheets());
        noteSheetRecyclerView = (RecyclerView) findViewById(R.id.noteSheetRecyclerView);
        noteSheetRecyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        noteSheetRecyclerView.setLayoutManager(llm);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CameraIntentHelper.REQUEST_CODE:
                controller.storeFileFromCameraIntent(resultCode);
                break;
            case FabOnClickListener.SELECT_FILE_REQUEST_CODE:
                controller.storeFileFromFileChooser(resultCode,
                        data.getData().getPath());
                break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter.setSheets(dirFacade.getRoot().getNotesheets());
        adapter.notifyDataSetChanged();
        controller.dismissDialog();
        super.onResume();
    }

}
