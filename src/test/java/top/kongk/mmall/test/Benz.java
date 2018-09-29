package top.kongk.mmall.test;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/29 8:14
 */
public class Benz extends Car {
    public String name = "Benz erzi";

    @Override
    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        Car car = new Benz();
        System.out.println(car.name);
    }
}
