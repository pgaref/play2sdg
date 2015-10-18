/**
 * Accessor Interface Implementing Datastax Object Mapping
 * Specific interface for PlayList class
 * @author pgaref
 *
 */
package accessors;


import java.util.List;
import java.util.UUID;

import models.PlayList;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

@Accessor
public interface PlaylistAccessorI {
	
	@Query("SELECT * FROM play_cassandra.playlists WHERE usermail = :usermail" )
	public Result<PlayList> getUserPlaylists(@Param("usermail") String usermail);
	
	@Query("UPDATE play_cassandra.playlists SET tracks=:tracks WHERE id = :id")
	public ResultSet deleteUserPlayListSong(@Param("id") UUID id, @Param("tracks") List<String> tracks);
	
    @Query("SELECT * FROM play_cassandra.playlists")
    public Result<PlayList> getAll();

    @Query("SELECT * FROM play_cassandra.playlists")
    public ListenableFuture<Result<PlayList>> getAllAsync();

}
