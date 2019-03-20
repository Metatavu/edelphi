package fi.metatavu.edelphi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;

public class StreamUtils {

  public static byte[] readStreamToByteArray(InputStream inputStream) throws IOException {
    return IOUtils.toByteArray(inputStream);
  }
  
  public static String readStreamToString(InputStream inputStream, String charset) throws UnsupportedEncodingException, IOException {
    return new String(readStreamToByteArray(inputStream), charset);
  }
}
