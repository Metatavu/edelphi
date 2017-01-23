package fi.metatavu.edelphi.test.ui.base.panel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import fi.metatavu.edelphi.test.mock.FolderMocker;
import fi.metatavu.edelphi.test.mock.PanelMocker;
import fi.metatavu.edelphi.test.mock.QueryMocker;
import fi.metatavu.edelphi.test.ui.base.AbstractUITest;

public class QueryTestsBase extends AbstractUITest {

  private static final int SCALE2D_GRAPH_MARGIN_X = 87;
  private static final int SCALE2D_GRAPH_MARGIN_Y = 3;

  private FolderMocker folderMocker;
  private QueryMocker queryMocker;
  private PanelMocker panelMocker;
  
  @Before
  public void before() {
    folderMocker = new FolderMocker();
    queryMocker = new QueryMocker();
    panelMocker = new PanelMocker();
  }
  
  @After
  public void after() {
    queryMocker.cleanup();
    folderMocker.cleanup();
    panelMocker.cleanup();
  }
  
  @Test
  public void testPageWalk() {
    login(ADMIN_EMAIL);
    createTestPanel();
    
    createTestQuery("test", "basic-text-page", "basic-expertise-page", "basic-scale1d-radio-page", "basic-scale2d-radio-page", 
        "basic-timeserie-page", "basic-timeline-page", "basic-grouping-page", "basic-multiselect-page", "basic-order-page", 
        "basic-form-page", "basic-background-form-page", "basic-collage2d-page");
    
    navigate("/test/test-query");
    
    // Text
    
    waitAndAssertText(".queryPageTitle", "Text page");
    nextPage(1);
    
    // Expertise
    waitAndAssertText(".queryPageTitle", "Expertise matrix");
    nextPage(2);
    
    // Scale 1d
    
    waitPresent("input[name='queryPageType'][value='THESIS_SCALE_1D']");
    waitAndClick(".queryScaleRadioListItemInput[value='4']");
    nextPage(3);
    
    // Scale 2d
    
    waitPresent("input[name='queryPageType'][value='THESIS_SCALE_2D']");
    waitAndClick(".queryScaleRadioListItemInput[name='valueX'][value='5']");
    waitAndClick(".queryScaleRadioListItemInput[name='valueY'][value='2']");
    nextPage(4);
    
    // Time serie
    
    answerTimeSerie();
    nextPage(5);

    // Timeline
    
    waitPresent("input[name='queryPageType'][value='THESIS_TIMELINE']");
    clickOffset(".queryTimelineTrack", getElementWidth(".queryTimelineTrack") / 2, 5);
    nextPage(6);

    // Grouping
    
    waitPresent("input[name='queryPageType'][value='THESIS_GROUPING']");

    drag(".queryGroupingItemContainer .queryGroupingItem:nth-child(2)", ".queryGroupingGroupContainer .queryGroupingGroup:nth-child(1) .queryGroupingGroupItemContainer");
    drag(".queryGroupingItemContainer .queryGroupingItem:nth-child(3)", ".queryGroupingGroupContainer .queryGroupingGroup:nth-child(1) .queryGroupingGroupItemContainer");
    drag(".queryGroupingItemContainer .queryGroupingItem:nth-child(4)", ".queryGroupingGroupContainer .queryGroupingGroup:nth-child(2) .queryGroupingGroupItemContainer");
    nextPage(7);
    
    // Multi select
    
    waitPresent("input[name='queryPageType'][value='THESIS_MULTI_SELECT']");
    waitAndClick(".queryMultiselectQuestionContainer .queryMultiselectListItemContainer:nth-child(3) input");
    waitAndClick(".queryMultiselectQuestionContainer .queryMultiselectListItemContainer:nth-child(4) input");
    waitAndClick(".queryMultiselectQuestionContainer .queryMultiselectListItemContainer:nth-child(5) input");
    nextPage(8);
    
    // Order
    
    waitPresent("input[name='queryPageType'][value='THESIS_ORDER']");
    waitAndClick(".queryOrderingField .queryOrderingFieldItemContainer:nth-child(2) .queryOrderingFieldItemMoveUpButton");
    waitAndClick(".queryOrderingField .queryOrderingFieldItemContainer:nth-child(3) .queryOrderingFieldItemMoveDownButton");
    nextPage(9);
    
    // Form
    
    waitPresent("input[name='queryPageType'][value='FORM']");
    waitAndType(".queryFormFieldContainer input[type='text']", "text");
    nextPage(10);

    // Background form

    waitPresent("input[name='queryNextPageNumber'][value='11']");
    waitAndClick(".queryFormFieldContainer:nth-of-type(1) div:nth-of-type(1) input[type='radio']");
    waitAndClick(".queryFormFieldContainer:nth-of-type(2) div:nth-of-type(2) input[type='radio']");
    waitAndClick(".queryFormFieldContainer:nth-of-type(3) div:nth-of-type(3) input[type='radio']");
    waitAndClick(".queryFormFieldContainer:nth-of-type(4) div:nth-of-type(4) input[type='radio']");
    waitAndClick(".queryFormFieldContainer:nth-of-type(5) div:nth-of-type(5) input[type='radio']");
    nextPage(11);
    
    // Collage 2d
    
    waitPresent("input[name='queryPageType'][value='COLLAGE_2D']");
    assertVisible(".queryCollage2DQuestionFlotrContainer");
    
    finishQuery();
  }
  
