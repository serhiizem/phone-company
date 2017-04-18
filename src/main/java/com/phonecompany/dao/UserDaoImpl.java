package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.AddressDao;
import com.phonecompany.dao.interfaces.RoleDao;
import com.phonecompany.dao.interfaces.UserDao;
import com.phonecompany.exception.EntityInitializationException;
import com.phonecompany.exception.EntityNotFoundException;
import com.phonecompany.exception.PreparedStatementPopulationException;
import com.phonecompany.model.User;
import com.phonecompany.util.QueryLoader;
import com.phonecompany.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserDaoImpl extends CrudDaoImpl<User>
        implements UserDao {

    @Value("${spring.datasource.url}")
    private String connStr;

    private QueryLoader queryLoader;
    private RoleDao roleDao;
    private AddressDao addressDao;

    @Autowired
    public UserDaoImpl(QueryLoader queryLoader,
                       AddressDao addressDao,
                       RoleDao roleDao) {
        this.queryLoader = queryLoader;
        this.addressDao = addressDao;
        this.roleDao = roleDao;
    }

    @Override
    public User findByEmail(String email) {
        try (Connection conn = DriverManager.getConnection(connStr);
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getByEmail"))) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return init(rs);
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException(-1l, e);
        }
        return null;
    }

    @Override
    public String getQuery(String type) {
        return queryLoader.getQuery("query.user." + type);
    }

    @Override
    public void populateSaveStatement(PreparedStatement preparedStatement, User user){
        try {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getSecondName());
            preparedStatement.setObject(5, TypeMapper.getNullableId(user.getAddress()));
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getPassword());
            preparedStatement.setObject(8, TypeMapper.getNullableId(user.getRole()));
            preparedStatement.setString(9, user.getUserName());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public User init(ResultSet rs) {
        User user = new User();
        try {
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setLastName(rs.getString("lastname"));
            user.setFirstName(rs.getString("firstname"));
            user.setSecondName(rs.getString("secondname"));
            user.setAddress(addressDao.getById(rs.getLong("address")));
            user.setPhone(rs.getString("phone"));
            user.setPassword(rs.getString("password"));
            user.setRole(roleDao.getById(rs.getLong("role_id")));
            user.setUserName(rs.getString("username"));
        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return user;
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, User user) {
        try {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getSecondName());
            preparedStatement.setObject(5, TypeMapper.getNullableId(user.getAddress()));
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getPassword());
            preparedStatement.setObject(8, TypeMapper.getNullableId(user.getRole()));
            preparedStatement.setString(9, user.getUserName());
            preparedStatement.setLong(10, user.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }
}
