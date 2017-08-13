package fi.metatavu.edelphi.test.ui.local.index;

import fi.metatavu.edelphi.test.ui.base.index.LoginTestsBase;

public class LoginTestsIT extends LoginTestsBase {

  public LoginTestsIT() {
    setWebDriver(createLocalDriver());
  }

}
