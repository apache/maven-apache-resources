package org.apache.its;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.assertTarContents;
import static org.apache.its.util.TestUtils.getTestDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Test;

public class IT_ZipAndTarCreation
{

    private static final String BASENAME = "zip-and-tar";
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
        
       // make sure the tar did NOT get created by default
        File tarAssemblyFile = new File( testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.tar.gz" );
        Assert.assertTrue( "tar assembly should  have been created", tarAssemblyFile.exists() );
        
        File zipAssemblyFile = new File( testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip" );
        Assert.assertTrue( "zip assembly should  have been created", zipAssemblyFile.exists() );
        
        Set<String> required = new HashSet<String>();
        
        required.add( archivePathFromProject( BASENAME, VERSION, "/pom.xml" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child1", "pom.xml" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child2", "/pom.xml" ) );
        
        required.add( archivePathFromChild( BASENAME, VERSION, "child1", "/src/main/java/org/apache/assembly/it/App.java" ) );
        required.add( archivePathFromChild( BASENAME, VERSION, "child1", "/src/main/resources/META-INF/plexus/components.xml" ) );
        
        required.add( archivePathFromChild( BASENAME, VERSION, "child2", "/src/main/java/org/apache/assembly/it/App.java" ) );
        
        Set<String> banned = Collections.emptySet();
        
        assertZipContents( required, banned, zipAssemblyFile );
        assertTarContents( required, banned, tarAssemblyFile );
    }
    
}
