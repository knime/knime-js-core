package org.knime.core.wizard.rpc;

import java.util.Map;

/**
 * Service which is exposes the functionality to partially re-execute a component to, e.g., the JS component (i.e.
 * composite view) view implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface ReexecutionService {

    /**
     * (Partially) re-executes the component.
     *
     * @param nodeID the id of the node to be reset (and all the downstream nodes)
     * @param viewValues the view values to apply to the reset nodes
     * @return the re-executed or re-executing page
     */
    PageContainer reexecutePage(String nodeID, Map<String, String> viewValues);

    /**
     * @return the re-executed or re-executing page
     */
    PageContainer getPage();

}

