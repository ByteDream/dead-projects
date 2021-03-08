package org.blueshard.android.cryptogx;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdSize;
import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class TextEnDecrypt extends Fragment {

    private static TextInputEditText textEnDecryptKeyEntry;
    private static TextInputEditText textEnDecryptSaltEntry;
    private static EditText textEnDecryptDecryptedText;
    private static EditText textEnDecryptEncryptedText;
    private static Spinner textEnDecryptAlgorithms;

    public static final String ARG = "textEnDecrypt";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_en_decrypt, container, false);

        textEnDecryptKeyEntry = view.findViewById(R.id.textEnDecryptKeyEntry);
        textEnDecryptSaltEntry = view.findViewById(R.id.textEnDecryptSaltEntry);
        textEnDecryptDecryptedText = view.findViewById(R.id.textEnDecryptDecryptedText);
        textEnDecryptEncryptedText = view.findViewById(R.id.textEnDecryptEncryptedText);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bannerSize = AdSize.BANNER.getHeight();

        EditText textEnDecryptDecryptedText = view.findViewById(R.id.textEnDecryptDecryptedText);
        textEnDecryptDecryptedText.getLayoutParams().height = Utils.percentOf(displayMetrics.heightPixels - bannerSize, 21);

        EditText textEnDecryptEncryptedText = view.findViewById(R.id.textEnDecryptEncryptedText);
        textEnDecryptEncryptedText.getLayoutParams().height = Utils.percentOf(displayMetrics.heightPixels - bannerSize, 21);

        int textEnDecryptAdvancedTextWidth = view.findViewById(R.id.textEnDecryptAdvancedText).getLayoutParams().width;
        View textEnDecryptSeparator1 = view.findViewById(R.id.textEnDecryptSeparator1);
        View textEnDecryptSeparator2 = view.findViewById(R.id.textEnDecryptSeparator2);
        textEnDecryptSeparator1.getLayoutParams().width = (displayMetrics.widthPixels - textEnDecryptAdvancedTextWidth) / 2 - 10;
        textEnDecryptSeparator2.getLayoutParams().width = (displayMetrics.widthPixels - textEnDecryptAdvancedTextWidth) / 2 - 10;

        textEnDecryptAlgorithms = view.findViewById(R.id.textEnDecryptAlgorithms);
        String[] algorithms = Utils.algorithms.keySet().toArray(new String[Utils.algorithms.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, algorithms);
        textEnDecryptAlgorithms.setAdapter(arrayAdapter);

        return view;
    }

    protected static void encrypt(View view) {
        if (textEnDecryptKeyEntry.getText().toString().isEmpty()) {
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_key), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] salt;
            int lenght;

            try {
                lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 7));
            } catch (NumberFormatException e) {
                try {
                    lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 8));
                } catch (NumberFormatException ex) {
                    lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 6));
                }
            }

            if (textEnDecryptSaltEntry.getText().toString().isEmpty()) {
                salt = new byte[16];
            } else {
                salt = textEnDecryptSaltEntry.getText().toString().getBytes(EnDecrypt.UTF_8);
            }
            EnDecrypt.AES encrypt = new EnDecrypt.AES(textEnDecryptKeyEntry.getText().toString(),
                    salt,
                    Utils.algorithms.get(textEnDecryptAlgorithms.getSelectedItem().toString()),
                    lenght);
            String encryptedText = encrypt.encrypt(textEnDecryptDecryptedText.getText().toString());
            textEnDecryptEncryptedText.setText(encryptedText);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected static void decrypt(View view) {
        if (textEnDecryptKeyEntry.getText().toString().isEmpty()) {
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_key), Toast.LENGTH_SHORT).show();
            return;
        } else if (textEnDecryptEncryptedText.getText().toString().isEmpty()) {
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_encrypted_text), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            byte[] salt;
            int lenght;

            try {
                lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 7));
            } catch (NumberFormatException e) {
                try {
                    lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 8));
                } catch (NumberFormatException ex) {
                    lenght = Integer.parseInt(textEnDecryptAlgorithms.getSelectedItem().toString().substring(4, 6));
                }
            }

            if (textEnDecryptSaltEntry.getText().toString().isEmpty()) {
                salt = new byte[16];
            } else {
                salt = textEnDecryptSaltEntry.getText().toString().getBytes(EnDecrypt.UTF_8);
            }
            EnDecrypt.AES decrypt = new EnDecrypt.AES(textEnDecryptKeyEntry.getText().toString(),
                    salt,
                    Utils.algorithms.get(textEnDecryptAlgorithms.getSelectedItem().toString()),
                    lenght);
            String encryptedText = decrypt.decrypt(textEnDecryptEncryptedText.getText().toString());
            textEnDecryptDecryptedText.setText(encryptedText);
        } catch (IllegalArgumentException e) {
            Toast.makeText(view.getContext(), view.getResources().getString(R.string.empty_key), Toast.LENGTH_LONG).show();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