  @Test
  public void testTextPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-text-page");
    navigate("/test/test-query");
    waitAndAssertText(".queryPageTitle", "Text page");
    assertText(".queryTextContainer div p", "Page with text");
  }

  @Test
  public void testScale1dRadioNextDisabledPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-scale1d-radio-page");
    navigate("/test/test-query");
    assertFinishDisabled();
    waitPresent("input[name='queryPageType'][value='THESIS_SCALE_1D']");
    waitAndClick(".queryScaleRadioListItemInput[value='4']");
    assertFinishNotDisabled();
    finishQuery();
    navigate("/test/test-query");
    assertFinishNotDisabled();
  }

  @Test
  public void testScale1dSliderNextDisabledPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-scale1d-slider-page");
    navigate("/test/test-query");
    assertFinishDisabled();
    clickSlider(0, 7, 2);
    assertFinishNotDisabled();
    finishQuery();
    navigate("/test/test-query");
    assertFinishNotDisabled();
  }

  @Test
  public void testScale2dRadioNextDisabledPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-scale2d-radio-page");
    navigate("/test/test-query");
    assertFinishDisabled();
    waitPresent("input[name='queryPageType'][value='THESIS_SCALE_2D']");
    waitAndClick(".queryScaleRadioListItemInput[name='valueX'][value='5']");
    waitAndClick(".queryScaleRadioListItemInput[name='valueY'][value='2']");
    assertFinishNotDisabled();
    finishQuery();
    navigate("/test/test-query");
    assertFinishNotDisabled();
  }

  @Test
  public void testScale2dSliderNextDisabledPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-scale2d-slider-page");
    navigate("/test/test-query");
    assertFinishDisabled();
    clickSlider(0, 7, 2);
    clickSlider(1, 6, 3);
    assertFinishNotDisabled();
    finishQuery();
    navigate("/test/test-query");
    assertFinishNotDisabled();
  }

  @Test
  public void testScale2dGraphNextDisabledPage() {
    login(ADMIN_EMAIL);
    createTestPanel();
    createTestQuery("test", "basic-scale2d-graph-page");
    navigate("/test/test-query");
    assertFinishDisabled();
    clickScale2dGraph(7,7, 2, 3);
    assertFinishNotDisabled();
    finishQuery();
    navigate("/test/test-query");
    assertFinishNotDisabled();
  }

  private void createTestPanel() {
    navigate("/");
    createPanel("test");
    panelMocker.addCreatedPanel("test");
  }
  
  private void createTestQuery(String panelUrl, String...pages) {
    Long folderId = folderMocker.findPanelRoot(panelUrl);
    long queryId = queryMocker.addQuery("test-query", "test-query", folderId, true, "Test Query", 0, true, null, "ACTIVE");
    long sectionId = queryMocker.addDefaultSection(queryId);
    
    for (int i = 0; i < pages.length; i++) {
      queryMocker.addPage(i, queryId, sectionId, pages[i]);
    }
    
    saveQuery(panelMocker.findPanelByRootFolder(folderId), queryId);
  }
  
  private void saveQuery(long panelId, long queryId) {
    navigate(String.format("/panel/admin/editquery.page?panelId=%d&queryId=%d", panelId, queryId));
    waitAndClick("#panelAdminQueryEditorBlockContent input[name='save']");
  }

  private void drag(String selector, String toSelector) {
    new Actions(getWebDriver())
      .clickAndHold(findElement(selector))
      .moveToElement(findElement(toSelector), 0, 0)
      .build()
      .perform();
    
    new Actions(getWebDriver())
      .release()
      .build()
      .perform();
  }

  private void answerTimeSerie() {
    waitPresent("input[name='queryPageType'][value='THESIS_TIME_SERIE']");

    int labelsWidth = 152;
    int margin = 20;
    int width = getElementWidth(".queryTimeSerieQuestionFlotrContainer");
    int steps = 11;
    int stepWidth = (width - ((margin * 2) + labelsWidth)) / (steps - 1) - 2;
    int offsetX = 0;
    
    for (int i = 0; i < steps; i++) {
      clickOffset(".queryTimeSerieQuestionFlotrContainer", labelsWidth + margin + offsetX + (i * stepWidth), (i + 1) * 20);
    }
  }
  
  private void nextPage(Integer page) {
    waitAndClick(String.format("input[name='%s']", "next"));
    waitUrlMatches(String.format(".*page=%d", page));
  }
  
  private void finishQuery() {
    waitAndClick(String.format("input[name='%s']", "finish"));
  }
  
  private void assertFinishDisabled() {
    assertNotNull("disabled", findElement(String.format("input[name='%s']", "finish")).getAttribute("disabled"));
  }
  
  private void assertFinishNotDisabled() {
    assertNull(findElement(String.format("input[name='%s']", "finish")).getAttribute("disabled"));
  }
  
  private void clickScale2dGraph(int xCount, int yCount, int x, int y) {
    String selector = ".queryScaleGraphQuestionFlotrContainer .flotr-canvas";
    int offsetX = SCALE2D_GRAPH_MARGIN_X + getElementWidth(selector) / xCount * x;
    int offsetY = SCALE2D_GRAPH_MARGIN_Y + getElementHeight(selector) / yCount * y;
    
    clickOffset(selector, offsetX, offsetY);
  }

  private void clickSlider(int index, int valueCount, int value) {
    String selector = ".queryScaleSliderTrack";
    List<WebElement> elements = findElements(selector);
    int width = getElementWidth(elements.get(index));
    clickOffset(elements.get(index), width / valueCount * value, 0);
  }

}
