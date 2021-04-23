package org.knime.core.wizard.rpc;

import java.util.List;

import org.knime.js.core.JSONWebNodePage;

import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Object that contains the wizard page and some additional information.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface PageContainer {

    /**
     * @return the nodes that have been reset or <code>null</code> if the component is in executed state and
     *         {@link #getPage()} returns page content
     */
    List<String> getResetNodes();

    /**
     * Returns the actual page content, i.e. as json-serialized {@link JSONWebNodePage}-object.
     *
     * @return the actual page content or <code>null</code> if the component is in execution
     */
    RawValue getPage();

}
