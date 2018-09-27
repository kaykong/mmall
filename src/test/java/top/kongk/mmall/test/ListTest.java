package top.kongk.mmall.test;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/27 22:43
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println(list.size());
        System.out.println("over");
    }
}
