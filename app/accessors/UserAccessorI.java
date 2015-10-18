/**
 * Accessor Interface Implementing Datastax Object Mapping
 * Specific interface for User class
 * @author pgaref
 *
 */
package accessors;

import models.User;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

@Accessor
public interface UserAccessorI {
/*	
    @Query("SELECT * FROM play_cassandra.users WHERE key = :id")
    User getUserNamed(@Param("key") String email);

    @Query("SELECT * FROM play_cassandra.users WHERE id = ?")
    User getOnePosition(String email);

    @Query("UPDATE play_cassandra.users SET addresses[:name]=:address WHERE id = :id")
    ResultSet addAddress(@Param("id") UUID id, @Param("name") String addressName, @Param("address") Address address);
*/

    @Query("SELECT * FROM play_cassandra.users")
    public Result<User> getAll();

    @Query("SELECT * FROM play_cassandra.users")
    public ListenableFuture<Result<User>> getAllAsync();
}
