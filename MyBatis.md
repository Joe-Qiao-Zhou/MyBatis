# 一、入门

## 1.概述

- 三层架构与框架的关系

  ![image-20220404193825067](C:\Users\91494\AppData\Roaming\Typora\typora-user-images\image-20220404193825067.png)

- 持久层技术解决方案

  - JDBC技术：Connection、PreparedStatement、ResultSet
  - Spring的JdbcTemplate：Spring对jdbc的简单封装
  - Apache的DBUtils

  但以上都不是框架，JDBC是规范，后两者是工具类

- MyBatis

  - 内部封装了jdbc，只需要关注sql语句本身，省去了加载驱动、创建连接等过程
  
  - 通过xml或注解的方式配置各种sql语句即statement，省去代码修改部分，并使用ORM思想实现对结果集的封装
    
    > ORM：Object Relational Mapping 对象关系映射，将表与实体类及其属性对应，通过操作实体类来操作表

## 2.环境搭建

1. 选择maven工程，不选骨架

2. 创建数据库表

3. 设置打包方式为jar包

4. 导入mybatis坐标

5. 导入mysql8.0.11、log4j1.2.12、junit4.10坐标

   [标红解决方法](https://blog.csdn.net/xc_zhou/article/details/121802937)

   ```xml
   <!--pom.xml-->
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>zhouqiao</groupId>
       <artifactId>mybatis_dynamicSQL</artifactId>
       <version>1.0-SNAPSHOT</version>
       <!--3.设置打包方式-->
       <packaging>jar</packaging>
       <!--4.导入mybatis坐标-->
       <dependencies>
           <dependency>
               <groupId>org.mybatis</groupId>
               <artifactId>mybatis</artifactId>
               <version>3.4.5</version>
           </dependency>
           <!--5.导入其他依赖的坐标-->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>8.0.11</version>
           </dependency>
           <dependency>
               <groupId>log4j</groupId>
               <artifactId>log4j</artifactId>
               <version>1.2.12</version>
           </dependency>
           <dependency>
               <groupId>junit</groupId>
               <artifactId>junit</artifactId>
               <version>4.10</version>
               <scope>test</scope>
           </dependency>
       </dependencies>
   </project>
   ```

6. 导入`log4j.properties`文件

   ```properties
   # Set root category priority to INFO and its only appender to CONSOLE.
   #log4j.rootCategory=INFO, CONSOLE            debug   info   warn error fatal
   log4j.rootCategory=debug, CONSOLE, LOGFILE
   
   # Set the enterprise logger category to FATAL and its only appender to CONSOLE.
   log4j.logger.org.apache.axis.enterprise=FATAL, CONSOLE
   
   # CONSOLE is set to be a ConsoleAppender using a PatternLayout.
   log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
   log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
   log4j.appender.CONSOLE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n
   
   # LOGFILE is set to be a File appender using a PatternLayout.
   log4j.appender.LOGFILE=org.apache.log4j.FileAppender
   log4j.appender.LOGFILE.File=d:\axis.log
   log4j.appender.LOGFILE.Append=true
   log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
   log4j.appender.LOGFILE.layout.ConversionPattern=%d{ISO8601} %-6r [%15.15t] %-5p %30.30c %x - %m\n
   ```

7. 创建`User`类实现`Serializable`接口，属性名与表列名要保持一致

   > 序列化：把对象转换为字节序列的过程。当需要通过网络传输对象的状态信息，或者将对象的状态信息持久化以便将来使用时，都需要把对象进行序列化。接口里面无内容，只是用来通知JVM进行序列化。

8. 创建`UserDao`接口，定义`findAll()`方法，返回`List<user>`

9. `resources`下创建mybatis的主配置文件`SqlMapConfig.xml`，并导入约束

10. 配置环境

11. 指定dao独立的配置文件位置

    ```xml
    <!--SqlMapConfig.xml-->
    <!--9.导入约束-->
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE configuration
            PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <!--mybatis的主配置文件-->
    <configuration>
        <!--配置properties文件位置-->
        <properties resource="jdbcConfig.properties"></properties>
        <!--配置别名-->
        <typeAliases>
            <typeAlias type="domain.User" alias="user"></typeAlias>
        </typeAliases>
    
        <!--10.配置环境-->
        <environments default="mysql">
            <!--配置mysql环境-->
            <environment id="mysql">
                <!--配置事务类型-->
                <transactionManager type="JDBC"></transactionManager>
                <!--配置数据源/连接池-->
                <dataSource type="POOLED">
                    <!--下列情况二选一-->
                    <!--配置连接数据库的4个基本信息-->
                    <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                    <property name="url" value="jdbc:mysql:///mybatis?serverTimezone=UTC"/>
                    <property name="username" value="root"/>
                    <property name="password" value="root"/>
                    <!--使用配置文件-->
                    <property name="driver" value="${jdbc.driver}"/>
                    <property name="url" value="${jdbc.url}"/>
                    <property name="username" value="${jdbc.username}"/>
                    <property name="password" value="${jdbc.password}"/>
                </dataSource>
            </environment>
        </environments>
    
    <!--11.指定映射配置文件的位置
    	该文件指的是每个dao独立的配置文件
        如果使用注解配置，此处应该使用class属性指定被注解的dao全限定类名
    -->
        <mappers>
            <!--xml配置二选一-->
            <package name="dao"/>
            <mapper resource="dao/UserDao.xml"/>
            <!--注解配置-->
            <mapper class="dao.UserDao"/>
        </mappers>
    </configuration>
    ```

12. 在resources下创建同样的目录结构，并在`UserDao`接口对应的位置创建`UserDao.xml`，导入约束，并创建`mapper`

    ```xml
    <!--UserDao.xml-->
    <!--导入约束-->
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">4
    <!--创建mapper-->
    <mapper namespace="dao.UserDao">
    <!--id为方法名-->
        <select id="findAll" resultType="domain.User">
            select * from user ;
        </select>
    </mapper>
    ```

1. 注意

   - MyBatis中把持久层操作接口与映射文件叫做Mapper，因此UserDao和UserMapper是一样的

   - IDEA中directory和package不一样，directory不分级

   - MyBatis的映射配置文件位置必须和dao接口的包**结构相同**

   - 映射配置文件的mapper标签的namespace属性必须是dao接口的**全限定类名**

   - 映射配置文件的操作配置的id属性必须是dao接口的**方法名**

   - 遵循了后三点，在开发中就**不必写实现类**

## 3.使用注解配置

1. 不再需要`UserDao.xml`，直接在`UserDao`接口的`findAll()`方法上加上注解`@Select("select * from user ")`
2. 修改`SqlMapConfig.xml`的`<mapper>`标签为`class`属性`<mapper class="dao.UserDao"/>`

## 4.使用与测试

1. 导入log4j.properties

2. 创建一个测试类

   1. 读取配置文件
      - 绝对路径和相对路径用的都不多，实际只用2种
        1. 使用类加载器，只能去读类路径的配置文件
        2. 使用ServletContext对象的`getRealPath()`
   2. 创建SqlSessionFactory工厂
      - mybatis使用了构建者模式，builder就是构建者，流就是需求，隐藏对象的创建细节
   3. 使用工厂生产SqlSession对象
      - 使用了工厂模式，达到解耦/降低类之间的依赖关系的目的
   4. 使用SqlSession创建Dao接口的代理对象
      - 使用了代理模式，不修改源码就可增强已有方法
   5. 使用代理对象执行方法
   6. 释放资源

   ```java
   public class MyBatisTest {
       public static void main(String[] args) throws Exception{
           // 1.读取配置文件
           InputStream is = Resources.getResourceAsStream("SqlMapConfig.xml");
           // 2.创建SqlSessionFactory工厂
           SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
           SqlSessionFactory factory = builder.build(is);
           // 3.使用工厂生产SqlSession对象
           SqlSession session = factory.openSession();
           // 4.使用SqlSession创建Dao接口的代理对象
           UserDao userDao = session.getMapper(UserDao.class);
           // 5.使用代理对象执行方法
           List<User> users = userDao.findAll();
           for (User user : users) {
               System.out.println(user);
           }
           // 6.释放资源
           session.close();
           is.close();
       }
   }
   ```

### 使用实现类

1. 删除创建代理对象的步骤
2. 重写实现类的构造函数，参数传递工厂类
3. 使用工厂创建SqlSession对象
4. 使用sesssion.selectList查询，查询语句为UserDao.xml的namespace+id的方法获得
5. 查询完成关闭session
6. 返回查询结果
7. 在测试类中创建实现类对象并调用方法

## 自定义框架

<img src="C:\Users\91494\AppData\Roaming\Typora\typora-user-images\image-20220404204114832.png" alt="image-20220404204114832" style="zoom:200%;" />

- SqlMapConfig.xml中的4个properties用于连接数据库，创建Connection对象

  对应JDBC中的注册驱动和获取数据库连接对象

  ```java
   Class.forName("com.mysql.cj.jdbc.Driver");
   Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db3", "root", "root");
  ```

- SqlMapConfig.xml中的mapper用于获取映射配置信息，用于读取UserDao.xml

- UserDao.xml中的mapper用于获取执行的sql语句，以此获取PerparedStatement，并且还有实体类的全限定类名以进行封装

  对应JDBC中的获取执行sql的对象以及sql语句，并对结果进行封装

  ```java
  PreparedStatement pstmt = conn.prepareStatement(sql);
  ResultSet rs = pstmt.executeQuery();
  // 接下来读取rs，将每个rs内容进行封装，并添加到实现类的List中
  List<E> list = new ArrayList();
  while(re.next()){
      E element = (E)Class.forName(实体类的全限定类名).newInstance;
      // 使用反射封装
      list.add(element);
  }
  ```

- 使用dom4j解析xml

- 由于反射信息中需要的sql语句和类名都在同一个方法中使用，因此需要将其封装为一个对象，对象为Map，键为类名+方法名，值为Mapper对象，其中包含sql语句和DomainClassPath

# 二、基本使用

## 1.单表的CRUD操作

- 添加操作
  1. 在`UserDao`接口中定义方法名
  
     ```java
     public interface UserDao {
         List<User> findAll();
     
         User findById(Integer userId);
     
         List<User> findByUsername(String username);
     
         List<User> findByVo(QueryVo vo);
     
         List<User> findByCondition(User user);
     
         List<User> findInIds(QueryVo vo);
     }
     ```
  
  2. 编写`QueryVo`类，方便复杂查询
  
     ```java
     public class QueryVo {
         private User user;
     
         private List<Integer> ids;
         
         // 对应的setter&getter
     }
     ```
  
  3. 编写`UserDao.xml`，可以将代码的公共部分抽取出，sql语句中占位符格式为`#{属性名}`
  
     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <!DOCTYPE mapper
             PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
             "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
     <mapper namespace="dao.UserDao">
         <!--抽取公共代码-->
         <sql id="select*">
             select * from user 
         </sql>
         <!--方法名-->
         <select id="findAll" resultType="USER">
             <include refid="select*"></include>
         </select>
     
         <select id="findById" parameterType="Integer" resultType="domain.User">
             <include refid="select*"></include>
             where id = #{id};
         </select>
     
         <select id="findByUsername" parameterType="String" resultType="domain.User">
             select *
             from user
             where username like #{username};
     --         select * from user where username like '%${value}%' ;
         </select>
     
         <select id="findByVo" parameterType="domain.QueryVo" resultType="domain.User">
             select *
             from user
             where username like #{user.username};
         </select>
     
         <select id="findByCondition" parameterType="domain.User" resultType="user">
             select * from user
             <!--<where>标签会自动去掉第一个and/or-->
             <where>
                 <if test="username != null">
                 and username = #{username}
                 </if>
                 <if test="sex != null">
                     and sex = #{sex}
                 </if>
             </where>
         </select>
     
         <select id="findInIds" parameterType="domain.QueryVo" resultType="user">
             select * from user
             <where>
                 <if test="ids != null and ids.size() > 0">
                     <foreach collection="ids" open="and id in (" close=")" item="id" separator=",">
                         #{id}
                     </foreach>
                 </if>
             </where>
         </select>
     </mapper>
     ```
  
     > 占位符和拼接符的区别
     >
     > 占位符是预编译处理，mybatis在处理sql语句时会将#{}替换成？，并调用PreparedStatement的set()方法赋值，传入到字符串后还会在值两边加上单引号
     >
     > 拼接符则是字符串替换，mybatis在处理时会将${}替换为变量的值，且不会加上单引号；使用拼接符会导致sql注入问题
  
  4. 编写测试类
  
     1. 定义变量，并且抽取出初始化方法和销毁方法，加上`@Before`和`@After`注解
  
     2. 提交事务保证能够正常执行，也可将该代码放到`@After`方法中
  
     3. id在回滚时已经使用过，所以id可能不是连续的
  
        ```java
        public class MyBatisTest {
        
            private InputStream is;
            private SqlSession session;
            private UserDao userDao;
        
            @Before
            public void init() throws Exception{
                // 1.读取配置文件
                is = Resources.getResourceAsStream("SqlMapConfig.xml");
                // 2.创建SqlSessionFactory工厂
                SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
                SqlSessionFactory factory = builder.build(is);
                // 3.使用工厂生产SqlSession对象
                session = factory.openSession();
        		//session = factory.openSession(true);
                // 4.使用SqlSession创建Dao接口的代理对象
                userDao = session.getMapper(UserDao.class);
            }
        
            @After
            public void destory() throws Exception{
                // 提交事务
                session.commit();
                // 6.释放资源
                session.close();
                is.close();
            }
        
            @Test
            public void testFindAll(){
                // 5.使用代理对象执行方法
                List<User> users = userDao.findAll();
                for (User user : users) {
                    System.out.println(user);
                }
            }
        
            @Test
            public void testFindById() throws Exception{
                // 5.使用代理对象执行方法
                User user = userDao.findById(41);
                System.out.println(user);
            }
        
            @Test
            public void testFindByUsername() throws Exception{
                // 5.使用代理对象执行方法
                List<User> users = userDao.findByUsername("%王%");
                for (User user : users) {
                    System.out.println(user);
                }
            }
        
            @Test
            public void testFindByVo() throws Exception{
                QueryVo vo = new QueryVo();
                User user = new User();
                user.setUsername("%王%");
                vo.setUser(user);
                // 5.使用代理对象执行方法
                List<User> users = userDao.findByVo(vo);
                for (User u : users) {
                    System.out.println(u);
                }
            }
        
            @Test
            public void testFindByConditon(){
                User user = new User();
                user.setUsername("老王");
                user.setSex("男");
                List<User> users = userDao.findByCondition(user);
                for (User u : users) {
                    System.out.println(u);
                }
            }
        
            @Test
            public void testFindInIds(){
                QueryVo vo = new QueryVo();
                List<Integer> list = new ArrayList<>();
                list.add(41);
                list.add(42);
                list.add(43);
                vo.setIds(list);
                List<User> users = userDao.findInIds(vo);
                for (User u : users) {
                    System.out.println(u);
                }
            }
        }
        ```
  
- 修改操作同上

- 删除操作只需要传一个id，`parameterType`可以为int，Integer等，因为只有一个参数，所以形参只是占位，可以随便取名

- 模糊查询是在传递参数时加上`%`，也可以用`'%${value}%'`，尽量用前者

- 返回插入后的id：在`<select>`标签中加入`<selectKey keyProperty="id" keyColumn="id" resultType="int" order="AFTER"> select last_insert_id();`

## 参数与返回值

- OGNL表达式：Object graphic Navigation Language，对象图导航语言，在写法上把取值方法的get省略，例如user.username；mybatis中由于ResultType已经传递类名，所以直接不需要写对象名

- pojo对象：Plain Ordinary Java Object，简单的Java对象，即JavaBeans

- 定义QueryVo对象进行多表查询

  - `private User user;`

  - `List<User> findByVo(QueryVo vo);`

  - ```xml
        <select id="findByVo" parameterType="domain.QueryVo" resultType="domain.User">
            select *
            from user
            where username like #{user.username};
        </select>
    ```

  - ```java
            QueryVo vo = new QueryVo();
            User user = new User();
            user.setUsername("%王%");
            vo.setUser(user);
            List<User> users = userDao.findByVo(vo);
    ```

- 如果修改属性名不予列名相同，删改查只需要修改对应位置，但是增的话无法成功，因为找不到对应列，但windows不区分大小写，所以只是大小写不同也能插入

  - 解决办法

    - sql语句中起别名与属性相同

    - 通过配置

      ```xml
      <resultMap id="随便取userMap" type="domain.User">
      	<id property="userid" column="id"></id>
          
          <result property="userName" column="username"></result>
      </resultMap>
      
      <select resultMap="userMap"></select>
      ```

      

## DAO编写(跳过)

- 实现类中定义SqlSessionFactory，并在构造函数中传值
- 在每个方法中获取SqlSession对象，调用selectList方法传入类名和方法名

## 配置细节

### 几个标签的使用

- \<properties>：将原先\<property>的内容定义到其中，在底下value属性中使用"${name}"来引用以解耦，这样就可以将配置信息放在外部文件中，通过resource属性输入文件名获取；url属性更麻烦一点，要写完整

- ```xml
  // 取别名后不再区分大小写
  <typeAliases>
      <typeAlias type="domain.User" alias="user"></typeAlias>
      <package name="domain"/>
  </typeAliases>
  ```

- `<package name="domain"></package>`，包中所有类都取别名

- mappers中也有\<package>，写的是dao接口所在的包，写完后就不需要再写mapper、resource和class了

- if标签用于多条件查询，查询语句中要写where 1=1

- where标签用于包裹if标签，不需要写where 1=1

- foreach标签用于in查询

  ```xml
      <select id="findInIds" parameterType="domain.QueryVo" resultType="user">
          select * from user
          <where>
              <if test="ids != null and ids.size() > 0">
                  <foreach collection="ids" open="and id in (" close=")" item="id" separator=",">
                      #{id}
                  </foreach>
              </if>
          </where>
      </select>
  ```

- sql标签用于定义sql语句，include标签用于引入语句，解决重复书写的问题

  ```xml
  <sql id="select*">
          select * from user
      </sql>
  
      <select id="findById" parameterType="Integer" resultType="domain.User">
          <include refid="select*"></include>
          where id = #{id};
      </select>
  ```

# 深入与多表

## 连接池(JNDI跳过)

- 一个集合对象，必须保证线程安全，且满足队列特性
- mybatis提供了3种配置方式
  - 配置位置：主配置文件的dataSource标签，type属性表示采用何种连接池方式，POOLED、UNPOOLED、JNDI，第二种是传统的获取连接方式，没有使用池的思想，最后一种采用不同服务器提供的技术获取不同DataSource对象，只有web或maven的war工程才能使用
  - 原理：一共有空闲池和活动池两个连接池，如果空闲池没有就去活动池，如果活动池数量小于限制就创建一个新的，不然就把最先返回的对象给新线程

## 事务控制与设计方法

- 什么是事务？事务的四大特性ACID？不考虑隔离性会产生的3大问题？解决办法：4种隔离级别

## 多表查询

- mybatis把多对一看成一对一，一个账户只能属于一个用户/多个账户可以属于一个用户

- 一对一

  1. 在Account类中定义`private User user;`

  2. ```xml
     <!--    定义封装account和user的resultMap-->
     <resultMap id="accountUserMap" type="account">
         <!-- 这里的uid是sql语句中起的别名 -->
         <id property="id" column="aid"></id>
         <result property="uid" column="uid"></result>
         <result property="MONEY" column="MONEY"></result>
         <!--        一对一的关系映射-->
         <association property="user" column="uid" javaType="user">
             <id property="id" column="id"></id>
             <result property="username" column="username"></result>
             <result property="birthday" column="birthday"></result>
             <result property="sex" column="sex"></result>
             <result property="address" column="address"></result>
         </association>
     ```

###  一对多

1. 在User类中定义一对多关系映射`private List<Account> accounts;`

2. ```xml
       <resultMap id="userAccountMap" type="user">
           <id property="id" column="id"></id>
           <result property="username" column="username"></result>
           <result property="birthday" column="birthday"></result>
           <result property="sex" column="sex"></result>
           <result property="address" column="address"></result>
   <!--        配置user对象中accounts集合的映射-->
           <collection property="accounts" ofType="account">
               <id property="id" column="aid"></id>
               <result property="uid" column="UID"></result>
               <result property="MONEY" column="MONEY"></result>
           </collection>
   ```

3. ```xml
       <select id="findAll" resultMap="userAccountMap">
           -- 方法体
           select u.*, a.ID as aid, a.UID, a.MONEY from user u left outer join account a on u.id = a.uid
       </select>
   ```

### 多对多

1. Role中添加多对多关系映射`private List<User> users;`

2. ```xml
       <resultMap id="roleMap" type="role">
           <id property="roleId" column="rid"></id>
           <result property="roleName" column="ROLE_NAME"></result>
           <result property="roleDesc" column="ROLE_DESC"></result>
           <collection property="users" ofType="user">
               <id property="id" column="id"></id>
               <result property="username" column="username"></result>
               <result property="address" column="address"></result>
               <result property="sex" column="sex"></result>
               <result property="birthday" column="birthday"></result>
           </collection>
       </resultMap>
   ```

3. ```xml
       <select id="findAll" resultMap="roleMap">
           select u.*, r.id as rid, r.role_name, r.role_desc from role r 
           left outer join user_role ur on r.id = ur.rid 
           left outer join user u on u.id = ur.uid
       </select>
   ```

# 缓存与注解开发

## 加载时机/查询时机

- 延迟加载：按需加载/懒加载，一对多、多对多

  1. 修改select语句，去掉连接操作，只保留查询本表操作

  2. 将association/collections标签下的配置也删除，select属性填入**另一个表查询方法**的全类名并将该方法的resultmap改为resulttype

  3. association标签中的select属性填入方法的全类名

  4. 但是为了调用必须在全局配置文件中，configuration标签下，properties标签后配置settings标签

     ```xml
         <settings>
             <!--        打开延迟加载-->
             <setting name="lazyLoadingEnabled" value="true"/>
             <!--        改为按需加载而非全部加载-->
             <setting name="aggressiveLazyLoading" value="false"/>
         </settings>
     ```

- 立即加载：只要调用就查询，一对一、多对一

## 一级缓存与二级缓存

- 经常查询且不常改变、数据正确性影响不大
- 一级缓存：指的是SqlSession对象的缓存，查询的结果回存入SqlSession提供的一块区域中，该区域是块Map；
  - SqlSession.close()和clearCache()都可以清除缓存
  - 当修改删除添加操作以及事务提交都会清空缓存
- 二级缓存：指的是SqlSessionFactory对象的缓存，由同一个工厂对象创建的SqlSession共享其缓存
  1. 让mybatis支持二级缓存，在全局配置文件中配置：`<setting name="cacheEnabed" value="true"/>`
  2. 让当前映射文件支持二级缓存，UserDao.xml：`<cache/>`
  3. 让当前操作支持二级缓存，select标签：userCache属性为true
  4. 二级缓存中存放的是数据而非对象，不同于一级缓存
- 注解开发的二级缓存：只需要在Dao类上使用@CacheNameSpace(blocking=true)

## 注解开发

### 单表CRUD

- 在mybatis中针对CURD共有四个注解：@Select @Insert @Update @Delete

- 使用注解开发时，只要同样目录中存在对应dao的xml文件，不论是否配置mapper.class属性都会报错；要不删掉要不挪到其他位置

- 如果属性名不一致，在一个方法上使用Results标签，其他方法使用ResultMap标签

  ```java
  @Results(id="userMap",value={
      @Result(id=true,column="id",property="userid"),
      @Result(column="username",property="userName"),
  })
  
  @ResultMap(value={"userMap"})
  ```

### 多表查询

- 一对一

  ```xml
      @Select("select * from account ")
      @Results(id = "accountMap", value = {
              @Result(id = true, column = "id", property = "id"),
              @Result(column = "uid", property = "uid"),
              @Result(column = "money", property = "money"),
              @Result(column = "uid", property = "user", one = @One(select = "dao.UserDao.findById", fetchType = FetchType.EAGER))
      })
  ```

- 一对多：将one换成many，EAGER换成LAZY