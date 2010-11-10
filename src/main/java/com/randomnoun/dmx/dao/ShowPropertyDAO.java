package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.ShowPropertyTO;

public class ShowPropertyDAO {

    private JdbcTemplate jt;

    public static class ShowPropertyDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowPropertyTO s = new ShowPropertyTO();
            s.setId(rs.getLong("id"));
            s.setShowId(rs.getLong("showId"));
            s.setKey(rs.getString("key"));
            s.setValue(rs.getString("value"));
            return s;
        }
    }

    public ShowPropertyDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of showPropertys, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowPropertyTO objects that satisfy the supplied criteria
     */
    public List<ShowPropertyTO> getShowPropertys(String sqlWhereClause) {
        String sql =
            "SELECT id, showId, key, value " +
            " FROM showProperty " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowPropertyTO>) jt.query(sql, new ShowPropertyDAORowMapper());
    }

    /** Return a showProperty
     *
     * @param showPropertyId the showPropertyId
     *
     * @return the requested ShowPropertyTO object
     */
    public ShowPropertyTO getShowProperty(long showPropertyId) {
        return (ShowPropertyTO) jt.queryForObject(
            "SELECT id, showId, key, value " +
            " FROM showProperty " +
            " WHERE id = ?",
            new Object[] { new Long(showPropertyId) }, 
            new ShowPropertyDAORowMapper());
    }

    /** Update a showProperty
     *
     * @param showProperty the showProperty to update
     */
    public void updateShowProperty(ShowPropertyTO showProperty) {
        String sql =
            "UPDATE showProperty " +
            " SET showId=?, key=?, value=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                showProperty.getShowId(),
                showProperty.getKey(),
                showProperty.getValue(),
                showProperty.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("showProperty update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a showProperty into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param showProperty the showProperty to insert
     *
     * @return the id of the new record
     */
    public long createShowProperty(ShowPropertyTO showProperty) {
        String sql =
            "INSERT INTO showProperty " + 
            " (showId, key, value) " +
            " VALUES (?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                showProperty.getShowId(),
                showProperty.getKey(),
                showProperty.getValue()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("showProperty insert failed (" + updated + " rows updated)");
        }
        long showPropertyId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        showProperty.setId(showPropertyId);
        return showPropertyId;
    }
}

