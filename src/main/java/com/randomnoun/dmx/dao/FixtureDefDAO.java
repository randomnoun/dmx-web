package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
            f.setClassName(rs.getString("className"));
            f.setScript(rs.getString("script"));
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
            "SELECT id, name, className, script " +
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
            "SELECT id, name, className, script " +
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
            " SET name=?, className=?, script=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                fixtureDef.getName(),
                fixtureDef.getClassName(),
                fixtureDef.getScript(),
                fixtureDef.getId() });
        if (updated!=1) {
            throw new UncategorizedSQLException("fixtureDef update failed (" + updated + " rows updated)", sql, null);
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
            " (name, className, script) " +
            " VALUES (?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                fixtureDef.getName(),
                fixtureDef.getClassName(),
                fixtureDef.getScript()});
        if (updated!=1) {
            throw new UncategorizedSQLException("fixtureDef insert failed (" + updated + " rows updated)", sql, null);
        }
        long fixtureDefId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        fixtureDef.setId(fixtureDefId);
        return fixtureDefId;
    }
}

