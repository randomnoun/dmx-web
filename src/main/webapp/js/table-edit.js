/* RN Table definition

   This is pretty old code; probably needs a barrage of unit tests

   @author  knoxg
   @version $Id$
*/


var isFormDirty = false;
var isSubmitting = false;

function rnTable(varname, lastrow, lastrowid, tableid, tablekeyfield, 
  htmltable, htmlform, deleteprompt, columns, filterColumns) 
{
  this.varname = varname;
  this.lastrow = lastrow;
	this.lastrowid = lastrowid;
	this.tableid = tableid;
	this.tablekeyfield = tablekeyfield;
	this.htmltable = htmltable;
	this.htmlform = htmlform;
	this.deleteprompt = deleteprompt;
	this.columns = columns;
	this.filterColumns = filterColumns;
}

/** Debugging function to return all properties of an object in HTML*/
function dumpProperties(obj, obj_name) {
 var result = ""
 for (var i in obj) {
   result += obj_name + "." + i + " = " + obj[i] + "\n"
 }
 result += "<HR>"
 return result
}

function makeTag(t) { return document.createElement(t); } 
function makeText(tag,text) { t=makeTag(tag); t.appendChild(document.createTextNode(text)); return t; } 

/** Same as dump_props, in a new window (only works in Mozilla */
function dumpPropertiesNewWindow(obj, obj_name) {
  d=window.open().document; 
  d.open();
  d.close(); 
  
  b=d.body; 
  b.appendChild(makeText('h3', d.title='Properties of ' + obj_name)); 
  b.appendChild(makeText('pre', dumpProperties(obj, obj_name))); 
  void 0;
}


/** Triggered when the 'add row' button is click on a DHTML table */
function fnAddRow(tblObj) {
	tblObj.lastrow = tblObj.lastrow + 1;
  tblObj.lastrowid = tblObj.lastrowid + 1

  entryTableEl = document.getElementById(tblObj.htmltable);

  origCells = tblObj.newRowCells;
  if (!origCells) {
    alert("initRnTable() has not been invoked for this table");
    return;
  }

  // empty the fields and increment the rowid's 
  //  
 	fieldRegExp = new RegExp("^" +tblObj.tableid + "\[[0-9]+\]\.");  // compiling this RegExp breaks it in Mozilla
  for (i=0; i<origCells.length; i++) {
	  // perform deep clone of the original empty cell
	  cells[i] = origCells[i].cloneNode(true);
		fields = cells[i].getElementsByTagName("input");
		for (j=0; j<fields.length; j++) {
		  field = fields.item(j);
			field.setAttribute("value", "");  // Mozilla clears automatically; required for IE.
			field.setAttribute("name", field.getAttribute("name").replace(fieldRegExp, 
			  tblObj.tableid + "[" + tblObj.lastrowid + "]."));
		}
		fields = cells[i].getElementsByTagName("select");
		for (j=0; j<fields.length; j++) {
		  field = fields.item(j);
			field.setAttribute("value", "");  // Mozilla clears automatically; required for IE.
			field.setAttribute("name", field.getAttribute("name").replace(fieldRegExp, 
			  tblObj.tableid + "[" + tblObj.lastrowid + "]."));
		}
  }

	// fix delete button href (assumes this is always in the first cell)
	//  (this works for Mozilla, but not IE... probably due to Javascript in the attribute value,
	//   odd that it works for innerHTML, though)
	//cells[0].getElementsByTagName("a").item(0).setAttribute("onclick", 
	//  "fnDeleteRow(" + tblObj.varname + "," + tblObj.lastrowid + ",0); return 0;");

	cells[0].innerHTML = '<div class="redrollover"><a href="javascript:void(0)" onclick="fnDeleteRow(' + 
	  tblObj.varname + ',' + tblObj.lastrowid + ',0); return 0;">' +
	  '<img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>' +
	  '<input type="hidden" name="' + tblObj.tableid + "[" + tblObj.lastrowid + '].cmdUpdate" value="N">';	  

  // insert these cells into a new row	
  newrow = entryTableEl.insertRow(tblObj.lastrow+1);
	newrow.setAttribute('id', 'rowid.' + tblObj.lastrowid);
	newrow.setAttribute('valign', 'top');
  for (i=0; i<cells.length; i++) {
	  newrow.appendChild(cells[i]);
  }		

	// selects the first input field created
	firstInputField = newrow.getElementsByTagName("input").item(0);
	if (firstInputField) {
	  firstInputField.select();
	}  
}

