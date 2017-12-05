package com.luoye.simpleC.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


public class ShellUtils {

    public static final String COMMAND_SU       = "su";
    public static final String COMMAND_SH       = "sh";
    public static final String COMMAND_EXIT     = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
        throw new AssertionError();
    }

    /**
     * 检查是否有root的权限
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] {command}, isRoot, true,null);
    }

    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true,null);
    }

    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true,null);
    }

    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[] {command}, isRoot, isNeedResultMsg,null);
    }

    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg,null);
    }


    /**
     * 执行shell命令，默认返回执行结果
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg,String[] env) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        //BufferedReader errorResult = null;
        StringBuilder msg = null;
       // StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH,env);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                msg = new StringBuilder();
                //errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                //errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s=null;
                while ((s = successResult.readLine()) != null) {
                    msg.append(s);
                    msg.append("\n");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if(msg!=null)
                msg.append(e.toString());
        }  finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, msg==null?null:msg.toString());
    }
    /**
     * 携带环境变量执行shell命令
     */
    public static CommandResult execCommand(String command,List<String> args,Map<String,String> env) {
        int result = -1;
        if (command == null || command.length() == 0) {
            return new CommandResult(result, null);
        }

        Process process = null;
        BufferedReader inputBufferedReader = null;
        StringBuilder msg = null;
        try {
            ProcessBuilder processBuilder=new ProcessBuilder(command);
           List<String> argsList=processBuilder.command();
            for(String arg:args)
            {
                argsList.add(arg);
            }
            processBuilder.redirectErrorStream(true);//合并输入流和错误流
            Map<String,String> map=processBuilder.environment();
            map.clear();
            for (Map.Entry<String,String> entry:env.entrySet())
            {
                map.put(entry.getKey(),entry.getValue());
            }
            process=processBuilder.start();
            result = process.waitFor();
             msg = new StringBuilder();

            inputBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String s=null;
            while ((s = inputBufferedReader.readLine()) != null) {
                msg.append(s);
                msg.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(msg!=null)
                msg.append(e.toString());
        }  finally {
            try {
                if (inputBufferedReader != null) {
                    inputBufferedReader.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, msg==null?"":msg.toString());
    }
    /**
     * 命令结果类
     */
    public static class CommandResult {

        public int    result;

        public String getMsg() {
            return msg;
        }

        public String msg;


        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String msg) {
            this.result = result;
            this.msg=msg;
        }
    }
}