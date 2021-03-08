package org.blueshard.android.cryptogx;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

    public CollectionPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        position++;

        if (position == 1) {
            Fragment fragment = new TextEnDecrypt();
            Bundle args = new Bundle();
            args.putInt(TextEnDecrypt.ARG, position);
            return fragment;
        } else if (position == 2) {
            Fragment fragment = new FileEnDecrypt();
            Bundle args = new Bundle();
            args.putInt(FileEnDecrypt.ARG, position);
            return fragment;
        } else if (position == 3) {
            Fragment fragment = new SecureDeleteFiles();
            Bundle args = new Bundle();
            args.putInt(SecureDeleteFiles.ARG, position);
            return fragment;
        } else {
            return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        position++;
        if (position == 1) {
            return "text en- / decrypt";
        } else if (position == 2) {
            return "file en- / decrypt";
        } else if (position == 3) {
            return "secure delete files";
        }
        return null;
    }
}
