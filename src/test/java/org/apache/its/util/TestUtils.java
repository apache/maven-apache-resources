package org.apache.its.util;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class TestUtils
{
    
    public static String archivePathFromChild( String artifactId, String version, String childName, String childPath )
    {
        if ( !childPath.startsWith( "/" ) )
        {
            childPath = "/" + childPath;
        }
        
        return ( artifactId + "-" + version + "/" + artifactId + "-" + childName + childPath ).replace( '\\',
                                                                                                  File.separatorChar )
                                                                                        .replace( '/',
                                                                                                  File.separatorChar );
    }

    public static String archivePathFromProject( String artifactId, String version, String path )
    {
        if ( !path.startsWith( "/" ) )
        {
            path = "/" + path;
        }
        
        return ( artifactId + "-" + version + path ).replace( '\\', File.separatorChar ).replace( '/', File.separatorChar );
    }

//    @SuppressWarnings( "unchecked" )
    public static void assertZipContents( Set<String> required, Set<String> banned, File assembly )
        throws ZipException, IOException
    {
        assertTrue( "Assembly archive missing: " + assembly, assembly.isFile() );
        
        ZipFile zf = new ZipFile( assembly );
        
//        System.out.println( "Contents of: " + assembly + ":\n\n" );
//        for( Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zf.entries(); e.hasMoreElements(); )
//        {
//            System.out.println( e.nextElement().getName() );
//        }
//        System.out.println( "\n\n" );

        Set<String> missing = new HashSet<String>();
        for ( String name : required )
        {
            if ( zf.getEntry( name ) == null )
            {
                missing.add( name );
            }
        }

        Set<String> banViolations = new HashSet<String>();
        for ( String name : banned )
        {
            if ( zf.getEntry( name ) != null )
            {
                banViolations.add( name );
            }
        }

        if ( !missing.isEmpty() || !banViolations.isEmpty() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "The following errors were found in:\n\n" );
            msg.append( assembly );
            msg.append( "\n");
            msg.append( "\nThe following REQUIRED entries were missing from the bundle archive:\n" );

            if ( missing.isEmpty() )
            {
                msg.append( "\nNone." );
            }
            else
            {
                for ( String name : missing )
                {
                    msg.append( "\n" ).append( name );
                }
            }

            msg.append( "\n\nThe following BANNED entries were present from the bundle archive:\n" );

            if ( banViolations.isEmpty() )
            {
                msg.append( "\nNone.\n" );
            }
            else
            {
                for ( String name : banViolations )
                {
                    msg.append( "\n" ).append( name );
                }
            }

            fail( msg.toString() );
        }
    }

    public static File getTestDir( String name )
        throws IOException, URISyntaxException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL resource = cloader.getResource( name );

        if ( resource == null )
        {
            throw new IOException( "Cannot find test directory: " + name );
        }

        return new File( new URI( resource.toExternalForm() ).normalize().getPath() );
    }

}
