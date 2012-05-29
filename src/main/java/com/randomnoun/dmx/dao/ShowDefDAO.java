package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.ShowDefTO;

public class ShowDefDAO {

    private JdbcTemplate jt;

    public static class ShowDefDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowDefTO s = new ShowDefTO();
            s.setId(rs.getLong("id"));
            s.setName(rs.getString("name"));
            s.setClassName(rs.getString("className"));
            s.setScript(rs.getString("script"));
            s.setJavadoc(rs.getString("javadoc"));
            s.setRecorded("Y".equals(rs.getString("ynRecorded")));
            return s;
        }
    }

    public ShowDefDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of showDefs, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowDefTO objects that satisfy the supplied criteria
     */
    public List<ShowDefTO> getShowDefs(String sqlWhereClause) {
        String sql =
            "SELECT id, name, className, script, javadoc, ynRecorded " +
            " FROM showDef " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowDefTO>) jt.query(sql, new ShowDefDAORowMapper());
    }

    /** Return a showDef
     *
     * @param showDefId the showDefId
     *
     * @return the requested ShowDefTO object
     */
    public ShowDefTO getShowDef(long showDefId) {
        return (ShowDefTO) jt.queryForObject(
            "SELECT id, name, className, script, javadoc, ynRecorded " +
            " FROM showDef " +
            " WHERE id = ?",
            new Object[] { new Long(showDefId) }, 
            new ShowDefDAORowMapper());
    }

    /** Update a showDef
     *
     * @param showDef the showDef to update
     */
    public void updateShowDef(ShowDefTO showDef) {
        String sql =
            "UPDATE showDef " +
            " SET name=?, className=?, script=?, javadoc=?, ynRecorded=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                showDef.getName(),
                showDef.getClassName(),
                showDef.getScript(),
                showDef.getJavadoc(),
                showDef.isRecorded() ? "Y" : "N",
                showDef.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("showDef update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a showDef into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param showDef the showDef to insert
     *
     * @return the id of the new record
     */
    public long createShowDef(ShowDefTO showDef) {
        String sql =
            "INSERT INTO showDef " + 
            " (name, className, script, javadoc, ynRecorded) " +
            " VALUES (?, ?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                showDef.getName(),
                showDef.getClassName(),
                showDef.getScript(),
                showDef.getJavadoc(),
                showDef.isRecorded() ? "Y" : "N"});
        if (updated!=1) {
            throw new DataIntegrityViolationException("showDef insert failed (" + updated + " rows updated)");
        }
        long showDefId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        showDef.setId(showDefId);
        return showDefId;
    }
    
    /** Deletes a showDef into the database.
    *
    * @param showDef the showDef to delete
    */
   public void deleteShowDef(ShowDefTO showDef) {
       String sql =
           "DELETE FROM showDef " + 
           " WHERE id = ?";
       long updated = jt.update(sql,
           new Object[] { new Long(showDef.getId()) });
       if (updated!=1) {
           throw new DataIntegrityViolationException("showDef insert failed (" + updated + " rows updated)");
       }
   }
}
