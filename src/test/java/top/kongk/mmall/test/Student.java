package top.kongk.mmall.test;

/**
 * 描述：学生子类
 *
 * @author kk
 * @date 2018/9/29 7:59
 */
public class Student extends Human {

    private String name;

    public Student(String name) {
        super(name);
        this.name = name + " Student Constructor";
        System.out.println(this.name);
    }

    @Override
    public String getName() {
        return name;
    }

    public void testName(String name) {
        this.name = name + " Student";
        System.out.println(this.name);
    }
}
