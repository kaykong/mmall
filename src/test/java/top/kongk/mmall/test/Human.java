package top.kongk.mmall.test;

/**
 * 描述：测试继承属性
 *
 * @author kk
 * @date 2018/9/29 7:56
 */
public class Human {

    private String name;

    public Human(String name) {
        this.name = name + " Human Constructor";
        System.out.println(this.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void testName(String name) {
        this.name = name + " Human";
        System.out.println(this.name);
    }
}
