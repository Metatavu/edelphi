package fi.metatavu.edelphi.jsons.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.edelphi.smvcj.Severity;
import fi.metatavu.edelphi.smvcj.SmvcRuntimeException;
import fi.metatavu.edelphi.smvcj.controllers.JSONRequestContext;
import fi.metatavu.edelphi.DelfoiActionName;
import fi.metatavu.edelphi.EdelfoiStatusCode;
import fi.metatavu.edelphi.dao.querylayout.QueryPageDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.dao.querylayout.QuerySectionDAO;
import fi.metatavu.edelphi.dao.resources.FolderDAO;
import fi.metatavu.edelphi.dao.resources.QueryDAO;
import fi.metatavu.edelphi.dao.resources.ResourceDAO;
import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.querylayout.QuerySection;
import fi.metatavu.edelphi.domainmodel.resources.Folder;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.domainmodel.resources.QueryState;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.jsons.JSONController;
import fi.metatavu.edelphi.query.QueryPageHandler;
import fi.metatavu.edelphi.query.QueryPageHandlerFactory;
import fi.metatavu.edelphi.utils.ResourceUtils;
import fi.metatavu.edelphi.utils.SystemUtils;

public class SaveQueryJSONRequestController extends JSONController {

  public SaveQueryJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    UserDAO userDAO = new UserDAO();
    ResourceDAO resourceDAO = new ResourceDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    FolderDAO folderDAO = new FolderDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    String queryIdParam = jsonRequestContext.getString("queryId");
    boolean createNewQuery = "NEW".equals(queryIdParam);
    Integer sectionCount = jsonRequestContext.getInteger("sectionCount");

