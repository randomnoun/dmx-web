package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.ShowTypeTO;

public class ShowTypeDAO {

    private JdbcTemplate jt;

    public static class ShowTypeDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowTypeTO s = new ShowTypeTO();
            s.setId(rs.getLong("id"));
            s.setClassName(rs.getString("className"));
            s.setScript(rs.getString("script"));
            return s;
        }
    }

    public ShowTypeDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of showTypes, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowTypeTO objects that satisfy the supplied criteria
     */
    public List<ShowTypeTO> getShowTypes(String sqlWhereClause) {
        String sql =
            "SELECT id, className, script " +
            " FROM showType " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowTypeTO>) jt.query(sql, new ShowTypeDAORowMapper());
    }

    /** Return a showType
     *
     * @param showTypeId the showTypeId
     *
     * @return the requested ShowTypeTO object
     */
    public ShowTypeTO getShowType(long showTypeId) {
        return (ShowTypeTO) jt.queryForObject(
            "SELECT id, className, script " +
            " FROM showType " +
            " WHERE id = ?",
            new Object[] { new Long(showTypeId) }, 
            new ShowTypeDAORowMapper());
    }

    /** Update a showType
     *
     * @param showType the showType to update
     */
    public void updateShowType(ShowTypeTO showType) {
        String sql =
            "UPDATE showType " +
            " SET className=?, script=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                showType.getClassName(),
                showType.getScript(),
                showType.getId() });
        if (updated!=1) {
            throw new UncategorizedSQLException("showType update failed (" + updated + " rows updated)", sql, null);
        }
    }

    /** Inserts a showType into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param showType the showType to insert
     *
     * @return the id of the new record
     */
    public long createShowType(ShowTypeTO showType) {
        String sql =
            "INSERT INTO showType " + 
            " (className, script) " +
            " VALUES (?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                showType.getClassName(),
                showType.getScript()});
        if (updated!=1) {
            throw new UncategorizedSQLException("showType insert failed (" + updated + " rows updated)", sql, null);
        }
        long showTypeId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        showType.setId(showTypeId);
        return showTypeId;
    }
}
