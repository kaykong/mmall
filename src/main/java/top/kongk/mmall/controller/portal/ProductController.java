package top.kongk.mmall.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.service.ProductService;

/**
 * 描述：前台的产品 controller
 *
 * @author kk
 * @date 2018/9/27 14:44
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDetail(Integer productId) {
        return productService.getDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse list(Integer categoryId, String keyword,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               String orderBy) {
        return productService.list(categoryId, keyword, pageNum, pageSize, orderBy);
    }

}
