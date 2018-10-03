package top.kongk.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.Product;
import top.kongk.mmall.service.FileService;
import top.kongk.mmall.service.ProductService;
import top.kongk.mmall.util.PropertiesUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：后台产品 controller
 *
 * @author kk
 * @date 2018/9/26 11:08
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;

    /**
     * 描述：产品list
     *
     * @param session  session
     * @param pageNum  第几页, 默认第一页
     * @param pageSize 每一页显示多少个, 默认显示10个
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }
        System.out.println("pageNum:" + pageNum);
        System.out.println("pageSize:" + pageSize);

        return productService.getManageProductList(pageNum, pageSize);
    }

    /**
     * 描述：根据产品名和产品id 搜索
     *
     * @param session     session
     * @param productName 产品名
     * @param productId   产品id
     * @param pageNum     第几页
     * @param pageSize    每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse search(HttpSession session, String productName, Integer productId,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }

        return productService.search(productName, productId, pageNum, pageSize);
    }

    /**
     * 描述：新增OR更新产品
     *
     * @param session session
     * @param product 产品
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ServerResponse insertOrUpdate(HttpSession session, Product product) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }

        return productService.insertOrUpdate(product);
    }

    /**
     * 描述：更改产品状态(在售1,下架2,删除3)
     *
     * @param session   session
     * @param productId 产品id
     * @param status    状态码
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }

        return productService.setSaleStatus(productId, status);
    }

    /**
     * 描述：获取产品详情
     *
     * @param session   session
     * @param productId 产品id
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }

        return productService.getManageDetail(productId);
    }

    /**
     * 描述：上传图片文件到ftp服务器
     *
     * @param session       session检查登录
     * @param multipartFile 上传的文件
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile multipartFile) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            return ServerResponse.createNeedloginError("管理用户未登录,请登录");
        }

        //只是一个项目路径
        String path = session.getServletContext().getRealPath("mmallUpload");

        String targetName = fileService.upload(multipartFile, path);
        if (targetName == null) {
            return ServerResponse.createErrorWithMsg("上传文件失败");
        } else {
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix");
            Map<String, String> map = new HashMap<>(2);
            map.put("uri", targetName);
            map.put("url", url + PropertiesUtil.getProperty("ftp.server.filePath.img") + targetName);
            return ServerResponse.createSuccess(map);
        }
    }

    /**
     * 描述：在富文本中上传图片文件到ftp服务器, 注意上传成功后要添加header信息
     *
     * @param session       session检查登录
     * @param multipartFile 上传的文件
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session,
                                            @RequestParam(value = "upload_file", required = false) MultipartFile multipartFile,
                                            HttpServletResponse response) {

        if (session.getAttribute(Const.CURRENT_ADMIN) == null) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("success", false);
            map.put("msg", "请使用管理员用户登录");
            return map;
        }

        //只是一个项目路径
        String path = session.getServletContext().getRealPath("mmallUpload");

        String targetName = fileService.upload(multipartFile, path);
        if (targetName == null) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("success", false);
            map.put("msg", "上传失败");
            return map;
        } else {
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix");
            Map<String, Object> map = new HashMap<>(2);
            map.put("file_path", url + targetName);
            map.put("msg", "上传成功");
            map.put("success", true);

            /**
             * 在上传成功后, 要在response的header中添加信息
             */
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return map;
        }
    }

}
