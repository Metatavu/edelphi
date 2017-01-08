package fi.metatavu.edelphi.test.mock;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.edelphi.test.mock.QueryPageMock.SettingType;

public class QueryMocker extends AbstractResourceMocker {

  private static Logger logger = Logger.getLogger(QueryMocker.class.getName());

  private List<Long> queryIds = new ArrayList<>();
  private List<Long> sectionIds = new ArrayList<>();
  private List<PageMeta> pages = new ArrayList<>();
  private List<Long> pageSettingIds = new ArrayList<>();

  public QueryMocker mock() {
    return this;
  }

  public QueryMocker cleanup() {
    for (Long pageSettingId : pageSettingIds) {
      deleteQueryPageSetting(pageSettingId);
    }

    for (PageMeta page : pages) {
      deleteQueryPage(page.getId());
    }

    for (Long sectionId : sectionIds) {
      deleteQuerySection(sectionId);
    }

    for (Long queryId : queryIds) {
      deleteQuery(queryId);
    }

    return this;
  }

  public long addQuery(String name, String urlName, Long parentFolderId, boolean visible, String description,
      Integer indexNumber, boolean allowEditReply, Date closes, String state) {
    Date now = new Date();
    Long creatorId = 1l;
    return createQuery(name, urlName, parentFolderId, visible, creatorId, now, creatorId, now, description, indexNumber,
        allowEditReply, closes, state);
  }

  public long addDefaultSection(Long queryId) {
    Date now = new Date();
    Long creatorId = 1l;
    return createQuerySection(queryId, "default", creatorId, now, creatorId, now, true, 0, true, true);
  }

  public long addSection(Long queryId, String title, boolean visible, Integer sectionNumber, boolean commentable,
      boolean viewDiscussions) {
    Date now = new Date();
    Long creatorId = 1l;
    return createQuerySection(queryId, title, creatorId, now, creatorId, now, visible, sectionNumber, commentable,
        viewDiscussions);
  }

  public long addPage(Integer pageNumber, Long queryId, Long sectionId, String templateName) {
    return mockPage(pageNumber, queryId, sectionId, readQueryPageMock(templateName));
  }

  private long mockPage(Integer pageNumber, Long queryId, Long sectionId, QueryPageMock pageMock) {
    String type = pageMock.getType();
    String title = pageMock.getTitle();
    Boolean visible = pageMock.getVisible();
    Date now = new Date();
    Long creatorId = 1l;
    long id = createQueryPage(pageNumber, queryId, creatorId, now, creatorId, now, type, title, sectionId, visible);
    Map<String, SettingType> settingTypes = pageMock.getSettingTypes();

    for (Entry<String, String> settingEntry : pageMock.getSettings().entrySet()) {
      String settingKey = settingEntry.getKey();
      String settingValue = settingEntry.getValue();
      SettingType settingType = SettingType.TEXT;

      if (settingTypes != null && settingTypes.containsKey(settingKey)) {
        settingType = settingTypes.get(settingKey);
      }

      if (settingType == SettingType.QUERY_PAGE_IDS_BY_TYPE) {
        settingValue = StringUtils.join(getPageIdsByType(settingValue), ",");
      }

      createQueryPageSetting(id, settingKey, settingValue);
    }

    return id;
  }

  private long createQueryPageSetting(Long queryPageId, String key, String value) {
    long id = getNextId("QueryPageSetting");
    String sql = "INSERT INTO " + "  QueryPageSetting (id, key_id, queryPage_id, value) " + "VALUES "
        + "  (?, (select id from QueryPageSettingKey where name = ?), ?, ?)";

    executeSql(sql, id, key, queryPageId, value);

    return id;
  }

  private QueryPageMock readQueryPageMock(String name) {
    ObjectMapper objectMapper = new ObjectMapper();
    String template = String.format("fi/metatavu/edelphi/test/mock/%s.json", name);

    try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(template)) {
      return objectMapper.readValue(templateStream, QueryPageMock.class);
    } catch (IOException e) {
      logger.log(Level.SEVERE, String.format("Failed to read template query page %s", name), e);
      fail(String.format("Failed to read template query page %s", template));
    }

