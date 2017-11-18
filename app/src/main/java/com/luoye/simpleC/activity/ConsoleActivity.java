package com.luoye.simpleC.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.luoye.simpleC.R;
import com.luoye.simpleC.util.ConsoleSession;

import java.util.List;
import android.os.Handler;

import jackpal.term.emulatorview.EmulatorView;
import jackpal.term.emulatorview.TermSession;
import jackpal.term.emulatorview.compat.ClipboardManagerCompat;
import jackpal.term.emulatorview.compat.ClipboardManagerCompatFactory;

/**
 * Created by zyw on 2017/11/12.
 */
public class ConsoleActivity extends Activity implements TermSession.EOFCallback {
    Process process = null;
    private static final String TAG = "ConsoleActivity";
    private EmulatorView mEmulatorView;
    private ConsoleSession mSession;
    private final  int MSG_SESSION_FINISH=0x100;
    private  long currentTime=0L;
    private long startTime=0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);
        ActionBar actionBar=getActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mEmulatorView=(EmulatorView)findViewById(R.id.emulatorView) ;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mEmulatorView.setDensity(metrics);
        String cmd=getIntent().getStringExtra("bin");
        mSession = createLocalTermSession(cmd);
        mEmulatorView.setOnKeyListener(mKeyListener);
        mEmulatorView.setExtGestureListener(new EmulatorViewGestureListener(mEmulatorView));
        mEmulatorView.attachSession(mSession);
        mSession.setEOFCallback(this);
    }

    @Override
    public void onEOF() {
        handler.sendEmptyMessage(MSG_SESSION_FINISH);
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==MSG_SESSION_FINISH){
                mSession.setFinish(true);
                mSession.appendTextToEmulator("\n\n["+"程序执行完毕，耗时："+((double)currentTime-startTime)/1000+"s"+"]\n");
                mSession.update();
            }
        }
    };

    /**
     * Intercepts keys before the view/terminal gets it.
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return backkeyInterceptor(keyCode, event) || keyboardShortcuts(keyCode, event);
        }

        /**
         * Keyboard shortcuts (tab management, paste)
         */
        private boolean keyboardShortcuts(int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            boolean isCtrlPressed = (event.getMetaState() & KeyEvent.META_CTRL_ON) != 0;
            boolean isShiftPressed = (event.getMetaState() & KeyEvent.META_SHIFT_ON) != 0;

            if (keyCode == KeyEvent.KEYCODE_TAB && isCtrlPressed) {
                if (isShiftPressed) {
                    //mViewFlipper.showPrevious();
                } else {
                    //mViewFlipper.showNext();
                }

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_V && isCtrlPressed && isShiftPressed) {
                doPaste();

                return true;
            } else {
                return false;
            }
        }

        private boolean backkeyInterceptor(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK ) {
                onKeyUp(keyCode, event);

                return false;
            } else {
                return false;
            }
        }
    };
    private boolean canPaste() {
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        if (clip.hasText()) {
            return true;
        }
        return false;
    }

    private void doPaste() {
        if (!canPaste()) {
            return;
        }
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        CharSequence paste = clip.getText();
        mSession.write(paste.toString());
    }
    private class EmulatorViewGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private EmulatorView view;

        public EmulatorViewGestureListener(EmulatorView view) {
            this.view = view;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Let the EmulatorView handle taps if mouse tracking is active
            if (view.isMouseTrackingActive()) return false;

            doToggleSoftKeyboard();
            return true;
        }

    }
    /**
     *
     * Send a URL up to Android to be handled by a browser.
     * @param link The URL to be opened.
     */
    private void execURL(String link)
    {
        Uri webLink = Uri.parse(link);
        Intent openLink = new Intent(Intent.ACTION_VIEW, webLink);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(openLink, 0);
        if(handlers.size() > 0)
            startActivity(openLink);
    }
    private void doToggleSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }
    @Override
    protected void onResume() {
        super.onResume();

        /* You should call this to let EmulatorView know that it's visible
           on screen. */
        mEmulatorView.onResume();
    }

    @Override
    protected void onPause() {
        /* You should call this to let EmulatorView know that it's no longer
           visible on screen. */
        mEmulatorView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /**
         * Finish the TermSession when we're destroyed.  This will free
         * resources, stop I/O threads, and close the I/O streams attached
         * to the session.
         *
         * For the local session, closing the streams will kill the shell; for
         * the Telnet session, it closes the network connection.
         */
        if (mSession != null) {

            mSession.finish();
        }
        if(process !=null)
            process.destroy();
        super.onDestroy();
    }

    /**
     * Create a TermSession connected to a local shell.
     */
    private ConsoleSession createLocalTermSession(String bin) {
        /* Instantiate the TermSession ... */


        /* ... create a process ... */
        /* TODO:Make local session work without execpty.
        String execPath = LaunchActivity.getDataDir(this) + "/bin/execpty";
        ProcessBuilder execBuild =
                new ProcessBuilder(execPath, "/system/bin/sh", "-");
        */
        ProcessBuilder execBuild =
                new ProcessBuilder(bin);
        execBuild.redirectErrorStream(true);

        try {
            startTime=System.currentTimeMillis();
            process = execBuild.start();

        } catch (Exception e) {
            Log.e(TAG, "Could not start terminal process.", e);
            return null;
        }

        final ConsoleSession session = new ConsoleSession(process.getInputStream(), process.getOutputStream());
        /* You're done! */
        final Process finalExec = process;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalExec.waitFor();
                    currentTime=System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return session;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
