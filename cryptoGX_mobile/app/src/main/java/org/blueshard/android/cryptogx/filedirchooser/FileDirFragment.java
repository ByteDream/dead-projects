package org.blueshard.android.cryptogx.filedirchooser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.blueshard.android.cryptogx.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDirFragment extends Fragment {

    private Context context;
    private File directory;
    private RecyclerView files;
    private RecyclerView selectedFileDirs;
    private FileDirAdapter filesAdapter;
    private SelectedFileDirsAdapter selectedFileDirsAdapter;
    private FileDirData[] dataSet;
    private ArrayList<FileDirData> selectedDataSet;

    private boolean multipleMode = false;

    public FileDirFragment(File directory) {
        dataSet = new FileDirData[directory.listFiles().length];
        this.directory = directory;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        RecyclerView files = (RecyclerView) view.findViewById(R.id.files);

        filesInDir(directory);

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

        files = view.findViewById(R.id.files);
        files.setAdapter(filesAdapter);
        files.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        selectedFileDirs = new FileDirChooser().findViewById(R.id.selectedFileDirs);
    }

    private void filesInDir(File directory) {
        final File[] files = directory.listFiles();

        System.out.println(Arrays.toString(files));

        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return object1.getName().compareToIgnoreCase(object2.getName());
                }
            });

            final int cores;
            if (files.length == 0) {
                return;
            } else if (files.length < Runtime.getRuntime().availableProcessors()) {
                cores = files.length;
            } else {
                cores = (int) Math.ceil(files.length / Runtime.getRuntime().availableProcessors());
            }
            final AtomicInteger itemsPerThread = new AtomicInteger((int) Math.ceil(files.length / cores));
            for (int core = 0; core < cores; core++) {
                final int finalCore = core;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        File file;
                        for (int i = itemsPerThread.get() * finalCore; i < i + itemsPerThread.get(); i++) {
                            try {
                                file = files[i];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                return;
                            }
                            if (file.isDirectory()) {
                                dataSet[i] = new FileDirData(file, getResources().getDrawable(R.drawable.normal_folder));
                                continue;
                            } else {
                                try {
                                    /*FileInputStream fis = new FileInputStream(file);
                                    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                                    Float width = new Float(imageBitmap.getWidth());
                                    Float height = new Float(imageBitmap.getHeight());
                                    Float ratio = width/height;
                                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int)(64 * ratio), 64, false);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageData = baos.toByteArray();
                                    BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                    dataSet[i] = new FileDirData(file, new BitmapDrawable(BitmapFactory.decodeByteArray(imageData, 0, imageData.length)));*/
                                    dataSet[i] = new FileDirData(file, context.getResources().getDrawable(R.drawable.image_file));

                                } catch (Exception e) {
                                    dataSet[i] = new FileDirData(file, getResources().getDrawable(R.drawable.normal_file));
                                }
                            }
                        }
                    }
                };
                thread.start();
            }
        }
    }

}
