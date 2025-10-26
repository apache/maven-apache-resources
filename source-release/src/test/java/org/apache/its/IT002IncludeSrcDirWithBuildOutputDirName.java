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
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Test;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.createVerifier;
import static org.apache.its.util.TestUtils.getTestDir;

public class IT002IncludeSrcDirWithBuildOutputDirName {

    private static final String BASENAME = "src-contains-output-dir-name";
    private static final String VERSION = "1";

    @Test
    public void execute() throws VerificationException, IOException, URISyntaxException {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = createVerifier(testDir);

        verifier.executeGoal("package");

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File assembly = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");

        Set<String> required = new HashSet<>();

        required.add(archivePathFromProject(BASENAME, VERSION, "/pom.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/pom.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child2", "/pom.xml"));

        required.add(archivePathFromProject(BASENAME, VERSION, "/src/main/resources/target/test.txt"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/main/java/target/App.java"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child2", "/src/main/java/target/App.java"));

        Set<String> banned = new HashSet<>();

        banned.add(archivePathFromProject(BASENAME, VERSION, "/target/"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/target/"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child2", "/target/"));

        assertZipContents(required, banned, assembly);
    }
}
