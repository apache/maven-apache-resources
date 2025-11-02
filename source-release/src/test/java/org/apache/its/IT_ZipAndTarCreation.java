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
package org.apache.its;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Test;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertTarContents;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.createVerifier;
import static org.apache.its.util.TestUtils.getTestDir;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IT_ZipAndTarCreation {

    private static final String BASENAME = "zip-and-tar";
    private static final String VERSION = "1";

    @Test
    public void execute() throws VerificationException, IOException, URISyntaxException {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = createVerifier(testDir);

        verifier.executeGoal("package");

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File tarAssemblyFile = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.tar.gz");
        assertTrue(tarAssemblyFile.exists(), "tar assembly should  have been created");

        File zipAssemblyFile = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");
        assertTrue(zipAssemblyFile.exists(), "zip assembly should  have been created");

        Set<String> required = new HashSet<>();

        required.add(archivePathFromProject(BASENAME, VERSION, "/pom.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "pom.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child2", "/pom.xml"));

        required.add(
                archivePathFromChild(BASENAME, VERSION, "child1", "/src/main/java/org/apache/assembly/it/App.java"));
        required.add(archivePathFromChild(
                BASENAME, VERSION, "child1", "/src/main/resources/META-INF/plexus/components.xml"));

        required.add(
                archivePathFromChild(BASENAME, VERSION, "child2", "/src/main/java/org/apache/assembly/it/App.java"));

        Set<String> banned = Collections.emptySet();

        assertZipContents(required, banned, zipAssemblyFile);
        assertTarContents(required, banned, tarAssemblyFile);
    }
}
