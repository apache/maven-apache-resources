/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.its.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.codehaus.plexus.archiver.tar.GZipTarFile;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestUtils {

    public static String archivePathFromChild(String artifactId, String version, String childName, String childPath) {
        if (!childPath.startsWith("/")) {
            childPath = "/" + childPath;
        }

        return (artifactId + "-" + version + "/" + artifactId + "-" + childName + childPath);
    }

    public static String archivePathFromProject(String artifactId, String version, String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        return (artifactId + "-" + version + path);
    }

    public static void assertTarContents(Set<String> required, Set<String> banned, File assembly) throws IOException {
        assertTrue("Assembly archive missing: " + assembly, assembly.isFile());

        GZipTarFile tarFile = null;
        try {
            tarFile = new GZipTarFile(assembly);

            LinkedHashSet<String> pathSet = new LinkedHashSet<>();

            for (@SuppressWarnings("unchecked") Enumeration<ArchiveEntry> enumeration = tarFile.getEntries();
                    enumeration.hasMoreElements(); ) {
                pathSet.add(enumeration.nextElement().getName());
            }
            assertArchiveContents(required, banned, assembly.getAbsolutePath(), pathSet);
        } finally {
            if (tarFile != null) {
                tarFile.close();
            }
        }
    }

    public static void assertZipContents(Set<String> required, Set<String> banned, File assembly)
            throws ZipException, IOException {
        assertTrue("Assembly archive missing: " + assembly, assembly.isFile());

        ZipFile zf = null;
        try {
            zf = new ZipFile(assembly);

            LinkedHashSet<String> pathSet = new LinkedHashSet<>();

            for (Enumeration<? extends ZipEntry> enumeration = zf.entries(); enumeration.hasMoreElements(); ) {
                pathSet.add(enumeration.nextElement().getName());
            }
            assertArchiveContents(required, banned, assembly.getAbsolutePath(), pathSet);
        } finally {
            if (zf != null) {
                zf.close();
            }
        }
    }

    private static void assertArchiveContents(
            Set<String> required, Set<String> banned, String assemblyName, Set<String> contents) {

        Set<String> missing = new HashSet<>();
        for (String name : required) {
            if (!contents.contains(name)) {
                missing.add(name);
            }
        }

        Set<String> banViolations = new HashSet<>();
        for (String name : banned) {
            if (contents.contains(name)) {
                banViolations.add(name);
            }
        }

        if (!missing.isEmpty() || !banViolations.isEmpty()) {
            StringBuffer msg = new StringBuffer();
            msg.append("The following errors were found in:\n\n");
            msg.append(assemblyName);
            msg.append("\n");
            msg.append("\nThe following REQUIRED entries were missing from the bundle archive:\n");

            if (missing.isEmpty()) {
                msg.append("\nNone.");
            } else {
                for (String name : missing) {
                    msg.append("\n").append(name);
                }
            }

            msg.append("\n\nThe following BANNED entries were present from the bundle archive:\n");

            if (banViolations.isEmpty()) {
                msg.append("\nNone.\n");
            } else {
                for (String name : banViolations) {
                    msg.append("\n").append(name);
                }
            }

            msg.append("\n").append("Archive contents:\n");
            for (String path : contents) {
                msg.append("\n").append(path);
            }

            fail(msg.toString());
        }
    }

    public static File getTestDir(String name) throws IOException, URISyntaxException {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL resource = cloader.getResource(name);

        if (resource == null) {
            throw new IOException("Cannot find test directory: " + name);
        }

        return new File(new URI(resource.toExternalForm()).normalize().getPath());
    }
}
