package fi.metatavu.edelphi.paytrail;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import fi.metatavu.paytrail.io.IOHandler;
import fi.metatavu.paytrail.io.IOHandlerResult;

public class HttpClientIOHandler implements IOHandler {
  
  private static Logger logger = Logger.getLogger(HttpClientIOHandler.class.getName());

	@Override
	public IOHandlerResult doPost(String merchantId, String merchantSecret, String url, String data) throws IOException {
	  URI uri = URI.create(url);
	  HttpHost target = new HttpHost(uri.getHost(), uri.getPort());
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials(merchantId, merchantSecret));
    
	  try (CloseableHttpClient client = createHttpClient(credsProvider)) {
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
	  } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
	    logger.log(Level.SEVERE, "Failed to create client", e);
	    throw new IOException(e);
    }
	}

  private CloseableHttpClient createHttpClient(CredentialsProvider credsProvider) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    return HttpClients.custom()
      .setDefaultCredentialsProvider(credsProvider)
      .setSSLSocketFactory(createSSLConnectionSocketFactory())
      .build();
  }

	private SSLConnectionSocketFactory createSSLConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
	  SSLContextBuilder builder = new SSLContextBuilder();
    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    return new SSLConnectionSocketFactory(builder.build());
	}

}
