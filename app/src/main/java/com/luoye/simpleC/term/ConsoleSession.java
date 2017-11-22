package com.luoye.simpleC.term;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.AlertDialog;
import android.util.Log;

import com.termux.terminal.TerminalSession;


public class ConsoleSession
{
    private static final String TAG = "TelnetSession";
    private  TermuxService mTermuxService;
    private static final boolean DEBUG = false;
    private ArrayList<Byte> list;
    private  final  int LF = 10;
    private  final  int CR = 13;
    private  boolean finish=false;
    private  TerminalSession terminalSession;
    public ConsoleSession(InputStream inputStream,OutputStream outputStream)
    {
        super();
        list=new ArrayList<>();

    }

    public void setFinish(boolean finish)
    {
        this.finish=finish;
    }

    public boolean getFinish(){
        return finish;
    }


}
