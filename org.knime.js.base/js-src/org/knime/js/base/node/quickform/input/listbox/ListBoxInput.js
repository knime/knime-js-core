/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   Oct 14, 2013 (Patrick Winter, KNIME.com AG, Zurich, Switzerland): created
 */
org_knime_js_base_node_quickform_input_listbox = function() {
	var listboxInput = {
			version: "1.0.0"
	};
	listboxInput.name = "Listbox input";
	var viewValue;
	var input;
	var errorMessageLine1;
	var errorMessageLine2;
	var separator;
	var omitEmpty;

	listboxInput.init = function(representation, value) {
		viewValue = value;
		var body = $('body');
		input = $('<textarea>');
		body.append(input);
		input.css('white-space', 'pre');
		input.css('overflow', 'auto');
		input.attr('wrap', 'off');
		input.attr('rows', '5');
		input.attr('cols', '20');
		input.attr("pattern", representation.regex);
		input.val(representation.defaultvalue);
		body.append($('<br>'));
		errorMessageLine1 = $('<span>');
		errorMessageLine2 = $('<span>'+representation.errormessage+'</span>');
		errorMessages = errorMessageLine1.add(errorMessageLine2);
		errorMessages.css('display', 'none');
		errorMessages.css('color', 'red');
		errorMessages.css('font-style', 'italic');
		errorMessages.css('font-size', '75%');
		body.append(errorMessageLine1);
		body.append($('<br>'));
		body.append(errorMessageLine2);
		if (representation.separator==null || representation.separator.length==0) {
			separator = null;
		} else {
			separator = new RegExp(representation.separatorregex);
		}
		omitEmpty = representation.omitempty;
	};

	listboxInput.validate = function() {
		var index;
		var value = input.val();
		var regex = input.attr("pattern");
		var values = new Array();
        if (separator == null) {
            if (!(omitEmpty && value.length==0)) {
                values.push(value);
            }
        } else {
            var splitValue = value.split(separator);
            for (var i=0; i<splitValue.length; i++) {
                if (!(omitEmpty && splitValue[i].length==0)) {
                    values.push(splitValue[i]);
                }
            }
        }
		if (regex != null && regex.length > 0) {
			var valid = true;
			for (var i=0; i<values.length; i++) {
				valid = valid && matchExact(regex, values[i]);
				if (!valid) {
					index = i+1;
					break;
				}
			}
			if (!valid) {
				errorMessageLine1.text('Value ' + index + ' is not valid:');
				errorMessageLine1.add(errorMessageLine2).css('display', 'inline');
			} else {
				errorMessageLine1.add(errorMessageLine2).css('display', 'none');
			}
			return valid;
		} else {
			return true;
		}
	};

	listboxInput.value = function() {
		viewValue.string = input.val();
		return viewValue;
	};
	
	function matchExact(r, str) {
		var match = str.match(r);
		return match != null && str == match[0];
	}
	
	return listboxInput;
	
}();
