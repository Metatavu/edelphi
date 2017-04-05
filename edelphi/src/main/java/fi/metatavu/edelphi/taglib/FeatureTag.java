package fi.metatavu.edelphi.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import fi.metatavu.edelphi.dao.users.UserDAO;
import fi.metatavu.edelphi.domainmodel.features.Feature;
import fi.metatavu.edelphi.domainmodel.users.SubscriptionLevel;
import fi.metatavu.edelphi.domainmodel.users.User;
import fi.metatavu.edelphi.i18n.Messages;
import fi.metatavu.edelphi.pages.PageController;
import fi.metatavu.edelphi.smvcj.controllers.RequestController;
import fi.metatavu.edelphi.smvcj.controllers.RequestControllerMapper;
import fi.metatavu.edelphi.utils.SubscriptionLevelUtils;

public class FeatureTag extends BodyTagSupport {

  private static final long serialVersionUID = -278574417526284703L;
  
  private static final Logger logger = Logger.getLogger(FeatureTag.class.getName());

  private Feature feature;
  private String featurePage;

  public FeatureTag() {
    super();
  }

  @Override
  public int doStartTag() throws JspException {
    List<String> classes = new ArrayList<>();
    Map<String, String> attributes = new HashMap<>();
    
    classes.add("feature");
    
    Feature resolvedFeature = getResolvedFeature();
    if (resolvedFeature != null) {
      Long loggedUserId = null;
      HttpSession session = pageContext.getSession();
      if (session != null) {
        loggedUserId = (Long) session.getAttribute("loggedUserId");  
      }
      
      if (loggedUserId != null) {
        UserDAO userDAO = new UserDAO();
        User loggedUser = userDAO.findById(loggedUserId);
        if (loggedUser != null && !SubscriptionLevelUtils.isFeatureEnabled(loggedUser.getSubscriptionLevel(), resolvedFeature)) {
          classes.add("featureNotAvailableOnSubscriptionLevel");
          Locale locale = pageContext.getRequest().getLocale();
          attributes.put("title", getTooltip(resolvedFeature, loggedUser, locale));
        }
      }
    }
    
    attributes.put("class", StringUtils.join(classes, ' '));
    
    writeOut(String.format("<div %s>", getAttributesAsString(attributes)));
    
    return EVAL_BODY_INCLUDE;
  }

  private String getTooltip(Feature resolvedFeature, User loggedUser, Locale locale) {
    SubscriptionLevel minimumSubscriptionLevel = SubscriptionLevelUtils.getMinimumLevelFor(resolvedFeature);
    Messages messages = Messages.getInstance();
    String userLevel = messages.getText(locale, String.format("generic.subscriptionLevels.%s", loggedUser.getSubscriptionLevel()));
    String minimumLevel = messages.getText(locale, String.format("generic.subscriptionLevels.%s", minimumSubscriptionLevel));
    return messages.getText(locale, "generic.tooltips.featureNotAvailableNotSubscriptionLevel", new Object[] {
      userLevel,
      minimumLevel
    });
  }

  @Override
  public int doEndTag() throws JspException {
    writeOut("</div>");
    return EVAL_BODY_INCLUDE;
  }

  private void writeOut(String string) throws JspException {
    JspWriter writer = pageContext.getOut();
    try {
      writer.append(string);
    } catch (IOException e) {
      throw new JspException(e);
    }
  }

  @Override
  public void release() {
    super.release();
    feature = null;
    featurePage = null;
  }

  public void setFeature(Feature feature) {
    this.feature = feature;
  }
  
  public Feature getFeature() {
    return feature;
  }
  
  public void setFeaturePage(String featurePage) {
    this.featurePage = featurePage;
  }
  
  public String getFeaturePage() {
    return featurePage;
  }
  
  private Feature getResolvedFeature() {
    if (feature != null) {
      return feature;
    }
    
    if (getFeaturePage() != null) {
      RequestController requestController = RequestControllerMapper.getRequestController(getFeaturePage());
      if (requestController instanceof PageController) {
        return ((PageController) requestController).getFeature();
      }
      
      logger.log(Level.SEVERE, () -> String.format("Failed to resolve feature for featurePage %s", getFeaturePage()));
    } else {
      logger.log(Level.SEVERE, "Feature not specified on ed:feature tag");
    }

    return null;
  }

  private String getAttributesAsString(Map<String, String> attributes) {
    StringBuilder resultBuilder = new StringBuilder();
    
    for (Entry<String, String> attribute : attributes.entrySet()) {
      resultBuilder.append(String.format("%s=\"%s\"", attribute.getKey(), StringEscapeUtils.escapeXml11(attribute.getValue())));
    }
    
    return resultBuilder.toString();
  }
 
}
