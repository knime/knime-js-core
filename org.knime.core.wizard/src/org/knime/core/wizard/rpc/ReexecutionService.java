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
     * @param resetNodeIDSuffix the project relative id suffix of the node to be reset (and all the downstream nodes)
     * @param viewValues the view values to apply to the reset nodes
     * @return the re-executed or re-executing page
     */
    PageContainer triggerComponentReexecution(String resetNodeIDSuffix, Map<String, String> viewValues);

    /**
     * (Partially) re-executes the complete component.
     *
     * @param snc the container of the component
     * @param viewValues the view values to apply
     * @return the re-executed or re-executing page
     */
    PageContainer triggerCompleteComponentReexecution(final Map<String, String> viewValues);

    /**
     * Get the current state of the re-executed or re-executing page
     *
     * @param snc the container of the component
     * @return the re-executed or re-executing page
     */
    PageContainer pollCompleteComponentReexecutionStatus();

    /**
     * @return the re-executed or re-executing page
     */
    PageContainer getPage();

    /**
     * The same function as getPage, but it doesnt rely on the current state of the service as much. m_resetNodes and
     * m_reexecutedNodes will not be used to determine the page.
     *
     * @param resetNodeIDSuffix the project relative id suffix of the node to be reset (and all the downstream nodes)
     * @return the re-executed or re-executing page
     */
    PageContainer pollComponentReexecutionStatus(String resetNodeIDSuffix);

}
