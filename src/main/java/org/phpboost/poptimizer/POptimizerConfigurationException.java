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

/**
 * This exception is thrown if the configuration of POptimizer is not correct
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt;
 */
public class POptimizerConfigurationException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = -8786054422325971309L;

    /**
     * Builds a configuration exception
     * @param errorMessage
     * The error message
     */
    public POptimizerConfigurationException(final String errorMessage) {
        super(errorMessage);
    }
}
