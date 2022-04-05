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
import java.util.List;

public class AccountTest {

    private InputStream is;
    private SqlSession session;
    private AccountDao accountDao;

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
        accountDao = session.getMapper(AccountDao.class);

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
        List<Account> accounts = accountDao.findAll();
        for (Account a : accounts) {
            System.out.println(a);
            System.out.println(a.getUser());
        }
    }

}
