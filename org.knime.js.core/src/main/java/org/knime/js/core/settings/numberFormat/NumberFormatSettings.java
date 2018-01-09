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
 *   29 Sep 2016 (albrecht): created
 */
package org.knime.js.core.settings.numberFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Settings for a number formatter to be used for node config settings, dialog and for inclusion in JSON serialized objects.
 * Incorporates all settings for the <a href="http://refreshless.com/wnumb/">wNumb</a> number formatter, as well as an extra classes parameter for negative values.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 * @since 3.3
 */
@JsonAutoDetect
@JsonInclude(Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class NumberFormatSettings implements Cloneable {

    private static final String CFG_DECIMALS = "decimals";
    private static final Integer DEFAULT_DECIMALS = 2;
    private Integer m_decimals = DEFAULT_DECIMALS;

    private static final String CFG_MARK = "mark";
    private static final String DEFAULT_MARK = ".";
    private String m_mark = DEFAULT_MARK;

    private static final String CFG_THOUSAND = "thousand";
    private String m_thousand;

    private static final String CFG_PREFIX = "prefix";
    private String m_prefix;

    private static final String CFG_POSTFIX = "postfix";
    private String m_postfix;

    private static final String CFG_NEGATIVE = "negative";
    private static final String DEFAULT_NEGATIVE = "-";
    private String m_negative = DEFAULT_NEGATIVE;

    private static final String CFG_NEGATIVE_BEFORE = "negativeBefore";
    private String m_negativeBefore;

    private static final String CFG_NEGATIVE_CLASSES = "negativeClasses";
    private String m_negativeClasses;

    private static final String CFG_ENCODER = "encoder";
    private String m_encoder;

    private static final String CFG_DECODER = "decoder";
    private String m_decoder;

    private static final String CFG_EDIT = "edit";
    private String m_edit;

    private static final String CFG_UNDO = "undo";
    private String m_undo;

    /**
     * @return the decimals
     */
    public Integer getDecimals() {
        return m_decimals;
    }
    /**
     * @param decimals the decimals to set
     */
    public void setDecimals(final Integer decimals) {
        m_decimals = decimals;
    }
    /**
     * @return the mark
     */
    public String getMark() {
        return m_mark;
    }
    /**
     * @param mark the mark to set
     */
    public void setMark(final String mark) {
        m_mark = noEmptyString(mark);
    }
    /**
     * @return the thousands
     */
    public String getThousand() {
        return m_thousand;
    }
    /**
     * @param thousand the thousands to set
     */
    public void setThousand(final String thousand) {
        m_thousand = noEmptyString(thousand);
    }
    /**
     * @return the prefix
     */
    public String getPrefix() {
        return m_prefix;
    }
    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(final String prefix) {
        m_prefix = noEmptyString(prefix);
    }
    /**
     * @return the postfix
     */
    public String getPostfix() {
        return m_postfix;
    }
    /**
     * @param postfix the postfix to set
     */
    public void setPostfix(final String postfix) {
        m_postfix = noEmptyString(postfix);
    }
    /**
     * @return the negative
     */
    public String getNegative() {
        return m_negative;
    }
    /**
     * @param negative the negative to set
     */
    public void setNegative(final String negative) {
        m_negative = noEmptyString(negative);
    }
    /**
     * @return the negativeBefore
     */
    public String getNegativeBefore() {
        return m_negativeBefore;
    }
    /**
     * @param negativeBefore the negativeBefore to set
     */
    public void setNegativeBefore(final String negativeBefore) {
        m_negativeBefore = noEmptyString(negativeBefore);
    }

    /**
     * @return the negativeClasses
     */
    public String getNegativeClasses() {
        return m_negativeClasses;
    }

    /**
     * @param negativeClasses the negativeClasses to set
     */
    public void setNegativeClasses(final String negativeClasses) {
        m_negativeClasses = noEmptyString(negativeClasses);
    }

    /**
     * @return the encoder
     */
    public String getEncoder() {
        return m_encoder;
    }

    /**
     * @param encoder the encoder to set
     */
    public void setEncoder(final String encoder) {
        m_encoder = noEmptyString(encoder);
    }

    /**
     * @return the decoder
     */
    public String getDecoder() {
        return m_decoder;
    }

    /**
     * @param decoder the decoder to set
     */
    public void setDecoder(final String decoder) {
        m_decoder = noEmptyString(decoder);
    }

    /**
     * @return the edit
     */
    public String getEdit() {
        return m_edit;
    }

    /**
     * @param edit the edit to set
     */
    public void setEdit(final String edit) {
        m_edit = noEmptyString(edit);
    }

    /**
     * @return the undo
     */
    public String getUndo() {
        return m_undo;
    }

    /**
     * @param undo the undo to set
     */
    public void setUndo(final String undo) {
        m_undo = noEmptyString(undo);
    }

    private String noEmptyString(final String s) {
        return "".equals(s) ? null : s;
    }

    /**
     * Validates the current settings.
     * @throws InvalidSettingsException If validation fails.
     */
    @JsonIgnore
    public void validateSettings() throws InvalidSettingsException {
        if (m_decimals != null) {
            if (m_decimals < 0) {
                throw new InvalidSettingsException("Number of decimals must be a positive integer.");
            } else if (m_decimals > 7) {
                throw new InvalidSettingsException("JavaScript floating points are only stable up to 7 decimals");
            }
        }
        if (existsAndEquals(m_mark, m_thousand)) {
            throw new InvalidSettingsException("Decimal separator and thousands mark can not be set to the same character.");
        }
        if (existsAndEquals(m_prefix, m_negative)) {
            throw new InvalidSettingsException("Prefix and negative sign can not be set to the same string.");
        }
        if (existsAndEquals(m_prefix, m_negativeBefore)) {
            throw new InvalidSettingsException("Prefix and negative prefix can not be set to the same string.");
        }
    }

    private boolean existsAndEquals(final String string1, final String string2) {
        return string1 != null && string2 != null && string1.equals(string2);
    }

    /**
     * Saves the current state to the given node settings object.
     * @param settings The settings object to save to.
     */
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_DECIMALS, m_decimals == null ? null : Integer.toString(m_decimals));
        settings.addString(CFG_MARK, m_mark);
        settings.addString(CFG_THOUSAND, m_thousand);
        settings.addString(CFG_PREFIX, m_prefix);
        settings.addString(CFG_POSTFIX, m_postfix);
        settings.addString(CFG_NEGATIVE, m_negative);
        settings.addString(CFG_NEGATIVE_BEFORE, m_negativeBefore);
        settings.addString(CFG_NEGATIVE_CLASSES, m_negativeClasses);
        settings.addString(CFG_ENCODER, m_encoder);
        settings.addString(CFG_DECODER, m_decoder);
        settings.addString(CFG_EDIT, m_edit);
        settings.addString(CFG_UNDO, m_undo);
    }

    /**
     * Populates the object by loading from the NodeSettings object.
     * The values are validated before being applied. On error this object stays unchanged.
     * @param settings The settings to load from
     * @throws InvalidSettingsException on load or validation error
     */
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NumberFormatSettings nVal = new NumberFormatSettings();
        nVal.loadValidateSettings(settings);
        nVal.validateSettings();
        copyInternals(nVal, this);
    }

    private void loadValidateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        String decimalString = settings.getString(CFG_DECIMALS);
        m_decimals = decimalString == null ? null : Integer.parseInt(decimalString);
        m_mark = settings.getString(CFG_MARK);
        m_thousand = settings.getString(CFG_THOUSAND);
        m_prefix = settings.getString(CFG_PREFIX);
        m_postfix = settings.getString(CFG_POSTFIX);
        m_negative = settings.getString(CFG_NEGATIVE);
        m_negativeBefore = settings.getString(CFG_NEGATIVE_BEFORE);
        m_negativeClasses = settings.getString(CFG_NEGATIVE_CLASSES);
        m_encoder = settings.getString(CFG_ENCODER);
        m_decoder = settings.getString(CFG_DECODER);
        m_edit = settings.getString(CFG_EDIT);
        m_undo = settings.getString(CFG_UNDO);
    }

    /**
     * Loading from NodeSettings object with defaults fallback.
     * @param settings the settings object to load from.
     */
    @JsonIgnore
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        String decimalString = settings.getString(CFG_DECIMALS, null);
        m_decimals = decimalString == null ? DEFAULT_DECIMALS : Integer.parseInt(decimalString);
        m_mark = settings.getString(CFG_MARK, DEFAULT_MARK);
        m_thousand = settings.getString(CFG_THOUSAND, null);
        m_prefix = settings.getString(CFG_PREFIX, null);
        m_postfix = settings.getString(CFG_POSTFIX, null);
        m_negative = settings.getString(CFG_NEGATIVE, DEFAULT_NEGATIVE);
        m_negativeBefore = settings.getString(CFG_NEGATIVE_BEFORE, null);
        m_negativeClasses = settings.getString(CFG_NEGATIVE_CLASSES, null);
        m_encoder = settings.getString(CFG_ENCODER, null);
        m_decoder = settings.getString(CFG_DECODER, null);
        m_edit = settings.getString(CFG_EDIT, null);
        m_undo = settings.getString(CFG_UNDO, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_decimals)
                .append(m_mark)
                .append(m_thousand)
                .append(m_prefix)
                .append(m_postfix)
                .append(m_negative)
                .append(m_negativeBefore)
                .append(m_negativeClasses)
                .append(m_encoder)
                .append(m_decoder)
                .append(m_edit)
                .append(m_undo)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        NumberFormatSettings other = (NumberFormatSettings)obj;
        return new EqualsBuilder()
                .append(m_decimals, other.m_decimals)
                .append(m_mark, other.m_mark)
                .append(m_thousand, other.m_thousand)
                .append(m_prefix, other.m_prefix)
                .append(m_postfix, other.m_postfix)
                .append(m_negative, other.m_negative)
                .append(m_negativeBefore, other.m_negativeBefore)
                .append(m_negativeClasses, other.m_negativeClasses)
                .append(m_encoder, other.m_encoder)
                .append(m_decoder, other.m_decoder)
                .append(m_edit, other.m_edit)
                .append(m_undo, other.m_undo)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public NumberFormatSettings clone() {
        NumberFormatSettings clonedSettings = new NumberFormatSettings();
        copyInternals(this, clonedSettings);
        return clonedSettings;
    }

    private static synchronized void copyInternals(final NumberFormatSettings settingsFrom, final NumberFormatSettings settingsTo) {
        //all members immutable
        settingsTo.m_decimals = settingsFrom.m_decimals;
        settingsTo.m_mark = settingsFrom.m_mark;
        settingsTo.m_thousand = settingsFrom.m_thousand;
        settingsTo.m_prefix = settingsFrom.m_prefix;
        settingsTo.m_postfix = settingsFrom.m_postfix;
        settingsTo.m_negative = settingsFrom.m_negative;
        settingsTo.m_negativeBefore = settingsFrom.m_negativeBefore;
        settingsTo.m_negativeClasses = settingsFrom.m_negativeClasses;
        settingsTo.m_encoder = settingsFrom.m_encoder;
        settingsTo.m_decoder = settingsFrom.m_decoder;
        settingsTo.m_edit = settingsFrom.m_edit;
        settingsTo.m_undo = settingsFrom.m_undo;
    }

}
