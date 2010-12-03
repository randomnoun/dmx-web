package com.randomnoun.dmx.web;

import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.*;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;

/**
 * This class is used to assist in working with structured tables. It should be
 * instantiated once for a single structured object, and can operate on any
 * number of tables contained within that object (although only one of these
 * is <i>current</i> at any point in time.
 *
 * <p>
 * The <i>current table</i> is set at instantiation time, but can be modified
 * using the setCurrentTable() method. The active row within this table can be
 * set with the setCurrentRow() method. Most utility operations in this class
 * only affect the current row in the current table.
 * </p>
 *
 * <p>
 * Methods contained within this class perform validation (checkXxxxx),
 * formatting (formatXxxxx). To populate rows, either use addRow(), which
 * takes a column name->column value map as a parameter, or addRowFromBean(),
 * which takes an EJBLocalObject.
 * </p>
 *
 * @author knoxg
 * @version $Id$
 */
public class Table {
  public static String _revision = "$Id$";

  private static final Pattern datePattern = Pattern.compile("^((0|1|2)[0-9]|3[0-1])/(0?[1-9]|1[0-2])/[1-9][0-9][0-9][0-9]$");

  /** Constant returned by getOperation() to denote an invalid key */
  public final static int INVALID_KEY = -1;

  /** Constant returned by getOperation() to denote a row creation request */
  public final static int ROW_CREATE = 1;

  /** Constant returned by getOperation() to denote a row update request */
  public final static int ROW_UPDATE = 2;

  /** Constant returned by getOperation() to denote a row deletion request */
  public final static int ROW_DELETE = 3;

  /** Constant returned by getOperation() to denote a no-op */
  public final static int ROW_SKIP = 4;

  /** The structured object that this table is contained within */
  private Map form;

  /** The list of rows representing this table */
  private List rows;

  /** The current row map (maps column names to column values) */
  private Map row;

  /** The current row number (index into the rows variable above) */
  private int currentRowNumber;

  /** The table within the current form that we are processing */
  private String tableName;

  /** The column representing the primary key in this table */
  private String primaryKeyColumnName;

  /** The name of the editable key field */
  private boolean hasEditableKey;

  /**
   * The class of the primary key. Must be a simple java type (e.g.
   * Integer.class)
   */
  private Class primaryKeyClass;

  /** The object containing validation errors for this structured object */
  private ErrorList errors;
  Logger logger;

  /**
   * Create a utility class for the supplied structured object (form), and sets
   * the active table to a particular table, with the supplied primary key
   * column name. This method will also create a ErrorData object for the
   * structured object, if it does not exist.
   *
   * @param form Typically the request object passed to the business-layer
   *   service, that contains the table.
   * @param tableName The key of 'form' that holds the table we are interested in
   * @param primaryKeyColumnName The primary key of the table.
   * @param primaryKeyClass The class of the primary key
   *   (e.g. java.lang.String.class)
   */
  public Table(Map form, String tableName, String primaryKeyColumnName,
    Class primaryKeyClass) {
    logger = Logger.getLogger(Table.class.getName());
    this.form = form;
    this.errors = (ErrorList) form.get("errors");
    this.primaryKeyClass = primaryKeyClass;
    this.hasEditableKey = false;

    if (errors == null) {
      this.errors = new ErrorList();
      form.put("errors", errors);
    }

    setCurrentTable(tableName, primaryKeyColumnName, primaryKeyClass);
  }

  /**
   * Returns the validation error list for this structured object
   *
   * @return the validation error list for this structured object
   */
  public ErrorList getErrors() {
    return errors;
  }

  /**
   * Returns the form in which this table is contained
   *
   * @return the form in which this table is contained
   */
  public Map getForm() {
    return form;
  }

  /**
   * Defines whether this table has an editable key field or not.
   *
   * @param hasEditableKey true if the table has an editable key
   */
  public void setEditableKey(boolean hasEditableKey) {
    this.hasEditableKey = hasEditableKey;
  }

