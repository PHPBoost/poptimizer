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
 * This class is a comments optimizer. It removes all the PHP comments (// up to
 * the end of the file, /* *\/ or ## ## but doesn't modifies the line numbers.
 * @author benoit
 */
public class CommentsOptimizer extends AbstractOptimizer {
    /**
     * States of the automaton
     */
    private enum ParserStatus {
        OUTSIDE_PHP_CODE, PHP_CODE, MULTILINE_COMMENT, INLINE_COMMENT, SIMPLE_QUOTED_STRING, DOUBLE_QUOTED_STRING
    };

    /**
     * Builds a comments optimizer
     * @param in
     * String input
     */
    public CommentsOptimizer(final String in) {
        super(in);
    }

    /**
     * Optimizer input
     * @param decorate
     */
    public CommentsOptimizer(final AbstractOptimizer decorate) {
        super(decorate);
    }

    /**
     * Optimizes the content of the optimizer
     */
    @Override
    public String optimize() {
        super.optimize();

        ParserStatus state = ParserStatus.OUTSIDE_PHP_CODE;
        final StringBuilder result = new StringBuilder();
        char previousChar = '\0';
        Boolean isEscaped = false;

        for (Integer i = Integer.valueOf(0); i < input.length(); i++) {
            final char currentChar = input.charAt(i);
            switch (state) {
                case MULTILINE_COMMENT:
                    if (currentChar == '\n') {
                        result.append(currentChar);
                    } else if (currentChar == '/' && previousChar == '*') {
                        state = ParserStatus.PHP_CODE;
                    }
                    break;
                case INLINE_COMMENT:
                    if (currentChar == '\n') {
                        state = ParserStatus.PHP_CODE;
                        result.append(currentChar);
                    }
                    break;
                case SIMPLE_QUOTED_STRING:
                    if (currentChar == '\'' && !isEscaped) {
                        state = ParserStatus.PHP_CODE;
                    } else if (currentChar == '\\' && !isEscaped) {
                        isEscaped = true;
                    } else {
                        isEscaped = false;
                    }
                    result.append(currentChar);
                    break;
                case DOUBLE_QUOTED_STRING:
                    if (currentChar == '"' && !isEscaped) {
                        state = ParserStatus.PHP_CODE;
                    } else if (currentChar == '\\' && !isEscaped) {
                        isEscaped = true;
                    } else {
                        isEscaped = false;
                    }
                    result.append(currentChar);
                    break;
                case PHP_CODE:
                    if (currentChar == '/' && previousChar == '/') {
                        result.deleteCharAt(result.length() - 1);
                        state = ParserStatus.INLINE_COMMENT;
                    } else if (currentChar == '*' && previousChar == '/') {
                        result.deleteCharAt(result.length() - 1);
                        state = ParserStatus.MULTILINE_COMMENT;
                    } else if (currentChar == '"') {
                        state = ParserStatus.DOUBLE_QUOTED_STRING;
                        result.append(currentChar);
                    } else if (currentChar == '\'') {
                        state = ParserStatus.SIMPLE_QUOTED_STRING;
                        result.append(currentChar);
                    } else if (currentChar == '>' && previousChar == '?') {
                        state = ParserStatus.OUTSIDE_PHP_CODE;
                        result.append(currentChar);
                    } else {
                        result.append(currentChar);
                    }
                    break;
                case OUTSIDE_PHP_CODE:
                default:
                    if (currentChar == '?' && previousChar == '<'
                            && input.substring(i + 1, i + 4).equals("php")) {
                        state = ParserStatus.PHP_CODE;
                    }
                    result.append(currentChar);
            }
            previousChar = currentChar;
        }

        return result.toString();
    }
}