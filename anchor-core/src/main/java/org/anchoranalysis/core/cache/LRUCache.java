package org.anchoranalysis.core.cache;

import java.util.concurrent.ExecutionException;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.index.GetOperationFailedException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * A cache that discards items that haven't being used recently or frequently (as per Guava's size-based eviction's defaults)
 * 
 * <p>See <a href="https://github.com/google/guava/wiki/CachesExplained>Guava's Caches Explained</a></p>
 * <p>It's thread-safe.</p>
 * 
 * @author Owen Feehan
 *
 */
public class LRUCache<K,V> {

	private LoadingCache<K, V> cache;

	/** Calculates a value for a given key */
	@FunctionalInterface
	public interface CalculateForCache<K,V> {
		V calculate(K index) throws CacheRetrievalFailed;
	}
	
	public static class CacheRetrievalFailed extends AnchorFriendlyCheckedException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6073623853829327626L;

		public CacheRetrievalFailed( Exception exc ) {
			super( exc );
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param cacheSize maximum-size of cache
	 * @param calculator calculates the value for a given key if it's not already in the cache
	 */
	public LRUCache(int cacheSize, CalculateForCache<K,V> calculator) {
	
		cache = CacheBuilder.newBuilder()
	       .maximumSize(cacheSize)
	       .build(
	           new CacheLoader<K, V>() {
				 public V load(K key) throws CacheRetrievalFailed {
					 return calculator.calculate(key);
	             }
	           }
	        );
	}
	
	public V get(K key) throws GetOperationFailedException {
		try {
			return cache.get(key);
		} catch (ExecutionException e) {
			throw new GetOperationFailedException(e.getCause());
		} catch (UncheckedExecutionException e) {
			throw new GetOperationFailedException(e.getCause());
		}
	}
	
	public boolean has(K key) {
		return cache.getIfPresent(key)!=null;
	}

	/** Number of items currently in the cache */
	public long sizeCurrentLoad() {
		return cache.size();
	}
}
