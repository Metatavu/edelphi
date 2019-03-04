package fi.metatavu.edelphi.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.StreamingOutput;

/**
 * Implementation of JAX-RS StreamingOutput
 * 
 * @author Antti Leppä
 */
public class StreamingOutputImpl implements StreamingOutput {
    
  private InputStream inputStream;
  
  public StreamingOutputImpl(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void write(OutputStream output) throws IOException {
    byte[] buffer = new byte[1024 * 100];
    int bytesRead;
    
    while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
      output.write(buffer, 0, bytesRead);
      output.flush();
    }
    
    output.flush();
  }
  
}