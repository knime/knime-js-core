package org.knime.core.wizard.rpc;

import java.util.Map;

/**
 * Service which is exposes the functionality to partially re-execute a component to, e.g., the JS component (i.e.
 * composite view) view implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 *
 * @since 4.5
 */
public interface ReexecutionService {

    /**
     * (Partially) re-executes the component.
     *
     * @param nodeIDSuffix the project relative id suffix of the node to be reset (and all the downstream nodes)
     * @param viewValues the view values to apply to the reset nodes
     * @return the re-executed or re-executing page
     */
    PageContainer reexecutePage(String nodeIDSuffix, Map<String, String> viewValues);

    /**
     * @return the re-executed or re-executing page
     */
    PageContainer getPage();

    /**
     * The same function as above, but it doesnt rely on the current state of the service as much
     * m_resetNodes and m_reexecutedNodes will not be used to determine the page. WIP
     * @param nodeIDSuffix
     * @return the re-executed or re-executing page
     */
    PageContainer getPage(String nodeIDSuffix);

}

