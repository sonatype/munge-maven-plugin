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
 * Munge.
 * 
 * @goal munge
 * @phase generate-sources
 */
public class MungeMojo
    extends AbstractMojo
{
    /**
     * @parameter default-value="${classifier}"
     */
    private String classifier;

    /**
     * @parameter default-value="${symbols}"
     */
    private String symbols;

    /**
     * @parameter default-value="${includes}"
     */
    private String includes;

    /**
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

        final String mungeDirectory = build.getDirectory() + File.separator + classifier;

        final String mungedMainDirectory = mungeDirectory + File.separator + "main";
        final String mungedTestDirectory = mungeDirectory + File.separator + "test";

        munge( build.getSourceDirectory(), mungedMainDirectory, includes, excludes );
        munge( build.getTestSourceDirectory(), mungedTestDirectory, includes, excludes );

        if ( null != executedProject )
        {
            executedProject.getCompileSourceRoots().clear();
            executedProject.addCompileSourceRoot( mungedMainDirectory );

            executedProject.getTestCompileSourceRoots().clear();
            executedProject.addTestCompileSourceRoot( mungedTestDirectory );

            final Build executedBuild = executedProject.getBuild();
            executedBuild.setDirectory( mungeDirectory );

            executedBuild.setOutputDirectory( mungeDirectory + File.separator + "classes" );
            executedBuild.setTestOutputDirectory( mungeDirectory + File.separator + "test-classes" );
        }
    }

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
