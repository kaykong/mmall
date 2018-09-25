##一. insert语句的总结
**插入的时候，如果插入了自增的id**
- id值如果在该表中不存在，并且**其他字段没有错误**的话，mysql 会插入该行数据。
  - 如果插入的 id 的值大于表自增的值，那么表自增的值就更新到这个id值。例如，表的自增主键在被**手动插入**后的最大的 id 值为100，那么下一个**自动插入**的 id 就递增到了101。
- id值在该表中已经存在了，那么就会报异常org.springframework.dao.DuplicateKeyException，我们应该 try-catch 异常，根据是否捕获到异常来决定接下来怎么做。

**代码演示: **
```
    @Test
    public void insertSelectiveWithoutTime() throws Exception {
        //RandomUser.getNormalUser() 没有设置createTime, updateTime 
        //它们都为null
        User user = RandomUser.getNormalUser();

        //故意设置一个重复的 自增id，那么会捕获到异常，count 将为 0
        //user.setId(90);
        System.out.println(user);

        int count = 0;
        try {
            count = userMapper.insertSelectiveWithoutTime(user);
        }catch (Exception e) {
            System.out.println("出现了异常: " + e);
        }

        System.out.println("count: " + count);
    }
```
 
##二. 对 mybatis 逆向工程生成的 xml 的改进
#### 问题缘由：
> ###### &ensp;&ensp; 项目中数据库的每一张表都包含创建时间和更新时间字段，为什么所有的表都需要这两个字段呢？<br/>
>- **好处**：不仅可以简单的记录下操作时间，而且也可以用于以后数据分析中。<br/>
>- **必要性：**
>    - 创建时间：大部分数据在创建了之后就不会更新了。
>    - 更新时间：如果数据需要大量的更新，那么我们也不可能记录每一次的更新时间，最好的方式是记录最后一次的更新时间。


#### 思考点：
>##### 1.创建时间和更新时间有必要交给 java 代码来做吗？
>##### 2.让数据库去做这个是不是更适合，更方便些？
<br>


##### 对 mybatis 逆向工程生成的映射文件的改进
&ensp;&ensp; 


