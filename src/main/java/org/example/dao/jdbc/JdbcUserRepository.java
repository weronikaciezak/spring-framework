package org.example.dao.jdbc;

import org.example.dao.IUserRepository;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class JdbcUserRepository implements IUserRepository {
    private static JdbcUserRepository instance;
    private final DatabaseManager databaseManager;

    private static String GET_ALL_USERS_SQL = "SELECT login, password, role, rentedPlate FROM tuser";
    private static String GET_USER_SQL = "SELECT * FROM tuser WHERE login LIKE ?";

    private JdbcUserRepository() {
        this.databaseManager = DatabaseManager.getInstance();
    }


    @Override
    public User getUser(String login) {
        User user = null;
        Connection connection =null;
        ResultSet rs = null;
        try{
            connection = databaseManager.getConnection();
            connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_SQL);
            preparedStatement.setString(1, login);
            rs = preparedStatement.executeQuery();
            if(rs.next()) {
                user = new User(
                    rs.getString("login"),
                    rs.getString("password"),
                    User.Role.valueOf(rs.getString("role")),
                    rs.getString("rentedPlate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
                //TODO:close connection,statement and resultset.
        }
        return user;
    }

    @Override
    public void addUser(User user) {
        //TODO:impement method addUser
    }

    @Override
    public void removeUser(String login) {
        //TODO:implement method removeUser
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> users = new ArrayList<>();
        try(Connection connection = databaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_USERS_SQL)) {
            while(resultSet.next()){
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                User.Role role = User.Role.valueOf(resultSet.getString("role"));
                String plate = resultSet.getString("rentedPlate");

                User user = new User(login, password, role,plate);
                users.add(user);
            }
        }catch (SQLException e){
                e.printStackTrace();
        }
        return users;
    }


    public static JdbcUserRepository getInstance(){
        if (JdbcUserRepository.instance == null){
            JdbcUserRepository.instance = new JdbcUserRepository();
        }
        return instance;
    }

}
