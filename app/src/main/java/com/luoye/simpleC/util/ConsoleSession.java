package com.luoye.simpleC.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

import jackpal.term.emulatorview.TermSession;
import jackpal.term.emulatorview.UpdateCallback;


public class ConsoleSession extends TermSession
{
    private static final String TAG = "TelnetSession";
    private static final boolean DEBUG = false;
    private ArrayList<Byte> list;
    private  final  int LF = 10;
    private  final  int CR = 13;
    private  boolean finish=false;

    public ConsoleSession(InputStream inputStream,OutputStream outputStream)
    {
        super();
        list=new ArrayList<>();
        setTermIn(inputStream);
        setTermOut(outputStream);
        setDefaultUTF8Mode(true);

    }

    public void setFinish(boolean finish)
    {
        this.finish=finish;
    }

    public boolean getFinish(){
        return finish;
    }


    /**
     * 数据通过用户进入模拟器
     * @param data An array of bytes to write to the terminal.
     * @param offset The offset into the array at which the data starts.
     * @param count The number of bytes to be written.
     */
    StringBuilder sb=new StringBuilder();
    @Override
    public void write(byte[] data, int offset, int count) {

        if(finish)
            return;

        if(DEBUG) {
            System.out.println("write1:"+data[offset]+","+count);
        }

        appendToEmulator(data,offset, count);

        notifyUpdate();

        for(int i=offset;i<count+offset;i++){
            if(DEBUG) {
                System.out.println("write2:"+data[i]);
            }
            list.add(data[i]);
            if(data[i]==CR){
                if(DEBUG) {
                    System.out.println("write3:"+list.toString());
                }
                byte[] temp=new byte[list.size()];
                Object[] objs=list.toArray();
                for (int j=0;j<objs.length;j++)
                {
                    temp[j]=(byte)objs[j];
                }
                super.write(temp,0,temp.length);
                list.clear();
            }

        }

    }


    public  void update()
    {
        notifyUpdate();
    }
    public  void appendTextToEmulator(String text){
        try {
            byte[] bytes=text.getBytes("UTF-8");
            appendDataToEmulator(bytes,0,bytes.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public  void appendDataToEmulator(byte[] data, int offset, int count){
        appendToEmulator(data, offset, count);

    }

    /**
     * 数据通过外部程序进入模拟器
     * @param data A byte array containing the data read.
     * @param offset The offset into the buffer where the read data begins.
     * @param count The number of bytes read.
     */
    @Override
    protected void processInput(byte[] data, int offset, int count) {
        if(DEBUG) {
            System.out.println("processInput:"+new String(data, offset, count));
        }

        appendDataToEmulator(data,offset,count);
        list.clear();

    }
}
