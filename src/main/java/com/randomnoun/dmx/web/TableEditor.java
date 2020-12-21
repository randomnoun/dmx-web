package com.randomnoun.dmx.web;

import java.util.*;
import org.apache.log4j.*;
import com.randomnoun.common.ErrorList;
import com.randomnoun.common.ExceptionUtil;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;

/**
 * Utility class for updating tabular data
 *
 * @author knoxg
 * @version $Id$
 */

public abstract class TableEditor {
  public static String _revision = "$Id$";

  Logger logger;
  public TableEditor() {
    logger = Logger.getLogger(TableEditor.class.getName());
  }


  public static class TableEditorResult {
	  ErrorList errors;
	  List rows;
	  
    public ErrorList getErrors() {
		return errors;
	}
	public void setErrors(ErrorList errors) {
		this.errors = errors;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
  }
  
  protected Table  table = null;


	/** Removes empty rows from the supplied structured map */
	public static void removeEmptyRows(Map form, String[] columns,
			String rowPrefix) {
		int rowindex = 0;
		int colindex = 0;
		boolean isEmpty = true;
		Map row;
		String data;

		List rows = (List) form.get(rowPrefix);
		if (rows == null) { return; }
		for (rowindex = 0; rowindex < rows.size(); rowindex++) {
			isEmpty = true;
			row = (Map) rows.get(rowindex);
			if (row != null) {
				for (colindex = 0; colindex < columns.length; colindex++) {
					data = (String) Struct.getValue(row, columns[colindex]);
					// logger.info("row " + rowindex + ", column = " +
					// columns[colindex] + ", data = " + data);
					if (!Text.isBlank(data)) {
						isEmpty = false;
					}
				}
			}
			if (isEmpty) {
				rows.remove(rowindex);
				// logger.info("*********************************************************");
				// logger.info("Removing row ="+rowindex +
				// "***************************" );
				rowindex--; // the next row has now decreased it's index number by 1
			}
		}
	}

  
  /** Contains any exception that occurred during maintenance */
  private Exception maintainException;

  /** Response object */
  private TableEditorResult result = new TableEditorResult();

  /** Override this method in order to perform row validations. Should return
   * true for a valid row, and false otherwise (Validation errors should
   * be entered into the table's ErrorList object). It can be assumed that
   * the current table name and row has been set.
   *
   * @param row The row we are validating
   *
   * @return true if valid, false otherwise
   */
  public boolean validateRow(Map row) {
    return true;
  }

  /** Override this method in order to perform row validations when creating rows.
   * Should return true for a valid row, and false otherwise (Validation errors should
   * be entered into the table's ErrorList object). It can be assumed that
   * the current table name and row has been set, and validateRow() has already
   * been called for this row and returned true.
   *
   * @param row The row we are intending to create
   *
   * @return true if valid, false otherwise
   */
  public boolean validateCreateRow(Map row) {
    return true;
  }

  /** Override this method in order to perform row validations when updating rows.
   * Should return true for a valid row, and false otherwise (Validation errors should
   * be entered into the table's ErrorList object). It can be assumed that
   * the current table name and row has been set, and validateRow() has already
   * been called for this row and returned true.
   *
   * @param row The row to validate
   *
   * @return true if valid, false otherwise
   */
  public boolean validateUpdateRow(Map row) {
    return true;
  }

  /** Override this method in order to determine whether a row
   * should be allowed to be deleted.
   *
   * @param row The row to check
   *
   * @return true if deletion can proceed, false otherwise
   */
  public boolean validateDeleteRow(Map row) {
    return true;
  }


  /** Used to initialise this class for any row creation/update/modification
   * or deletion.
   *
   */
  public void initMaintain() throws Exception { }

  /** Used to release any resources taken by initMaintain
   *
   */
  public void releaseMaintain() throws Exception { }

  /** Override this method in order to perform row creation. It can be
   * assumed that validate() has already been invoked for this row,
   * and that it has returned 'true'.
   *
   * @param row The row to validate
   */
  public abstract void createRow(Map row) throws Exception;

  /** Override this method in order to perform row updates. It can be
   * assumed that validate() has already been invoked for this row,
   * and that it has returned 'true'.
   *
   * @param row The row to validate
   */
  public abstract void updateRow(Map row) throws Exception;

  /** Override this method in order to perform row deletion. It can be
   * assumed that validate() has already been invoked for this row,
   * and that it has returned 'true'.
   *
   * @param row The row to validate
   */
  public abstract void deleteRow(Map row) throws Exception;

