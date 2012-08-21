package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.FixtureDefTO;

public class FixtureDefDAO {

    private JdbcTemplate jt;

    public static class FixtureDefDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            FixtureDefTO f = new FixtureDefTO();
            f.setId(rs.getLong("id"));
            f.setName(rs.getString("name"));
            f.setFixtureDefClassName(rs.getString("fixtureDefClassName"));
            f.setFixtureDefScript(rs.getString("fixtureDefScript"));
            f.setFixtureControllerClassName(rs.getString("fixtureControllerClassName"));
            f.setFixtureControllerScript(rs.getString("fixtureControllerScript"));
            f.setChannelMuxerClassName(rs.getString("channelMuxerClassName"));
            f.setChannelMuxerScript(rs.getString("channelMuxerScript"));
            f.setDmxChannels(rs.getLong("dmxChannels"));
            f.setHtmlImg16(rs.getString("htmlImg16"));
            return f;
        }
    }

    public FixtureDefDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of fixtureDefs, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of FixtureDefTO objects that satisfy the supplied criteria
     */
    public List<FixtureDefTO> getFixtureDefs(String sqlWhereClause) {
        String sql =
            "SELECT id, name, fixtureDefClassName, fixtureDefScript, fixtureControllerClassName, fixtureControllerScript, channelMuxerClassName, channelMuxerScript, dmxChannels, htmlImg16 " +
            " FROM fixtureDef " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<FixtureDefTO>) jt.query(sql, new FixtureDefDAORowMapper());
    }

    /** Return a fixtureDef
     *
     * @param fixtureDefId the fixtureDefId
     *
     * @return the requested FixtureDefTO object
     */
    public FixtureDefTO getFixtureDef(long fixtureDefId) {
        return (FixtureDefTO) jt.queryForObject(
            "SELECT id, name, fixtureDefClassName, fixtureDefScript, fixtureControllerClassName, fixtureControllerScript, channelMuxerClassName, channelMuxerScript, dmxChannels, htmlImg16 " +
            " FROM fixtureDef " +
            " WHERE id = ?",
            new Object[] { new Long(fixtureDefId) }, 
            new FixtureDefDAORowMapper());
    }

    /** Update a fixtureDef
     *
     * @param fixtureDef the fixtureDef to update
     */
    public void updateFixtureDef(FixtureDefTO fixtureDef) {
        String sql =
            "UPDATE fixtureDef " +
            " SET name=?, fixtureDefClassName=?, fixtureDefScript=?, fixtureControllerClassName=?, fixtureControllerScript=?, channelMuxerClassName=?, channelMuxerScript=?, dmxChannels=?, htmlImg16=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                fixtureDef.getName(),
                fixtureDef.getFixtureDefClassName(),
                fixtureDef.getFixtureDefScript(),
                fixtureDef.getFixtureControllerClassName(),
                fixtureDef.getFixtureControllerScript(),
                fixtureDef.getChannelMuxerClassName(),
                fixtureDef.getChannelMuxerScript(),
                new Long(fixtureDef.getDmxChannels()),
                fixtureDef.getHtmlImg16(),
                fixtureDef.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDef update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a fixtureDef into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param fixtureDef the fixtureDef to insert
     *
     * @return the id of the new record
     */
    public long createFixtureDef(FixtureDefTO fixtureDef) {
        String sql =
            "INSERT INTO fixtureDef " + 
            " (name, fixtureDefClassName, fixtureDefScript, fixtureControllerClassName, fixtureControllerScript, channelMuxerClassName, channelMuxerScript, dmxChannels, htmlImg16) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        long updated = jt.update(sql,
            new Object[] { 
                fixtureDef.getName(),
                fixtureDef.getFixtureDefClassName(),
                fixtureDef.getFixtureDefScript(),
                fixtureDef.getFixtureControllerClassName(),
                fixtureDef.getFixtureControllerScript(),
                fixtureDef.getChannelMuxerClassName(),
                fixtureDef.getChannelMuxerScript(),
                new Long(fixtureDef.getDmxChannels()),
                fixtureDef.getHtmlImg16()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDef insert failed (" + updated + " rows updated)");
        }
        long fixtureDefId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        fixtureDef.setId(fixtureDefId);
        return fixtureDefId;
    }

    /** Deletes a fixtureDef from the database.
    *
    * @param fixtureDef the fixtureDef to delete
    *
    */
   public void deleteFixtureDef(FixtureDefTO fixtureDef) {
       String sql =
           "DELETE FROM fixtureDef " + 
           " WHERE id = ?";
       long updated = jt.update(sql,
           new Object[] { 
               fixtureDef.getId(),
           });
       if (updated!=1) {
           throw new DataIntegrityViolationException("fixtureDef delete failed (" + updated + " rows updated)");
       }
   }
    
}

