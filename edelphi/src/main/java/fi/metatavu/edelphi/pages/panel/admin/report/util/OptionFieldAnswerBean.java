package fi.metatavu.edelphi.pages.panel.admin.report.util;

import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionField;

public class OptionFieldAnswerBean extends FormFieldAnswerBean {

  public OptionFieldAnswerBean(String fieldType, QueryOptionField queryOptionField, Integer fieldIndex) {
    super(fieldType, fieldIndex);
    this.optionField = queryOptionField;
  }

  public QueryOptionField getOptionField() {
    return optionField;
  }

  private final QueryOptionField optionField;

}
