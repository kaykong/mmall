package top.kongk.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.CategoryService;

import javax.servlet.http.HttpSession;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/25 10:45
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    CategoryService categoryService;

    /**
     * 描述：根据父类id,获取他的第一层孩子
     *
     * @param parentId 父类id
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value = "categoryId", defaultValue = "0")
                                              Integer parentId, HttpSession session) {
        System.out.println(parentId);
        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请重新登录");
        }

        return categoryService.selectCategoriesByParentId(parentId);
    }



    /**
     * 描述：根据父节点的id,商品类别名称添加新的商品类别
     *
     * @param parentId 父节点的id
     * @param categoryName 商品类别名称
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value = "parentId", defaultValue = "0")
                                              Integer parentId, String categoryName, HttpSession session) {
        System.out.println(parentId);
        System.out.println(categoryName);
        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请重新登录");
        }

        return categoryService.addCategory(parentId, categoryName);
    }


    @RequestMapping(value = "/set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId, String categoryName, HttpSession session) {
        System.out.println(categoryId);
        System.out.println(categoryName);

        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请重新登录");
        }

        return categoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "/get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId, HttpSession session) {
        System.out.println(categoryId);

        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请重新登录");
        }

        return categoryService.getDeepCategory(categoryId);
    }

}
