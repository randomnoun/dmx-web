package com.randomnoun.dmx.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.randomnoun.common.StreamUtils;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.to.FixtureDefImageTO;
import com.randomnoun.dmx.to.FixtureDefTO;

public class FixtureDefImageDAO {

    private JdbcTemplate jt;

    public static class FixtureDefImageDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            FixtureDefImageTO f = new FixtureDefImageTO();
            f.setId(rs.getLong("id"));
            f.setFixtureDefId(rs.getLong("fixtureDefId"));
            f.setName(rs.getString("name"));
            f.setSize(rs.getLong("size"));
            f.setContentType(rs.getString("contentType"));
            f.setFileLocation(rs.getString("fileLocation"));
            f.setDescription(rs.getString("description"));
            return f;
        }
    }

    public FixtureDefImageDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of fixtureDefImages, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of FixtureDefImageTO objects that satisfy the supplied criteria
     */
    public List<FixtureDefImageTO> getFixtureDefImages(String sqlWhereClause) {
        String sql =
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
            " FROM fixtureDefImage " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<FixtureDefImageTO>) jt.query(sql, new FixtureDefImageDAORowMapper());
    }

    /** Return a list of fixtureDefImages for a given fixture.
     *
     * @param fixtureDef the fixtureDefTO to return images for
     *
     * @return a list of FixtureDefImageTO objects
     */
    public List<FixtureDefImageTO> getFixtureDefImages(FixtureDefTO fixtureDef) {
        String sql =
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description" +
            " FROM fixtureDefImage " +
            " WHERE fixtureDefId = " + fixtureDef.getId();
	    return (List<FixtureDefImageTO>) jt.query(sql, new FixtureDefImageDAORowMapper());
    }
    
    
    /** Return a fixtureDefImage
     *
     * @param fixtureDefImageId the fixtureDefImageId
     *
     * @return the requested FixtureDefImageTO object
     */
    public FixtureDefImageTO getFixtureDefImage(long fixtureDefImageId) {
        return (FixtureDefImageTO) jt.queryForObject(
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
            " FROM fixtureDefImage " +
            " WHERE id = ?",
            new Object[] { new Long(fixtureDefImageId) }, 
            new FixtureDefImageDAORowMapper());
    }

    /** Return a fixtureDefImage for a given fixture definition id / filename
    *
    * @param fixtureDefId the fixtureDefImageId
    * @param filename the filename
    *
    * @return the requested FixtureDefImageTO object
    */
   public FixtureDefImageTO getFixtureDefImage(long fixtureDefId, String filename) {
       return (FixtureDefImageTO) jt.queryForObject(
           "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
           " FROM fixtureDefImage " +
           " WHERE fixtureDefId = ? AND name = ?",
           new Object[] { new Long(fixtureDefId), filename }, 
           new FixtureDefImageDAORowMapper());
   }
    
    
    /** Update a fixtureDefImage
     *
     * @param fixtureDefImage the fixtureDefImage to update
     */
    public void updateFixtureDefImage(FixtureDefImageTO fixtureDefImage) {
        String sql =
            "UPDATE fixtureDefImage " +
            " SET fixtureDefId=?, name=?, size=?, contentType=?, fileLocation=?, description=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                fixtureDefImage.getFixtureDefId(),
                fixtureDefImage.getName(),
                fixtureDefImage.getSize(),
                fixtureDefImage.getContentType(),
                fixtureDefImage.getFileLocation(),
                fixtureDefImage.getDescription(),
                fixtureDefImage.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDefImage update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a fixtureDefImage into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param fixtureDefImage the fixtureDefImage to insert
     *
     * @return the id of the new record
     */
    public long createFixtureDefImage(FixtureDefImageTO fixtureDefImage) {
    	fixtureDefImage.setFileLocation(
    		"fixtureDefs/" + fixtureDefImage.getFixtureDefId() + "/" + 
    		sanitiseFilename(fixtureDefImage.getName()));
    	// @TODO check for duplicate filenames + add a discriminator
    	
        String sql =
            "INSERT INTO fixtureDefImage " + 
            " (fixtureDefId, name, size, contentType, fileLocation, description) " +
            " VALUES (?, ?, ?, ?, ?, ?)";
        long updated = jt.update(sql,
            new Object[] { 
                fixtureDefImage.getFixtureDefId(),		
                fixtureDefImage.getName(),
                fixtureDefImage.getSize(),
                fixtureDefImage.getContentType(),
                fixtureDefImage.getFileLocation(),
                fixtureDefImage.getDescription()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDefImage insert failed (" + updated + " rows updated)");
        }
        long fixtureDefImageId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        fixtureDefImage.setId(fixtureDefImageId);
        return fixtureDefImageId;
    }
    
    public void saveImage(FixtureDefImageTO fixtureDefImage, InputStream is) throws IOException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	File newFile = new File(imageBase, fixtureDefImage.getFileLocation());
    	if (!newFile.getParentFile().isDirectory()) {
    		newFile.getParentFile().mkdirs();
    	}
    	FileOutputStream fos = new FileOutputStream(newFile);
    	StreamUtils.copyStream(is, fos);
    	fos.close();
    }
    
    public InputStream loadImage(FixtureDefImageTO fixtureDefImage) throws FileNotFoundException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	return new FileInputStream(new File(imageBase, fixtureDefImage.getFileLocation()));
    }

    public static String sanitiseFilename(String f) {
    	f = f.toLowerCase();
    	if (f.indexOf("/")!=-1) { f = f.substring(f.lastIndexOf("/")); }
    	if (f.indexOf("\\")!=-1) { f = f.substring(f.lastIndexOf("\\")); }
    	if (f.indexOf(":")!=-1) { f = f.substring(f.lastIndexOf(":")); }
    	for (int i=0; i<f.length(); i++) {
    		char ch = f.charAt(i);
    		if ((ch>='0' && ch<='9') || (ch>='a' && ch<='z') || ch=='.') {
    			// ok
    		} else {
    			f = f.substring(0, i) + "_" + f.substring(i+1);
    		}
    	}
    	return f;
    }
    
    /** Deletes a fixtureDefImage from the database, and
     * removes any filesystem resources.
    *
    * The id column of the object will be populated on return
    *
    * @param fixtureDefImage the fixtureDefImage to delete
    *
    */
   public void deleteFixtureDefImage(FixtureDefImageTO fixtureDefImage) {
       String sql =
           "DELETE FROM fixtureDefImage " + 
           " WHERE id = ?";
       long updated = jt.update(sql,
           new Object[] { 
               fixtureDefImage.getId(),		
           });
       if (updated!=1) {
           throw new DataIntegrityViolationException("fixtureDefImage delete failed (" + updated + " rows updated)");
       }
   		File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
   		File file = new File(imageBase, fixtureDefImage.getFileLocation());
   		if (!file.delete()) {
   			throw new DataIntegrityViolationException("fixtureDefImage delete failed - could not remove filesystem resource");
   		}
   }

}
