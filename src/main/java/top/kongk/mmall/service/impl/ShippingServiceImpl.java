package top.kongk.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.ShippingMapper;
import top.kongk.mmall.pojo.Shipping;
import top.kongk.mmall.service.ShippingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：收货地址模块的 service 实现
 *
 * @author kk
 * @date 2018/9/29 17:38
 */
@Service
public class ShippingServiceImpl implements ShippingService{

    private static final Logger logger = LoggerFactory.getLogger(ShippingServiceImpl.class);

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {

        if (shipping == null) {
            return ServerResponse.createIllegalArgumentError();
        } else {
            shipping.setUserId(userId);
        }

        int count = 0;
        try {
            count = shippingMapper.insertSelective(shipping);
        } catch (Exception e) {
            logger.error("ShippingServiceImpl.add Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("新建地址失败");
        } else {
            Map map = new HashMap(1);
            map.put("shippingId", shipping.getId());
            return ServerResponse.createSuccess("新建地址成功", map);
        }
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {

        if (shippingId == null) {
            return ServerResponse.createIllegalArgumentError();
        }

        int count = 0;
        try {
            count = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        } catch (Exception e) {
            logger.error("ShippingServiceImpl.delete Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("删除地址失败");
        } else {
            return ServerResponse.createErrorWithMsg("删除地址成功");
        }
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {

        if (shipping == null || shipping.getId() == null) {
            return ServerResponse.createIllegalArgumentError();
        } else {
            shipping.setUserId(userId);
        }

        int count = 0;
        try {
            count = shippingMapper.updateByUserIdAndShippingIdSelective(shipping);
        } catch (Exception e) {
            logger.error("ShippingServiceImpl.update Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("更新地址失败");
        } else {
            return ServerResponse.createErrorWithMsg("更新地址成功");
        }
    }

    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ServerResponse.createIllegalArgumentError();
        }

        Shipping shipping = null;
        try {
            shipping = shippingMapper.selectByPrimaryKey(shippingId);
        } catch (Exception e) {
            logger.error("ShippingServiceImpl.select Execption", e);
        }
        if (shipping == null) {
            return ServerResponse.createErrorWithMsg("查找失败");
        } else {
            return ServerResponse.createSuccess(shipping);
        }
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = new ArrayList<>(1);
        try {
            shippings = shippingMapper.selectByUserId(userId);
        } catch (Exception e) {
            logger.error("ShippingServiceImpl.list Execption", e);
        }

        PageInfo pageInfo = new PageInfo(shippings);

        return ServerResponse.createSuccess(pageInfo);
    }
}
