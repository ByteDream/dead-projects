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

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

public class FileEnDecrypt extends Fragment {

    public static final String ARG = "fileEnDecrypt";

    private static Fragment fragment;
    private static boolean permission = false;
    private static byte[] buffer = new byte[64];
    private static Map<String, Uri> idFileMap = Collections.synchronizedMap(new HashMap<String, Uri>());
    private static final int fileChooseReturnCode = 23905;

    private static ListView fileEnDecryptFileBox;
    private static Spinner fileEnDecryptAlgorithms;
    private static TextInputEditText fileEnDecryptKeyEntry;
    private static TextInputEditText fileEnDecryptSaltEntry;

    private static ArrayAdapter<String> fileEnDecryptFileBoxAdapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == fileChooseReturnCode && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    try {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        idFileMap.put(Utils.getFileName(this.getContext(), uri), uri);
                        fileEnDecryptFileBoxAdapter.add(Utils.getFileName(this.getContext(), uri));
                        fileEnDecryptFileBoxAdapter.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (data.getData() != null) {
                try {
                    Uri uri = data.getData();
                    idFileMap.put(Utils.getFileName(this.getContext(), uri), uri);
                    fileEnDecryptFileBoxAdapter.add(Utils.getFileName(this.getContext(), uri));
                    fileEnDecryptFileBoxAdapter.notifyDataSetChanged();
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
        View view = inflater.inflate(R.layout.file_en_decrypt, container, false);

        fragment = this;

        fileEnDecryptKeyEntry = view.findViewById(R.id.fileEnDecryptKeyEntry);
        fileEnDecryptSaltEntry = view.findViewById(R.id.fileEnDecryptSaltEntry);
        fileEnDecryptFileBox = view.findViewById(R.id.fileEnDecryptFileBox);
        fileEnDecryptFileBoxAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1);
        fileEnDecryptFileBox.setAdapter(fileEnDecryptFileBoxAdapter);
        fileEnDecryptAlgorithms = view.findViewById(R.id.fileEnDecryptAlgorithm);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bannerSize = AdSize.BANNER.getHeight();

        fileEnDecryptFileBox.getLayoutParams().height = Utils.percentOf(displayMetrics.heightPixels - bannerSize, 30);

        int fileEnDecryptAdvancedTextWidth = view.findViewById(R.id.fileEnDecryptAdvancedText).getLayoutParams().width;
        View fileEnDecryptSeparator1 = view.findViewById(R.id.fileEnDecryptSeparator1);
        View fileEnDecryptSeparator2 = view.findViewById(R.id.fileEnDecryptSeparator2);
        fileEnDecryptSeparator1.getLayoutParams().width = (displayMetrics.widthPixels - fileEnDecryptAdvancedTextWidth) / 2 - 10;
        fileEnDecryptSeparator2.getLayoutParams().width = (displayMetrics.widthPixels - fileEnDecryptAdvancedTextWidth) / 2 - 10;

        fileEnDecryptAlgorithms = view.findViewById(R.id.fileEnDecryptAlgorithm);
        String[] algorithms = Utils.algorithms.keySet().toArray(new String[Utils.algorithms.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, algorithms);
        fileEnDecryptAlgorithms.setAdapter(arrayAdapter);

        return view;
    }

    protected static void chooseFiles(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        fragment.startActivityForResult(Intent.createChooser(intent, "Choose files"), fileChooseReturnCode);
    }

    protected static void encrypt(View view) {
        permission = Utils.askPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);

        if (permission) {
            File cryptoGXFileDir = new File(Environment.getExternalStorageDirectory() + "/cryptoGX/");
            if (!cryptoGXFileDir.isDirectory()) {
                if (!cryptoGXFileDir.mkdir()) {
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.could_not_create_folder) + " cryptoGX", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                File cryptoGXEncryptedFilesDir = new File(cryptoGXFileDir + "/encrypted/");
                if (!cryptoGXEncryptedFilesDir.isDirectory()) {
                    if (!cryptoGXEncryptedFilesDir.mkdir()) {
                        Toast.makeText(view.getContext(), view.getResources().getString(R.string.could_not_create_folder) + " cryptoGX/encrypted", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                String key = fileEnDecryptKeyEntry.getText().toString();
                byte[] salt;

                if (key.isEmpty()) {
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_key), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fileEnDecryptSaltEntry.getText().toString().isEmpty()) {
                    salt = new byte[16];
                } else {
                    try {
                        salt = fileEnDecryptSaltEntry.getText().toString().getBytes(Utils.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        salt = new byte[16];
                    }
                }

                int lenght;

                try {
                    lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 7));
                } catch (NumberFormatException e) {
                    try {
                        lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 8));
                    } catch (NumberFormatException ex) {
                        lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 6));
                    }
                }

                HashSet<String> success = new HashSet<>();

                EnDecrypt.AES encrypt = new EnDecrypt.AES(key,
                        salt,
                        Utils.algorithms.get(fileEnDecryptAlgorithms.getSelectedItem().toString()),
                        lenght);

                for (Map.Entry<String, Uri> entry : idFileMap.entrySet()) {
                    String name = entry.getKey();
                    Uri file = entry.getValue();

                    try {
                        encrypt.encryptFile(view.getContext().getContentResolver().openInputStream(file), new FileOutputStream(cryptoGXEncryptedFilesDir + "/" + name + ".cryptoGX"), buffer);
                        success.add(name);
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }

                for (String name: success) {
                    idFileMap.remove(name);
                    fileEnDecryptFileBoxAdapter.remove(name);
                }
                fileEnDecryptFileBoxAdapter.notifyDataSetChanged();
            }
        }
    }

    protected static void decrypt(View view) {
        permission = Utils.askPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);

        if (permission) {
            File cryptoGXFileDir = new File(Environment.getExternalStorageDirectory() + "/cryptoGX/");
            if (!cryptoGXFileDir.isDirectory()) {
                if (!cryptoGXFileDir.mkdir()) {
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.could_not_create_folder) + " cryptoGX", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                File cryptoGXDecryptedFilesDir = new File(cryptoGXFileDir + "/decrypted/");
                if (!cryptoGXDecryptedFilesDir.isDirectory()) {
                    if (!cryptoGXDecryptedFilesDir.mkdir()) {
                        Toast.makeText(view.getContext(), view.getResources().getString(R.string.could_not_create_folder) + " cryptoGX/decrypted", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                String key = fileEnDecryptKeyEntry.getText().toString();
                byte[] salt;

                if (key.isEmpty()) {
                    Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_key), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fileEnDecryptSaltEntry.getText().toString().isEmpty()) {
                    salt = new byte[16];
                } else {
                    try {
                        salt = fileEnDecryptSaltEntry.getText().toString().getBytes(Utils.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        salt = new byte[16];
                    }
                }

                int lenght;

                try {
                    lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 7));
                } catch (NumberFormatException e) {
                    try {
                        lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 8));
                    } catch (NumberFormatException ex) {
                        lenght = Integer.parseInt(fileEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 6));
                    }
                }

                HashSet<String> success = new HashSet<>();

                EnDecrypt.AES decrypt = new EnDecrypt.AES(key,
                        salt,
                        Utils.algorithms.get(fileEnDecryptAlgorithms.getSelectedItem().toString()),
                        lenght);

                for (Map.Entry<String, Uri> entry : idFileMap.entrySet()) {
                    String name = entry.getKey();
                    Uri file = entry.getValue();

                    try {
                        decrypt.decryptFile(view.getContext().getContentResolver().openInputStream(file), new FileOutputStream(cryptoGXDecryptedFilesDir + "/" + name + ".cryptoGX"), buffer);
                        success.add(name);
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }

                for (String name: success) {
                    idFileMap.remove(name);
                    fileEnDecryptFileBoxAdapter.remove(name);
                }
                fileEnDecryptFileBoxAdapter.notifyDataSetChanged();
            }
        }
    }

}
