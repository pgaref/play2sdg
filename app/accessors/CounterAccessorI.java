/**
 * Accessor Interface Implementing Datastax Object Mapping
 * Specific interface for Counter class
 * @author pgaref
 *
 */
package accessors;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

import models.Counter;

@Accessor
public interface CounterAccessorI {
	/*
	 * 	UPDATE counterks.page_view_counts
 	 * 	SET counter_value = counter_value + 1
 	 *  WHERE url_name='counters
	 */
	@Query("UPDATE play_cassandra.counters SET counter = counter + 1 WHERE key = :key")
	public ResultSet incrementCounter(@Param("key") String key);
	
	@Query("UPDATE play_cassandra.counters SET counter = counter - 1 WHERE key = :key")
	public ResultSet decrementCounter(@Param("key") String key);
	
	@Query("UPDATE play_cassandra.counters SET counter = counter + :value WHERE key = :key")
	public ResultSet increaseCounterByValue(@Param("key") String key, @Param("value") long value);
	
    @Query("SELECT * FROM play_cassandra.counters")
    public Result<Counter> getAll();

    @Query("SELECT * FROM play_cassandra.counters")
    public ListenableFuture<Result<Counter>> getAllAsync();

}
