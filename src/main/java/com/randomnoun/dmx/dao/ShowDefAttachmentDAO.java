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
import com.randomnoun.dmx.to.ShowDefAttachmentTO;
import com.randomnoun.dmx.to.ShowDefTO;

public class ShowDefAttachmentDAO {

    private JdbcTemplate jt;

    public static class ShowDefAttachmentDAORowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ShowDefAttachmentTO s = new ShowDefAttachmentTO();
            s.setId(rs.getLong("id"));
            s.setShowDefId(rs.getLong("showDefId"));
            s.setName(rs.getString("name"));
            s.setSize(rs.getLong("size"));
            s.setContentType(rs.getString("contentType"));
            s.setFileLocation(rs.getString("fileLocation"));
            s.setDescription(rs.getString("description"));
            return s;
        }
    }

    public ShowDefAttachmentDAO(JdbcTemplate jt) {
        this.jt = jt;
    }

    /** Return a list of showDefAttachments, using the supplied SQL WHERE clause. If a
     * null sqlWhereClause is supplied, all showDefAttachments are returned.
     *
     * @param sqlWhereClause a condition to apply to the SQL SELECT
     *
     * @return a list of ShowDefAttachmentTO objects that satisfy the supplied criteria
     */
    public List<ShowDefAttachmentTO> getShowDefAttachments(String sqlWhereClause) {
        String sql =
            "SELECT id, showDefId, name, size, contentType, fileLocation, description " +
            " FROM showDefAttachment " +
            (sqlWhereClause == null ? "" : " WHERE " + sqlWhereClause);
	    return (List<ShowDefAttachmentTO>) jt.query(sql, new ShowDefAttachmentDAORowMapper());
    }
    
    /** Return a list of showDefAttachments for a given showDef.
    *
    * @param showDef the showDefTO to return attachments for
    *
    * @return a list of ShowDefAttachemntTO objects
    */
   public List<ShowDefAttachmentTO> getShowDefAttachments(ShowDefTO showDef) {
       String sql =
           "SELECT id, showDefId, name, size, contentType, fileLocation, description" +
           " FROM showDefAttachment " +
           " WHERE showDefId = " + showDef.getId();
	    return (List<ShowDefAttachmentTO>) jt.query(sql, new ShowDefAttachmentDAORowMapper());
   }
   

    /** Return a showDefAttachment
     *
     * @param showDefAttachmentId the showDefAttachmentId
     *
     * @return the requested ShowDefAttachmentTO object
     */
    public ShowDefAttachmentTO getShowDefAttachment(long showDefAttachmentId) {
        return (ShowDefAttachmentTO) jt.queryForObject(
            "SELECT id, showDefId, name, size, contentType, fileLocation, description " +
            " FROM showDefAttachment " +
            " WHERE id = ?",
            new Object[] { new Long(showDefAttachmentId) }, 
            new ShowDefAttachmentDAORowMapper());
    }
    
    /** Return a showDefImage for a given show definition id / filename
    *
    * @param showDefId the showDefId
    * @param filename the filename
    *
    * @return the requested FixtureDefImageTO object
    */
   public ShowDefAttachmentTO getShowDefAttachment(long showDefId, String filename) {
       return (ShowDefAttachmentTO) jt.queryForObject(
           "SELECT id, showDefId, name, size, contentType, fileLocation, description " +
           " FROM showDefAttachment " +
           " WHERE showDefId = ? AND name = ?",
           new Object[] { new Long(showDefId), filename }, 
           new ShowDefAttachmentDAORowMapper());
   }
   

    /** Update a showDefAttachment
     *
     * @param showDefAttachment the showDefAttachment to update
     */
    public void updateShowDefAttachment(ShowDefAttachmentTO showDefAttachment) {
        String sql =
            "UPDATE showDefAttachment " +
            " SET showDefId=?, name=?, size=?, contentType=?, fileLocation=?, description=? " + 
            " WHERE id = ?";
        int updated = jt.update(sql, 
            new Object[] { 
                showDefAttachment.getShowDefId(),
                showDefAttachment.getName(),
                showDefAttachment.getSize(),
                showDefAttachment.getContentType(),
                showDefAttachment.getFileLocation(),
                showDefAttachment.getDescription(),
                showDefAttachment.getId() });
        if (updated!=1) {
            throw new DataIntegrityViolationException("showDefAttachment update failed (" + updated + " rows updated)");
        }
    }

    /** Inserts a showDefAttachment into the database.
     *
     * The id column of the object will be populated on return
     *
     * @param showDefAttachment the showDefAttachment to insert
     *
     * @return the id of the new record
     */
    public long createShowDefAttachment(ShowDefAttachmentTO showDefAttachment) {
    	showDefAttachment.setFileLocation(
        		"showDefs/" + showDefAttachment.getShowDefId() + "/" + 
        		sanitiseFilename(showDefAttachment.getName()));
        	// @TODO check for duplicate filenames + add a discriminator
    	
        String sql =
            "INSERT INTO showDefAttachment " + 
            " (showDefId, name, size, contentType, fileLocation, description) " +
            " VALUES (?, ?, ?, ?, ?, ? )";
        long updated = jt.update(sql,
            new Object[] { 
                showDefAttachment.getShowDefId(),
                showDefAttachment.getName(),
                showDefAttachment.getSize(),
                showDefAttachment.getContentType(),
                showDefAttachment.getFileLocation(),
                showDefAttachment.getDescription()});
        if (updated!=1) {
            throw new DataIntegrityViolationException("showDefAttachment insert failed (" + updated + " rows updated)");
        }
        long showDefAttachmentId = jt.queryForLong("SELECT LAST_INSERT_ID()");
        showDefAttachment.setId(showDefAttachmentId);
        return showDefAttachmentId;
    }
    
    /** Copies all data from the supplied input stream to the file location associated with the
     * given showDefAttachment
     * 
     * @param showDefAttachment
     * @param is
     * @throws IOException
     */
    public void writeStream(ShowDefAttachmentTO showDefAttachment, InputStream is) throws IOException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	File newFile = new File(imageBase, showDefAttachment.getFileLocation());
    	if (!newFile.getParentFile().isDirectory()) {
    		newFile.getParentFile().mkdirs();
    	}
    	FileOutputStream fos = new FileOutputStream(newFile);
    	StreamUtils.copyStream(is, fos);
    	fos.close();
    }

    /** Returns an InputStream that can be used to obtain the data associated with this 
     * showDefAttachment. The calling code is responsible for closing this stream once it
     * has finished using it.
     * 
     * @param showDefAttachment
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getInputStream(ShowDefAttachmentTO showDefAttachment) throws FileNotFoundException {
    	File imageBase = new File(AppConfig.getAppConfig().getProperty("webapp.fileUpload.path"));
    	return new FileInputStream(new File(imageBase, showDefAttachment.getFileLocation()));
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

   /** Delete a showDefAttachment
    *
    * @param showDefAttachment the showDefAttachment to delete
    */
    public void deleteShowDefAttachment(ShowDefAttachmentTO showDefAttachment) {
        String sql =
            "DELETE FROM showDefAttachment " +
            " WHERE id = ?";
        int updated = jt.update(sql,
          new Object[] { showDefAttachment.getId() } );
        if (updated!=1) {
            throw new DataIntegrityViolationException("showDefAttachment delete failed (" + updated + " rows updated)");
        }
    }
}

