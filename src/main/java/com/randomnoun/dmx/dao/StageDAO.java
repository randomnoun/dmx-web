package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.StageTO;

// currently, only one stage can be active at one time
// may have to remove this restriction later when I can think of a good reason for doing so
// (portability of stage/show defs ?)
public class StageDAO {

    private JdbcTemplate jt;

    public static class StageDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            StageTO s = new StageTO();
            s.setId(rs.getLong("id"));
            s.setName(rs.getString("name"));
            s.setFilename(rs.getString("filename")); if (rs.wasNull()) { s.setFilename(null); }
            s.setActive(rs.getString("active"));
            return s;
        }
    }

    public StageDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of stages, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause is supplied, all stages are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of StageTO objects that satisfy the supplied criteria
     */
    public List<StageTO> getStages(String sqlWhereClause) {
        String sql =
            "SELECT id, name, filename, active " +
            " FROM stage " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<StageTO>) jt.query(sql, new StageDAORowMapper());
    }

    /** Return a stage
     *
     * @param stageId the stageId
     *
     * @return the requested StageTO object
     */
    public StageTO getStage(long stageId) {
        return (StageTO) jt.queryForObject(
            "SELECT id, name, filename, active " +
            " FROM stage " +
            " WHERE id = ?",
            new Object[] { new Long(stageId) }, 
            new StageDAORowMapper());
    }

    /** Update a stage
     *
     * @param stage the stage to update
     */
    public void updateStage(StageTO stage) {
        String sql =
            "UPDATE stage " +
            " SET name=?, filename=?, active=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                stage.getName(),
                stage.getFilename(),
                stage.getActive(),
                stage.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("stage update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a stage into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param stage the stage to insert
     *
     * @return the id of the new record
     */
    public long createStage(StageTO stage) {
        String sql =
            "INSERT INTO stage " + 
            " (name, filename, active) " +
            " VALUES (?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                stage.getName(),
                stage.getFilename(),
                stage.getActive()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("stage insert failed (" + updated + " rows updated)");
        }
        long stageId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        stage.setId(stageId);
        return stageId;
    }

   /** Delete a stage
    *
    * @param stage the stage to delete
    */
    public void deleteStage(StageTO stage) {
        String sql =
            "DELETE FROM stage " +
            " WHERE id = ?";
        int updated = jt.update(sql,
          new Object[] { stage.getId() } );
        if (updated!=1) {
            throw new DataIntegrityViolationException("stage delete failed (" + updated + " rows updated)");
        }
    }
    
    public Long getActiveStageId() {
    	Map result =  (Map) DataAccessUtils.requiredSingleResult(jt.queryForList(
            "SELECT MIN(id) AS minId, COUNT(id) AS countId " +
            " FROM stage " +
            " WHERE active = 'Y'"));
    	long count = ((Number) result.get("countId")).longValue();
    	if (count==0) {
    		return null;
    	} else {
    		return ((Number) result.get("minId")).longValue();
    	}
    	
    }
}

