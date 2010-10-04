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
 * @execute lifecycle=munge phase=prepare-package
 */
public class MungeForkMojo
    extends AbstractMojo
{
    public void execute()
        throws MojoExecutionException
    {
    }
}
