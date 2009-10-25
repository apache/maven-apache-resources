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

public class IT_ExcludeSrcDirWithinBuildOutputDir
{
    
    private static final String BASENAME = "output-dir-contains-src-name";
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
        required.add( archivePathFromChild( BASENAME, VERSION, "child1", "/pom.xml" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child2", "/pom.xml" ) );
        
        required.add( archivePathFromProject( BASENAME, VERSION, "/src/test/resources/project/src/main/resources/test.properties" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child1", "/src/test/resources/project/src/main/resources/test.properties" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child2", "/src/test/resources/project/src/main/resources/test.properties" ) );
        
        Set<String> banned = new HashSet<String>();
        
        banned.add( archivePathFromProject( BASENAME, VERSION, "/target/" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/target/test-classes/project/src/main/resources/test.properties" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/target/" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/target/test-classes/project/src/main/resources/test.properties" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/target/" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/target/test-classes/project/src/main/resources/test.properties" ) );
        
        assertZipContents( required, banned, assembly );
    }

}