package org.sonatype.plugins.munge;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * Munges source files by keeping or removing sections of code according to what symbols are enabled.
 * 
 * @goal munge
 * @phase generate-sources
 */
public class MungeMojo
    extends AbstractMojo
{
    /**
     * Where to put the munged source files.
     * 
     * @parameter default-value="${project.build.directory}/munged"
     */
    private String mungedDirectory;

    /**
     * List of symbol names (separated by commas) identifying which pieces of code to retain when munging.
     * 
     * @parameter default-value="${symbols}"
     */
    private String symbols;

    /**
     * List of patterns (separated by commas) used to specify the files that should be included in munging.
     * 
     * @parameter default-value="${includes}"
     */
    private String includes;

    /**
     * List of patterns (separated by commas) used to specify the files that should be excluded from munging.
     * 
     * @parameter default-value="${excludes}"
     */
    private String excludes;

    /**
     * @parameter expression="${project.build}"
     */
    private Build build;

    /**
     * @parameter expression="${executedProject}"
     */
    private MavenProject executedProject;

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException
    {
        for ( final String s : symbols.split( "," ) )
        {
            Munge.symbols.clear();
            Munge.symbols.put( s, Boolean.TRUE );
        }

        final String mungedMainDirectory = mungedDirectory + File.separator + "main";
        final String mungedTestDirectory = mungedDirectory + File.separator + "test";

        munge( build.getSourceDirectory(), mungedMainDirectory, includes, excludes );
        munge( build.getTestSourceDirectory(), mungedTestDirectory, includes, excludes );

        if ( null != executedProject )
        {
            executedProject.getCompileSourceRoots().clear();
            executedProject.addCompileSourceRoot( mungedMainDirectory );

            executedProject.getTestCompileSourceRoots().clear();
            executedProject.addTestCompileSourceRoot( mungedTestDirectory );

            final Build executedBuild = executedProject.getBuild();
            executedBuild.setDirectory( mungedDirectory );

            executedBuild.setOutputDirectory( mungedDirectory + File.separator + "classes" );
            executedBuild.setTestOutputDirectory( mungedDirectory + File.separator + "test-classes" );
        }
    }

    /**
     * Munges source files found in {@code from} and places them in {@code to}, honoring any includes or excludes.
     * 
     * @param from The original source directory
     * @param to The munged source directory
     * @param includes Comma-separated list of files to include
     * @param excludes Comma-separated list of files to exclude
     * @throws MojoExecutionException
     */
    @SuppressWarnings( "unchecked" )
    public static void munge( final String from, final String to, final String includes, final String excludes )
        throws MojoExecutionException
    {
        try
        {
            for ( final File f : (List<File>) FileUtils.getFiles( new File( from ), includes, excludes ) )
            {
                final String inPath = f.getPath();
                final String outPath = inPath.replace( from, to );
                new File( outPath ).getParentFile().mkdirs();
                final Munge munge = new Munge( inPath, outPath );
                munge.process();
                munge.close();
            }
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( e.toString() );
        }
    }
}
