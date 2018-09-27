package top.kongk.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 描述：上传图片文件到ftp服务器的工具类
 *
 * @author kk
 * @date 2018/9/27 8:13
 */
public class FtpUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public FtpUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    private boolean connectServer(String ip, int port, String user, String password) {
        boolean login = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            login = ftpClient.login(user, password);
        } catch (Exception e) {
            logger.error("FtpUtil.connectServer Execption", e);
        }

        return login;
    }

    public static boolean uploadToFtp(String romotePath, List<File> fileList) throws IOException {
        if (CollectionUtils.isEmpty(fileList)) {
            return false;
        }

        FtpUtil ftpUtil = new FtpUtil(ftpIp, 21, ftpUser, ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile(romotePath, fileList);
        logger.info("上传文件到ftp服务器结束, 上传结果{}", result);

        return result;
    }


    private boolean uploadFile(String romotePath, List<File> fileList) throws IOException {
        //初始化为false
        boolean isSuccess = false;
        FileInputStream fis = null;
        if (connectServer(this.ip, this.port, this.user, this.password)) {
            try {
                //在调用connectServer的时候ftpClient已经初始化了
                //在ftp服务器根目录下创建一个文件夹 romotePath
                ftpClient.changeWorkingDirectory(romotePath);
                /**
                 * 设置每次读取文件流时缓存数组的大小。
                 * 上传或者下载都是先将文件流拿到，然后将文件流一点一点的读到缓存，
                 * 然后程序再从缓存将所需的内容读取出来放进要导入的文件中。
                 */
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");

                //把文件类型设置成二进制的类型, 可以防止一些乱码的问题
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                /**
                 * 这个方法的意思就是每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据。
                 * 为什么要这样做呢，因为ftp server可能每次开启不同的端口来传输数据，
                 * 但是在linux上，由于安全限制，可能某些端口没有开启，所以就出现阻塞。
                 */
                ftpClient.enterLocalPassiveMode();

                for (File file : fileList) {
                    fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fis);
                }

                //走到这一步说明上传成功了
                isSuccess = true;
            } catch (IOException e) {
                logger.error("method: uploadFile() : " + e);
            } finally {
                //登出ftp服务器
                ftpClient.logout();
                //如果关闭连接失败, 在此抛出异常
                if (fis != null) {
                    fis.close();
                }
                ftpClient.disconnect();
            }
        } else {
            logger.error("method: uploadToFtp() : 连接ftp服务器错误!");
            isSuccess = false;
        }

        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
