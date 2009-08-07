package org.apache.its;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.getTestDir;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IT_005_MiscellaneousExcludes
{
    
    private static final String BASENAME = "shared-resources";
    private static final String VERSION = "1";
    
    @Test
    public void execute()
        throws VerificationException, IOException, URISyntaxException
    {
        File testDir = getTestDir( BASENAME );
        
        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        
        verifier.executeGoal( "package" );
        
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        File assembly = new File( testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip" );
        
        Set<String> required = Collections.emptySet();
        
        Set<String> banned = new HashSet<String>();
        
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.classpath" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.project" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/maven-eclipse.xml" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/ide-excludes.iml" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/ide-excludes.ipr" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/ide-excludes.iws" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.deployables" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.settings" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.wtpmodules" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/.externalToolBuilders" ) );
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.classpath" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.project" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/maven-eclipse.xml" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.deployables" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.settings" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.wtpmodules" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/.externalToolBuilders" ) );
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/ide-excludes-child2.iml" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/ide-excludes-child2.ipr" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/ide-excludes-child2.iws" ) );
        
        assertZipContents( required, banned, assembly );
    }

}
