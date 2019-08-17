package fi.metatavu.edelphi.batch;

import javax.batch.api.chunk.ItemProcessor;

/**
 * Abstract item processor with generic typings
 * 
 * @author Antti Leppä
 *
 * @param <I> type of items processor input
 * @param <R> type of items processor returns
 */
public abstract class TypedItemProcessor <I, R> implements ItemProcessor {
  
  public abstract R process(I item) throws Exception;

  @SuppressWarnings("unchecked")
  @Override
  public Object processItem(Object item) throws Exception {
    return process((I) item);
  }

}
