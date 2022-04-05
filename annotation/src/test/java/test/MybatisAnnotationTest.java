package test;

import dao.AccountDao;
import dao.UserDao;
import domain.Account;
import domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class MybatisAnnotationTest {

    private InputStream is;
    private SqlSession session;
    private UserDao userDao;
    private AccountDao accountDao;

    @Before
    public void init() throws Exception {
        // 1.读取配置文件
        is = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2.创建SqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(is);
        // 3.使用工厂生产SqlSession对象
        session = factory.openSession();
        // session = factory.openSession(true);
        // 4.使用SqlSession创建Dao接口的代理对象
        userDao = session.getMapper(UserDao.class);
        accountDao = session.getMapper(AccountDao.class);

    }

    @After
    public void destroy() throws Exception {
        // 提交事务
        session.commit();
        // 6.释放资源
        session.close();
        is.close();
    }

    @Test
    public void testFindAll() {
        // 5.使用代理对象执行方法
        List<User> users = userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testSaveUser() {
        // 5.使用代理对象执行方法
        User user = new User();

        user.setUsername("annotation user");
        user.setAddress("福州市仓山区");

        userDao.saveUser(user);
    }

    @Test
    public void testUpdateUser() {
        // 5.使用代理对象执行方法
        User user = new User();

        user.setId(53);
        user.setUsername("updated annotation user");
        user.setAddress("福州市仓山区");
        user.setBirthday(new Date());
        user.setSex("女");

        userDao.updateUser(user);
    }

    @Test
    public void testDeleteUser() {
        // 5.使用代理对象执行方法
        Integer userId = 53;
        userDao.deleteUser(userId);
    }

    @Test
    public void testFindById() {
        // 5.使用代理对象执行方法
        Integer userId = 52;
        User user = userDao.findById(userId);
        System.out.println(user);
    }

    @Test
    public void testFindUserByName() {
        // 5.使用代理对象执行方法
//        List<User> users = userDao.findUserByName("%王%");
        List<User> users = userDao.findUserByName("王");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testFindTotalUser() {
        int total = userDao.findTotalUser();

        System.out.println(total);
    }


    @Test
    public void testAccountFindAll() {
        // 5.使用代理对象执行方法
        List<Account> accounts = accountDao.findAll();
        for (Account account : accounts) {
            System.out.println(account);
            System.out.println(account.getUser());
        }
    }

}
