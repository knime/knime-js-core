/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   10.06.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.util.table;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class WebTableNodeDialogPane extends DefaultNodeSettingsPane {

    /**
     * Creates a new dialog pane.
     */
    public WebTableNodeDialogPane() {
        super();

        final SettingsModelBoolean hideInWizardModel = WebTableNodeModel.createHideInWizardModel();
        addDialogComponent(new DialogComponentBoolean(hideInWizardModel, "Hide in wizard"));

        addDialogComponent(new DialogComponentNumber(
            WebTableNodeModel.createLastDisplayedRowModel(WebTableNodeModel.END),
            "No. of rows to display:", 10));

        final SettingsModelIntegerBounded heightModel = WebTableNodeModel.createTableHeightModel();
        addDialogComponent(new DialogComponentNumber(heightModel, "Maximum table height in layout", 1));

        final SettingsModelBoolean fullFrameModel = WebTableNodeModel.createFullFrameModel();
        addDialogComponent(new DialogComponentBoolean(fullFrameModel, "Extend frame to table height"));
        heightModel.setEnabled(!fullFrameModel.getBooleanValue());
        fullFrameModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                heightModel.setEnabled(!fullFrameModel.getBooleanValue());
            }
        });

        final SettingsModelBoolean numberFormatterModel = WebTableNodeModel.createUseNumberFormatterModel();
        addDialogComponent(new DialogComponentBoolean(numberFormatterModel, "Enable number formatter"));

        addDialogComponent(new DialogComponentNumber(WebTableNodeModel.createDecimalPlacesModel(numberFormatterModel),
            "Decimal places", 1));

        final SettingsModelBoolean selectionEnabledModel = WebTableNodeModel.createEnableSelectionModel();
        addDialogComponent(new DialogComponentBoolean(selectionEnabledModel, "Enable selection"));

        final SettingsModelString selectionColumnNameModel = WebTableNodeModel.createSelectionColumnNameModel();
        addDialogComponent(new DialogComponentString(selectionColumnNameModel, "Selection column name"));

        // get standard signs according to current locale
//        @SuppressWarnings("static-access")
//        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
//        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
//        char decSep = symbols.getDecimalSeparator();
//        char grSep = symbols.getGroupingSeparator();

//        addDialogComponent(new DialogComponentString(new SettingsModelString(
//            WebTableNodeModel.CFG_DECIMAL_SEPARATOR, String.valueOf(decSep)), "Decimal Separator", true, 2));
//
//        addDialogComponent(new DialogComponentString(new SettingsModelString(
//            WebTableNodeModel.CFG_THOUSANDS_SEPARATOR, String.valueOf(grSep)), "Thousands Separator", false, 2));

    }

}
