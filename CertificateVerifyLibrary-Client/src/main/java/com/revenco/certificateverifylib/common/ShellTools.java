package com.revenco.certificateverifylib.common;

import android.app.Instrumentation;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ShellTools {
    public static final String TAG = "ShellTools";

    public static boolean runCmd(String... cmds) {
        if ((cmds == null) || (cmds.length == 0)) {
            return false;
        }
        boolean result = false;
        Process process = null;
        DataOutputStream outputStream = null;
        try {
            process = Runtime.getRuntime().exec("sh\n");
            OutputStream oStream = process.getOutputStream();
            outputStream = new DataOutputStream(oStream);
            for (String cmd : cmds) {
                outputStream.writeBytes(cmd);
            }
            outputStream.writeBytes("exit\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            result = process.waitFor() == 0;
            if (!result) {
                //test
                Log.e(TAG, "shell exit: " + process.exitValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static String runCmdAndReturn(String workDir, String... cmds) {
        String outStr = null;
        InputStream inStream = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmds);
            if (!TextUtils.isEmpty(workDir)) {
                processBuilder.directory(new File(workDir));
            }
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            inStream = process.getInputStream();
            byte[] re = new byte[1024];
            StringBuilder builder = new StringBuilder();
            int readLen = -1;
            while ((readLen = inStream.read(re)) != -1) {
                builder.append(new String(re, 0, readLen));
            }
            outStr = builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outStr;
    }

    public static boolean runCmd(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            return false;
        }
        boolean result = false;
        Process process = null;
        DataOutputStream outputStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream oStream = process.getOutputStream();
            outputStream = new DataOutputStream(oStream);
            outputStream.writeBytes(cmd);
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /*
     * args[0] : shell 命令 如"ls" 或"ls -1"; args[1] : 命令执行路径 如"/" ;
     * adb shell input keyevent 4
     *
     */
    public static String execute(String[] cmmand, String directory) {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(cmmand);
            if (directory != null)
                builder.directory(new File(directory));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            // 得到命令执行后的结果
            InputStream is = process.getInputStream();
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                result = result + new String(buffer);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            //执行失败代码
            result = "404";
        }
        return result;
    }

    /**
     * @param command command
     * @return null
     */
    public static String runCommand(String command) {
        BufferedReader br2 = null;
        String line = "";
        InputStream is = null;
        InputStreamReader isReader = null;
        try {
            Runtime.getRuntime().exec("su");
            Process process = Runtime.getRuntime().exec(command);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            return line;
        } finally {
            if (isReader != null) {
                try {
                    isReader.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (br2 != null) {
                try {
                    br2.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static void exec() {
        String[] cmdHeader = {"input", "keyevent", "4"};
//        String[] cmdHeader = {"/bin/bash", "-c", "/usr/local/bin/adb"};
        Runtime cmd = Runtime.getRuntime();
        Process p = null;
        try {
            p = cmd.exec(cmdHeader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            System.out.println(1);
            System.out.println(line);
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟键盘事件方法
     *
     * @param keyCode
     */
    public static void actionKey(final int keyCode, final onKeySendListener listener) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                    if (listener != null)
                        listener.onKeySendSucceed();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onKeySendFailed();
                }
            }
        }.start();
    }

    public interface onKeySendListener {
        void onKeySendSucceed();

        void onKeySendFailed();
    }
}
