package org.blueshard.android.cryptogx.filedirchooser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import org.blueshard.android.cryptogx.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDirChooser extends AppCompatActivity {

    private RecyclerView files;
    private RecyclerView selectedFileDirs;
    private FileDirAdapter filesAdapter;
    private SelectedFileDirsAdapter selectedFileDirsAdapter;
    private Context context = this;
    private FileDirData[] dataSet;
    private ArrayList<FileDirData> selectedDataSet;

    private boolean multipleMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_dir_chooser);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fileDirFragment, new FileDirFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        File startDirectory;
        if (extras != null) {
            String directory = extras.getString("directory");
            if (directory != null && !directory.equals("<root>")) {
                startDirectory = new File(directory);
            } else {
                startDirectory = Environment.getExternalStorageDirectory();
            }

            multipleMode = extras.getBoolean("multipleMode");

        } else {
            startDirectory = Environment.getExternalStorageDirectory();
        }

        dataSet = new FileDirData[startDirectory.listFiles().length];

        filesInDir(startDirectory);

        filesAdapter = new FileDirAdapter(dataSet, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = filesAdapter.getData(files.getChildLayoutPosition(view)).getFile();
                if (file.isDirectory()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fileDirFragment, new FileDirFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (!multipleMode) {
                    View selectedFileDirs = findViewById(R.id.selectedFileDirs);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) selectedFileDirs.getLayoutParams();
                    params.height = 0;
                    selectedFileDirs.setLayoutParams(params);
                }
            }

            }, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    FileDirData data = filesAdapter.getData(files.getChildLayoutPosition(view));
                    if (multipleMode) {
                        selectedDataSet.add(data);
                        selectedFileDirsAdapter.notifyDataSetChanged();
                        return true;
                    } else if (data.getFile().isFile()) {
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        return true;
                    }
                    return false;
                }
            });

        files = findViewById(R.id.files);
        files.setAdapter(filesAdapter);
        files.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        selectedFileDirs = findViewById(R.id.selectedFileDirs);
    }


}