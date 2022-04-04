package dao;

import domain.QueryVo;
import domain.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User findById(Integer userId);

    List<User> findByUsername(String username);

    List<User> findByVo(QueryVo vo);

    List<User> findByCondition(User user);

    List<User> findInIds(QueryVo vo);

}
