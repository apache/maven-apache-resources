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

import org.apache.maven.shared.verifier.VerificationException;
import org.apache.maven.shared.verifier.Verifier;
import org.junit.jupiter.api.Test;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.createVerifier;
import static org.apache.its.util.TestUtils.getTestDir;

public class ITIncludeIdeFilesWithinSrcDir {

    private static final String BASENAME = "src-contains-ide-files";
    private static final String VERSION = "1";

    @Test
    public void execute() throws VerificationException, IOException, URISyntaxException {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = createVerifier(testDir);

        verifier.addCliArgument("package");
        verifier.execute();

        verifier.verifyErrorFreeLog();

        File assembly = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");

        Set<String> required = new HashSet<>();

        required.add(archivePathFromProject(BASENAME, VERSION, "/pom.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/pom.xml"));

        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.classpath"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.project"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/maven-eclipse.xml"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/ide-excludes.iml"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/ide-excludes.ipr"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/ide-excludes.iws"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.deployables/"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.settings/"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.wtpmodules/"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/.externalToolBuilders/"));

        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/release.properties"));
        required.add(archivePathFromProject(BASENAME, VERSION, "/src/test/resources/pom.xml.releaseBackup"));

        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.classpath"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.project"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/maven-eclipse.xml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/ide-excludes.iml"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/ide-excludes.ipr"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/ide-excludes.iws"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.deployables/"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.settings/"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.wtpmodules/"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/.externalToolBuilders/"));

        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/release.properties"));
        required.add(archivePathFromChild(BASENAME, VERSION, "child1", "/src/test/resources/pom.xml.releaseBackup"));

        Set<String> banned = new HashSet<>();

        assertZipContents(required, banned, assembly);
    }
}
