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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.executor.ExecutorHelper;
import org.apache.maven.executor.ExecutorRequest;
import org.apache.maven.executor.ExecutorResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Minimal, purpose-built stand-in for the (now deprecated) shared Verifier component this module used to
 * depend on, built on top of {@code org.apache.maven.executor}. Only covers the subset of behaviour this
 * module's ITs use: running a build with a fixed goal list, and asserting the resulting log is error free.
 */
public class Verifier {

    private final File basedir;
    private final List<String> cliArguments = new ArrayList<>();
    private String logFileName = "log.txt";
    private ExecutorResult result;

    public Verifier(String basedir) {
        this.basedir = new File(basedir);
    }

    public void setLocalRepo(String localRepo) {
        cliArguments.add("-Dmaven.repo.local=" + localRepo);
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void addCliArgument(String argument) {
        cliArguments.add(argument);
    }

    public void execute() throws IOException {
        String mavenHomeProperty = System.getProperty("maven.home");
        if (mavenHomeProperty == null || mavenHomeProperty.isEmpty()) {
            fail("System property 'maven.home' is not set: cannot locate the Maven installation "
                    + "to run this integration test build with.");
        }

        // the old shared verifier component ran "clean" ahead of every build by default (autoclean)
        List<String> arguments = new ArrayList<>();
        arguments.add("clean");
        arguments.addAll(cliArguments);

        // forked on purpose: embedding Maven 4 inside the test JVM hangs (apache/maven-executor#47)
        try (ExecutorHelper executorHelper =
                ExecutorHelper.forMavenInstallation(new File(mavenHomeProperty).toPath(), ExecutorHelper.Mode.FORKED)) {
            ExecutorRequest request = ExecutorRequest.mavenBuilder()
                    .cwd(basedir.toPath())
                    .arguments(arguments.toArray(new String[0]))
                    .grabOutputAsString(true)
                    .build();
            result = executorHelper.execute(request);
        }

        String log = result.stdOutString().orElse("")
                + System.lineSeparator()
                + result.stdErrString().orElse("");
        Files.write(new File(basedir, logFileName).toPath(), log.getBytes(StandardCharsets.UTF_8));
    }

    public void verifyErrorFreeLog() {
        assertTrue(
                result != null && result.success(),
                "Build did not succeed, exit code: "
                        + (result == null
                                ? "n/a"
                                : result.exitCode().map(String::valueOf).orElse("unknown")));

        String stdOut = result.stdOutString().orElse("");
        String stdErr = result.stdErrString().orElse("");
        assertFalse(
                stdOut.contains("[ERROR]") || stdErr.contains("[ERROR]"),
                "Build log contains [ERROR]:\n" + stdOut + "\n" + stdErr);
    }
}
