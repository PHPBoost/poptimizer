/**
 * Copyright (C) 2009 Loïc Rouchon <horn@phpboost.com>, Benoit Sautel <ben.popeye@phpboost.com>, Régis Viarre <regis.viarre@phpboost.com>
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
 * This class is an optimizer which removes all the useless spaces in the PHP
 * files. It removes all the indentation and all the spaces which are in the
 * file only to improve its readability.
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt;, Loic Rouchon
 * &lt;horn@phpboost.com&gt;, Régis Viarre &lt;regis.viarre@phpboost.com&gt;
 */
public class SpacesOptimizer extends AbstractOptimizer {
    private StringBuilder result;
    private int pointer;

    /**
     * States of the automaton
     */
    private static enum ParserStatus {
        OUTSIDE_PHP_CODE, PHP_CODE, INLINE_COMMENT, MULTILINE_COMMENT, SIMPLE_QUOTED_STRING, DOUBLE_QUOTED_STRING, INSIDE_CONCATENATION
    };

    /**
     * Builds an optimizer from a string
     * @param in
     * input content
     */
    public SpacesOptimizer(final String in) {
        super(in);
    }

    /**
     * Builds an optimizer from another optimizer
     * @param decorate
     */
    public SpacesOptimizer(final AbstractOptimizer decorate) {
        super(decorate);
    }

    /**
     * Optimizes the content
     */
    @Override
    public String optimize() {
        super.optimize();

        ParserStatus state = ParserStatus.OUTSIDE_PHP_CODE;

        result = new StringBuilder();
        char previousChar = '\0';
        Boolean isEscaped = false;

        pointer = 0;
        while (pointer < input.length()) {
            char currentChar = input.charAt(pointer);
            switch (state) {
                case MULTILINE_COMMENT:
                    result.append(currentChar);
                    if (currentChar == '/' && previousChar == '*') {
                        state = ParserStatus.PHP_CODE;
                    }
                    break;
                case INLINE_COMMENT:
                    result.append(currentChar);
                    if (currentChar == '\n') {
                        state = ParserStatus.PHP_CODE;
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
                    if (currentChar == '"') {
                        // Enter a PHP String
                        result.append(currentChar);
                        state = ParserStatus.DOUBLE_QUOTED_STRING;
                    } else if (currentChar == '\'') {
                        // Enter a PHP String
                        state = ParserStatus.SIMPLE_QUOTED_STRING;
                        result.append(currentChar);
                    } else if (currentChar == '/' && previousChar == '/') {
                        // Enter a PHP comment
                        result.append(currentChar);
                        state = ParserStatus.INLINE_COMMENT;
                    } else if (currentChar == '*' && previousChar == '/') {
                        // Enter a PHP comment
                        result.append(currentChar);
                        state = ParserStatus.MULTILINE_COMMENT;
                    } else if (currentChar == '\n' && isTabOrSpace(previousChar)
                            || isOperator(currentChar) && isTabOrSpace(previousChar)) {
                        // Last character of the line is a blank character
                        // Or
                        // Current is an operator and previous one a Tab or a Space
                        // we remove it
                        result.deleteCharAt(result.length() - 1);
                        result.append(currentChar);

                        // Special handling for concatenation with integer
                        if (currentChar == '.') {
                            pointer++;
                            consumeAllBlankCharacters();
                            currentChar = input.charAt(pointer);
                            if (isNumeric(currentChar) || currentChar == '.') {
                                result.append(" ");
                                while (!isBlank(currentChar)) {
                                    result.append(currentChar);
                                    pointer++;
                                    currentChar = input.charAt(pointer);
                                }
                                result.append("  ");
                            } else {
                                pointer--; // It's not an integer, we rewind the pointer to read correctly the
                                // character in the next iteration.
                            }
                        }
                    } else if (currentChar == '>' && previousChar == '?') {
                        state = ParserStatus.OUTSIDE_PHP_CODE;
                        result.append(currentChar);
                    } else if (!(isTabOrSpace(currentChar) && (isBlank(previousChar) || isOperator(previousChar)))) {
                        // Previous and current characters were blank
                        // we do not add the new one
                        result.append(currentChar);
                    } else {
                        currentChar = previousChar;
                    }
                    break;
                case OUTSIDE_PHP_CODE:
                default:
                    if (currentChar == '?' && previousChar == '<'
                            && input.substring(pointer + 1, pointer + 4).equals("php")) {
                        state = ParserStatus.PHP_CODE;
                        result.append(currentChar);
                        result.append(input.substring(pointer + 1, pointer + 5));
                        pointer += 4;
                    } else {
                        result.append(currentChar);
                    }
            }
            previousChar = currentChar;

            pointer++;
        }

        return result.toString();
    }

    /**
     * Consume all blank charaters
     */
    private void consumeAllBlankCharacters() {
        char currentChar = input.charAt(pointer);
        while (isBlank(currentChar)) {
            pointer++;
            currentChar = input.charAt(pointer);
        }
    }

    /**
     * Tells whether a character is an operator
     * @param aChar
     * @return true if the character is an operator, false otherwise
     */
    private Boolean isOperator(final char aChar) {
        switch (aChar) {
            case '.':
            case '=':
            case '+':
            case '-':
            case '*':
            case '/':
            case '?':
            case ':':
            case ';':
            case '(':
            case ')':
            case '{':
            case '}':
            case '[':
            case ']':
            case '&':
            case '%':
            case '!':
            case ',':
            case '<':
            case '>':
                return true;
            default:
                return false;
        }
    }

    /**
     * Tells whether a character is a blank character
     * @param aChar
     * @return true if the character is a blank character, false otherwise
     */
    private Boolean isBlank(final char aChar) {
        switch (aChar) {
            case ' ':
            case '\t':
            case '\n':
                return true;
            default:
                return false;
        }
    }

    /**
     * Tells whether a character is a tabulation or a space
     * @param aChar
     * @return true if the character is a space or a tabulation, false otherwise
     */
    private Boolean isTabOrSpace(final char aChar) {
        switch (aChar) {
            case ' ':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    /**
     * Tells whether a character is a number
     * @param aChar
     * @return true if the character is a number, false otherwise
     */
    private Boolean isNumeric(final char aChar) {
        return aChar > '0' && aChar < '9';
    }
}
