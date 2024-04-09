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
        PreparedStatement preparedStatement = null;
        try{
            connection = databaseManager.getConnection();
            connection.createStatement();
            preparedStatement = connection.prepareStatement(GET_USER_SQL);
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
            //TODO:zamkniecia polaczenia i
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

    @Override
    public void addUser(User user) {
        //TODO:impement method addUser : done
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = databaseManager.getConnection();
            connection.createStatement();
            String INSERT_USER = "INSERT INTO tuser (login, password, rentedPlate, role) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(INSERT_USER);

            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRentedPlate());
            preparedStatement.setString(4, user.getRole().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeUser(String login) {
        //TODO:implement method removeUser : DONE
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = databaseManager.getConnection();
                String CHECK_EXIST = "SELECT COUNT(*) FROM tuser WHERE username = ?";
                preparedStatement = connection.prepareStatement(CHECK_EXIST);
                preparedStatement.setString(1, login);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    String DELETE_USER = "DELETE FROM tuser WHERE username = ?";
                    preparedStatement = connection.prepareStatement(DELETE_USER);
                    preparedStatement.setString(1, login);
                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("User " + login + " was deleted successfully!");
                    } else {
                        System.out.println("No user found with username: " + login);
                    }
                } else {
                    System.out.println("No user found with username: " + login);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

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
