package com.randomnoun.dmx.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.dmx.dao.ShowDAO.ShowDAORowMapper;
import com.randomnoun.dmx.dao.ShowDAO.ShowWithPropertyCountDAORowMapper;
import com.randomnoun.dmx.to.DeviceTO;
import com.randomnoun.dmx.to.ShowTO;

public class DeviceDAO {

    private JdbcTemplate jt;

    public static class DeviceDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            DeviceTO d = new DeviceTO();
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setClassName(rs.getString("className"));
            d.setType(rs.getString("type"));
            d.setYnActive(rs.getString("ynActive"));
            d.setUniverseNumber(rs.getLong("universeNumber")); if (rs.wasNull()) { d.setUniverseNumber(null); }
            return d;
        }
    }
    
    public static class DeviceWithPropertyCountDAORowMapper extends DeviceDAORowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            DeviceTO d = (DeviceTO) super.mapRow(rs, rowNum);
            d.setDevicePropertyCount(rs.getLong("devicePropertyCount"));
            return d;
        }
    }


    public DeviceDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of devices, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause is supplied, all devices are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of DeviceTO objects that satisfy the supplied criteria
     */
    public List<DeviceTO> getDevices(String sqlWhereClause) {
        String sql =
            "SELECT id, name, className, type, ynActive, universeNumber " +
            " FROM device " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<DeviceTO>) jt.query(sql, new DeviceDAORowMapper());
    }
    
    /** Return a list of devices, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause is supplied, all devices are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of DeviceTO objects that satisfy the supplied criteria
     */
    public List<DeviceTO> getDevicesWithPropertyCounts(String sqlWhereClause) {
        String sql =
    		"SELECT device.id, name, className, type, ynActive, universeNumber, COUNT(deviceProperty.id) AS devicePropertyCount " +
            " FROM device LEFT JOIN deviceProperty " +
            " ON device.id = deviceProperty.deviceId " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause) +
            " GROUP BY device.id";
	    return (List<DeviceTO>) jt.query(sql, new DeviceWithPropertyCountDAORowMapper());
    }

    /** Return a device
     *
     * @param deviceId the deviceId
     *
     * @return the requested DeviceTO object
     */
    public DeviceTO getDevice(long deviceId) {
        return (DeviceTO) jt.queryForObject(
            "SELECT id, name, className, type, ynActive, universeNumber " +
            " FROM device " +
            " WHERE id = ?",
            new Object[] { new Long(deviceId) }, 
            new DeviceDAORowMapper());
    }

    /** Update a device
     *
     * @param device the device to update
     */
    public void updateDevice(DeviceTO device) {
        String sql =
            "UPDATE device " +
            " SET name=?, className=?, type=?, ynActive=?, universeNumber=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                device.getName(),
                device.getClassName(),
                device.getType(),
                device.getYnActive(),
                device.getUniverseNumber(),
                device.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("device update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a device into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param device the device to insert
     *
     * @return the id of the new record
     */
    public long createDevice(DeviceTO device) {
        String sql =
            "INSERT INTO device " + 
            " (name, className, type, ynActive, universeNumber) " +
            " VALUES (?, ?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                device.getName(),
                device.getClassName(),
                device.getType(),
                device.getYnActive(),
                device.getUniverseNumber()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("device insert failed (" + updated + " rows updated)");
        }
        long deviceId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        device.setId(deviceId);
        return deviceId;
    }

   /** Delete a device
    *
    * @param device the device to delete
    */
    public void deleteDevice(DeviceTO device) {
        String sql =
            "DELETE FROM device " +
            " WHERE id = ?";
        int updated = jt.update(sql,
          new Object[] { device.getId() } );
        if (updated!=1) {
            throw new DataIntegrityViolationException("device delete failed (" + updated + " rows updated)");
        }
    }
}

