package com.chaoxing;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

    private static Properties props;
    private static final String propertieFile = "/Users/hechen/lucenenews/application.properties";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class); 

    private PropertiesUtil(String fileName) {
        
    }
    
    static{
        try {
            props = new Properties();
            InputStream fis = PropertiesUtil.class.getResourceAsStream(propertieFile);
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readProperties(String fileName) {
        try {
            props = new Properties();
            InputStream fis = PropertiesUtil.class.getResourceAsStream(fileName);
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某个属性
     * @throws UnsupportedEncodingException 
     */
    public static String getValue(String key) {
        if (props == null) {
            readProperties(propertieFile);
        }
        try {
            
            LOGGER.info("获取属性 {}",key);
            String tmp = new String(props.getProperty(key).getBytes("ISO8859-1"), "UTF8");
            LOGGER.info("获取属性 {}:{}", key, tmp);
            return tmp;
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("编码格式不支持:{}",e.getMessage());
            return "";
        } 
    }

    /**
     * 获取所有属性，返回一个map,不常用 可以试试props.putAll(t)
     */
    public Map getAllProperty() {
        if (props == null) {
            readProperties(propertieFile);
        }
        
        Map map = new HashMap();
        Enumeration enu = props.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = props.getProperty(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 在控制台上打印出所有属性，调试时用。
     */
    public void printProperties() {
        props.list(System.out);
    }

    /*public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(PropertiesUtil.getProperty("loginUsername"));
    }*/
}