/** Triggered when the 'X' button is hit on a DHTML table */
function fnDeleteRow(tblObj, rowNumber, isExistingRow) {
  var primaryKey;
  isFormDirty = true;
  entryForm = document.getElementById(tblObj.htmlform);	
  
  //alert ( dumpPropertiesNewWindow (
  // entryForm.elements[tblObj.tableid + '.' + rowNumber + '.' + tblObj.tablekeyfield]
  // , tblObj.tableid + '.' + rowNumber + '.' + tblObj.tablekeyfield));
  
  // only check to see whether we want to delete existing records if 
	// this record is currently in the database
	if (isExistingRow) {
	  if (!confirm(tblObj.deleteprompt)) {
		  return 0;
		}
		
		//var deletedElem = document.getElementById(tblObj.tableid + '.' + rowNumber + '.' + tblObj.tablekeyfield);
		var deletedElem = entryForm.elements[tblObj.tableid + '[' + rowNumber + '].' + tblObj.tablekeyfield];
    // capture the primary key before we delete the row
    // document.forms[tblObj.htmlform]
	 	if ("SELECT" == deletedElem.type) {
			primaryKey = deletedElem.options[deletedElem.selectedIndex].value;	 
	 	} else {
	    	//primaryKey = deletedElem.value;
		 	primaryKey = entryForm.elements[tblObj.tableid + '[' + rowNumber + '].' + tblObj.tablekeyfield].value;			
		}
	}

  // the row to be deleted
  oldrow = document.getElementById('rowid.' + rowNumber);
	
	// we need to find the original row index
	// (this isn't necessary if we just use .insertBefore on the cell we
	//  are just about to delete, but Internet Explorer 5 doesn't like this).

	
	// create the deleted row placeholder
  if (isExistingRow) {	
    entryTableEl = document.getElementById(tblObj.htmltable);
    // rows = entryTableEl.getElementsByTagName("tr"); don't want to get sub-tables
    rows = entryTableEl.rows;
    
    deleteRowIndex = -1;
    for (rownum = 0; rownum < rows.length; rownum++) {
      if (rows[rownum].getAttribute("id") == 'rowid.' + rowNumber) {
        deleteRowIndex = rownum;
      }
    }
    if (deleteRowIndex == -1) {
      alert("Invalid row index");
      return;
    }
    // find number of columns in the row we are deleting
    // oldDeleteRowCols = rows(deleteRowIndex).getElementsByTagName("td").length;
		oldDeleteRowCols = rows[deleteRowIndex].cells.length;
    
  	deleteRowText = "<div style=\"margin:5px; color: black;\"><i>This row has been " +
  	  "marked for deletion. Click the 'Update' button to perform the deletion.</i></div>";
    deleteRowEl = entryTableEl.insertRow(deleteRowIndex);
    deleteCellEl = deleteRowEl.insertCell(-1);
    deleteCellEl = deleteRowEl.insertCell(-1);
    // deleteCellEl.setAttribute("colSpan", tblObj.newRowCells.length-1);
    deleteCellEl.setAttribute("colSpan", oldDeleteRowCols-1);
    deleteCellEl.setAttribute("bgColor", "#FFAAAA");
    deleteCellEl.innerHTML = deleteRowText;
    
    // insert it before where the old row used to be.
    oldrow.parentNode.insertBefore(deleteRowEl, oldrow);
  }
	
	// delete the row
	oldrow.parentNode.removeChild(oldrow);

  if (isExistingRow) {
    // insert some hidden fields marking this field as deleted
  	hiddenfield = document.createElement("input");
  	hiddenfield.setAttribute("type", "hidden");
  	hiddenfield.setAttribute("name", tblObj.tableid + "[" + rowNumber + "]." + tblObj.tablekeyfield);
  	hiddenfield.setAttribute("value", primaryKey);
  	entryForm.insertBefore(hiddenfield, entryForm.firstChild);
  	hiddenfield = document.createElement("input");
  	hiddenfield.setAttribute("type", "hidden");
  	hiddenfield.setAttribute("name", tblObj.tableid + "[" + rowNumber + "].cmdDelete");
  	hiddenfield.setAttribute("value", "Y");
  	entryForm.insertBefore(hiddenfield, entryForm.firstChild);
	} else {
	  tblObj.lastrow = tblObj.lastrow - 1;
	}
}

