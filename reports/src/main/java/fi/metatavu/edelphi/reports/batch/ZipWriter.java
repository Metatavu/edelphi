package fi.metatavu.edelphi.reports.batch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import fi.metatavu.edelphi.batch.TypedItemWriter;

/**
 * Batch writer for writing files into a zip folder
 * 
 * Writer "saves" resulting zip in job context transientUserData as a BinaryFile
 * 
 * @author Antti Leppä
 */
@Named
public class ZipWriter extends TypedItemWriter<BinaryFile> {

  @Inject
  private Logger logger;

  @Inject
  private JobContext jobContext;
  
  private File tempFile;

  private FileSystem zipfs;
  
  @Override
  public void open(Serializable checkpoint) throws Exception {
    super.open(checkpoint);

    Map<String, String> zipProperties = new HashMap<>();
    zipProperties.put("create", "true");
    zipProperties.put("encoding", "UTF-8");

    tempFile = new File(String.format("%s/%s.zip", System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()));
    zipfs = FileSystems.newFileSystem(URI.create(String.format("jar:file:%s", tempFile.getAbsolutePath())), zipProperties);
  }

  @Override
  public void close() throws Exception {
    zipfs.close(); 
    
    try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
      jobContext.setTransientUserData(new BinaryFile("report.zip", "application/zip", IOUtils.toByteArray(fileInputStream)));
    }
    
    Files.delete(tempFile.toPath());

    super.close();
  }

  @Override
  public void write(List<BinaryFile> binaryFiles) throws Exception {
    logger.info("Zipping {} files", binaryFiles.size());

    for (BinaryFile binaryFile : binaryFiles) {
      Path path = zipfs.getPath(binaryFile.getName());

      try (ByteArrayInputStream pdfStream = new ByteArrayInputStream(binaryFile.getData())) {
        Files.copy(pdfStream, path);
      }
    }
  }

}
