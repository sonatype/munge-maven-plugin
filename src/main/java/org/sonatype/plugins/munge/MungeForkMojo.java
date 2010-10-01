package org.sonatype.plugins.munge;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Fork.
 * 
 * @goal munge-fork
 * @execute lifecycle=munge phase=package
 */
public class MungeForkMojo
    extends AbstractMojo
{
    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * @parameter default-value="${classifier}"
     */
    private String classifier;

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * @parameter expression="${executedProject}"
     */
    private MavenProject executedProject;

    public void execute()
        throws MojoExecutionException
    {
        final Artifact mungedArtifact = executedProject.getArtifact();
        projectHelper.attachArtifact( project, mungedArtifact.getType(), classifier, mungedArtifact.getFile() );
    }
}
