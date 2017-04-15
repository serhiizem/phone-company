package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.AddressDao;
import com.phonecompany.dao.interfaces.RoleDao;
import com.phonecompany.dao.interfaces.UserDao;
import com.phonecompany.exception.EntityInitializationException;
import com.phonecompany.exception.PreparedStatementPopulationException;
import com.phonecompany.model.User;
import com.phonecompany.util.QueryLoader;
import com.phonecompany.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDaoImpl extends CrudDaoImpl<User>
        implements UserDao {

    private QueryLoader queryLoader;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    public UserDaoImpl(QueryLoader queryLoader) {
        this.queryLoader = queryLoader;
    }

    @Override
    public User findByUsername(String userName) {
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
            preparedStatement.setLong(5, user.getAddress().getId());
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getPassword());
            preparedStatement.setLong(8, user.getRole().getId());
            preparedStatement.setLong(9, user.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }
}
