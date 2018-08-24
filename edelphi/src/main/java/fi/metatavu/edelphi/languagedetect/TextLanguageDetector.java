package fi.metatavu.edelphi.languagedetect;

import java.io.IOException;
import java.util.List;

import com.google.api.client.repackaged.com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

/**
* This class is for detecting language from a text
*
* @author Ville Koivukangas
*/
public class TextLanguageDetector {
	
  private static TextLanguageDetector INSTANCE = null;
  
  public synchronized static TextLanguageDetector getInstance() throws IOException {
    if (INSTANCE == null) {
      INSTANCE = new TextLanguageDetector();
    }
    
    return INSTANCE;
  }
	
  private List<LanguageProfile> languageProfiles;
  private LanguageDetector languageDetector;
  private TextObjectFactory textObjectFactory;

  public TextLanguageDetector() throws IOException {
    languageProfiles = new LanguageProfileReader().readAllBuiltIn();
    languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
      .withProfiles(languageProfiles)
      .build();
    textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
  }

  /**
   * Get language from string
   * 
   * @param text Detect language from this String
   * @return String Detected language
   */
  public String getLanguage (String text) {
    String language = null;
    TextObject textObject = textObjectFactory.forText(text);
    com.google.common.base.Optional<LdLocale> lang = languageDetector.detect(textObject);
    
    if (lang.isPresent()) {
      language = lang.get().toString();
    }
    
    return language;
  }
	
}
