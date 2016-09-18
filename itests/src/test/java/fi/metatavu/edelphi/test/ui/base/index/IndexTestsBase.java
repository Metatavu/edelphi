package fi.metatavu.edelphi.test.ui.base.index;

import org.junit.Test;

import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class IndexTestsBase extends AbstractUITest {
  
  @Test
  public void testIndex() {
    navigate("/");
    waitAndAssertText(".menuItem.activeTrail", "Index");
  }
  
}
