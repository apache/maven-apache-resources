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
import java.util.HashSet;
import java.util.Set;

public class IT_003_SharedResourceInclusion
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
        
        Set<String> required = new HashSet<String>();
        
        required.add( archivePathFromProject( BASENAME, VERSION, "/pom.xml" ) );
        required.add( archivePathFromProject( BASENAME, VERSION, "/LICENSE" ) );
        required.add( archivePathFromProject( BASENAME, VERSION, "/DEPENDENCIES" ) );
        required.add( archivePathFromProject( BASENAME, VERSION, "/NOTICE" ) );
        
        Set<String> banned = new HashSet<String>();
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/LICENSE" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/DEPENDENCIES" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/NOTICE" ) );
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/LICENSE" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/DEPENDENCIES" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/NOTICE" ) );
        
        assertZipContents( required, banned, assembly );
    }

}
