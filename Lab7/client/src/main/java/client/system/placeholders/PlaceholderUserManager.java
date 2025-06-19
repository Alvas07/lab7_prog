package client.system.placeholders;

import common.data.auth.AuthCredentials;
import common.managers.UserManager;

public class PlaceholderUserManager implements UserManager {
  @Override
  public Integer authenticate(AuthCredentials auth) {
    return 0;
  }

  @Override
  public Integer register(AuthCredentials auth) {
    return 0;
  }

  @Override
  public String getUsernameById(int userId) {
    return "";
  }
}
