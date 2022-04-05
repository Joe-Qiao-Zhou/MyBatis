package test;

import dao.RoleDao;
import dao.UserDao;
import domain.Role;
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

public class RoleTest {

    private InputStream is;
    private SqlSession session;
    private RoleDao roleDao;

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
        roleDao = session.getMapper(RoleDao.class);

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
        List<Role> roles = roleDao.findAll();
        for (Role role : roles) {
            System.out.println(role);
            System.out.println(role.getUsers());
        }
    }

}
