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

package org.phpboost.poptimizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.phpboost.poptimizer.optimizers.AbstractOptimizer;
import org.phpboost.poptimizer.optimizers.NeutralOptimizer;
import org.phpboost.poptimizer.optimizers.Optimizer;

/**
 * This class enables you to optimize some PHP files.
 * @author Benoit Sautel &lt;ben.popeye@phpboost.com&gt; Loic Rouchon
 * &lt;horn@phpboost.com&gt;
 */
public class POptimizer {
    /**
     * Output folder
     */
    private File output = null;
    /**
     * Input folder or file
     */
    private File input = null;
    /**
     * Input path
     */
    private String inputPath = null;
    /**
     * Output path
     */
    private String outputPath = null;
    /**
     * List of the ignored directories
     */
    private final List<String> excludedDirectories = new ArrayList<String>();
    /**
     * Modules selection (bits)
     */
    private EnumSet<Optimizer> modules = EnumSet.noneOf(Optimizer.class);
    /**
     * Charset of the files to read
     */
    private FileCharset inputCharset = FileCharset.UTF8;
    /**
     * Charset in which the files must be written
     */
    private FileCharset outputCharset = FileCharset.UTF8;

    /**
     * Builds an optimizer with its properties
     * @param source
     * Source file/folder
     * @param destination
     * Destination file/folder
     * @param exclude
     * List of the files/folders to ignore
     * @throws IOException
     */
    public POptimizer(final File source, final File destination, final List<File> exclude)
            throws POptimizerConfigurationException, IOException {
        input = source;
        output = destination;

        if (output == null) {
            throw new POptimizerConfigurationException(
                    "Please enter an output folder (-o path)");
        }
        if (input == null) {
            throw new POptimizerConfigurationException(
                    "Please enter an input folder/file (-i path)");
        }
        if (!input.exists()) {
            throw new POptimizerConfigurationException(
                    "The input file/folder must exist: "
                            + input.getCanonicalPath());
        }
        if (output.exists() && !output.isDirectory()) {
            throw new POptimizerConfigurationException(
                    "Please enter an output folder and not and output file: "
                            + destination.getAbsolutePath() + " entered");
        } else {
            createDirectory(output);
        }

        for (final File aFile : exclude) {
            excludedDirectories.add(aFile.getCanonicalPath());
        }

        inputPath = input.isDirectory() ? input.getCanonicalPath() : input
                .getParentFile().getCanonicalPath();
        outputPath = output.getCanonicalPath();
    }

    /**
     * Version of the program
     */
    public static final String VERSION = "1.1";

    /**
     * Optimizes the source file/folder
     * @throws IOException
     */
    public void optimize() throws IOException {
        process(input);
    }

    /**
     * Sets the input charset (charset of the files to read)
     * @param cs
     * charset
     */
    public void setInputCharset(final FileCharset cs) {
        inputCharset = cs;
    }

    /**
     * Sets the charset in which files must be written
     * @param cs
     */
    public void setOutputCharset(final FileCharset cs) {
        outputCharset = cs;
    }

    /**
     * Adds a module to the module selection
     * @param optimizer
     * optimizer
     */
    public void selectModule(final Optimizer optimizer) {
        modules.add(optimizer);
    }

    /**
     * Selects all the modules
     */
    public void selectAllModules() {
        modules = EnumSet.allOf(Optimizer.class);
    }

    /**
     * Removes a module from the selection
     * @param optimizer
     * Optimizer
     */
    public void removeModule(final Optimizer optimizer) {
        if (modules.contains(optimizer)) {
            modules.remove(optimizer);
        }
    }

    /**
     * Tells whether a module is in the modules selection
     * @param moduleBit
     * Optimizer
     * @return true if the module is in the selection, otherwise false
     */
    public boolean isModuleSelected(final Optimizer optimizer) {
        return modules.contains(optimizer);
    }

