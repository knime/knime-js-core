/**
 *
 */
package org.knime.js.core.components.datetime;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Oleg Yasnev, KNIME GmbH, Berlin, Germany
 *
 */
public class ConversionDateTimeFormat {
    private static Map<String, String> m_oldToNewMap = null;

    /**
     * Converts a string in old date&time format to the new one
     * @param oldFormat
     * @return string in the new date&time format
     */
    public static String oldToNew(final String oldFormat) {
        Map<String, String> oldToNewMap = getOldToNewMap();
        String[] oldFormatMasks = oldToNewMap.keySet().toArray(new String[oldToNewMap.size()]);
        String[] newFormatMasks = oldToNewMap.values().toArray(new String[oldToNewMap.size()]);
        return StringUtils.replaceEach(oldFormat, oldFormatMasks, newFormatMasks);
    }

    /**
     * Gets a conversion map for masks from the old date&time format to the new
     * @return conversion map
     */
    public static Map<String, String> getOldToNewMap() {
        if (m_oldToNewMap == null) {
            initOldToNewMap();
        }
        return m_oldToNewMap;
    }

    private static void initOldToNewMap() {
        m_oldToNewMap = new TreeMap<String, String>(Collections.reverseOrder());
        // we have to sort desc, otherwise "d" will be processed before "dd" and break the pattern

        m_oldToNewMap.put("d", "D");
        m_oldToNewMap.put("dd", "DD");
        m_oldToNewMap.put("ddd", "ddd");
        m_oldToNewMap.put("dddd", "dddd");
        m_oldToNewMap.put("m", "M");
        m_oldToNewMap.put("mm", "MM");
        m_oldToNewMap.put("mmm", "MMM");
        m_oldToNewMap.put("mmmm", "MMMM");
        m_oldToNewMap.put("yy", "YY");
        m_oldToNewMap.put("yyyy", "YYYY");
        m_oldToNewMap.put("h", "h");
        m_oldToNewMap.put("hh", "hh");
        m_oldToNewMap.put("H", "H");
        m_oldToNewMap.put("HH", "HH");
        m_oldToNewMap.put("M", "m");
        m_oldToNewMap.put("MM", "mm");
        m_oldToNewMap.put("s", "s");
        m_oldToNewMap.put("ss", "ss");
        m_oldToNewMap.put("l", "SSS");
        m_oldToNewMap.put("L", "SS");
        m_oldToNewMap.put("t", "a");
        m_oldToNewMap.put("tt", "a");
        m_oldToNewMap.put("T", "A");
        m_oldToNewMap.put("TT", "A");
        m_oldToNewMap.put("Z", "z");
        m_oldToNewMap.put("o", "ZZ");
        m_oldToNewMap.put("S", "o");
    }
}
