package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class RegisterInfoTestsBase extends AbstractUITest {
  
  @Test
  public void testTitle() {
    navigate("/");
    waitAndAssertText(".documenttitle", "Tietosuojaseloste - eDelfoi");
  }
  
}
