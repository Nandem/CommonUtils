package cn.nandem.cli;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Time: 10:55 PM
 * Date: 11/14/2018
 * Name: Nandem(nandem@126.com)
 * ----------------------------
 * Desc: 命令行工具，简化Java执行命令行的操作
 */
public class CommandLine
{
    /**
     * 同步执行cmd并输出信息
     * @param cmdBat 待执行的命令
     * @return 0：正常执行完毕，-1：执行异常
     */
    public int exec(String... cmdBat)
    {
        try
        {
            Process process = Runtime.getRuntime().exec("cmd");
            SequenceInputStream sis = new SequenceInputStream(process.getInputStream(),
                    process.getErrorStream());
            InputStreamReader isr = new InputStreamReader(sis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);

            for (String cmd : cmdBat)
            {
                bw.write(cmd);
                bw.newLine();
            }

            bw.flush();
            bw.close();
            osw.close();
            // read
            String line;
            while (null != (line = br.readLine()))
            {
                System.out.println(line);
            }
            process.destroy();
            br.close();
            isr.close();
            return 0;
        }
        catch (IOException e)
        {
            return -1;
        }
    }

    /**
     * 异步执行多个cmd并输出信息
     * @param cmdBat 待执行的命令
     * @param callback 执行完成后的回调
     */
    public void exec(String[] cmdBat, Consumer<Integer> callback)
    {
        new Thread(() -> callback.accept(exec(cmdBat))).start();
    }

    /**
     * 异步执行单个cmd并输出信息
     * @param cmdBat 待执行的命令
     * @param callback 执行完成后的回调
     */
    public void exec(String cmdBat, Consumer<Integer> callback)
    {
        new Thread(() -> callback.accept(exec(cmdBat))).start();
    }
}
