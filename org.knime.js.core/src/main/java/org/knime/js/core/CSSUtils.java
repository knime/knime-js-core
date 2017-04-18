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
 *   May 11, 2015 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.core;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
public class CSSUtils {

    /**
     * Returns an rgb hex string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgb-color">W3C CSS Color Module Level 3</a>.
     * @param color The color to extract the rgb hex string from.
     * @return The extracted rgb hex string.
     */
    public static String cssHexStringFromColor(final Color color) {
        //get color value, omit alpha
        int colorValue = color.getRGB() & 0xFFFFFF;
        //convert to CSS hex color string
        String hexString = Integer.toHexString(colorValue);
        return "#" + StringUtils.leftPad(hexString, 6, '0').toUpperCase();
    }

    /**
     * Returns an rgb string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgb-color">W3C CSS Color Module Level 3</a>.
     * @param color The color to extract the rgb string from.
     * @return The extracted rgb string.
     */
    public static String rgbStringFromColor(final Color color) {
        return rgbaStringFromColor(color, 3);
    }

    /**
     * Returns an rgba string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgba-color">W3C CSS Color Module Level 3</a>.
     * @param color The color to extract the rgba string from.
     * @return The extracted rgba string.
     */
    public static String rgbaStringFromColor(final Color color) {
        return rgbaStringFromColor(color, 4);
    }

    private static String rgbaStringFromColor(final Color color, final int numValues) {
        Object[] args = new Object[4];
        args[0] = color.getRed();
        args[1] = color.getGreen();
        args[2] = color.getBlue();
        args[3] = color.getAlpha()/255f;
        StringBuilder formatBuilder = new StringBuilder();
        formatBuilder.append("rgb");
        if (numValues > 3) {
            formatBuilder.append("a");
        }
        formatBuilder.append("(");
        for (int i = 0; i < numValues; i++) {
            if (i > 0) {
                formatBuilder.append(",");
            }
            if (i < 3) {
                formatBuilder.append("%d");
            } else {
                formatBuilder.append("%.3f");
            }
        }
        formatBuilder.append(")");
        return String.format(formatBuilder.toString(), args);
    }

    /**
     * Returns an {@link Color} from an rgb hex string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgb-color">W3C CSS Color Module Level 3</a>.
     * @param hexString The string to extract the color from.
     * @return The extracted color.
     * @throws NumberFormatException If the String does not contain a parsable integer.
     */
    public static Color colorFromCssHexString(final String hexString) throws NumberFormatException {
        int colorValue = Integer.decode(hexString);
        return new Color(colorValue);
    }

    /**
     * Returns an {@link Color} from an rgb string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgb-color">W3C CSS Color Module Level 3</a>.
     * @param rgbString The string to extract the color from.
     * @return The extracted color.
     */
    public static Color colorFromRgbString(final String rgbString) {
        return colorFromRgbaString(rgbString, 3);
    }

    /**
     * Returns an {@link Color} from an rgba string as defined by the <a href="http://www.w3.org/TR/css3-color/#rgba-color">W3C CSS Color Module Level 3</a>.
     * @param rgbaString The string to extract the color from.
     * @return The extracted color.
     */
    public static Color colorFromRgbaString(final String rgbaString) {
        return colorFromRgbaString(rgbaString, 4);
    }

    private static Color colorFromRgbaString(final String rgbaString, final int numValues) {
        String zeroTo255 = "([01]?[0-9]?[0-9]|2[0-4][0-9]|25[0-5])";
        String zeroTo1 = "(0(?:.\\d+)?|1(?:.0+)?)";
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("^rgb");
        if (numValues > 3) {
            patternBuilder.append("a");
        }
        patternBuilder.append(Pattern.quote("("));
        for (int i = 0; i < numValues; i++) {
            if (i > 0) {
                patternBuilder.append(",");
            }
            if (i < 3) {
                patternBuilder.append(zeroTo255);
            } else {
                patternBuilder.append(zeroTo1);
            }
        }
        patternBuilder.append(Pattern.quote(")"));
        patternBuilder.append("$");
        Pattern pattern = Pattern.compile(patternBuilder.toString());
        Matcher matcher = pattern.matcher(rgbaString);
        if (!matcher.matches() || matcher.groupCount() != numValues) {
            return null;
        }
        int r = Integer.parseInt(matcher.group(1));
        int g = Integer.parseInt(matcher.group(2));
        int b = Integer.parseInt(matcher.group(3));
        if (numValues > 3) {
            int a = (int)Math.round(Double.parseDouble(matcher.group(4))*255);
            return new Color(r, g, b, a);
        }
        return new Color(r, g, b);
    }

}