    Long queryId = createNewQuery == false ? NumberUtils.createLong(queryIdParam) : null;
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);
    String description = jsonRequestContext.getString("description");
    Boolean allowEditReply = "1".equals(jsonRequestContext.getString("allowEditReply"));
    QueryState state = QueryState.valueOf(jsonRequestContext.getString("state"));
    // TODO: Remove this property
    Date closes = null;

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Query query = null;

    if (createNewQuery) {
      Folder parentFolder = folderDAO.findById(parentFolderId);
      if (ResourceUtils.isUrlNameAvailable(urlName, parentFolder)) {
        Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
        query = queryDAO.create(parentFolder, name, urlName, allowEditReply, description, state, closes, indexNumber, loggedUser);
      }
      else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
      }
    }
    else {
      query = queryDAO.findById(queryId);

      if (ResourceUtils.isUrlNameAvailable(urlName, query.getParentFolder(), query)) {
        resourceDAO.updateDescription(query, description, loggedUser);

        queryDAO.updateName(query, loggedUser, name, urlName);
        queryDAO.updateAllowEditReply(query, loggedUser, allowEditReply);
        queryDAO.updateState(query, loggedUser, state);
        queryDAO.updateCloses(query, loggedUser, closes);
      }
      else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
      }
    }

    // Special handling for collage pages, Part I :/

    List<QueryPage> collagePages = new ArrayList<QueryPage>();
    HashMap<String, Long> pageIds = new HashMap<String, Long>();

    // Default save logic

    int pageNumber = 0;
    int sectionNumber = 0;
    int newPageCount = 0;
    int removedPageCount = 0;

    int newSectionCount = 0;
    int removedSectionCount = 0;

    for (int sectionIndex = 0; sectionIndex < sectionCount; sectionIndex++) {
      String sectionPrefix = "section." + sectionIndex + ".";

      String sectionIdParam = jsonRequestContext.getString(sectionPrefix + "id");
      String sectionTitle = jsonRequestContext.getString(sectionPrefix + "title");
      Boolean sectionVisible = "1".equals(jsonRequestContext.getString(sectionPrefix + "visible"));
      Boolean sectionCommentable = "1".equals(jsonRequestContext.getString(sectionPrefix + "commentable"));
      Boolean sectionViewDiscussions = "1".equals(jsonRequestContext.getString(sectionPrefix + "viewDiscussions"));

      boolean createNewSection = jsonRequestContext.getBoolean(sectionPrefix + "isNew");
      boolean deleteSection = jsonRequestContext.getBoolean(sectionPrefix + "isDeleted");
      QuerySection querySection;

      Integer pageCount = jsonRequestContext.getInteger(sectionPrefix + "pageCount");

      sectionNumber++;
      
      if (!deleteSection && (sectionTitle == null || "".equals(sectionTitle))) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNNAMED_QUERY_SECTION, messages.getText(locale, "exception.1037.unnamedQuerySection", new Integer[] { sectionNumber }));
      }

      if (createNewSection) {
        // Skip a section that's marked as new and deleted
        if (deleteSection) {
          jsonRequestContext.addResponseParameter("removedSection." + removedSectionCount + ".id", sectionIdParam);
          removedSectionCount++;
          continue;
        }
        querySection = querySectionDAO.create(loggedUser, query, sectionTitle, sectionNumber, sectionVisible, sectionCommentable, sectionViewDiscussions);
        jsonRequestContext.addResponseParameter("newSection." + newSectionCount + ".id", querySection.getId());
        jsonRequestContext.addResponseParameter("newSection." + newSectionCount + ".tempId", sectionIdParam);
        newSectionCount++;
      }
      else {
        querySection = querySectionDAO.findById(NumberUtils.createLong(sectionIdParam));
        querySectionDAO.updateTitle(querySection, sectionTitle, loggedUser);
        querySectionDAO.updateVisible(querySection, sectionVisible, loggedUser);
        querySectionDAO.updateSectionNumber(querySection, sectionNumber, loggedUser);
        querySectionDAO.updateCommentable(querySection, sectionCommentable, loggedUser);
        querySectionDAO.updateViewDiscussions(querySection, sectionViewDiscussions, loggedUser);

        if (deleteSection) {
          jsonRequestContext.addResponseParameter("removedSection." + removedSectionCount + ".id", querySection.getId());
          removedSectionCount++;
          querySectionDAO.archive(querySection);
          continue;
        }
      }

      for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
        String pagePrefix = sectionPrefix + "page." + pageIndex + ".";

        String queryPageIdParam = jsonRequestContext.getString(pagePrefix + "id");
        boolean createNewPage = jsonRequestContext.getBoolean(pagePrefix + "isNew");
        boolean deletePage = jsonRequestContext.getBoolean(pagePrefix + "isDeleted");
        boolean editablePage = jsonRequestContext.getBoolean(pagePrefix + "isEditable");
        Long queryPageId = createNewPage ? null : NumberUtils.createLong(queryPageIdParam);
        
        QueryPageType queryPageType = QueryPageType.valueOf(jsonRequestContext.getString(pagePrefix + "type"));
        String title = StringUtils.abbreviate(jsonRequestContext.getString(pagePrefix + "title"), SystemUtils.MAX_QUERY_PAGE_TITLE);
        QueryPage queryPage;
        QueryPageHandler handler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPageType);

        if (editablePage && !deletePage && (title == null || "".equals(title))) {
          throw new SmvcRuntimeException(EdelfoiStatusCode.UNNAMED_QUERY_PAGE, messages.getText(locale, "exception.1038.unnamedQueryPage", new Integer[] { pageIndex + 1 }));
        }

        Map<String, String> settings = new HashMap<String, String>();
        Integer optionCount = jsonRequestContext.getInteger(pagePrefix + "optionCount");
        for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
          String optionPrefix = pagePrefix + "option." + optionIndex + ".";
          String optionName = jsonRequestContext.getString(optionPrefix + "name");
          String optionValue = jsonRequestContext.getString(optionPrefix + "value");
          settings.put(optionName, optionValue);
        }
        
        if (editablePage) {
          if (createNewPage) {
            // Skip page that's new and deleted
            if (deletePage) {
              jsonRequestContext.addResponseParameter("removedPage." + removedPageCount + ".id", queryPageIdParam);
              removedPageCount++;
              continue;
            }

            queryPage = queryPageDAO.create(loggedUser, querySection, queryPageType, pageNumber, title, Boolean.TRUE);
            jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".id", queryPage.getId());
            jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".tempId", queryPageIdParam);
            jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".number", queryPage.getPageNumber());
            jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".sectionId", queryPage.getQuerySection().getId());
            newPageCount++;

            // Special handling for collage pages, Part II :/

            if (queryPageIdParam != null && queryPage.getId() != null) {
              pageIds.put(queryPageIdParam, queryPage.getId());
            }
            if (queryPage.getPageType() == QueryPageType.COLLAGE_2D) {
              collagePages.add(queryPage);
            }
          }
          else {
            queryPage = queryPageDAO.findById(queryPageId);
            queryPageDAO.updateSection(queryPage, querySection, loggedUser);
            queryPageDAO.updateTitle(queryPage, title, loggedUser);
            queryPageDAO.updatePageNumber(queryPage, pageNumber, loggedUser);

            // Special handling for collage pages, Part III :/

            if (!deletePage) {
              pageIds.put(queryPageId.toString(), queryPageId);
              if (queryPage.getPageType() == QueryPageType.COLLAGE_2D) {
                collagePages.add(queryPage);
              }
            }
          }

          handler.updatePageOptions(settings, queryPage, loggedUser, false);

          if (deletePage) {
            jsonRequestContext.addResponseParameter("removedPage." + removedPageCount + ".id", queryPage.getId());
            removedPageCount++;
            queryPageDAO.archive(queryPage);
          }
        }
        else {
          queryPage = queryPageDAO.findById(queryPageId);
          queryPageDAO.updateSection(queryPage, querySection, loggedUser);
          queryPageDAO.updatePageNumber(queryPage, pageNumber, loggedUser);
          handler.updatePageOptions(settings, queryPage, loggedUser, true);

          pageIds.put(queryPageIdParam, queryPage.getId());
          if (queryPage.getPageType() == QueryPageType.COLLAGE_2D) {
            collagePages.add(queryPage);
          }
        }

        if (!deletePage) {
          pageNumber++;
        }
      }
    }

    // Special handling for collage pages, Part IV :/

    if (!collagePages.isEmpty()) {
      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      for (QueryPage collagePage : collagePages) {

        // Included pages; remove deleted pages and replace temporary ids of new pages

        QueryPageSettingKey key = queryPageSettingKeyDAO.findByName("collage2d.includedPages");
        QueryPageSetting includedPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, collagePage);
        if (includedPageSetting != null) {
          List<String> includedPageIds = new ArrayList<String>();
          String[] includedPages = includedPageSetting.getValue().split("&");
          for (int i = 0; i < includedPages.length; i++) {
            if (pageIds.containsKey(includedPages[i])) {
              includedPageIds.add(pageIds.get(includedPages[i]).toString());
            }
          }
          if (!includedPageIds.isEmpty()) {
            queryPageSettingDAO.updateValue(includedPageSetting, StringUtils.join(includedPageIds, '&'));
          }
        }

        // Included page settings; remove deleted pages and replace temporary ids of new pages

        key = queryPageSettingKeyDAO.findByName("collage2d.pageSettings");
        QueryPageSetting pageSettingsSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, collagePage);
        if (pageSettingsSetting != null) {
          List<String> pageSettingsList = new ArrayList<String>();
          String[] pageSettings = pageSettingsSetting.getValue().split("&");
          for (int i = 0; i < pageSettings.length; i++) {
            int eqPos = pageSettings[i].indexOf('=');
            String pageIdPart = pageSettings[i].substring(0, eqPos);
            String rgbPart = pageSettings[i].substring(eqPos);
            if (pageIds.containsKey(pageIdPart)) {
              pageSettingsList.add(pageIds.get(pageIdPart) + rgbPart);
            }
          }
          if (!pageSettingsList.isEmpty()) {
            queryPageSettingDAO.updateValue(pageSettingsSetting, StringUtils.join(pageSettingsList, '&'));
          }
        }
      }
    }

    jsonRequestContext.addResponseParameter("newPageCount", newPageCount);
    jsonRequestContext.addResponseParameter("removedPageCount", removedPageCount);
    jsonRequestContext.addResponseParameter("newSectionCount", newSectionCount);
    jsonRequestContext.addResponseParameter("removedSectionCount", removedSectionCount);
    jsonRequestContext.addResponseParameter("queryId", query.getId());

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panelAdmin.block.query.savedMessage"));
  }
}