  /**
   * Sets the active table to a different table in the same structured object
   * (Uses same error validation object as form). This method also resets the
   * editable key flag to 'false'.
   *
   * @param tableName Table name; must be a structured list in the main
   *        structured object. (subtables are not yet supported, but could be
   *        implemented relatively easily at a later point).
   * @param primaryKeyColumnName The primary key column for this table
   * @param primaryKeyClass The class of this primary key; e.g.
   *        java.lang.String or java.lang.Long.
   */
  public void setCurrentTable(String tableName, String primaryKeyColumnName,
    Class primaryKeyClass) {
    this.tableName = tableName;
    this.primaryKeyColumnName = primaryKeyColumnName;
    this.rows = (List) form.get(tableName);
    this.primaryKeyClass = primaryKeyClass;
    this.hasEditableKey = false;
  }

  /**
   * Sets the current row number of this table. If the row is not valid, then
   * this method will probably throw some kind of runtime exception.
   *
   * @param currentRowNumber The new current row number.
   *
   * @return A map of column name->value pairs for the current row.
   */
  public Map setCurrentRow(int currentRowNumber) {
    this.currentRowNumber = currentRowNumber;
    this.row = (Map) rows.get(currentRowNumber);

    return row;
  }

  /**
   * DOCUMENT ME
   *
   * @return DOCUMENT ME
   */
  public int getCurrentRow() {
    return currentRowNumber;
  }

  /**
   * Returns true if the current row is empty, i.e. contains no column/values.
   * Note that if the current row has any columns, then this method will
   * return <i>false</i>, even if all those columns are all null or set to the
   * empty string.
   *
   * @return <i>true</i> if the row is empty, <i>false</i> otherwise
   */
  public boolean isEmptyRow() {
    return (row == null);
  }

  /**
   * Returns the primary key of this row, as a String
   *
   * @return the primary key of this row, as a String
   */
  public String getPrimaryKeyString() {
    return (String) row.get(primaryKeyColumnName);
  }

  /**
   * Returns the primary key of this row, as a Integer object
   *
   * @return the primary key of this row, as a Integer object
   */
  public Integer getPrimaryKeyInteger() {
    return new Integer((String) row.get(primaryKeyColumnName));
  }

  /**
   * Returns the primary key of this row, as a Long object
   *
   * @return the primary key of this row, as a Long object
   */
  public Long getPrimaryKeyLong() {
    return new Long((String) row.get(primaryKeyColumnName));
  }

  /**
   * Determines which operation a user wishes to perform on a DHTML-generated
   * table.
   *
   * @return This method will return one of the following constants:
   *         <ul><li>ROW_CREATE - the user wishes to insert this row
   *         <li>ROW_DELETE - the user wishes to delete this row
   *         <li>ROW_UPDATE - the user wishes to update this row
   *         <li>INVALID_KEY - the primary key for this row is invalid.  </ul>
   */
  public int getOperation() {
    int operation;
    String keyString = getPrimaryKeyString();

    if (hasEditableKey) {
      // if the key is editable, then *every* row must contain it.
      // if an empty key is invalid, then this should be picked up
      // by the appropriate validation routines
      if (!Text.isBlank(keyString)) {
        if (primaryKeyClass == Integer.class) {
          try {
            int i = Integer.parseInt(keyString);
          } catch (NumberFormatException nfe) {
            return INVALID_KEY;
          }
        }
      }

      if ("Y".equals((String) row.get("cmdDelete"))) {
        operation = ROW_DELETE;
      } else if ("Y".equals((String) row.get("cmdUpdate")) ||
                 "AY".equals((String) row.get("cmdUpdate"))) {
        operation = ROW_UPDATE;
      } else if ("X".equals((String) row.get("cmdUpdate"))) {
        operation = ROW_SKIP;
      } else {
        operation = ROW_CREATE;
      }

      return operation;
    }

    // we do *NOT* have an editable key field.
    if (Text.isBlank(keyString)) {
      operation = ROW_CREATE;
    } else {
      // NB: should only really do this if on a numeric key.
      if (primaryKeyClass == Integer.class) {
        try {
          int i = Integer.parseInt(keyString);
        } catch (NumberFormatException nfe) {
          return INVALID_KEY;
        }
      }
      if ("Y".equals((String) row.get("cmdDelete"))) {
        operation = ROW_DELETE;
      } else if ("X".equals((String) row.get("cmdUpdate"))) {
    	  // row has not changed, but had been submitted in a previous POST
    	  // (may or may not have validation errors on it) 
        operation = ROW_UPDATE;
      } else if ("N".equals((String) row.get("cmdUpdate"))) {
    	  // non-autoincrementing non-editable keys
    	operation = ROW_CREATE;
      } else {
        operation = ROW_UPDATE;
      }
    }

    return operation;
  }

