package top.kongk.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 描述：文件上传用到的service
 *
 * @author kk
 * @date 2018/9/26 23:36
 */
public interface FileService {



    /**
     * 描述：把文件上传到Ftp服务器上
     *
     * @param file 上传的文件
     * @param path 上传的路径
     * @return java.lang.String 返回null代表上传失败, 否则是上传后文件的名字
     */
    String upload(MultipartFile file, String path);

}
