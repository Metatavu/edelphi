package fi.metatavu.edelphi.batch;

import javax.batch.api.chunk.AbstractItemReader;

/**
 * Abstract item reader with generic typings
 * 
 * @author Antti Leppä
 *
 * @param <T> type of items reader reads
 */
public abstract class TypedItemReader <T> extends AbstractItemReader {

  @Override
  public Object readItem() throws Exception {
    return this.read();
  }

  public abstract T read() throws Exception;
  
}
