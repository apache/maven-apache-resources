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

public class IT_004_IdeExcludes
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
        
        banned.add( archivePathFromProject( BASENAME, VERSION, "/cobertura.ser" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/release.properties" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/debug.log" ) );
        banned.add( archivePathFromProject( BASENAME, VERSION, "/pom.xml.releaseBackup" ) );
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child1", "/debug.log" ) );
        
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/cobertura.ser" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/pom.xml.bak" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/pom.xml.diff" ) );
        banned.add( archivePathFromChild( BASENAME, VERSION, "child2", "/pom.xml.patch" ) );
        
        assertZipContents( required, banned, assembly );
    }

}
