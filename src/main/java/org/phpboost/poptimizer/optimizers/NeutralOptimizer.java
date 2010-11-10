/**
 * Copyright (C) 2009 Loïc Rouchon <horn@phpboost.com>, Benoit Sautel <ben.popeye@phpboost.com>
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

package org.phpboost.poptimizer.optimizers;

/**
 * This neutral optimizer doesn't optimize anything
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt; Loic Rouchon
 * &lt;horn@phpboost.com&gt;
 */
public class NeutralOptimizer extends AbstractOptimizer {
    /**
     * Builds an optimizer from an optimizer
     * @param optimizer
     * optimizer
     */
    public NeutralOptimizer(final AbstractOptimizer optimizer) {
        super(optimizer);
    }

    /**
     * Builds an optimizer from a string
     * @param file
     * optimizer
     */
    public NeutralOptimizer(final String file) {
        super(file);
    }
}
