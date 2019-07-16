package fi.metatavu.edelphi.batch;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;

/**
 * Abstract item writer with generic typings
 * 
 * @author Antti Leppä
 *
 * @param <T> type of items writer writes
 */
public abstract class TypedItemWriter <T> extends AbstractItemWriter {

  @SuppressWarnings("unchecked")
  @Override
  public void writeItems(List<Object> items) throws Exception {
    this.write((List<T>) items);
  }
  
  public abstract void write(List<T> items) throws Exception;
  
}
