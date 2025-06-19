package server.managers;

import common.data.auth.AuthCredentials;
import common.exceptions.PasswordHashException;
import common.managers.UserManager;
import common.utils.PasswordUtils;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlUserManager implements UserManager {
  private static final Logger logger = LogManager.getLogger();
  private final Connection connection;
  private static final String CREATE_TABLE_QUERY =
      "CREATE TABLE IF NOT EXISTS users ("
          + "id SERIAL PRIMARY KEY,"
          + "username TEXT UNIQUE NOT NULL,"
          + "password TEXT NOT NULL,"
          + "salt varchar(64) NOT NULL)";
  private static final String SELECT_AUTH_QUERY =
      "SELECT id, password, salt FROM users WHERE username = ?";
  private static final String INSERT_AUTH_QUERY =
      "INSERT INTO users (username, password, salt) VALUES (?, ?, ?) RETURNING id";
  private static final String SELECT_USERNAME_QUERY = "SELECT username FROM users WHERE id = ?";

  public SqlUserManager(Connection connection) throws SQLException {
    this.connection = connection;
    try (Statement statement = connection.createStatement()) {
      statement.execute(CREATE_TABLE_QUERY);
    }
  }

  @Override
  public Integer authenticate(AuthCredentials auth) {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_AUTH_QUERY)) {
      statement.setString(1, auth.username());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        int dbUserId = resultSet.getInt("id");
        String dbHashPassword = resultSet.getString("password");
        String dbSalt = resultSet.getString("salt");
        String clientHashPassword = PasswordUtils.hashPassword(auth.password(), dbSalt);
        if (clientHashPassword.equals(dbHashPassword)) {
          return dbUserId;
        }
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при авторизации пользователя.");
      return null;
    } catch (PasswordHashException e) {
      logger.error("Возникла ошибка при хешировании пароля.");
      return null;
    }
    return null;
  }

  @Override
  public Integer register(AuthCredentials auth) {
    try (PreparedStatement statement = connection.prepareStatement(INSERT_AUTH_QUERY)) {
      String salt = PasswordUtils.generateSalt(32);
      String hashPassword = PasswordUtils.hashPassword(auth.password(), salt);
      statement.setString(1, auth.username());
      statement.setString(2, hashPassword);
      statement.setString(3, salt);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при регистрации пользователя.");
      return null;
    } catch (PasswordHashException e) {
      logger.error("Возникла ошибка при хешировании пароля.");
      return null;
    }
    return null;
  }

  @Override
  public String getUsernameById(int userId) {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_USERNAME_QUERY)) {
      statement.setInt(1, userId);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getString("username");
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при поиске пользователя по id.");
      return null;
    }
    return null;
  }
}
