/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 15, 2021 (ben.laney): created
 */
package org.knime.core.wizard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 *
 * @author ben.laney
 */
@SuppressWarnings("javadoc")
public class TestSubnodeViewValueComparison {

    @Test
    public void testEqualityComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        testValue.setViewValues(getValueMap(null));
        assertTrue(defaultValue.compareViewValues(testValue));
        assertTrue(testValue.compareViewValues(defaultValue));
    }

    @Test
    public void testInequalityComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        testValue.setViewValues(getValueMap("{\"string\": \"3\"}"));
        assertFalse(defaultValue.compareViewValues(testValue));
        assertFalse(testValue.compareViewValues(defaultValue));
    }

    @Test
    public void testEmptyValueComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        testValue.setViewValues(getValueMap("{}"));
        assertTrue(defaultValue.compareViewValues(testValue));
        assertTrue(testValue.compareViewValues(defaultValue));
    }

    @Test
    public void testClassOnlyComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        testValue.setViewValues(getValueMap("{\"@class\": \"my.node.class\"}"));
        assertTrue(defaultValue.compareViewValues(testValue));
        assertTrue(testValue.compareViewValues(defaultValue));
    }

    @Test
    public void testMissingComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        testValue.setViewValues(null);
        assertFalse(defaultValue.compareViewValues(testValue));
        assertFalse(testValue.compareViewValues(defaultValue));
    }

    @Test
    public void testInvalidArgumentComparison() {
        SubnodeViewValue defaultValue = new SubnodeViewValue();
        SubnodeViewValue testValue = new SubnodeViewValue();
        defaultValue.setViewValues(getValueMap(null));
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("string", "1");
        testValue.setViewValues(valueMap);
        assertFalse(defaultValue.compareViewValues(testValue));
        assertFalse(testValue.compareViewValues(defaultValue));
    }

    private static Map<String, String> getValueMap(final String secondValue) {
        String nodeId1 = "0";
        String nodeId2 = "1";
        String nodeValue1 = "{\"string\": \"1\"}";
        String nodeValue2 = secondValue != null ? secondValue : "{\"string\": \"2\"}";
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put(nodeId1, nodeValue1);
        valueMap.put(nodeId2, nodeValue2);
        return valueMap;
    }
}
