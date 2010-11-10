package org.phpboost.poptimizer.optimizers;

import junit.framework.Assert;

import org.junit.Test;

public class SpacesOptimizerTest {
    @Test
    public void outsidePHPTest() {
        final String input = "foo bar <?php /* some php code */ ?>foo bar";
        final String expected = "foo bar <?php /* some php code */?>foo bar";
        runTest(input, expected);
    }

    @Test
    public void insidePHPSimpleTest() {
        final String input = "<?php echo 'toto';?>";
        runTest(input, input);
    }

    @Test
    public void insidePHPConcatenationTest() {
        runTest("<?php echo 'toto' . $tata; ?>", "<?php echo 'toto'.$tata;?>");
        runTest("<?php echo 'toto' . $tata . 'titi'; ?>", "<?php echo 'toto'.$tata.'titi';?>");
        runTest("<?php echo 'toto' . $tata . 'titi'; ?>", "<?php echo 'toto'.$tata.'titi';?>");
        runTest("<?php echo 'toto' . 'tata' . 'titi'; ?>", "<?php echo 'toto'.'tata'.'titi';?>");
    }

    @Test
    public void insideQuotedStringTest() {
        final String input = "<?php echo ' hello world ! ';?>";
        runTest(input, input);
    }

    @Test
    public void insideDoubleQuotedStringTest() {
        final String input = "<?php echo \" hello world ! \";?>";
        runTest(input, input);
    }

    @Test
    public void insidePHPHybridStringTest() {
        final String input = "<?php echo \"hello '\\\" world \\'\".'hello \" world \\\" \" ';?>";
        runTest(input, input);
    }

    @Test
    public void insideSingleLineCommentTest() {
        final String input = "<?php\n// Hello world, how are you?\n?>";
        runTest(input, input);
    }

    @Test
    public void indentationBasicTest() {
        final String input = "<?php if (true)\n{\n\techo 'hello world!';\n}\n?>";
        final String expected = "<?php if(true)\n{\necho 'hello world!';\n}\n?>";
        runTest(input, expected);
    }

    @Test
    public void indentationWithinStringTest() {
        final String input = "<?php echo 'hello\nworld';?>";
        runTest(input, input);
    }

    @Test
    public void contactenationWithIntegerTest() {
        String input = "<?php echo 'hello' . 1 . 'world';?>";
        String expected = "<?php echo 'hello'. 1 .'world';?>";
        runTest(input, expected);

        input = "<?php echo 'hello' .   125  . 'world';?>";
        expected = "<?php echo 'hello'. 125 .'world';?>";
        runTest(input, expected);

        input = "<?php echo 'hello' .   .125  . 'world';?>";
        expected = "<?php echo 'hello'. .125 .'world';?>";
        runTest(input, expected);

        input = "<?php echo 'hello' .   21.5  .'world';?>";
        expected = "<?php echo 'hello'. 21.5 .'world';?>";
        runTest(input, expected);
    }

    private void runTest(final String input, final String expected) {
        final SpacesOptimizer optimizer = new SpacesOptimizer(new NeutralOptimizer(
                input));
        Assert.assertEquals(expected, optimizer.optimize());
    }
}