    return null;
  }

  private long createQuery(String name, String urlName, Long parentFolderId, boolean visible, Long creatorId,
      Date created, Long modifierId, Date modified, String description, Integer indexNumber, boolean allowEditReply,
      Date closes, String state) {
    long id = getNextId("Resource");
    createQuery(id, name, urlName, parentFolderId, visible, creatorId, created, modifierId, modified, description,
        indexNumber, allowEditReply, closes, state);
    queryIds.add(id);
    return id;
  }

  private void createQuery(Long id, String name, String urlName, Long parentFolderId, boolean visible, Long creatorId,
      Date created, Long modifierId, Date modified, String description, Integer indexNumber, boolean allowEditReply,
      Date closes, String state) {
    String resourceSql = "INSERT INTO "
        + "  `resource` (`id`, `name`, `urlName`, `parentFolder_id`, `type`, `visible`, `archived`, `creator_id`, `created`, `lastModifier_id`, `lastModified`, `description`, `indexNumber`) "
        + "VALUES " + "  (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String querySql = "INSERT INTO " + "  `query` (`id`, `allowEditReply`, `closes`, `state`) " + "VALUES "
        + "  (?,?,?,?)";

    executeSql(resourceSql, id, name, urlName, parentFolderId, "QUERY", visible, false, creatorId, created, modifierId,
        modified, description, indexNumber);
    executeSql(querySql, id, allowEditReply, closes, state);
  }

  private Long createQuerySection(Long queryId, String title, Long creatorId, Date created, Long modifierId,
      Date modified, boolean visible, Integer sectionNumber, boolean commentable, boolean viewDiscussions) {
    long id = getNextId("QuerySection");
    createQuerySection(id, queryId, title, creatorId, created, modifierId, modified, visible, sectionNumber,
        commentable, viewDiscussions);
    sectionIds.add(id);
    return id;
  }

  private void createQuerySection(Long id, Long queryId, String title, Long creatorId, Date created, Long modifierId,
      Date modified, boolean visible, Integer sectionNumber, boolean commentable, boolean viewDiscussions) {
    String sql = "INSERT INTO "
        + "  `querysection` (`id`, `query_id`, `title`, `creator_id`, `created`, `lastModifier_id`, `lastModified`, `visible`, `archived`, `sectionNumber`, `commentable`, `viewDiscussions`) "
        + "VALUES " + "  (?,?,?,?,?,?,?,?,?,?,?,?)";

    executeSql(sql, id, queryId, title, creatorId, created, modifierId, modified, visible, false, sectionNumber,
        commentable, viewDiscussions);
  }

  private long createQueryPage(Integer pageNumber, Long queryId, Long creatorId, Date created, Long modifierId, Date modified,
      String type, String title, Long sectionId, boolean visible) {
    long id = getNextId("QueryPage");
    createQueryPage(id, pageNumber, creatorId, created, modifierId, modified, type, title, sectionId, visible);
    pages.add(new PageMeta(id, queryId, sectionId, type));
    return id;
  }

  private void createQueryPage(Long id, Integer pageNumber, Long creatorId, Date created, Long modifierId,
      Date modified, String type, String title, Long sectionId, boolean visible) {
    String sql = "INSERT INTO "
        + "  `querypage` (`id`, `pageNumber`, `archived`, `creator_id`, `created`, `lastModifier_id`, `lastModified`, `pageType`, `title`, `querySection_id`, `visible`) "
        + "VALUES " + "  (?,?,?,?,?,?,?,?,?,?,?)";

    executeSql(sql, id, pageNumber, false, creatorId, created, modifierId, modified, type, title, sectionId, visible);
  }

  private void deleteQuery(Long queryId) {
    executeSql("DELETE FROM QueryReply where query_id = ?", queryId);
    executeSql("DELETE FROM Query where id = ?", queryId);
    deleteResource(queryId);
  }

  private void deleteQuerySection(Long sectionId) {
    executeSql("delete from QuerySection where id = ?", sectionId);
  }

  private void deleteQueryPage(Long pageId) {
    executeSql("DELETE FROM QueryQuestionOptionGroupOptionAnswer where id in (SELECT id FROM QueryQuestionOptionAnswer WHERE option_id in (SELECT id FROM QueryOptionFieldOption WHERE optionField_id in (SELECT id from QueryField WHERE queryPage_id = ?)))", pageId);
    executeSql("DELETE FROM QueryQuestionOptionAnswer WHERE option_id in (SELECT id FROM QueryOptionFieldOption WHERE optionField_id in (SELECT id from QueryField WHERE queryPage_id = ?))", pageId);
    executeSql("DELETE FROM QueryQuestionTextAnswer WHERE id in (SELECT id FROM QueryQuestionAnswer WHERE queryField_id in (SELECT id from QueryField WHERE queryPage_id = ?))", pageId);
    executeSql("DELETE FROM QueryQuestionNumericAnswer WHERE id in (SELECT id FROM QueryQuestionAnswer WHERE queryField_id in (SELECT id from QueryField WHERE queryPage_id = ?))", pageId);
    executeSql("DELETE FROM __QueryQuestionMultiOptionAnswers WHERE answer_id in (SELECT id FROM QueryQuestionAnswer WHERE queryField_id in (SELECT id from QueryField WHERE queryPage_id = ?))", pageId);
    executeSql("DELETE FROM QueryQuestionMultiOptionAnswer WHERE id in (SELECT id FROM QueryQuestionAnswer WHERE queryField_id in (SELECT id from QueryField WHERE queryPage_id = ?))", pageId);
    executeSql("DELETE FROM QueryQuestionAnswer WHERE queryField_id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryOptionFieldOptionGroup WHERE optionField_id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryOptionFieldOption WHERE optionField_id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryOptionField WHERE id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryTextField WHERE id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryNumericField WHERE id in (SELECT id from QueryField WHERE queryPage_id = ?)", pageId);
    executeSql("DELETE FROM QueryField WHERE queryPage_id = ?", pageId);
    executeSql("DELETE FROM QueryPageSetting WHERE queryPage_id = ?", pageId);
    executeSql("DELETE FROM QueryPage WHERE id = ?", pageId);
  }

  private void deleteQueryPageSetting(Long id) {
    executeSql("delete from QueryPageSetting where id = ?", id);
  }
  
  private List<Long> getPageIdsByType(String type) {
    List<Long> result = new ArrayList<>();
    
    for (PageMeta page : pages) {
      if (StringUtils.equals(page.getType(), type)) {
        result.add(page.getId());
      }
    }
    
    return result;
  }

  private class PageMeta {

    private long id;
    private long queryId;
    private long sectionId;
    private String type;

    public PageMeta(long id, long queryId, long sectionId, String type) {
      super();
      this.id = id;
      this.queryId = queryId;
      this.sectionId = sectionId;
      this.type = type;
    }

    public long getId() {
      return id;
    }

    @SuppressWarnings("unused")
    public long getQueryId() {
      return queryId;
    }

    @SuppressWarnings("unused")
    public long getSectionId() {
      return sectionId;
    }

    public String getType() {
      return type;
    }

  }

}
