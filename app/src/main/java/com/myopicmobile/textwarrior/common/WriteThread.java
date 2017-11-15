package com.myopicmobile.textwarrior.common;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zyw on 2017/10/2.
 */
public class WriteThread extends  Thread {

    public static final int MSG_WRITE_OK =0x201;
    public static final int MSG_WRITE_FAIL =0x202;
    private Handler handler;
    private  String outputPath;
    private  String text;
    public WriteThread(String text,String outputPath, Handler handler)
    {
        this.outputPath=outputPath;
        this.text=text;
        this.handler=handler;
    }
    @Override
    public void run() {
        writeFile(text,new File(outputPath));
    }

    /**
     * 写文件
     * @param text
     * @param outputFile
     * @throws IOException
     */
    private   void writeFile( final  String text,File outputFile)  {
        boolean isOk=false;
        if(!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream=new FileOutputStream(outputFile);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.flush();
            isOk=true;
        }catch (IOException e){
            e.printStackTrace();
            isOk=false;

        }finally {
            if(fileOutputStream!=null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if(isOk) {
            if (handler != null)
                handler.sendMessage(Message.obtain(handler, MSG_WRITE_OK));
        }
        else
        {
            if(handler!=null)
                handler.sendMessage(Message.obtain(handler, MSG_WRITE_FAIL));
        }
    }
}