/* This function assigns a fixed pixel width to each header cell in the DHTML table. 
   This is done since otherwise after the [X] button removes a row, the
   column widths may change, causing the table to contract horizontally. Fixing the
   header widths causes the table to maintain it's shape during/after row
   deletion.
*/   
function assignHeaderWidths(tblObj) {
  entryTableEl = document.getElementById(tblObj.htmltable);
  headerrows = tblObj.lastrow - tblObj.lastrowid;
  widtherror = 0;

  if (headerrows<0) {
    headerrows=0;
  }
  
  // fix the table cell width for the top row(s); this will prevent cells from resizing
  // as the table has rows deleted
  
  // NB: IE (incorrectly) adds cell border/padding to the offsetWidth, which makes the 
  // cells grow in size when you assign the offsetWidth value to the width value. 
  // This is detected and corrected by checking the width after setting it, 
  // and calculating a delta which is reapplied to that cell, and to subsequent widths.
  for (row=0; row<=headerrows; row++) {
    toprow      = entryTableEl.rows[row];
  	toprowcells = toprow.cells;
  	for (i=0; i<toprowcells.length; i++) {
  	  origwidth = toprowcells[i].offsetWidth;
  	  toprowcells[i].setAttribute("width", origwidth + widtherror);
  	  if (toprowcells[i].offsetWidth != origwidth) {
  	    widtherror = origwidth - toprowcells[i].offsetWidth;
  	    // alert("widtherror now '" + widtherror + "'");
        toprowcells[i].setAttribute("width", origwidth + widtherror);  	    
  	  }
  	}
  }
}


function initRnTable(tblObj) {

  assignHeaderWidths(tblObj);
  
  entryTableEl = document.getElementById(tblObj.htmltable);
	lastrow = entryTableEl.rows[tblObj.lastrow+1];
	lastrowcells = lastrow.cells;
	
  //lastrow      = entryTableEl.getElementsByTagName("tr").item(tblObj.lastrow+1);
	//lastrowcells = lastrow.getElementsByTagName("td");


  // obtain a clone of the cells in the final row, 
  // which should contain the row used for new entries. These are appended 
  // to the table when the [+] button is hit
  cells = new Array();
  for (i=0; i<lastrowcells.length; i++) {
	  // perform deep clone of the cell in the previous row
	  cells[i] = lastrowcells[i].cloneNode(true);
  }

	// fix delete button href (assumes this is always in the first cell)
	//  (this works for Mozilla, but not IE... probably due to Javascript in the attribute value,
	//   odd that it works for innerHTML, though)
	//cells[0].getElementsByTagName("a").item(0).setAttribute("onclick", 
	//  "fnDeleteRow(" + tblObj.varname + "," + tblObj.lastrowid + ",0); return 0;");

  // cells[0] is now created via fnAddRow()
	// this is a bit of a kludge (hardcodes delete button HTML)
	//cells[0].innerHTML = '<div class="redrollover"><a href="javascript:void(0)" onclick="fnDeleteRow(' + 
	//  tblObj.varname + ',' + tblObj.lastrowid + ',0); return 0;">' +
	//  '<img src="../images/delete-icon.gif" width="18" height="17" border="0"></a></div>';
	
	tblObj.newRowCells = cells;

}



// this code has been modified from that found in the BEA Weblogic 8.1 console

function formUnloadCheck(formname) {

  if (isSubmitting == true) return;
  var form = getForm(formname);

  if (isDirty(formname) || (isFormDirty)) {
    if (window.confirm(
		  "The changes you have made on this page have not been submitted "+
			"to the server. Save your changes?\n\n" +
			"(Click OK to save changes, or Cancel to disregard changes)") ) {
    	isSubmitting=true; checkModify('mainForm', tblObj); form.submit()
	 // form.cmdUpdate.click();
     //form.submit(); 
    }
  }
}

