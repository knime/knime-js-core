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
 *   15 Nov 2017 (albrecht): created
 */
package org.knime.ext.seleniumdrivers.multios;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.js.core.JSCorePlugin;

/**
 * @param <T> requires a {@link ViewableModel} implementing {@link WizardNode} as well
 * @param <REP> the {@link WebViewContent} view representation implementation used
 * @param <VAL> the {@link WebViewContent} view value implementation used
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 3.5
 */
public class ChromiumWizardNodeView<T extends ViewableModel & WizardNode<REP, VAL>, REP extends WebViewContent, VAL extends WebViewContent>
    extends ChromeWizardNodeView<T, REP, VAL> {

    /**
     * @param viewableModel
     * @since 4.5
     */
    public ChromiumWizardNodeView(final SingleNodeContainer snc, final T viewableModel) {
        super(snc, viewableModel);
    }

    /**
     * @return true if the view is enabled, false otherwise
     * @since 3.5
     */
    public static boolean isEnabled() {
        if (!JSCorePlugin.osSupportsChromium()) {
            return false;
        } else {
            return MultiOSDriverActivator.getChromiumPath().isPresent() && ChromeWizardNodeView.isEnabled();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String initDriver(final int left, final int top, final int width, final int height) {
        return initDriver(left, top, width, height, true);
    }

}
