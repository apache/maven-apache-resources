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
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.shared.verifier.Verifier;
import org.junit.jupiter.api.Test;

import static org.apache.its.util.TestUtils.archivePathFromChild;
import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.createVerifier;
import static org.apache.its.util.TestUtils.getTestDir;

class IT005MiscellaneousExcludes {

    private static final String BASENAME = "misc-excludes";
    private static final String VERSION = "1";

    @Test
    void execute() throws Exception {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = createVerifier(testDir);

        verifier.addCliArgument("package");
        verifier.execute();

        verifier.verifyErrorFreeLog();

        File assembly = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");

        Set<String> required = new HashSet<>();

        Set<String> banned = new HashSet<>();

        banned.add(archivePathFromProject(BASENAME, VERSION, "/cobertura.ser"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/release.properties"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/pom.xml.releaseBackup"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/dependency-reduced-pom.xml"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.flattened-pom.xml"));

        banned.add(archivePathFromChild(BASENAME, VERSION, "child2", "/cobertura.ser"));

        required.add(archivePathFromProject(BASENAME, VERSION, "/some-target.txt"));
        assertZipContents(required, banned, assembly);
    }
}
