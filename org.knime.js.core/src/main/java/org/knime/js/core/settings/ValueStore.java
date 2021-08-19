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
 *   Jul 28, 2021 (hornm): created
 */
package org.knime.js.core.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.knime.core.node.NodeLogger;

/**
 * Little helper that stores key-value pairs and runs a 'transfer'-operation if a value to be stored changed since it
 * has been added the last time.
 *
 * Background: for a lot of view-node implementations, the view value (the 'mutable data' that is rendered JS-side for a
 * node view) is (also) taken from the node's configuration and thus copied (i.e. copied from config to value) on the
 * node's execution. However, the copy-step should only happen if either it hasn't been done, yet (first node execution)
 * or the config-value changed (flow-variable controlled). Otherwise we would overwrite view values which actually have
 * been changed on the JS-side by the user (which is possible for many view value properties). And this class helps to
 * only transfer the view values from the config if it's done for the first time or the value changed meanwhile (because
 * it's flow variable controlled).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 4.4
 */
public class ValueStore {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ValueStore.class);

    private final Map<String, Object> m_values = new HashMap<>();

    /**
     * Stores a new value for the given key and executes an operation if the value hasn't been stored, yet or if the new
     * value differs from the currently stored one. In any case, the new value is stored for the given key.
     *
     * @param <T>
     * @param key the key to store the value at
     * @param newValue the new value to store
     * @param transferOperationOnValueChange the transfer-operation to be called if the value has been changed (or
     *            added)
     */
    public <T> void storeAndTransfer(final String key, final T newValue,
        final Consumer<T> transferOperationOnValueChange) {
        storeAndTransfer(key, newValue, transferOperationOnValueChange, Objects::equals);
    }

    /**
     * Same as {@link #storeAndTransfer(String, Object, Consumer)}, but with a custom function which specifies how to
     * compare two values.
     *
     * @param <T>
     * @param key the key to store the value at
     * @param newValue the new value to store
     * @param transferOperationOnValueChange the transfer-operation to be called if the value has been changed (or
     *            added)
     * @param isEqual the function of how to compare two values
     */
    public <T> void storeAndTransfer(final String key, final T newValue,
        final Consumer<T> transferOperationOnValueChange, final BiPredicate<T, T> isEqual) {
        @SuppressWarnings("unchecked")
        T oldValue = (T)m_values.get(key);
        if (!isEqual.test(newValue, oldValue)) {
            LOGGER.debug(
                String.format("Value '%s' for key '%s' has been overwritten with '%s'", oldValue, key, newValue));
            transferOperationOnValueChange.accept(newValue);
            m_values.put(key, newValue);
        }
    }

    /**
     * Clears all stored values.
     */
    public void clear() {
        m_values.clear();
    }

}
