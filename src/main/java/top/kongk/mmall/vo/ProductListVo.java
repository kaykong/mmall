package top.kongk.mmall.vo;

import top.kongk.mmall.pojo.Product;
import top.kongk.mmall.util.PropertiesUtil;

import java.math.BigDecimal;

/**
 * 描述：产品列表需要的vo
 *
 * @author kk
 * @date 2018/9/26 12:57
 */
public class ProductListVo {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

    private static String imgHost;

    static {
        imgHost = PropertiesUtil.getProperty("ftp.server.http.prefix");
    }

    public ProductListVo() {
    }

    public ProductListVo(Product product) {
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.mainImage = product.getMainImage();

        this.price = product.getPrice();
        this.status = product.getStatus();
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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgHost() {
        return imgHost;
    }

    public static void setImgHost(String imgHost) {
        ProductListVo.imgHost = imgHost;
    }

    public static void main(String[] args) {
        /*for (int i = 1; i < 3; ++i) {
            ProductListVo productListVo = new ProductListVo();
        }*/
    }
}
