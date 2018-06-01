package cn.nandem.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class PropertiesReader
{
    private Map<String, String> varKey2ValueMapping = new HashMap<>();

    private Properties props = new Properties();

    public PropertiesReader()
    {
    }

    public PropertiesReader(String path)
    {
        read(path);
    }

    /**
     * 读取配置文件
     * @param path 配置文件路径
     */
    public void read(String path)
    {
        try
        {
            props.load(new FileInputStream(new File(path)));
            Enumeration enu2 = props.propertyNames();
            while(enu2.hasMoreElements())
            {
                String key = (String)enu2.nextElement();
                varKey2ValueMapping.put("${" + key + "}", props.getProperty(key));
            }
        }
        catch (IOException e)
        {
            System.err.println("配置文件读取错误，请检查路径。");
        }
    }

    /**
     * 根据key以字符串形式获取配置值
     * @param key 配置值的key
     * @return 配置值
     */
    public String getString(String key)
    {
        return getReplacedString(props.getProperty(key, ""));
    }

    /**
     * 根据key以整数形式获取配置值
     * @param key 配置值的key
     * @return 配置值
     */
    public Integer getInteger(String key)
    {
        return Integer.valueOf(props.getProperty(key, "0"));
    }

    /**
     * 获取传入的字符串中的所有"${parm}"形式的变量名
     * @param rawStr 需要提取变量名的字符串
     * @return 传入字符串中包含的变量名列表
     */
    private List<String> getVarNamesInString(String rawStr)
    {
        List<Character> valNameCharList = new ArrayList<>();
        if(rawStr.contains("${"))
        {
            char[] charArr = rawStr.toCharArray();
            boolean flag = false;
            for (char c : charArr)
            {
                if('$' == c)
                {
                    flag = true;
                }
                else if('}' == c)
                {
                    flag = false;
                    valNameCharList.add('}');
                    valNameCharList.add('#');
                }

                if(flag)
                {
                    valNameCharList.add(c);
                }
            }
        }

        StringBuilder valNameStr = new StringBuilder();
        for (Character c : valNameCharList)
        {
            valNameStr.append(c);
        }

        return new ArrayList<>(Arrays.asList(valNameStr.toString().split("#")));
    }

    /**
     * 将传入字符串中的所有变量替换为相应的值
     * @param rawStr 需要替换的字符串
     * @return 没有变量的配置值
     */
    private String getReplacedString(String rawStr)
    {
        List<String> list = getVarNamesInString(rawStr);
        for (String s : list)
        {
            String varValue;
            try
            {
                varValue = varKey2ValueMapping.get(s);
                rawStr = rawStr.replace(s, varValue);
            }
            catch (NullPointerException e)
            {
                System.err.println("未定义变量：" + s);
            }
        }

        if(rawStr.contains("${"))
        {
            rawStr = getReplacedString(rawStr);
        }

        return rawStr.replace("//", "/");//这里用正则去掉多余的斜杠，防止出现"D:/A//B/C//D"的情况
    }
}
