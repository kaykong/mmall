package top.kongk.mmall.vo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import top.kongk.mmall.pojo.Product;
import top.kongk.mmall.util.DateTimeUtil;

import java.math.BigDecimal;

/**
 * 描述：产品详情 vo
 *
 * @author kk
 * @date 2018/9/26 12:52
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ProductDetailVo {
    private Integer id;

    private Integer categoryId;

    /**
     * 新增的
     */
    private Integer parentCategoryId;

    private String name;

    private String subtitle;

    /**
     * 新增的
     */
    private String imageHost;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String createTime;

    private String updateTime;

    public ProductDetailVo() {
    }

    public ProductDetailVo(Product product) {
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.mainImage = product.getMainImage();
        this.subImages = product.getSubImages();
        this.detail = product.getDetail();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus();
        //使用工具类把Date转化为String, 这样在前台看到的就不是一串数字了
        this.createTime = DateTimeUtil.dateToString(product.getCreateTime());
        this.updateTime = DateTimeUtil.dateToString(product.getUpdateTime());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