  /**
   * Adds a row to the current table, using the passed local EJB object to
   * populate the columns. Only the fields we are specifically request will be
   * added to this row. See Codec.createStructuredMap for more information
   * about how this row is created.
   *
   * @param object The EJB object to extract data from
   * @param fields An array of fields within the EJB that we wish to retrieve
   *
   * @throws NoSuchMethodException A field name was specified that did not
   *   exist in the EJB
   * @throws IllegalAccessException A non-public field was requested
   * @throws java.lang.reflect.InvocationTargetException An exception
   *   occurred whilst executing the getXxxx method of the EJB
   *
  public void addRowFromBean(EJBLocalObject object, String[] fields)
  throws NoSuchMethodException, IllegalAccessException,
    java.lang.reflect.InvocationTargetException {
    row = Codec.createStructuredMap(object, fields);
    rows.add(row);
    currentRowNumber = rows.size() - 1;
  }
  */

  /**
   * Adds a row to the current table. The current row number is set to point to
   * this row. No checking is made to ensure that the columns in the Map
   * supplied to this object match the columns in the existing table.
   *
   * @param row The row to add to this table.
   */
  public void addRow(Map row) {
    rows.add(row);
    currentRowNumber = rows.size() - 1;
  }

  /**
   * Returns the current row
   *
   * @return the current row
   */
  public Map getRow() {
    return row;
  }

  /**
   * Returns the current set of rows
   *
   * @return the current set of rows
   */
  public List getRows() {
    return rows;
  }

  /**
   * Returns the table name
   *
   * @return DOCUMENT ME
   */
  public String getName() {
    return tableName;
  }

  /**
   * Returns the primary key column name
   *
   * @return the primary key column name
   */
  public String getPrimaryKeyColumnName() {
    return primaryKeyColumnName;
  }

