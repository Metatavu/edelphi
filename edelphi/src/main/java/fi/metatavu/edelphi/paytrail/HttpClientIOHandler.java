package fi.metatavu.edelphi.paytrail;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import fi.metatavu.paytrail.io.IOHandler;
import fi.metatavu.paytrail.io.IOHandlerResult;

public class HttpClientIOHandler implements IOHandler {

	@Override
	public IOHandlerResult doPost(String merchantId, String merchantSecret, String url, String data) throws IOException {
	  URI uri = URI.create(url);
	  HttpHost target = new HttpHost(uri.getHost(), uri.getPort());
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials(merchantId, merchantSecret));
    
	  try (CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build()) {
	    HttpPost httpPost = new HttpPost(url);
      
	    httpPost.setHeader("Content-Type", " application/json");
      httpPost.setHeader("Accept", " application/json");
      httpPost.setHeader("X-Verkkomaksut-Api-Version", "1");
      
      AuthCache authCache = new BasicAuthCache();
      BasicScheme basicAuth = new BasicScheme();
      authCache.put(target, basicAuth);
      HttpClientContext localContext = HttpClientContext.create();
      localContext.setAuthCache(authCache);
      
      httpPost.setEntity(new StringEntity(data, "UTF-8"));
      HttpResponse response = client.execute(httpPost);
      
      HttpEntity entity = response.getEntity();
      try {
        int status = response.getStatusLine().getStatusCode();
        if (status == 204) {
          // No Content
          return new IOHandlerResult(status, null);
        }
  
        return new IOHandlerResult(status, IOUtils.toString(entity.getContent()));
      } finally {
        EntityUtils.consume(entity);
      }
	  }
	}


}
