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
 * This class represents a kind optimizer. This class is build according to the
 * decorator design pattern
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt; Loic Rouchon
 * &lt;horn@phpboost.com&gt;
 */

public abstract class AbstractOptimizer {
    /**
     * The optimizer content
     */
    protected String input = null;

    /**
     * The previous optimizer (decorator design pattern)
     */
    private AbstractOptimizer decorate = null;

    /**
     * Builds an optimizer
     * @param in
     * Content to optimize
     */
    public AbstractOptimizer(final String in) {
        input = in;
    }

    /**
     * Builds an optimizer from another optimizer (decorator design pattern)
     * @param optimizer
     * input optimize
     */
    public AbstractOptimizer(final AbstractOptimizer optimizer) {
        decorate = optimizer;
    }

    /**
     * Optimizes the optimizer content
     * @return The optimized content
     */
    public String optimize() {
        if (decorate != null) {
            input = decorate.optimize();
        }
        return input;
    }
}