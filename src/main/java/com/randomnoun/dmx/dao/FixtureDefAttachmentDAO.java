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
import com.randomnoun.dmx.to.FixtureDefAttachmentTO;
import com.randomnoun.dmx.to.FixtureDefTO;

public class FixtureDefAttachmentDAO {

    private JdbcTemplate jt;

    public static class FixtureDefAttachmentDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            FixtureDefAttachmentTO f = new FixtureDefAttachmentTO();
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

    public FixtureDefAttachmentDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of fixtureDefAttachments, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause if supplied, all clients are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of FixtureDefAttachmentTO objects that satisfy the supplied criteria
     */
    public List<FixtureDefAttachmentTO> getFixtureDefAttachments(String sqlWhereClause) {
        String sql =
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
            " FROM fixtureDefAttachment " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<FixtureDefAttachmentTO>) jt.query(sql, new FixtureDefAttachmentDAORowMapper());
    }

    /** Return a list of fixtureDefAttachments for a given fixtureDef.
     *
     * @param fixtureDef the fixtureDefTO to return images for
     *
     * @return a list of FixtureDefAttachmentTO objects
     */
    public List<FixtureDefAttachmentTO> getFixtureDefAttachments(FixtureDefTO fixtureDef) {
        String sql =
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description" +
            " FROM fixtureDefAttachment " +
            " WHERE fixtureDefId = " + fixtureDef.getId();
	    return (List<FixtureDefAttachmentTO>) jt.query(sql, new FixtureDefAttachmentDAORowMapper());
    }
    
    
    /** Return a fixtureDefAttachment
     *
     * @param fixtureDefAttachmentId the fixtureDefAttachmentId
     *
     * @return the requested FixtureDefAttachmentTO object
     */
    public FixtureDefAttachmentTO getFixtureDefAttachment(long fixtureDefAttachmentId) {
        return (FixtureDefAttachmentTO) jt.queryForObject(
            "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
            " FROM fixtureDefAttachment " +
            " WHERE id = ?",
            new Object[] { new Long(fixtureDefAttachmentId) }, 
            new FixtureDefAttachmentDAORowMapper());
    }

    /** Return a fixtureDefAttachment for a given fixture definition id / filename
    *
    * @param fixtureDefId the fixtureDefId
    * @param filename the filename
    *
    * @return the requested FixtureDefAttachmentTO object
    */
   public FixtureDefAttachmentTO getFixtureDefAttachment(long fixtureDefId, String filename) {
       return (FixtureDefAttachmentTO) jt.queryForObject(
           "SELECT id, fixtureDefId, name, size, contentType, fileLocation, description " +
           " FROM fixtureDefAttachment " +
           " WHERE fixtureDefId = ? AND name = ?",
           new Object[] { new Long(fixtureDefId), filename }, 
           new FixtureDefAttachmentDAORowMapper());
   }
    
    
    /** Update a fixtureDefAttachment
     *
     * @param fixtureDefAttachment the fixtureDefAttachment to update
     */
    public void updateFixtureDefAttachment(FixtureDefAttachmentTO fixtureDefAttachment) {
        String sql =
            "UPDATE fixtureDefAttachment " +
            " SET fixtureDefId=?, name=?, size=?, contentType=?, fileLocation=?, description=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                fixtureDefAttachment.getFixtureDefId(),
                fixtureDefAttachment.getName(),
                fixtureDefAttachment.getSize(),
                fixtureDefAttachment.getContentType(),
                fixtureDefAttachment.getFileLocation(),
                fixtureDefAttachment.getDescription(),
                fixtureDefAttachment.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDefAttachment update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a fixtureDefAttachment into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param fixtureDefAttachment the fixtureDefAttachment to insert
     *
     * @return the id of the new record
     */
    public long createFixtureDefAttachment(FixtureDefAttachmentTO fixtureDefAttachment) {
    	fixtureDefAttachment.setFileLocation(
    		"fixtureDefs/" + fixtureDefAttachment.getFixtureDefId() + "/" + 
    		sanitiseFilename(fixtureDefAttachment.getName()));
    	// @TODO check for duplicate filenames + add a discriminator
    	
        String sql =
            "INSERT INTO fixtureDefAttachment " + 
            " (fixtureDefId, name, size, contentType, fileLocation, description) " +
            " VALUES (?, ?, ?, ?, ?, ?)";
        long updated = jt.update(sql,
            new Object[] { 
                fixtureDefAttachment.getFixtureDefId(),		
                fixtureDefAttachment.getName(),
                fixtureDefAttachment.getSize(),
                fixtureDefAttachment.getContentType(),
                fixtureDefAttachment.getFileLocation(),
                fixtureDefAttachment.getDescription()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("fixtureDefAttachment insert failed (" + updated + " rows updated)");
        }
        long fixtureDefAttachmentId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        fixtureDefAttachment.setId(fixtureDefAttachmentId);
        return fixtureDefAttachmentId;
    }
    
    public void saveImage(FixtureDefAttachmentTO fixtureDefAttachment, InputStream is) throws IOException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	File newFile = new File(imageBase, fixtureDefAttachment.getFileLocation());
    	if (!newFile.getParentFile().isDirectory()) {
    		newFile.getParentFile().mkdirs();
    	}
    	FileOutputStream fos = new FileOutputStream(newFile);
    	StreamUtils.copyStream(is, fos);
    	fos.close();
    }
    
    public InputStream loadImage(FixtureDefAttachmentTO fixtureDefAttachment) throws FileNotFoundException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	return new FileInputStream(new File(imageBase, fixtureDefAttachment.getFileLocation()));
    }

    public static String sanitiseFilename(String f) {
    	f = f.toLowerCase();
    	if (f.indexOf("/")!=-1) { f = f.substring(f.lastIndexOf("/")); }
    	if (f.indexOf("\\")!=-1) { f = f.substring(f.lastIndexOf("\\")); }
    	if (f.indexOf(":")!=-1) { f = f.substring(f.lastIndexOf(":")); }
    	for (int i=0; i<f.length(); i++) {
    		char ch = f.charAt(i);
    		if ((ch>='0' && ch<='9') || (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || ch=='.') {
    			// ok
    		} else {
    			f = f.substring(0, i) + "_" + f.substring(i+1);
    		}
    	}
    	return f;
    }
    
    /** Deletes a fixtureDefAttachment from the database, and
     * removes any filesystem resources.
    *
    * @TODO remove folder if empty
    *
    * The id column of the object will be populated on return
    *
    * @param fixtureDefAttachment the fixtureDefAttachment to delete
    *
    */
   public void deleteFixtureDefAttachment(FixtureDefAttachmentTO fixtureDefAttachment) {
       String sql =
           "DELETE FROM fixtureDefAttachment " + 
           " WHERE id = ?";
       long updated = jt.update(sql,
           new Object[] { 
               fixtureDefAttachment.getId(),		
           });
       if (updated!=1) {
           throw new DataIntegrityViolationException("fixtureDefAttachment delete failed (" + updated + " rows updated)");
       }
   		File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
   		File file = new File(imageBase, fixtureDefAttachment.getFileLocation());
   		if (!file.delete()) {
   			throw new DataIntegrityViolationException("fixtureDefAttachment delete failed - could not remove filesystem resource");
   		}
   }

}
