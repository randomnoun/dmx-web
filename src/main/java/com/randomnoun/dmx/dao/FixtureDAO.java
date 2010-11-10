package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.FixtureTO;

public class FixtureDAO {

    private JdbcTemplate jt;

    public static class FixtureDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            FixtureTO f = new FixtureTO();
            f.setId(rs.getLong("id"));
            f.setFixtureDefId(rs.getLong("fixtureDefId"));
            f.setName(rs.getString("name"));
            f.setDmxOffset(rs.getLong("dmxOffset"));
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
            "SELECT id, fixtureDefId, name, dmxOffset " +
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
            "SELECT id, fixtureDefId, name, dmxOffset " +
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
            " SET fixtureDefId=?, name=?, dmxOffset=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                fixture.getFixtureDefId(),
                fixture.getName(),
                fixture.getDmxOffset(),
                fixture.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixture update failed (" + updated + " rows updated)");
        }
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
            " (fixtureDefId, name, dmxOffset) " +
            " VALUES (?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                fixture.getFixtureDefId(),
                fixture.getName(),
                fixture.getDmxOffset()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixture insert failed (" + updated + " rows updated)");
        }
        long fixtureId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        fixture.setId(fixtureId);
        return fixtureId;
    }
}