  /** Override this method in order to perform any actions before
   * creation/update/delete takes place. It is invoked for each row in the
   * table to be updated. It can be
   * assumed that validate() has already been invoked for this row,
   * and that it has returned 'true'.
   *
   * @param row The row we are about to create/update/delete
   */
  public void preMaintainRow(Map row) throws Exception { }

  /** Calls the necessary methods within this class to perform validation.
   *  Subclasses of this class are expected to override the validateRow()
   *  method of this class in order to allow this method to work correctly.
   *
   * @return true if validation passes, false otherwise
   */
  public boolean validateTable() {

    List      rows = table.getRows();
    Map       row;
    ErrorList errors = table.getErrors();
    int       size = 0;
    int       rowCount;
    boolean   valid = true;
    boolean   validRow;

    if (rows != null) { size = rows.size();}
    for (rowCount=0; rowCount<size; rowCount++) {
      row = table.setCurrentRow(rowCount);
      if (table.isEmptyRow()) { continue; }
      switch (table.getOperation()) {
        case Table.ROW_CREATE:
          validRow = validateRow(row);
          // if row is valid, see if any creation-specific validations hold
          if (validRow) { validRow = validateCreateRow(row); }
          valid = valid & validRow;
          break;
        case Table.ROW_UPDATE:
          validRow = validateRow(row);
          // if input is valid, see if any update-specific validations hold
          if (validRow) { validRow = validateUpdateRow(row); }
          valid = valid & validRow;
          break;
        case Table.ROW_DELETE:
          // no checking here -- all deletions are considered valid;
          valid = valid & validateDeleteRow(row);
          break;
        case Table.INVALID_KEY:
          valid = false;
          errors.addError(table.getName() + "." + rowCount + "." + table.getPrimaryKeyColumnName(),
            "Illegal format", "Primary key must be numeric",
            ErrorList.SEVERITY_ERROR);
      }
    }
    if (valid==false) {
    	result.setErrors(table.getErrors());
    }
    return valid;
  }

  /** Calls the necessary methods within this class to perform updates on
   *  this table; i.e. creations, modifications and deletions.
   *
   *  Subclasses of this class are expected to override the createRow(),
   *  updateRow() and deleteRow() methods in order to allow this method to
   *  work correctly.
   *
   * @return true if modifications succeed, false otherwise
   */
  public boolean maintainTable() {
    List      rows = table.getRows();
    Map       row;
    ErrorList errors = table.getErrors();
    int       size = 0;
    int       rowCount;
    boolean   valid = true;

    if (rows != null) { size = rows.size();}
    try {
      initMaintain();
      for (rowCount=0; rowCount<size; rowCount++) {
        row = table.setCurrentRow(rowCount);
        if (table.isEmptyRow()) { continue; }
        preMaintainRow(row);
        switch (table.getOperation()) {
          case Table.ROW_CREATE:
            if (logger.isDebugEnabled()) { logger.debug("Inserting: " + Struct.structuredMapToString("row", row)); }
            createRow(row);
            break;
          case Table.ROW_UPDATE:
        	if (logger.isDebugEnabled()) { logger.debug("Updating PK " + table.getPrimaryKeyString() + ": " + Struct.structuredMapToString("row", row)); }
            updateRow(row);
            break;
          case Table.ROW_DELETE:
        	if (logger.isDebugEnabled()) { logger.debug("Deleting PK " + table.getPrimaryKeyString() + ": " + Struct.structuredMapToString("row", row)); }
            deleteRow(row);
            break;
          default:
          // should raise some sort of exception here.
        }
      }
    } catch (Exception ne) {
      maintainException = ne;
      logger.error("Exception occurred during maintenance", ne);
      table.getErrors().addError("System error", "Exception occurred during maintenance", ErrorList.SEVERITY_FATAL); 
      return false;
    } finally {
      try {
        releaseMaintain();
      } catch (Exception e2) {
        // just ignore this. (Log?)
        e2.printStackTrace();
      }
    }
    return true;
  }
  
  protected Table getTable() {
	  return table;
  }

  /** Returns the exception that occured during a call to maintainTable()
   *
   *  @return the exception that occured during a call to maintainTable()
   */
  public Exception getException() {
    return maintainException;
  }

  /** Returns the response object that has been generated so far. This response
   *  may include validation errors, etc... that have been generated during
   *  calls to other methods in this class
   *
   *  @return the response object that has been generated so far.
   */
  public TableEditorResult getResult() {
    return result;
  }

 /** Returns the username passed in to this request
  *  @return the username passed in to this request */
  public String getUsername() {
    return (String) table.getForm().get("authUsername");
  }

  /** Returns the password passed in to this request
   *  @return the password passed in to this request */
  public String getPassword() {
    return (String) table.getForm().get("authPassword");
  }

  
}