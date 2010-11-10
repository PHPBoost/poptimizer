/**
 * Copyright (C) 2009 Benoit Sautel <ben.popeye@phpboost.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phpboost.poptimizer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public enum FileCharset {
    /**
     * UTF-8
     */
    UTF8(new String[] { "UTF-8", "UTF8" }, "UTF-8"),
    /**
     * UTF-16
     */
    UTF16(new String[] { "UTF-16", "UTF16" }, "UTF-16"),
    /**
     * 7 bit ASCII
     */
    US_ASCII(new String[] { "US_ASCII", "ASCII" }, "US-ASCII"),
    /**
     * European encoding (approximately extended US-ASCII)
     */
    ISO_8859_1(new String[] { "ISO-8859-1", "ISO88591" }, "ISO-8859-1");

    /**
     * List of the possible names
     */
    private List<String> names;
    /**
     * Official name of the charset, used to work with the String class.
     */
    private String officialName;
    /**
     * Associated charset (used to work with the String class)
     */
    private Charset associatedCharset;

    /**
     * Returns the charset corresponding to a name
     * @param name
     * Written name (can be approximative)
     * @return The charset, if it could be found, or UTF8 as default value
     */
    public static FileCharset fromString(final String name) {
        for (final FileCharset charset : values()) {
            if (charset.hasThisName(name)) {
                return charset;
            }
        }
        // Default value
        return UTF8;
    }

    /**
     * Gets the official name of the charset (needed to work with the String
     * class)
     * @return The official name
     */
    public String officialName() {
        return officialName;
    }

    /**
     * Returns the associated charset (needed to work with the String class)
     * @return The associated charset
     */
    public Charset associatedCharset() {
        return associatedCharset;
    }

    /**
     * Constructs a charset from a list of possible names and its official name
     * @param namesList
     * List of the possible names
     * @param charsetOfficialName
     * Official name
     */
    private FileCharset(final String[] namesList, final String charsetOfficialName) {
        names = new ArrayList<String>();
        for (final String name : namesList) {
            names.add(name);
        }

        associatedCharset = Charset.forName(charsetOfficialName);
        officialName = charsetOfficialName;
    }

    /**
     * Check whether this charset can have this name
     * @param name
     * Name to check
     * @return true if this charset matches the possible names, false otherwise.
     */
    private boolean hasThisName(final String name) {
        if (names.contains(name.toUpperCase())) {
            return true;
        }
        return false;
    }
}
