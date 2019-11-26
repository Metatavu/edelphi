package fi.metatavu.edelphi.rest.test.functional.builder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Customer functional tests
 * 
 * @author Antti Leppä
 *
 */
public class PanelTestsIT extends AbstractFunctionalTest {

  @Test
  public void testCreatePanel() throws Exception {
    try (TestBuilder builder = new TestBuilder()) {
      assertNotNull(builder.admin().panels().create());
    }
  }

}
