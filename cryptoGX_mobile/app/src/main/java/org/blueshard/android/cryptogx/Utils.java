package org.blueshard.android.cryptogx;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Size;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

public class Utils {

    public static final int normalFile = 0;
    public static final int imageFile = 1;
    public static final int audioFile = 2;
    public static final int videoFile = 3;

    public static String UTF_8 = "UTF-8";
    public static TreeMap<String, String> algorithms = allAlgorithms();

    private static TreeMap<String, String> allAlgorithms() {
        TreeMap<String, String> return_map = new TreeMap<>();

        int[] aesKeySizes = {128, 192, 256};

        for (int i: aesKeySizes) {
            return_map.put("AES-" + i, "AES");
        }

        return return_map;
    }

    protected static boolean askPermission(Fragment fragment, String permission, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = fragment.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            fragment.requestPermissions(new String[]{permission}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (GetPathUtils.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (GetPathUtils.isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return GetPathUtils.getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (GetPathUtils.isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return GetPathUtils.getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (GetPathUtils.isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return GetPathUtils.getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static class GetPathUtils {
        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context
         *            The context.
         * @param uri
         *            The Uri to query.
         * @param selection
         *            (Optional) Filter used in the query.
         * @param selectionArgs
         *            (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri,
                                           String selection, String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = { column };

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri
                    .getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri
                    .getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri
                    .getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri
                    .getAuthority());
        }
    }

    public static int getTypeOfFile(String fname) {
        if (!fname.contains(".")) {
            return normalFile;
        }
        switch (fname.substring(fname.lastIndexOf(".") + 1)) {
            case "bpm":
            case "gif":
            case "jpg":
            case "png":
            case "webp":
                return imageFile;
            case "heic":
            case "heif":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return imageFile;
                } else {
                    return normalFile;
                }

            case "m4a":
            case "acc":
            case "flac":
            case "gsm":
            case "mid":
            case "xmf":
            case "mxmf":
            case "rtttl":
            case "rtx":
            case "ota":
            case "imy":
            case "mp3":
            case "wav":
            case "ogg":
                return audioFile;

            case "3gp":
            case "mp4":
            case "ts":
            case "webm":
            case "mkv":
                return videoFile;


            default:
                return normalFile;
        }
    }

    public static int percentOf(int i, float percent) {
        return (int) (i * (percent / 100));
    }

    public static class RecursivelyGetDirFile {

        private TreeSet<File> directories = new TreeSet<>();
        private TreeSet<File> files = new TreeSet<>();

        RecursivelyGetDirFile(File file) {
            getAll(file);
        }

        private void getAll(File startFile) {
            for (File file: startFile.listFiles()) {
                if (file.isFile()) {
                    files.add(file);
                } else {
                    directories.add(file);
                    getAll(file);
                }
            }
        }

        public TreeSet<File> getDirectories() {
            return directories;
        }

        public TreeSet<File> getFiles() {
            return files;
        }

    }


}

