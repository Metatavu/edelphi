package fi.metatavu.edelphi.domainmodel.querydata;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.metatavu.edelphi.domainmodel.querymeta.QueryOptionFieldOption;

@Entity
@PrimaryKeyJoinColumn(name="id")
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class QueryQuestionOptionAnswer extends QueryQuestionAnswer {

  public void setOption(QueryOptionFieldOption option) {
    this.option = option;
  }

  public QueryOptionFieldOption getOption() {
    return option;
  }
  
  @ManyToOne
  private QueryOptionFieldOption option;
}
