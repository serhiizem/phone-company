package com.phonecompany.service;

import com.phonecompany.annotations.ServiceStereotype;
import com.phonecompany.dao.interfaces.UserDao;
import com.phonecompany.exception.ConflictException;
import com.phonecompany.model.User;
import com.phonecompany.model.enums.Status;
import com.phonecompany.service.interfaces.UserService;
import com.phonecompany.util.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@ServiceStereotype
public class UserServiceImpl extends AbstractUserServiceImpl<User>
        implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${application-url}")
    private String applicationUrl;

    private UserDao userDao;
    private ShaPasswordEncoder shaPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           ShaPasswordEncoder shaPasswordEncoder) {
        this.userDao = userDao;
        this.shaPasswordEncoder = shaPasswordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        Assert.notNull(email, "Email should not be null");
        return userDao.findByEmail(email);
    }

    @Override
    public Status getStatus() {
        return Status.ACTIVATED;
    }

    /**
     * Performs a set of validating operations particular to
     *
     * @param user
     */
    @Override
    public void validate(User user) {
        String email = user.getEmail();
        int countByEmail = this.userDao.getCountByEmail(email);
        if (countByEmail != 0) {
            throw new ConflictException("User associated with " + email + " already exists");
        }
    }

    @Override
    public User update(User user) {
        Assert.notNull(user, "User should not be null");

        return super.update(user);
    }

    @Override
    public String resetPassword(User user) {
        String newPassword = generatePassword();
        user.setPassword(shaPasswordEncoder.encodePassword(newPassword, null));
        update(user);
        return newPassword;
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        String password = new BigInteger(50, random).toString(32);
        char[] specSymb = "!@$".toCharArray();
        char[] passwordWithSS = password.toCharArray();
        passwordWithSS[random.nextInt(passwordWithSS.length)] = specSymb[random.nextInt(specSymb.length)];
        password = String.valueOf(passwordWithSS);
        return password;
    }

    @Override
    public Map<String, Object> getAllUsersPaging(int page, int size, int roleId, String status, String email,
                                                 int orderBy, String orderByType) {
        Query query = buildQueryForUsersTable(page, size, roleId, status, email, orderBy, orderByType);
        Map<String, Object> response = new HashMap<>();
        response.put("users", this.userDao.executeForList(query.getQuery(), query.getPreparedStatementParams().toArray()));
        response.put("usersSelected", this.userDao.executeForInt(query.getCountQuery(), query.getCountParams().toArray()));
        return response;
    }

    private Query buildQueryForUsersTable(int page, int size, int roleId, String status, String email,
                                          int orderBy, String orderByType) {
        Query.Builder builder = new Query.Builder("dbuser");
        builder.where().addCondition("dbuser.role_id <> ?", 4).and().addCondition("dbuser.role_id <> ?", 1)
                .and().addLikeCondition("dbuser.email", email);
        if (roleId > 0) builder.and().addCondition("dbuser.role_id = ?", roleId);
        if (!status.equals("ALL")) builder.and().addCondition("dbuser.status = ?", status);
        String orderByField = buildOrderBy(orderBy);
        if (orderByField.length() > 0) {
            builder.orderBy(orderByField);
            builder.orderByType(orderByType);
        }
        builder.addPaging(page, size);
        return builder.build();
    }

    private String buildOrderBy(int orderBy) {
        switch (orderBy) {
            case 0://by email
                return "dbuser.email";
            case 1://by role
                return "dbuser.role_id";
            case 2://by status
                return "dbuser.status";
            default:
                return "";
        }
    }

    @Override
    public void changePassword(String oldPass, String newPass, User user) {
        if (shaPasswordEncoder.encodePassword(oldPass, null).equals(user.getPassword())) {
            user.setPassword(shaPasswordEncoder.encodePassword(newPass, null));
            userDao.update(user);
        } else {
            throw new ConflictException("Old password is wrong");
        }
    }

    @Override
    public void updateStatus(long id, Status status) {
        userDao.updateStatus(id, status);
    }

    @Override
    public Map<String, Object> getAllUsersSearch(int page, int size, String email, int userRole, String status) {
        Query.Builder queryBuilder = new Query.Builder("dbuser");
        queryBuilder.where().addLikeCondition("email", email);

        if (userRole > 0) {
            queryBuilder.and().addCondition("role_id = ?", userRole);
        } else if (userRole != 0) {
            throw new ConflictException("Incorrect search parameter: user role.");
        }

        if (!status.equals("ALL")) {
            queryBuilder.and().addCondition("status = ?", status);
        }
        queryBuilder.addPaging(page, size);

        Map<String, Object> response = new HashMap<>();
        Query query = queryBuilder.build();
        response.put("users", userDao.executeForList(query.getQuery(), query.getPreparedStatementParams().toArray()));
        response.put("entitiesSelected", userDao.executeForInt(query.getCountQuery(), query.getCountParams().toArray()));
        return response;
    }
}