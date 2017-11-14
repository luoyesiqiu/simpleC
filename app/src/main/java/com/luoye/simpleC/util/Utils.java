package com.luoye.simpleC.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import com.luoye.simpleC.activity.ConsoleActivity;
import com.luoye.simpleC.interfaces.CompileCallback;
import com.luoye.simpleC.interfaces.ExecCallback;
import com.luoye.simpleC.interfaces.UnzipCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by zyw on 2017/9/28.
 */


public class Utils {

    /**
     * 运行二进制
     * @param context
     */
    public  static  void execBin(Context context)
    {
        File f = context.getFilesDir();
        String cmd = "." + f.getAbsolutePath() + File.separator + "temp.o";
        Intent intent =
                new Intent(context, ConsoleActivity.class);
        intent.putExtra("bin", cmd);
        context.startActivity(intent);
    }

    /**
     * 获取头文件
     * @param context
     */
    public static ArrayList<String> getHeader(Context context)
    {
        ArrayList<String> list=new ArrayList<>();
        InputStream inputStream=null;
        try {
             inputStream= context.getAssets().open("header");
            BufferedReader bufferedInputStream=new BufferedReader(new InputStreamReader(inputStream));
            String line=null;
            while ((line=bufferedInputStream.readLine())!=null){
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return  list;
    }
    /**
     * 将Assets文件写出
     * @param context
     * @param assetName
     * @param outputDir
     * @param outputFileName
     * @throws IOException
     */
    public  static   void writeFile(Context context, final  String assetName,File outputDir,String outputFileName) throws IOException {
        AssetManager assetManager=context.getAssets();
        InputStream inputStream=assetManager.open(assetName);
        if(!outputDir.exists())
            outputDir.mkdirs();
        FileOutputStream fileOutputStream=new FileOutputStream(outputDir.getAbsolutePath()+File.separator+outputFileName);
        byte[] buf=new byte[8092];
        int len=-1;
        while ((len=inputStream.read(buf))!=-1)
        {
            fileOutputStream.write(buf,0,len);
            fileOutputStream.flush();
        }
        if(inputStream!=null)
            inputStream.close();
        if(fileOutputStream!=null)
            fileOutputStream.close();
    }
    /**
     * 写文件
     * @param text
     * @param outputFile
     * @throws IOException
     */
    public  static   void writeFile( final  String text,File outputFile) throws IOException {

        if(!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();
        FileOutputStream fileOutputStream=new FileOutputStream(outputFile);
        fileOutputStream.write(text.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 复制文件
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public  static   void copyFile( final  File inputFile,File outputFile) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(inputFile);

        if(!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();
        FileOutputStream fileOutputStream=new FileOutputStream(outputFile);
        byte[] buf=new byte[8092];
        int len=-1;
        while ((len=fileInputStream.read(buf))!=-1)
        {
            fileOutputStream.write(buf,0,len);
            fileOutputStream.flush();
        }
        if(fileInputStream!=null)
            fileInputStream.close();
        if(fileOutputStream!=null)
            fileOutputStream.close();
    }
    /**
     * 运行二进制文件
     * @param context
     * @param src
     */
    public static void execBin(Context context, File src,String args, ExecCallback execCallback)
    {
        File f=context.getFilesDir();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(".");
        stringBuilder.append(src.getAbsolutePath());
        stringBuilder.append(" "+args);
        //System.out.println("-------------------->"+stringBuilder.toString());
        ShellUtils.CommandResult result=ShellUtils.execCommand(stringBuilder.toString(),false);

        if(execCallback!=null)
            execCallback.onResult(result);
    }
    /**
     * 编译代码
     * @param context
     * @param src
     */
    public static void compile(Context context, File[] src,CompileCallback compileCallback)
    {
        File internalDir=context.getFilesDir();

        StringBuilder filesString=new StringBuilder();
        for(int i=0;i<src.length;i++)
            filesString.append(src[i]+" ");
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(".");
        stringBuilder.append(internalDir.getAbsolutePath()+File.separator);
        stringBuilder.append("tcc");
        stringBuilder.append(" "+filesString);
        stringBuilder.append(" -I"+internalDir.getAbsolutePath()+File.separator+"include");
        stringBuilder.append(" -L");
        stringBuilder.append(internalDir.getAbsolutePath()+File.separator+"lib");
        stringBuilder.append(" -static -ldl -lm -lrt -lpthread -lcrypt ");
        stringBuilder.append(" -o ");
        stringBuilder.append(internalDir.getAbsolutePath()+File.separator+"temp.o");
        stringBuilder.append(" "+internalDir.getAbsolutePath()+File.separator+"lib"+File.separator+"fix.o");
        //System.out.println("-------------------->"+stringBuilder.toString());
        ShellUtils.CommandResult result=ShellUtils.execCommand(stringBuilder.toString(),false);
        if(compileCallback!=null)
            compileCallback.onCompileResult(result);
    }

    /**
     * 更改某个文件为可执行
     * @param file
     */
    public static  void changeToExecutable(File file)
    {
        ShellUtils.execCommand("chmod 777 "+file.getAbsolutePath(),false);
    }
    /**
     * 解压文件
     * @param srcIn
     * @param targetDir
     * @param unzipCallback  解压回调
     * @return
     */
    public static  void unzip(InputStream srcIn, File targetDir,UnzipCallback unzipCallback)
    {
        ZipInputStream zipInputStream=null;
        try {
            zipInputStream = new ZipInputStream(srcIn);
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                //System.out.println("------------>:"+zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    File file=new File(targetDir+File.separator+zipEntry.getName());
                    file.mkdir();
                } else {
                        byte[] buf = new byte[1024];
                        FileOutputStream fileOutputStream = new FileOutputStream(targetDir + File.separator + zipEntry.getName());
                        int len = 0;
                        while ((len = zipInputStream.read(buf, 0, buf.length)) != -1) {
                            fileOutputStream.write(buf, 0, len);
                            fileOutputStream.flush();
                        }
                        zipInputStream.closeEntry();
                        fileOutputStream.close();
                    }
                }
            if(unzipCallback!=null)
                unzipCallback.onResult(true);
            }catch(IOException e){
                e.printStackTrace();
                if(unzipCallback!=null)
                    unzipCallback.onResult(false);
            }
            finally{
                if (zipInputStream != null)
                    try {
                        zipInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

    }
    /**
     * 获取app版本号
     * @return 当前应用的版本号
     */
    public static String getAppVersion(Context context) {
        String version=null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
             version = info.versionName;
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
            version="0.0.0";
        }
        return version;
    }
}
