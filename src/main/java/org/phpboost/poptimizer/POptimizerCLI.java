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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.phpboost.poptimizer.optimizers.Optimizer;

/**
 * This class is the Command Line Interface of POptimizer. It's also the entry
 * point of the application.
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt; Loic Rouchon
 * &lt;horn@phpboost.com&gt;
 */
public class POptimizerCLI {
    /**
     * Entry point of the program
     * @param args
     * Arguments of the program
     */
    public static void main(final String[] args) {
        File source = null;
        File destination = null;
        final List<File> exclude = new ArrayList<File>();
        final List<String> excludePath = new ArrayList<String>();
        final List<String> modules = new ArrayList<String>();
        FileCharset inputCS = null, outputCS = null;

        // We catch the arguments with a state transition system
        ArgsStates state = ArgsStates.NORMAL;
        for (final String arg : args) {
            if (arg.equals("-i")) {
                state = ArgsStates.INPUT;
                continue;
            } else if (arg.equals("-o")) {
                state = ArgsStates.OUTPUT;
                continue;
            } else if (arg.equals("-e")) {
                state = ArgsStates.EXCLUDE;
                continue;
            } else if (arg.equals("-m")) {
                state = ArgsStates.MODULES;
                continue;
            } else if (arg.equals("-h")) {
                printHelp();
                return;
            } else if (arg.equals("-v")) {
                printVersion();
                return;
            } else if (arg.equals("-ics")) {
                state = ArgsStates.INPUT_CHARSET;
                continue;
            } else if (arg.equals("-ocs")) {
                state = ArgsStates.OUTPUT_CHARSET;
                continue;
            }

            switch (state) {
                case INPUT:
                    source = new File(arg);
                    state = ArgsStates.NORMAL;
                    break;
                case EXCLUDE:
                    excludePath.add(arg);
                    break;
                case OUTPUT:
                    destination = new File(arg);
                    state = ArgsStates.NORMAL;
                    break;
                case MODULES:
                    modules.add(arg);
                    break;
                case INPUT_CHARSET:
                    inputCS = FileCharset.fromString(arg);
                    break;
                case OUTPUT_CHARSET:
                    outputCS = FileCharset.fromString(arg);
                    break;
                case NORMAL:
                default:
                    break;
            }
        }

        for (final String path : excludePath) {
            // Compute excluded path from the input one.
            // Could not be done before because parameters order is not defined
            exclude.add(new File(source.getAbsolutePath() + File.separatorChar
                    + path.replaceFirst("^/", "")));
        }

        POptimizer optimizer;
        try {
            optimizer = new POptimizer(source, destination, exclude);
        } catch (final POptimizerConfigurationException e) {
            System.err.println("POptmizer's configuration is not correct: "
                    + e.getMessage());
            return;
        } catch (final IOException e) {
            System.err
                    .println("An input/output error had the optimization failed: "
                            + e.getMessage());
            return;
        }

        System.out.println("Beginning Optimization");
        System.out
                .println("------------------------------------------------------------");

        if (inputCS != null) {
            optimizer.setInputCharset(inputCS);
        }

        if (outputCS != null) {
            optimizer.setOutputCharset(outputCS);
        }

        // We select all the modules if the user doesn't choose the modules he
        // wants to apply
        if (modules.size() == 0) {
            optimizer.selectAllModules();
        } else {
            // Otherwise, we enable the modules he chose
            for (final String module : modules) {
                if (module.equals("all")) {
                    optimizer.selectAllModules();
                    // We stop the modules browsing here
                    break;
                }
                final Optimizer optimizerModule = Optimizer.fromString(module);
                if (optimizerModule != null) {
                    optimizer.selectModule(optimizerModule);
                }
            }
        }

        // Bench
        final BigDecimal beginTime = BigDecimal.valueOf(System.currentTimeMillis());

        // We launch the optimization
        try {
            optimizer.optimize();
        } catch (final IOException e) {
            System.err
                    .println("An input/output error had the optimization failed: "
                            + e.getMessage());
            return;
        }

        // Bench
        final BigDecimal timeLength = BigDecimal.valueOf(System.currentTimeMillis())
                .add(beginTime.negate()).divide(BigDecimal.valueOf(1000));

        System.out
                .println("------------------------------------------------------------");
        System.out.println("Optimization achieved in " + timeLength
                + " seconds");
    }

    private static enum ArgsStates {
        INPUT, OUTPUT, EXCLUDE, MODULES, INPUT_CHARSET, OUTPUT_CHARSET, NORMAL
    };

    /**
     * Prints the documentation of POptimizer
     */
    private static void printHelp() {
        System.out.println("poptimizer Options");
        System.out
                .println("\t-i path: Input path (can refer to a file or a folder)");
        System.out
                .println("\t-o path: Output path (must be a folder, if it doesn't exist, it will be created");
        System.out
                .println("\t-e path1 path2: Paths to exclude (these files will be copied but not optimized)");
        System.out
                .println("\t-m module1 module2 ... modulen: Optimize modules you want to apply");
        System.out
                .println("\t\tcomments: this module will clean all your comments. It doesn't modifies the line numbers");
        System.out
                .println("\t\tspaces: this module will clean all the useless spaces in the code (indentation, spaces between operators...)");
        System.out
                .println("\t\tall: all the modules will be applied (default)");
        System.out
                .println("\t-ics input charset: Charset in which are encoded the files (optimized and not optimized). Must be either UTF-8, UTF-16, US-ASCII or ISO-8859-1");
        System.out
                .println("\t-ocs output charset: Charset in which the optimized or copied files are written. Must be either UTF-8, UTF-16, US-ASCII or ISO-8859-1");
        System.out.println("\t-v: Prints the version of POptimizer you use");
        System.out.println("\t-h: Prints help");
    }

    private static void printVersion() {
        System.out.println("POptimizer version " + POptimizer.VERSION);
    }
}
