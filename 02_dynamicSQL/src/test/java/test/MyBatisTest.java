package test;

import dao.UserDao;
import domain.QueryVo;
import domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
//        session = factory.openSession(true);
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
