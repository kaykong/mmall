package top.kongk.mmall.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.kongk.mmall.service.FileService;
import top.kongk.mmall.util.FtpUtil;
import top.kongk.mmall.util.PropertiesUtil;

import java.io.File;
import java.util.UUID;

/**
 * 描述：文件上传用到的service实现
 *
 * @author kk
 * @date 2018/9/26 23:36
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile multipartFile, String path) {
        String fileName = multipartFile.getOriginalFilename();

        //获取扩展名
        int index = fileName.lastIndexOf(".");
        String fileExtension = null;
        if (index == -1 ) {
            //没有扩展名
            return null;
        }
        fileExtension = fileName.substring(index + 1);
        if (StringUtils.isBlank(fileExtension)) {
            //扩展名为空白
            return null;
        }

        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

        logger.info("开始上传文件: 上传文件名{}, 上传的路径{}, 新的文件名{}", fileName, path, newFileName);

        //根据path创建文件夹
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //给新建文件的权限,并根据路径创建文件夹(tomcat目录下)
            fileDir.setWritable(true);
            fileDir.mkdirs();
            logger.error("method: upload() : " + "创建文件夹成功");
        }

        //根据path和新的UUID文件名, 在tomcat上新建一个文件
        File targetFile = new File(path, newFileName);

        try {
            //文件上传到 tomcat 上
            multipartFile.transferTo(targetFile);
            //上传到 ftp 服务器
            FtpUtil.uploadToFtp(PropertiesUtil.getProperty("ftp.server.filePath.img"), Lists.newArrayList(targetFile));
            //把 tomcat 上的文件删除
            targetFile.delete();
        } catch (Exception e) {
            logger.error("FileServiceImpl.upload Execption", e);
            return null;
        }

        /**
         * 这里targetFile其实只是栈里的一个引用, 它不代表实际的文件
         * 所以即使它指向的文件被delete了,还是能该获取到名字
         */
        return targetFile.getName();
    }
}
