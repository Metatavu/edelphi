package fi.metatavu.edelphi.queries.batch;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.JobProperty;
import fi.metatavu.edelphi.batch.i18n.BatchMessages;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.metatavu.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPage;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.metatavu.edelphi.domainmodel.querylayout.QueryPageType;
import fi.metatavu.edelphi.domainmodel.resources.Query;
import fi.metatavu.edelphi.mail.Mailer;
import fi.metatavu.edelphi.queries.QueryController;
import fi.metatavu.edelphi.queries.QueryPageController;

/**
 * Batchlet for finalizing query copy batch operation
 * 
 * @author Antti Leppä
 */
@Named
public class CopyQueryFinalizeBatchlet extends AbstractBatchlet {

  @Inject
  private Logger logger;

  @Inject
  private QueryController queryController;

  @Inject
  private BatchMessages batchMessages;
  
  @Inject
  private QueryPageController queryPageController;

  @Inject
  private QueryCopyBatchContext queryCopyBatchContext;

  @Inject
  private Mailer mailer;

  @Inject
  @JobProperty
  private Locale locale;

  @Inject
  @JobProperty
  private String baseUrl;

  @Inject
  @JobProperty
  private Long targetPanelId;

  @Inject
  @JobProperty
  private String deliveryEmail;
  
  @Inject
  private QueryPageSettingKeyDAO queryPageSettingKeyDAO;

  @Inject
  private QueryPageSettingDAO queryPageSettingDAO;
  
  @Override
  @Transactional (value = TxType.REQUIRES_NEW)
  public String process() throws Exception { 
    Query newQuery = queryController.findQueryById(queryCopyBatchContext.getNewQueryId());

    processCollagePages(newQuery);
    queryController.unarchiveQuery(newQuery);
    
    String newQueryUrl = String.format("%s/panel/admin/editquery.page?queryId=%d&panelId=%d", baseUrl, newQuery.getId(), targetPanelId);

    String newQueryName = newQuery.getName();
    String subject = batchMessages.getText(locale, "batch.copyQuery.success.mail.subject", newQueryName);
    String content = batchMessages.getText(locale, "batch.copyQuery.success.mail.contents", newQueryName, newQueryUrl);

    Email email = EmailBuilder.startingBlank()
      .from("noreply@edelphi.org")
      .to(deliveryEmail)
      .withSubject(subject)
      .withHTMLText(content)
      .buildEmail();
    
    mailer.sendMail(email);
    
    logger.info(String.format("Copy query mail sent into address %s", deliveryEmail));
  
    return "DONE";
  }
  
  /**
   * Processes query collage pages
   * 
   * @param query query
   */
  private void processCollagePages(Query query) {
    List<QueryPage> collagePages = queryPageController.listQueryPagesByType(query, QueryPageType.COLLAGE_2D);
    Map<Long, Long> queryPageIdMap = queryCopyBatchContext.getQueryPageIdMap();
    
    if (!collagePages.isEmpty()) {
      for (QueryPage collagePage : collagePages) {

        // Included pages
        
        QueryPageSettingKey key = queryPageSettingKeyDAO.findByName("collage2d.includedPages");
        QueryPageSetting includedPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, collagePage);
        if (includedPageSetting != null) {
          String[] includedPages = includedPageSetting.getValue().split("&");
          
          for (int i = 0; i < includedPages.length; i++) {
            Long originalId = NumberUtils.createLong(includedPages[i]);
            includedPages[i] = queryPageIdMap.get(originalId).toString();
          }
          
          if (includedPages.length > 0) {
            queryPageSettingDAO.updateValue(includedPageSetting, StringUtils.join(includedPages, '&'));
          }
        }
        
        // Included page settings
        
        key = queryPageSettingKeyDAO.findByName("collage2d.pageSettings");
        QueryPageSetting pageSettingsSetting = queryPageSettingDAO.findByKeyAndQueryPage(key, collagePage);
        if (pageSettingsSetting != null) {
          String[] pageSettings = pageSettingsSetting.getValue().split("&");
          for (int i = 0; i < pageSettings.length; i++) {
            int eqPos = pageSettings[i].indexOf('=');
            Long originalId = NumberUtils.createLong(pageSettings[i].substring(0, eqPos));
            Long newId = queryPageIdMap.get(originalId);
            pageSettings[i] = newId + pageSettings[i].substring(eqPos);
          }
          if (pageSettings.length > 0) {
            queryPageSettingDAO.updateValue(pageSettingsSetting, StringUtils.join(pageSettings, '&'));
          }
        }
      }
    }
  }
  
}
