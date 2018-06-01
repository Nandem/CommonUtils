package cn.nandem;

import cn.nandem.properties.PropertiesReader;

public class MainTester
{
    public static void main(String[] args)
    {
        PropertiesReader pr = new PropertiesReader("E:\\Java\\hht\\HHT-Runtime\\path.properties");
        System.out.println(pr.getString("hahaPath"));
    }
}
