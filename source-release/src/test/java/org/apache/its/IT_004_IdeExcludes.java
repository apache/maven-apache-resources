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
import org.junit.jupiter.api.Test;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.createVerifier;
import static org.apache.its.util.TestUtils.getTestDir;

public class IT_004_IdeExcludes {

    private static final String BASENAME = "ide-excludes";
    private static final String VERSION = "1";

    @Test
    public void execute() throws VerificationException, IOException, URISyntaxException {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = createVerifier(testDir);

        verifier.executeGoal("package");

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File assembly = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");

        Set<String> required = Collections.emptySet();

        Set<String> banned = new HashSet<>();

        banned.add(archivePathFromProject(BASENAME, VERSION, "/.classpath"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.project"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/maven-eclipse.xml"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/ide-excludes.iml"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/ide-excludes.ipr"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/ide-excludes.iws"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.deployables"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.settings"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.wtpmodules"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.externalToolBuilders"));

        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.classpath"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.project"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/maven-eclipse.xml"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.deployables"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.settings"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.wtpmodules"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child1", "/.externalToolBuilders"));

        banned.add(archivePathFromChild(BASENAME, VERSION, "child2", "/ide-excludes-child2.iml"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child2", "/ide-excludes-child2.ipr"));
        banned.add(archivePathFromChild(BASENAME, VERSION, "child2", "/ide-excludes-child2.iws"));

        assertZipContents(required, banned, assembly);
    }
}
