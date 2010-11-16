package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowTO;

public class ShowDAO {

    private JdbcTemplate jt;

    public static class ShowDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowTO s = new ShowTO();
            s.setId(rs.getLong("id"));
            s.setShowDefId(rs.getLong("showDefId"));
            s.setName(rs.getString("name"));
            s.setOnCancelShowId(rs.getLong("onCancelShowId"));
            if (rs.wasNull()) { s.setOnCancelShowId(null); }
            s.setOnCompleteShowId(rs.getLong("onCompleteShowId"));
            if (rs.wasNull()) { s.setOnCompleteShowId(null); }
            return s;
        }
    }

    public ShowDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of shows, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowTO objects that satisfy the supplied criteria
     */
    public List<ShowTO> getShows(String sqlWhereClause) {
        String sql =
            "SELECT id, showDefId, name, onCancelShowId, onCompleteShowId " +
            " FROM `show` " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowTO>) jt.query(sql, new ShowDAORowMapper());
    }

    /** Return a show
     *
     * @param showId the showId
     *
     * @return the requested ShowTO object
     */
    public ShowTO getShow(long showId) {
        return (ShowTO) jt.queryForObject(
            "SELECT id, showDefId, name, onCancelShowId, onCompleteShowId " +
            " FROM `show` " +
            " WHERE id = ?",
            new Object[] { new Long(showId) }, 
            new ShowDAORowMapper());
    }

    /** Update a show
     *
     * @param show the show to update
     */
    public void updateShow(ShowTO show) {
        String sql =
            "UPDATE `show` " +
            " SET showDefId=?, name=?, onCancelShowId=?, onCompleteShowId=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                show.getShowDefId(),
                show.getName(),
                show.getOnCancelShowId(),
                show.getOnCompleteShowId(),
                show.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("show update failed (" + updated + " rows updated)");
        }
    }

    /** Delete a show
    *
    * @param show the show to update
    */
   public void deleteShow(ShowTO fixture) {
       String sql =
           "DELETE FROM `show` " +
           " WHERE id = ?";
       int updated = jt.update(sql, 
           new Object[] { fixture.getId() } );
       if (updated!=1) {
           throw new DataIntegrityViolationException("show delete failed (" + updated + " rows updated)");
       }
   }
    
    
    /** Inserts a show into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param show the show to insert
     *
     * @return the id of the new record
     */
    public long createShow(ShowTO show) {
        String sql =
            "INSERT INTO `show` " + 
            " (showDefId, name, onCancelShowId, onCompleteShowId) " +
            " VALUES (?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                show.getShowDefId(),
                show.getName(),
                show.getOnCancelShowId(),
                show.getOnCompleteShowId()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("show insert failed (" + updated + " rows updated)");
        }
        long showId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        show.setId(showId);
        return showId;
    }
    

    
}