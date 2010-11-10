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
 * This enumeration contains all the modules available to optimize the PHP code.
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt;
 */
public enum Optimizer {
    /**
     * Comments optimizer (removes all the comments)
     * @see CommentsOptimizer
     */
    COMMENTS_OPTIMIZER("comments") {
        @Override
        public AbstractOptimizer getOptimizer(
                final AbstractOptimizer previousOptimizer) {
            return new CommentsOptimizer(previousOptimizer);
        }
    },
    /**
     * Spaces optimizer (removes all the useless spaces)
     * @see SpacesOptimizer
     */
    SPACES_OPTIMIZER("spaces") {
        @Override
        public AbstractOptimizer getOptimizer(
                final AbstractOptimizer previousOptimizer) {
            return new SpacesOptimizer(previousOptimizer);
        }
    };

    /**
     * Item name
     */
    private String name;

    /**
     * Finds an optimizer from it's name. This method is not case sensitive.
     * @param itemName
     * The optimizer's name.
     * @return
     */
    public static Optimizer fromString(final String itemName) {
        for (final Optimizer optimizer : values()) {
            if (optimizer.name.equals(itemName)) {
                return optimizer;
            }
        }
        return null;
    }

    /**
     * Builds an optimizer item from its name
     * @param itemName
     * Item name
     * @param optimizerClass
     * Optimizer class
     */
    private Optimizer(final String itemName) {
        name = itemName;
    }

    /**
     * Overrides the toString method and returns the item name.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns an instance of the optimizer
     * @param previousOptimizer
     * previous optimizer
     * @return the optimizer instance
     */
    public abstract AbstractOptimizer getOptimizer(
            AbstractOptimizer previousOptimizer);
}