    /**
     * Tells whether a file is to optimize or not (ignored).
     * @param file
     * The file to analyse
     * @return true if the file is to optimize, otherwise false
     * @throws IOException
     */
    private Boolean isExcluded(final File file) throws IOException {
        final String filePath = file.getCanonicalPath();
        for (final String excludedFile : excludedDirectories) {
            if (filePath.startsWith(excludedFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads a text file and returns its content. Reads the file according to
     * the input charset defined in the class.
     * @param file
     * The file to read.
     * @return The file content.
     * @throws IOException
     */
    private String read(final File file) throws IOException {
        final StringBuilder result = new StringBuilder();
        final FileInputStream fis = new FileInputStream(file);
        int byteToRead = fis.available();
        while (byteToRead > 0) {
            final byte[] fileBytes = new byte[byteToRead];
            if (fis.read(fileBytes) > 0) {
                result.append(new String(fileBytes, inputCharset
                        .associatedCharset()));
            }
            byteToRead = fis.available();
        }
        return result.toString();
    }

    /**
     * Writes a file. Its content will be exported in the charset defined as the
     * output charset.
     * @param file
     * The file to write
     * @param content
     * Its content
     * @throws IOException
     */
    private void write(final File file, final String content) throws IOException {
        final FileOutputStream ouput = new FileOutputStream(file);
        ouput.write(content.getBytes(outputCharset.associatedCharset()));
        ouput.close();
    }

    /**
     * Processes a file system element (both folder and files). If it's a
     * folder, it optimizes all its files, if it's a file, it optimizes the
     * file.
     * @param fileToOptimize
     * The file to process
     * @throws IOException
     */
    private void process(final File fileToOptimize) throws IOException {
        final File destinationFile = new File(outputPath
                + fileToOptimize.getCanonicalPath().substring(
                        inputPath.length()));
        // Folder
        if (fileToOptimize.isDirectory()) {
            if (!destinationFile.exists()) {
                createDirectory(destinationFile);
            }

            // We also process the children elements
            final File[] files = fileToOptimize.listFiles();
            for (final File file : files) {
                process(file);
            }
        }
        // File
        else {
            if (fileToOptimize.getName().matches(".+\\.php$")
                    && !isExcluded(fileToOptimize)) {
                optimize(fileToOptimize, destinationFile);
            } else {
                copy(fileToOptimize, destinationFile);
            }
        }
    }

    /**
     * Optimizes a file according to the selected modules
     * @param inputFileName
     * File to convert
     * @param outputFileName
     * Destination file
     * @throws IOException
     */
    private void optimize(final File inputFileName, final File outputFileName)
            throws IOException {
        System.out.println("Optimizing " + inputFileName);
        final String inputText = read(inputFileName);

        write(outputFileName, optimize(inputText));
    }

    /**
     * Optimizes a string using the optimizing configuration
     * @param content
     * The content to optimize
     * @return The optimized content
     */
    private String optimize(final String content) {
        AbstractOptimizer globalOptimizer = new NeutralOptimizer(content);

        // Application of the selected optimizers
        for (final Optimizer optimizer : modules) {
            globalOptimizer = optimizer.getOptimizer(globalOptimizer);
        }

        return globalOptimizer.optimize();
    }

    /**
     * Copies a file that is ignored by the optimizer
     * @param inputFile
     * The file to copy
     * @param outputFile
     * The target file
     * @throws IOException
     */
    private void copy(final File inputFile, final File outputFile) throws IOException {
        System.out.println("Copying " + inputFile);
        write(outputFile, read(inputFile));
    }

    /**
     * Creates a destination directory
     * @param destination
     * The directory to create
     * @throws IOException
     */
    private void createDirectory(final File destination) throws IOException {
        System.out.println("Creating directory "
                + destination.getCanonicalPath());
        if (!destination.mkdirs()) {
            throw new IOException("Unable to create directory "
                    + destination.getCanonicalPath());
        }
    }
}