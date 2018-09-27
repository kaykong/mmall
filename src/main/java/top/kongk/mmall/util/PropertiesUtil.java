package top.kongk.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 描述：获取配置文件信息的工具类
 *
 * @author kk
 * @date 2018/9/26 13:12
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;
    /**
     * 静态代码块优先于普通代码块优先于构造函数
     */
    static {
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(
                     PropertiesUtil.class.getClassLoader().getResourceAsStream("mmall.properties"),
                    "UTF-8"));
        } catch (IOException e) {
            logger.error("method: static initializer() : " + e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    public static void main(String[] args) {
        String value = PropertiesUtil.getProperty("ftp.server.http.prefix");
        System.out.println(value);
    }
}
