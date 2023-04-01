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

import org.apache.maven.shared.verifier.VerificationException;
import org.apache.maven.shared.verifier.Verifier;
import org.junit.Test;

import static org.apache.its.util.TestUtils.archivePathFromProject;
import static org.apache.its.util.TestUtils.assertZipContents;
import static org.apache.its.util.TestUtils.getTestDir;

public class IT_006_CiExcludes {

    private static final String BASENAME = "ci-excludes";
    private static final String VERSION = "1";

    @Test
    public void execute() throws VerificationException, IOException, URISyntaxException {
        File testDir = getTestDir(BASENAME);

        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.addCliArgument("package");
        verifier.execute();
        verifier.verifyErrorFreeLog();

        File assembly = new File(testDir, "target/" + BASENAME + "-" + VERSION + "-source-release.zip");

        Set<String> required = Collections.emptySet();

        Set<String> banned = new HashSet<String>();

        banned.add(archivePathFromProject(BASENAME, VERSION, "/.github"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/Jenkinsfile"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.asf.yaml"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/.travis.yml"));
        banned.add(archivePathFromProject(BASENAME, VERSION, "/deploySite.sh"));

        assertZipContents(required, banned, assembly);
    }
}