function clickedSubmit(formname) {
  var formxx = getForm(formname);
  isSubmitting = true;
  
  for(var i=0; i<formxx.elements.length; i++) {
    var control = formxx.elements[i];
    control.disabled = false;
  }

}

function getForm(formname) {
  return document.forms[formname];
}

function isDirty(formname) {
  var form = getForm(formname);
  // only check the main table
  //if (document.getElementById("editable") {
  	for(var i=0; i<form.elements.length; i++) {
    var elem = form.elements[i];
	 if ((elem.getAttribute("ignoreCheckEdits") == null) || (elem.getAttribute("ignoreCheckEdits") == "")) {
		 if (elem.type == "text" || elem.type == "TEXTAREA") {
			 if (elem.value != elem.defaultValue) return true;
		 } else if ("checkbox" == elem.type || "radio" == elem.type) {
			 if (elem.checked != elem.defaultChecked) return true;
		 } else if ("SELECT" == elem.type) {
			 var options = elem.options;
			 for (var j=0;j<options.length;j++) {
				 if (options[j].selected != options[j].defaultSelected) return true;
			 }
		 }
	  } 
  	}
  //}
  return false;
}

// js to check or uncheck checboxes

function checkAll(field) {
	for (i = 0; i < field.length; i++) {
	  field[i].checked = true ;
	}  
}

function uncheckAll(field) {
	for (i = 0; i < field.length; i++) {
		field[i].checked = false ;
  }		
}

// 
function checkModify(formname,tblObj) {

  entryForm = document.getElementById(tblObj.htmlform);	
  var form = getForm(formname);
  // fieldRegExp = new RegExp("^(" +tblObj.tableid + "\.[0-9]+)");
  fieldRegExp = new RegExp("^rowid\.([0-9]+)");
  
  
  entryTableEl = document.getElementById(tblObj.htmltable);
  rows = entryTableEl.rows;
  
  for (var rownum=0; rownum<rows.length; rownum++) {
    rowid = rows[rownum].id;
    matchArray = fieldRegExp.exec(rowid);    
    // only scan this row if we have a valid rowid
    if (matchArray!=null && matchArray.length>0) {
      // alert("Checking row # " + matchArray[1]);
      var dirtyRow = false;
      inputFields = rows[rownum].getElementsByTagName("input");
      for(var i=0; i<inputFields.length; i++) {
        var elem = inputFields.item(i);
     	if (elem.type == "text") {  // *** <-- this was !elem.type before 
      	  if (elem.value != elem.defaultValue) {
      	   //alert("Element '" + elem.name + "' is dirty");
      	   dirtyRow = true;
      	  }
        } else if ("checkbox" == elem.type || "radio" == elem.type) {
    	  if (elem.checked != elem.defaultChecked) {
    		//alert("Element '" + elem.name + "' is dirty");
      		dirtyRow = true;
    	  }
        } 
      }
      inputFields = rows[rownum].getElementsByTagName("select");
      for(var i=0; i<inputFields.length; i++) {
	      var elem = inputFields.item(i);		
			var options = elem.options;
			for (var j=0;j<options.length;j++) {
				if (options[j].selected != options[j].defaultSelected) {
				  //alert("Element '" + elem.name + "' is dirty");
				  dirtyRow = true;
				}
		   }	
 		}
		
		inputFields = rows[rownum].getElementsByTagName("textarea");
      for(var i=0; i<inputFields.length; i++) {
      	var elem = inputFields.item(i);
      	if (elem.value != elem.defaultValue) {
      		//alert("Element '" + elem.name + "' is dirty");
      	 	dirtyRow = true;
      	}
 		}   
      
      // TODO: huh ?
      if (!dirtyRow) {
        //alert("dirtyRow is false for this row, looking for '" + tblObj.tableid + "." + matchArray[1] + ".cmdUpdate" + "'");
        modifyfield = entryForm.elements[tblObj.tableid + "[" + matchArray[1] + "].cmdUpdate"];
  			if ((modifyfield != null) && (modifyfield.value == 'Y')) {
				modifyfield.value = "X";					
 			}				
      }
      
    }
  } 	
} 
 
