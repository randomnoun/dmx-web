package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowDefAttachmentTO;
import com.randomnoun.dmx.to.ShowDefTO;
import com.randomnoun.dmx.to.ShowPropertyTO;
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
            s.setShowGroupId(rs.getLong("showGroupId"));
            if (rs.wasNull()) { s.setShowGroupId(null); }
            s.setStageId(rs.getLong("stageId"));
            return s;
        }
    }

    public static class ShowWithPropertyCountDAORowMapper extends ShowDAORowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowTO s = (ShowTO) super.mapRow(rs, rowNum);
            s.setShowPropertyCount(rs.getLong("showPropertyCount"));
            return s;
        }
    }
    
    private static class ShowWithPropertyResultSetExtractor implements ResultSetExtractor {
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			List<ShowTO> result = new ArrayList<ShowTO>();
			long lastShowId = -1;
			ShowTO s = null;
			while (rs.next()) {
				long showId = rs.getLong("showId");
				if (showId!=lastShowId) {
					s = new ShowTO();
					s.setId(showId);
					s.setShowDefId(rs.getLong("showDefId"));
		            s.setName(rs.getString("name"));
		            s.setOnCancelShowId(rs.getLong("onCancelShowId"));
		            if (rs.wasNull()) { s.setOnCancelShowId(null); }
		            s.setOnCompleteShowId(rs.getLong("onCompleteShowId"));
		            if (rs.wasNull()) { s.setOnCompleteShowId(null); }
		            s.setShowGroupId(rs.getLong("showGroupId"));
		            if (rs.wasNull()) { s.setShowGroupId(null); }
		            s.setStageId(rs.getLong("stageId"));
		            s.setShowProperties(new ArrayList<ShowPropertyTO>());
		            result.add(s);
		            lastShowId = showId;
				}
				long showPropertyId = rs.getLong("showPropertyId");
				if (!rs.wasNull()) {
					ShowPropertyTO p = new ShowPropertyTO();
					p.setId(showPropertyId);
		            p.setShowId(showId);
		            p.setKey(rs.getString("key"));
		            p.setValue(rs.getString("value"));
		            s.getShowProperties().add(p);
				}
			}
			return result;
		}
    	
    }
    
    public ShowDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of shows, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all shows are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowTO objects that satisfy the supplied criteria
     */
    public List<ShowTO> getShows(String sqlWhereClause) {
        String sql =
            "SELECT id, showDefId, name, onCancelShowId, onCompleteShowId, showGroupId, stageId " +
            " FROM `show` " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowTO>) jt.query(sql, new ShowDAORowMapper());
    }

    /** Return a list of shows, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all shows are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowTO objects that satisfy the supplied criteria
     */
    public List<ShowTO> getShowsWithPropertyCounts(String sqlWhereClause) {
        String sql =
            "SELECT `show`.id, showDefId, name, onCancelShowId, onCompleteShowId, showGroupId, stageId, COUNT(showProperty.id) AS showPropertyCount " +
            " FROM `show` LEFT JOIN `showProperty` " +
            " ON `show`.id = `showProperty`.showId " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause) +
            " GROUP BY `show`.id";
	    return (List<ShowTO>) jt.query(sql, new ShowWithPropertyCountDAORowMapper());
    }

    /** Return a list of shows, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all shows are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowTO objects that satisfy the supplied criteria
     */
    public List<ShowTO> getShowsWithProperties(String sqlWhereClause) {
        String sql =
            "SELECT `show`.id AS showId, showDefId, name, onCancelShowId, onCompleteShowId, showGroupId, stageId, showProperty.id AS showPropertyId, showId, `key`, value " +
            " FROM `show` LEFT JOIN `showProperty` " +
            " ON `show`.id = `showProperty`.showId " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause) +
            " ORDER BY `show`.id";
	    return (List<ShowTO>) jt.query(sql, new ShowWithPropertyResultSetExtractor());
    }

    
    /** Return a show
     *
     * @param showId the showId
     *
     * @return the requested ShowTO object
     */
    public ShowTO getShow(long showId) {
        return (ShowTO) jt.queryForObject(
            "SELECT id, showDefId, name, onCancelShowId, onCompleteShowId, showGroupId, stageId " +
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
            " SET showDefId=?, name=?, onCancelShowId=?, onCompleteShowId=?, showGroupId=?, stageId=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                show.getShowDefId(),
                show.getName(),
                show.getOnCancelShowId(),
                show.getOnCompleteShowId(),
                show.getShowGroupId(),
                show.getStageId(),
                show.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("show update failed (" + updated + " rows updated)");
        }
    }

    /** Delete a show
    *
    * @param show the show to update
    */
   public void deleteShow(ShowTO show) {
       String sql =
           "DELETE FROM `show` " +
           " WHERE id = ?";
       int updated = jt.update(sql, 
           new Object[] { show.getId() } );
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
            " (showDefId, name, onCancelShowId, onCompleteShowId, showGroupId, stageid) " +
            " VALUES (?, ?, ?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                show.getShowDefId(),
                show.getName(),
                show.getOnCancelShowId(),
                show.getOnCompleteShowId(),
                show.getShowGroupId(),
                show.getStageId()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("show insert failed (" + updated + " rows updated)");
        }
        long showId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        show.setId(showId);
        return showId;
    }
    
    /** Returns the highest numbered show group, or null if no show groups exist */
    public Long getLastShowGroupId() {
    	List result = jt.queryForList("SELECT MAX(showGroupId) AS lastShow FROM `show`");
    	if (result.size()==0) {
    		return null;
    	} else {
    		Number number = (Number) ((Map)result.get(0)).get("lastShow");
    		return number == null ? null : new Long(number.longValue()); 
    	}
    }
    

    
}