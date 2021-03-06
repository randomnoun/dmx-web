package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.FixtureTO;

public class FixtureDAO {

    private JdbcTemplate jt;

    public static class FixtureDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            FixtureTO f = new FixtureTO();
            f.setId(rs.getLong("id"));
            f.setStageId(rs.getLong("stageId"));
            f.setFixtureDefId(rs.getLong("fixtureDefId"));
            f.setName(rs.getString("name")); if (rs.wasNull()) { f.setName(null); }
            f.setUniverseNumber(rs.getLong("universeNumber"));
            f.setDmxOffset(rs.getLong("dmxOffset"));
            f.setX(rs.getLong("x")); if (rs.wasNull()) { f.setX(null); }
            f.setY(rs.getLong("y")); if (rs.wasNull()) { f.setY(null); }
            f.setZ(rs.getLong("z")); if (rs.wasNull()) { f.setZ(null); }
            f.setLookingAtX(rs.getLong("lookingAtX")); if (rs.wasNull()) { f.setLookingAtX(null); }
            f.setLookingAtY(rs.getLong("lookingAtY")); if (rs.wasNull()) { f.setLookingAtY(null); }
            f.setLookingAtZ(rs.getLong("lookingAtZ")); if (rs.wasNull()) { f.setLookingAtZ(null); }
            f.setUpX(rs.getLong("upX")); if (rs.wasNull()) { f.setUpX(null); }
            f.setUpY(rs.getLong("upY")); if (rs.wasNull()) { f.setUpY(null); }
            f.setUpZ(rs.getLong("upZ")); if (rs.wasNull()) { f.setUpZ(null); }
            f.setSortOrder(rs.getLong("sortOrder")); if (rs.wasNull()) { f.setSortOrder(null); }
            f.setFixPanelType(rs.getString("fixPanelType"));
            f.setFixPanelX(rs.getLong("fixPanelX")); if (rs.wasNull()) { f.setFixPanelX(null); }
            f.setFixPanelY(rs.getLong("fixPanelY")); if (rs.wasNull()) { f.setFixPanelY(null); }
            return f;
        }
    }

    public FixtureDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of fixtures, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of FixtureTO objects that satisfy the supplied criteria
     */
    public List<FixtureTO> getFixtures(String sqlWhereClause) {
        String sql =
            "SELECT id, stageId, fixtureDefId, name, universeNumber, dmxOffset, x, y, z, lookingAtX, lookingAtY, lookingAtZ, upX, upY, upZ, sortOrder, fixPanelType, fixPanelX, fixPanelY " +
            " FROM fixture " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<FixtureTO>) jt.query(sql, new FixtureDAORowMapper());
    }

    /** Return a fixture
     *
     * @param fixtureId the fixtureId
     *
     * @return the requested FixtureTO object
     */
    public FixtureTO getFixture(long fixtureId) {
        return (FixtureTO) jt.queryForObject(
            "SELECT id, stageId, fixtureDefId, name, universeNumber, dmxOffset, x, y, z, lookingAtX, lookingAtY, lookingAtZ, upX, upY, upZ, sortOrder, fixPanelType, fixPanelX, fixPanelY " +
            " FROM fixture " +
            " WHERE id = ?",
            new Object[] { new Long(fixtureId) }, 
            new FixtureDAORowMapper());
    }

    /** Update a fixture
     *
     * @param fixture the fixture to update
     */
    public void updateFixture(FixtureTO fixture) {
        String sql =
            "UPDATE fixture " +
            " SET stageId=?, fixtureDefId=?, name=?, universeNumber=?, dmxOffset=?, x=?, y=?, z=?, lookingAtX=?, lookingAtY=?, lookingAtZ=?, upX=?, upY=?, upZ=?, sortOrder=?, fixPanelType=?, fixPanelX=?, fixPanelY=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
        		fixture.getStageId(),
                fixture.getFixtureDefId(),
                fixture.getName(),
                fixture.getUniverseNumber(),
                fixture.getDmxOffset(),
                fixture.getX(),
                fixture.getY(),
                fixture.getZ(),
                fixture.getLookingAtX(),
                fixture.getLookingAtY(),
                fixture.getLookingAtZ(),
                fixture.getUpX(),
                fixture.getUpY(),
                fixture.getUpZ(),
                fixture.getSortOrder(),
                fixture.getFixPanelType(),
                fixture.getFixPanelX(),
                fixture.getFixPanelY(),
                fixture.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixture update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a fixture into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param fixture the fixture to insert
     *
     * @return the id of the new record
     */
    public long createFixture(FixtureTO fixture) {
        String sql =
            "INSERT INTO fixture " + 
            " (stageId, fixtureDefId, name, universeNumber, dmxOffset, x, y, z, lookingAtX, lookingAtY, lookingAtZ, upX, upY, upZ, sortOrder, fixPanelType, fixPanelX, fixPanelY) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] {
        		fixture.getStageId(),
                fixture.getFixtureDefId(),
                fixture.getName(),
                fixture.getUniverseNumber(),
                fixture.getDmxOffset(),
                fixture.getX(),
                fixture.getY(),
                fixture.getZ(),
                fixture.getLookingAtX(),
                fixture.getLookingAtY(),
                fixture.getLookingAtZ(),
                fixture.getUpX(),
                fixture.getUpY(),
                fixture.getUpZ(),
                fixture.getSortOrder(),
                fixture.getFixPanelType(),
                fixture.getFixPanelX(),
                fixture.getFixPanelY()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixture insert failed (" + updated + " rows updated)");
        }
        long fixtureId = jt.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        fixture.setId(fixtureId);
        return fixtureId;
    }

   /** Delete a fixture
    *
    * @param fixture the fixture to update
    */
    public void deleteFixture(FixtureTO fixture) {
        String sql =
            "DELETE FROM fixture " +
            " WHERE id = ?";
        int updated = jt.update(sql,
          new Object[] { fixture.getId() } );
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixture delete failed (" + updated + " rows updated)");
        }
    }
    
    /** Delete a fixture, using the supplied SQL WHERE clause.
    *
    * @param fixture the fixture to update
    */
    public void deleteFixtures(String sqlWhereClause) {
    	if (sqlWhereClause==null) { throw new NullPointerException("null sqlWhereClause"); }
        String sql =
            "DELETE FROM fixture " +
            " WHERE " + sqlWhereClause;
        jt.update(sql);
    }
    
}

