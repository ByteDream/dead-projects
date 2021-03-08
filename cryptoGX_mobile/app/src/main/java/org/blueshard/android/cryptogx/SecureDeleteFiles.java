package org.blueshard.android.cryptogx;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.ads.AdSize;
import com.google.android.material.textfield.TextInputEditText;

import org.blueshard.android.cryptogx.filedirchooser.FileDirChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SecureDeleteFiles extends Fragment {

    public static final String ARG = "secureDeleteFiles";

    private static Fragment fragment;
    private static boolean permission;
    private static final int fileChooseReturnCode = 34589;

    private static ListView fileDeleteFileBox;
    private static TextInputEditText fileDeleteIterationsEntry;

    private static Map<String, Uri> idFileMap = Collections.synchronizedMap(new HashMap<String, Uri>());

    private static ArrayAdapter<String> fileDeleteFileBoxAdapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == fileChooseReturnCode && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    try {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        idFileMap.put(Utils.getFileName(this.getContext(), uri), uri);
                        fileDeleteFileBoxAdapter.add(Utils.getFileName(this.getContext(), uri));
                        fileDeleteFileBoxAdapter.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    idFileMap.put(Utils.getFileName(this.getContext(), uri), uri);
                    fileDeleteFileBoxAdapter.add(Utils.getFileName(this.getContext(), uri));
                    fileDeleteFileBoxAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                permission = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secure_delete_files, container, false);

        fragment = this;

        fileDeleteFileBox = view.findViewById(R.id.fileDeleteFileBox);
        fileDeleteFileBoxAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1);
        fileDeleteFileBox.setAdapter(fileDeleteFileBoxAdapter);
        fileDeleteIterationsEntry = view.findViewById(R.id.fileDeleteIterationsEntry);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bannerSize = AdSize.BANNER.getHeight();

        ListView fileDeleteFileBox = view.findViewById(R.id.fileDeleteFileBox);
        fileDeleteFileBox.getLayoutParams().height = Utils.percentOf(displayMetrics.heightPixels - bannerSize, 44);

        int fileDeleteAdvancedTextWidth = view.findViewById(R.id.fileDeleteAdvancedText).getLayoutParams().width;
        View fileDeleteSeparator1 = view.findViewById(R.id.fileDeleteSeparator1);
        View fileDeleteSeparator2 = view.findViewById(R.id.fileDeleteSeparator2);
        fileDeleteSeparator1.getLayoutParams().width = (displayMetrics.widthPixels - fileDeleteAdvancedTextWidth) / 2 - 10;
        fileDeleteSeparator2.getLayoutParams().width = (displayMetrics.widthPixels - fileDeleteAdvancedTextWidth) / 2 - 10;

        return view;
    }

    protected static void chooseFiles(View view) {
        Intent i = new Intent(fragment.getContext(), FileDirChooser.class);
        fragment.startActivity(i);
    }

    protected static void delete(View view) {
        permission = Utils.askPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);

        if (permission) {
            HashSet<String> success = new HashSet<>();
            int iterations;
            try {
                String iterationEntry = fileDeleteIterationsEntry.getText().toString();
                if (iterationEntry.isEmpty()) {
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_iterations), Toast.LENGTH_SHORT).show();
                    return;
                }
                iterations = Integer.parseInt(iterationEntry);
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(), view.getResources().getString(R.string.wrong_iterations), Toast.LENGTH_SHORT).show();
                return;
            }

            for (Map.Entry<String, Uri> entry: idFileMap.entrySet()) {
                try {
                    File file = new File(Utils.getPath(view.getContext(), entry.getValue()));
                    if (file == null) {
                        Toast.makeText(view.getContext(), view.getResources().getString(R.string.no_file_path) + " " + entry.getKey(), Toast.LENGTH_LONG).show();
                    } else {
                        SecureDelete.deleteFileLineByLine(file, iterations);
                        while (file.isFile()) {
                            file.delete();
                        }
                        success.add(entry.getKey());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (String name: success) {
                idFileMap.remove(name);
                fileDeleteFileBoxAdapter.remove(name);
            }
            fileDeleteFileBoxAdapter.notifyDataSetChanged();
        }
    }

}
