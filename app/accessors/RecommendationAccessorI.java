/**
 * Accessor Interface Implementing Datastax Object Mapping
 * Specific interface for Recommendation class
 * @author pgaref
 *
 */
package accessors;


import models.Recommendation;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

@Accessor
public interface RecommendationAccessorI {

	@Query("SELECT * FROM play_cassandra.recommendations ")
	public Result<Recommendation> getAll();

	@Query("SELECT * FROM play_cassandra.recommendations ")
	public ListenableFuture<Result<Recommendation>> getAllAsync();
}
