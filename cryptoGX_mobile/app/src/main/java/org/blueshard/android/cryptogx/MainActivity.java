package org.blueshard.android.cryptogx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Set<Thread> textEnDecryptThreads = Collections.synchronizedSet(new HashSet<Thread>());
    private Set<Thread> fileEnDecryptThreads = Collections.synchronizedSet(new HashSet<Thread>());
    private Set<Thread> fileDeleteThreads = Collections.synchronizedSet(new HashSet<Thread>());
    private AppCompatActivity mainView;

    CollectionPagerAdapter fragmentChanger;
    ViewPager viewPager;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainView = this;

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        fragmentChanger = new CollectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentChanger);
    }

    public void encryptTextButton(final View view) {
        final ProgressBar textEnDecryptProgressBar = mainView.findViewById(R.id.textEnDecryptProgressBar);
        textEnDecryptProgressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textEnDecryptThreads.add(Thread.currentThread());
                        TextEnDecrypt.encrypt(view);
                        if (textEnDecryptThreads.size() - 1 <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textEnDecryptProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        textEnDecryptThreads.remove(Thread.currentThread());
                    }
                });
            }
        };
        thread.start();
    }

    public void decryptTextButton(final View view) {
        final ProgressBar textEnDecryptProgressBar = mainView.findViewById(R.id.textEnDecryptProgressBar);
        textEnDecryptProgressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textEnDecryptThreads.add(currentThread());
                        TextEnDecrypt.decrypt(view);
                        if (textEnDecryptThreads.size() - 1 <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textEnDecryptProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        textEnDecryptThreads.remove(currentThread());
                    }
                });
            }
        };
        thread.start();
    }

    //------------------------------------------------------------//

    public void fileEnDecryptChooseFiles(View view) {
        FileEnDecrypt.chooseFiles(view);
    }

    public void encryptFileButton(final View view) {
        final ProgressBar fileEnDecryptProgressBar = mainView.findViewById(R.id.fileEnDecryptProgressBar);
        fileEnDecryptProgressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileEnDecryptThreads.add(currentThread());
                        FileEnDecrypt.encrypt(view);
                        if (fileEnDecryptThreads.size() - 1 <= 0) {
                            fileEnDecryptProgressBar.setVisibility(View.INVISIBLE);
                        }
                        fileEnDecryptThreads.remove(currentThread());
                    }
                });
            }
        };
        thread.start();
    }

    public void decryptFileButton(final View view) {
        final ProgressBar fileEnDecryptProgressBar = mainView.findViewById(R.id.fileEnDecryptProgressBar);
        fileEnDecryptProgressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileEnDecryptThreads.add(currentThread());
                        FileEnDecrypt.decrypt(view);
                        if (fileEnDecryptThreads.size() - 1 <= 0) {
                            fileEnDecryptProgressBar.setVisibility(View.INVISIBLE);
                        }
                        fileEnDecryptThreads.remove(currentThread());
                    }
                });
            }
        };
        thread.start();
    }

    public void cancelEnDecryptFileButton(View view) {
        for (Iterator<Thread> iterator = fileEnDecryptThreads.iterator(); iterator.hasNext();) {
            Thread thread = iterator.next();
            while (thread.isAlive() && !thread.isInterrupted()) {
                thread.stop();
                thread.interrupt();
            }
            iterator.remove();
        }
        mainView.findViewById(R.id.fileEnDecryptProgressBar).setVisibility(View.INVISIBLE);
    }

    //------------------------------------------------------------//

    public void fileDeleteChooseFiles(View view) {
        SecureDeleteFiles.chooseFiles(view);
    }

    public void deleteFileButton(final View view) {
        final ProgressBar fileEnDecryptProgressBar = mainView.findViewById(R.id.fileDeleteProgressBar);
        fileEnDecryptProgressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileDeleteThreads.add(currentThread());
                        SecureDeleteFiles.delete(view);
                        if (fileDeleteThreads.size() - 1 <= 0) {
                            fileEnDecryptProgressBar.setVisibility(View.INVISIBLE);
                        }
                        fileDeleteThreads.remove(currentThread());
                    }
                });
            }
        };
        thread.start();
    }

    public void cancelDeleteFileButton(View view) {
        for (Iterator<Thread> iterator = fileDeleteThreads.iterator(); iterator.hasNext();) {
            Thread thread = iterator.next();
            while (thread.isAlive() && !thread.isInterrupted()) {
                thread.stop();
                thread.interrupt();
            }
            iterator.remove();
        }
        mainView.findViewById(R.id.fileDeleteProgressBar).setVisibility(View.INVISIBLE);
    }

}
