package top.kongk.mmall.vo;

import top.kongk.mmall.pojo.Shipping;

/**
 * 描述：创建订单功能, 前台需要的收货地址 shippingVo
 *
 * @author kk
 * @date 2018/10/3 15:18
 */
public class ShippingVo {

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;

    public ShippingVo() {
    }

    public ShippingVo(Shipping shipping) {
        this.receiverName = shipping.getReceiverName();
        this.receiverPhone = shipping.getReceiverPhone();
        this.receiverMobile = shipping.getReceiverMobile();
        this.receiverProvince = shipping.getReceiverProvince();
        this.receiverCity = shipping.getReceiverCity();
        this.receiverDistrict = shipping.getReceiverDistrict();
        this.receiverAddress = shipping.getReceiverAddress();
        this.receiverZip = shipping.getReceiverZip();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }
}
