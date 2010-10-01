package org.sonatype.plugins.munge;

import java.io.File;
import java.io.IOException;

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
     * @parameter default-value="${project.build.sourceDirectory}"
     */
    private File mainSourceDirectory;

    /**
     * @parameter default-value="${project.build.testSourceDirectory}"
     */
    private File testSourceDirectory;

    /**
     * @parameter default-value="${project.build.directory}"
     */
    private File targetDirectory;

    /**
     * @parameter expression="${executedProject}"
     */
    private MavenProject executedProject;

    public void execute()
        throws MojoExecutionException
    {
        final File mungeDirectory = new File( targetDirectory, classifier );
        final File mungedMainDirectory = new File( mungeDirectory, "main" );
        final File mungedTestDirectory = new File( mungeDirectory, "test" );

        try
        {
            if ( mainSourceDirectory.isDirectory() )
            {
                FileUtils.copyDirectoryStructure( mainSourceDirectory, mungedMainDirectory );
            }
            if ( testSourceDirectory.isDirectory() )
            {
                FileUtils.copyDirectoryStructure( testSourceDirectory, mungedTestDirectory );
            }
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( e.toString() );
        }

        if ( null != executedProject )
        {
            executedProject.getCompileSourceRoots().clear();
            executedProject.addCompileSourceRoot( mungedMainDirectory.getPath() );

            executedProject.getTestCompileSourceRoots().clear();
            executedProject.addTestCompileSourceRoot( mungedTestDirectory.getPath() );

            executedProject.getBuild().setDirectory( mungeDirectory.getPath() );
            executedProject.getBuild().setOutputDirectory( mungeDirectory + "/classes" );
            executedProject.getBuild().setTestOutputDirectory( mungeDirectory + "/test-classes" );
        }
    }
}
