package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.to.DevicePropertyTO;

public class DevicePropertyDAO {

    private JdbcTemplate jt;

    public static class DevicePropertyDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            DevicePropertyTO d = new DevicePropertyTO();
            d.setId(rs.getLong("id"));
            d.setDeviceId(rs.getLong("deviceId"));
            d.setKey(rs.getString("key"));
            d.setValue(rs.getString("value")); if (rs.wasNull()) { d.setValue(null); }
            return d;
        }
    }

    public DevicePropertyDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of devicePropertys, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause is supplied, all devicePropertys are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of DevicePropertyTO objects that satisfy the supplied criteria
     */
    public List<DevicePropertyTO> getDevicePropertys(String sqlWhereClause) {
        String sql =
            "SELECT id, deviceId, key, value " +
            " FROM deviceProperty " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<DevicePropertyTO>) jt.query(sql, new DevicePropertyDAORowMapper());
    }

    /** Return a deviceProperty
     *
     * @param devicePropertyId the devicePropertyId
     *
     * @return the requested DevicePropertyTO object
     */
    public DevicePropertyTO getDeviceProperty(long devicePropertyId) {
        return (DevicePropertyTO) jt.queryForObject(
            "SELECT id, deviceId, key, value " +
            " FROM deviceProperty " +
            " WHERE id = ?",
            new Object[] { new Long(devicePropertyId) }, 
            new DevicePropertyDAORowMapper());
    }

    /** Update a deviceProperty
     *
     * @param deviceProperty the deviceProperty to update
     */
    public void updateDeviceProperty(DevicePropertyTO deviceProperty) {
        String sql =
            "UPDATE deviceProperty " +
            " SET deviceId=?, key=?, value=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                deviceProperty.getDeviceId(),
                deviceProperty.getKey(),
                deviceProperty.getValue(),
                deviceProperty.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("deviceProperty update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a deviceProperty into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param deviceProperty the deviceProperty to insert
     *
     * @return the id of the new record
     */
    public long createDeviceProperty(DevicePropertyTO deviceProperty) {
        String sql =
            "INSERT INTO deviceProperty " + 
            " (deviceId, key, value) " +
            " VALUES (?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                deviceProperty.getDeviceId(),
                deviceProperty.getKey(),
                deviceProperty.getValue()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("deviceProperty insert failed (" + updated + " rows updated)");
        }
        long devicePropertyId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        deviceProperty.setId(devicePropertyId);
        return devicePropertyId;
    }

   /** Delete a deviceProperty
    *
    * @param deviceProperty the deviceProperty to delete
    */
    public void deleteDeviceProperty(DevicePropertyTO deviceProperty) {
        String sql =
            "DELETE FROM deviceProperty " +
            " WHERE id = ?";
        int updated = jt.update(sql,
          new Object[] { deviceProperty.getId() } );
        if (updated!=1) {
            throw new DataIntegrityViolationException("deviceProperty delete failed (" + updated + " rows updated)");
        }
    }
}

