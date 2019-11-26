package fi.metatavu.edelphi.rest.test.functional.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.openapitools.client.api.PanelsApi;
import org.openapitools.client.model.Panel;
import org.openapitools.client.model.PanelAccessLevel;
import org.openapitools.client.model.PanelState;

import feign.FeignException;
import fi.metatavu.edelphi.rest.client.ApiClient;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;

/**
 * Test builder resource for panels
 * 
 * @author Antti Leppä
 */
public class PanelTestBuilderResource extends ApiTestBuilderResource<Panel, PanelsApi> {
  
  /**
   * Constructor
   * 
   * @param testBuilder test builder
   * @param apiClient initialized API client
   */
  public PanelTestBuilderResource(AbstractTestBuilder testBuilder, ApiClient apiClient) {
    super(testBuilder, apiClient);
  }
  
  /**
   * Creates new panel with default values
   * 
   * @return created panel
   * @throws ApiException 
   */
  public Panel create() {
    return create("default name", "default desc", PanelState.IN_PROGRESS, PanelAccessLevel.OPEN);
  }
  
  /**
   * Creates new panel
   * 
   * @param name name
   * @param description 
   * @param state 
   * @param accessLevel 
   * @throws ApiException 
   */
  public Panel create(String name, String description, PanelState state, PanelAccessLevel accessLevel) {
    Panel panel = new Panel();
    panel.setName(name);
    panel.setAccessLevel(accessLevel);
    panel.setDescription(description);
    panel.setState(state);
    Panel result = getApi().createPanel(panel);
    return addClosable(result);
  }
  
  /**
   * Finds a panel
   * 
   * @param panelId panel id
   * @return found panel
   * @throws ApiException 
   */
  public Panel findPanel(Long panelId) {
    return getApi().findPanel(panelId);
  }
  
  /**
   * Lists panels
   * 
   * @param urlName filter by URL name. Ignored if null is provided
   * @return found panels
   * @throws ApiException 
   */
  public List<Panel> listPanels(String urlName) {
    return getApi().listPanels(urlName);
  }

  /**
   * Updates a panel into the API
   * 
   * @param body body payload
   * @throws ApiException 
   */
  public Panel updatePanel(Panel body) {
    return getApi().updatePanel(body.getId(), body);
  }
  
  /**
   * Deletes a panel from the API
   * 
   * @param panel panel to be deleted
   * @throws ApiException 
   */
  public void delete(Panel panel) {
    getApi().deletePanel(panel.getId());  
    
    removeCloseable(closable -> {
      if (!(closable instanceof Panel)) {
        return false;
      }

      Panel closeablePanel = (Panel) closable;
      return closeablePanel.getId().equals(panel.getId());
    });
  }
  
  /**
   * Asserts panel count within the system
   * 
   * @param urlName filter by URL name. Ignored if null is provided
   * @param expected expected count
   * @throws ApiException 
   */
  public void assertCount(int expected, String urlName) {
    assertEquals(expected, getApi().listPanels(urlName).size());
  }
  
  /**
   * Asserts find status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panelId panel id
   */
  public void assertFindFailStatus(int expectedStatus, Long panelId) {
    try {
      getApi().findPanel(panelId);
      fail(String.format("Expected find to fail with status %d", expectedStatus));
    } catch (FeignException e) {  
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts create status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param name name
   * @param imageUrl image URL
   */
  public void assertCreateFailStatus(int expectedStatus, String name, String description, PanelState state, PanelAccessLevel accessLevel) {
    try {
      create(name, description, state, accessLevel);
      fail(String.format("Expected create to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts update status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panel panel
   */
  public void assertUpdateFailStatus(int expectedStatus, Panel panel) {
    try {
      updatePanel(panel);
      fail(String.format("Expected update to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts delete status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param panel panel
   */
  public void assertDeleteFailStatus(int expectedStatus, Panel panel) {
    try {
      getApi().deletePanel(panel.getId());
      fail(String.format("Expected delete to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }
  
  /**
   * Asserts list status fails with given status code
   * 
   * @param expectedStatus expected status code
   * @param urlName filter by URL name. Ignored if null is provided
   */
  public void assertListFailStatus(int expectedStatus, String urlName) {
    try {
      getApi().listPanels(urlName);
      fail(String.format("Expected list to fail with status %d", expectedStatus));
    } catch (FeignException e) {
      assertEquals(expectedStatus, e.status());
    }
  }

  /**
   * Asserts that actual panel equals expected panel when both are serialized into JSON
   * 
   * @param expected expected panel
   * @param actual actual panel
   * @throws JSONException thrown when JSON serialization error occurs
   * @throws IOException thrown when IO Exception occurs
   */
  public void assertPanelsEqual(Panel expected, Panel actual) throws IOException, JSONException {
    assertJsonsEqual(expected, actual);
  }

  @Override
  public void clean(Panel panel) {
    getApi().deletePanel(panel.getId());  
  }

}