  /**
   * Returns a value in a particular row. Navigates through structured objects
   * if required
   *
   * @param columnName The column name to retrieve.
   *
   * @return the value of the column or null if the column does not exist
   */
  public String getRowValue(String columnName) {
    try {
      return (String) PropertyUtils.getProperty(row, columnName);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Perform default formatting on a column. By default, this just converts the
   * value to a string using the .toString() method. If the field does not
   * exist, then it is not created. This method acts on a single row only;
   * i.e. setCurrentTable() and setCurrentRow() must be called before this is
   * invoked
   *
   * @param column the column to be formatted
   */
  public void formatStringColumn(String column) {
    Object obj = row.get(column);

    if (obj == null) {
      row.put(column, "");
    } else if (!(obj instanceof String)) {
      row.put(column, obj.toString());
    }
  }


  /**
   * Perform currency formatting on a column, converting the field to adhere to
   * the form <i>nnn.nn</i> if possible.
   *
   * <p>
   * This method acts on a single row only; i.e. setCurrentTable() and
   * setCurrentRow() must be called before this is invoked
   * </p>
   *
   * @param column the column to be formatted
   *
   * @throws IllegalArgumentException if the specified column did not contain a
   *         numeric value capable of being converted into currency format
   *
  public void formatCurrencyColumn(String column) throws IllegalArgumentException {
    Object obj = row.get(column);

    if (obj == null) {
      row.put(column, "");
    } else if (obj instanceof Long) {
      row.put(column, Text.toDollarDecimal(((Long) obj).longValue()));
    } else if (obj instanceof Integer) {
      row.put(column, Text.toDollarDecimal(((Integer) obj).intValue()));
    } else {
      throw new IllegalArgumentException("Expected numeric field for column '" +
          column + "'");
    }
  }
  */

  /**
   * Perform date formatting on a column. If the field does not exist, then it
   * is not created.
   *
   * <p>
   * This method acts on a single row only; i.e. setCurrentTable() and
   * setCurrentRow() must be called before this is invoked
   * </p>
   *
   * @param column the column to be formatted
   * @param dateFormat the date format to be applied
   *
   * @throws IllegalArgumentException The specified column did not contain a
   *         Date object
   */
  public void formatDateColumn(String column, SimpleDateFormat dateFormat) {
    Object obj = Struct.getValue(row, column);
    if (obj == null) {
      // logger.info("NULL DATE: " + Codec.getValue(row, "payRun.currentPEDate"));
    	Struct.setValue(row, column, "", false, false, true);
    } else if (obj instanceof Date) {
      // row.put(column, dateFormat.format((Date) obj));
    	Struct.setValue(row, column, dateFormat.format((Date) obj), false, false, true);
    } else {
      throw new IllegalArgumentException("Expected date field for column '" +
        column + "'");
    }
  }

  /**
   * Performs mandatoriness validation.
   *
   * @param columnName The column name in this row we are inspecting
   * @param length The length of the field to check, or -1 if there is no limit
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkMandatory(String columnName, int length, String label) {
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Missing field", "The field '" + label + "' is missing.", 
        ErrorList.SEVERITY_INVALID);

      return false;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (Text.isBlank(value)) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName,
        "Mandatory field", "The field '" + label + "' must be entered",
        ErrorList.SEVERITY_INVALID);

      return false;
    }

    if (length > -1 && value.length() > length) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Size exceeded",
        "The field '" + label + "' cannot be more than '" + length +
        "' characters", ErrorList.SEVERITY_INVALID);

      return false;
    }

    return true;
  }

  /**
   * Performs inclusive mandatoriness validation. If any of the fields specified are
   * entered, then all fields in the group must be entered. 
   *
   * @param columnNames The column names in this row we are inspecting
   * @param labels The column labels to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkMandatoryInclusive(String[] columnNames, String[] labels) {
	  
	  if (columnNames.length!=labels.length) {
		  throw new IllegalArgumentException("columnName array length must be == label array length");
	  }
	  
	  int count=0;
	  String fieldNames = "";
	  for (int i = 0; i < columnNames.length; i++) {
		  Object objValue = getRowValue(columnNames[i]);
	      String value;
		  if (objValue==null) {
		      errors.addError(tableName + "[" + currentRowNumber + "]." + columnNames[i], 
		    	        "Missing field", "The field '" + labels[i] + "' is missing.", 
		    	        ErrorList.SEVERITY_INVALID);
    	      return false;
		  }
		  if (objValue instanceof String) {
			  value = (String) objValue;
		  } else {
			  value = objValue.toString();
		  }
		  if (!Text.isBlank(value)) { count++; };
		  fieldNames += (i==0 ? "" : ((i==columnNames.length-1) ? " and " : ", ")) + 
		    "'" + labels[i] + "'";
	  }
	  if (count==0 || count==columnNames.length) {
		  // OK
		  return true;
	  } else {
		  
	      errors.addError(tableName + "[" + currentRowNumber + "]." +
	         Text.join(columnNames, "," + tableName + "[" + currentRowNumber + "]."),
	       	 "Mandatory fields", "You must enter all of the fields " + fieldNames + ", or leave them all empty",
	    	 ErrorList.SEVERITY_INVALID);
	      return false;
	  }
  }

  
  /**
   * Performs string length validation.
   *
   * @param columnName The column name in this row we are inspecting
   * @param length The length of the field to check, or -1 if there is no limit
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkLength(String columnName, int length, String label) {
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (!Text.isBlank(value) && value.length() > length) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Size exceeded",
        "The field '" + label + "' cannot be more than '" + length +
        "' characters", ErrorList.SEVERITY_INVALID);
      return false;
    }

    return true;
  }


  /**
   * Performs date format validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkDate(String columnName, String label) {
    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (value.equals("")) {
      return true;
    }

    try {
      sdf.parse(value);
    } catch (ParseException pe) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field",
        "The field '" + label + "' must be a date in the form dd/mm/yyyy", 
        ErrorList.SEVERITY_INVALID);
      return false;
    }
    Matcher matcher = datePattern.matcher(value);
    if (!matcher.matches()) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field",
        "The field '" + label + "' must be a date in the form dd/mm/yyyy", 
        ErrorList.SEVERITY_INVALID);
      return false;
    } 
    return true;
  }

  /**
   * Performs numeric field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkNumeric(String columnName, String label) {
    return checkNumeric(columnName, label, -1);
  }

  /**
   * Performs numeric field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   * @param maxDigits The maximum number of digits permissible. A negative value indicates
   *                  that no maximum constraint applies; a value of <i>0</i>, whilst
   *                  valid, is somewhat pointless.
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkNumeric(String columnName, String label, int maxDigits) {
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (value.equals("")) {
      return true;
    }

    if (!Text.isNumeric(value)) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field", "The field '" + label + "' must be numeric", 
        ErrorList.SEVERITY_INVALID);

      return false;
    }

    if (maxDigits >= 0 && value.length() > maxDigits) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field", "The field '" + label + "' must not contain more than " + 
        maxDigits + " digits", ErrorList.SEVERITY_INVALID);

      return false;
    }

    return true;
  }

  
  /**
   * Performs float field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkFloat(String columnName, String label) {
    return checkFloat(columnName, label, -1);
  }

  /**
   * Performs float field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   * @param maxDigits The maximum number of digits permissible. A negative value indicates
   *                  that no maximum constraint applies; a value of <i>0</i>, whilst
   *                  valid, is somewhat pointless.
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkFloat(String columnName, String label, int maxDigits) {
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (value.equals("")) {
      return true;
    }

    boolean canConvertToFloat = false;
    try {
    	float f = new Float(value).floatValue();
    	canConvertToFloat = true;
    } catch (NumberFormatException nfe) {
    	// fair enough then
    }
   
    if (!canConvertToFloat) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field", "The field '" + label + "' must be a valid number", 
        ErrorList.SEVERITY_INVALID);

      return false;
    }

    if (maxDigits >= 0 && value.length() > maxDigits) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
        "Invalid field", "The field '" + label + "' must not contain more than " + 
        maxDigits + " digits", ErrorList.SEVERITY_INVALID);

      return false;
    }

    return true;
  }
  
  
  /**
   * Performs currency field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label The column label to insert in the error message
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   *
  public boolean checkCurrency(String columnName, String label) {
    Object objValue = getRowValue(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = objValue.toString();
    }

    if (value.equals("")) {
      return true;
    }

    try {
      Text.toCentsValue(value);
    } catch (NumberFormatException nfe) {
      errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 0,
        "Invalid field", "The field '" + label + "' must represent a currency amount (" + nfe.getMessage() + ").");
      return false;
    }

    return true;
  }
  */

  /**
   * Performs combo field validation. Note that an empty field will pass this
   * validation rule (use checkMandatory if the field must be entered).
   *
   * @param columnName The column name in this row we are inspecting
   * @param label A string array containing allowable values
   * @param allowedValues DOCUMENT ME
   *
   * @return <i>true</i> if validation passes, <i>false</i> otherwise.
   */
  public boolean checkCombo(String columnName, String label,
    String[] allowedValues) {
    Object objValue = row.get(columnName);
    String value;

    if (objValue == null) {
      return true;
    }

    if (objValue instanceof String) {
      value = (String) objValue;
    } else {
      value = getRowValue(columnName);
    }

    if (value.equals("")) {
      return true;
    }

    for (int i = 0; i < allowedValues.length; i++) {
      if (value.equals(allowedValues[i])) {
        return true;
      }
    }

    errors.addError(tableName + "[" + currentRowNumber + "]." + columnName, 
      "Invalid field", "The field '" + label + "' contains an invalid value",
      ErrorList.SEVERITY_INVALID);

    return false;
  }
}
