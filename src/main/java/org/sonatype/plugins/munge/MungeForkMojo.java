package org.sonatype.plugins.munge;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Forks a new build that compiles and runs tests using the munged source in place of the original.
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
        // this mojo only exists to launch the forked build
    }
}
