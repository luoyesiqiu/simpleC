package com.myopicmobile.textwarrior.common;

import android.os.Handler;
import android.os.Message;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by zyw on 2017/10/2.
 */
public class ReadThread extends Thread
{
	public static final int MSG_READ_OK =0x101;
	public static final   int MSG_READ_FAIL =0x102;
	private  Handler handler;
	private  String path;
	public ReadThread(String path, Handler handler)
	{
		this.path=path;
		this.handler=handler;
	}
	@Override
	public void run() {
		readFile(path);
	}

	private  void readFile(String file)
	{
		boolean isOk=false;
		FileInputStream fileInputStream = null;
		StringBuilder stringBuilder=new StringBuilder();
		try {
			fileInputStream=new FileInputStream(file);
			byte[] buf=new byte[1024];
			int len=0;
			while ((len=fileInputStream.read(buf))!=-1){
				stringBuilder.append(new String(buf,0,len));
			}
			isOk=true;

		} catch (IOException e) {
			e.printStackTrace();
			isOk=false;
		}finally {
			if(fileInputStream!=null)
			{
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(isOk)
		{
			if(handler!=null)
			handler.sendMessage(Message.obtain(handler, MSG_READ_OK,stringBuilder.toString()));
		}else
		{
			if(handler!=null)
			handler.sendMessage(Message.obtain(handler, MSG_READ_FAIL));
		}

	}
}
