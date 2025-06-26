package common.commands;

import common.data.auth.AuthCredentials;
import common.exceptions.AuthenticationException;
import common.exceptions.CommandExecuteException;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import common.managers.UserManager;
import common.network.*;
import java.util.Scanner;

public class LoginCommand implements Command {
  private final UserManager userManager;
  private final ScannerManager scannerManager;
  private final ScriptManager scriptManager;

  public LoginCommand(
      UserManager userManager, ScannerManager scannerManager, ScriptManager scriptManager) {
    this.userManager = userManager;
    this.scannerManager = scannerManager;
    this.scriptManager = scriptManager;
  }

  @Override
  public Response execute(Request request) {
    String[] args = request.getRequestBody().getArgs();

    AuthCredentials auth = new AuthCredentials(args[0], args[1]);
    Integer currentId = userManager.authenticate(auth);

    if (currentId == null) {
      return new ResponseWithException(
          new AuthenticationException("Неверный логин или пароль."), request.getRequestId());
    }

    return new ResponseWithAuthCredentials(
        auth, "Авторизация прошла успешно.", request.getRequestId());
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 0) {
      throw new CommandExecuteException("Команда не принимает аргументы.");
    }

    Scanner scanner = scannerManager.getScanner();

    System.out.print("Введите логин: ");
    String login = scanner.nextLine();
    if (scriptManager.getFileMode()) {
      System.out.println(login);
    }
    System.out.print("Введите пароль: ");
    String password;
    if (scriptManager.getFileMode()) {
      password = scanner.nextLine();
    } else {
      password = String.valueOf(System.console().readPassword());
    }

    return new RequestBody(new String[] {login, password});
  }

  @Override
  public String getName() {
    return "login";
  }

  @Override
  public String getDescription() {
    return "авторизоваться в системе.";
  }
}
