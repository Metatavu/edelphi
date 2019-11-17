package fi.metatavu.edelphi.rest.test.functional.builder;

import fi.metatavu.edelphi.rest.test.functional.builder.auth.TestBuilderAuthentication;
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder;
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider;
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication;

public class TestBuilder extends AbstractTestBuilder {

  @Override
  public AuthorizedTestBuilderAuthentication createTestBuilderAuthentication(AbstractTestBuilder testBuilder, AccessTokenProvider accessTokenProvider) {
    return new TestBuilderAuthentication(testBuilder, accessTokenProvider);
  }

}
