/*
 * SonarQube PHP Plugin
 * Copyright (C) 2011-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.it.php;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import java.io.File;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;

import static org.assertj.core.api.Assertions.assertThat;

public class NoSonarTest {

  @ClassRule
  public static Orchestrator orchestrator = Tests.ORCHESTRATOR;
  private static final String PROJECT_KEY = "nosonar-project";
  private static final String PROJECT_NAME = "NOSONAR Project";

  private static IssueClient issueClient;
  private static final File PROJECT_DIR = Tests.projectDirectoryFor("nosonar");

  @BeforeClass
  public static void startServer() {
    Tests.provisionProject(PROJECT_KEY, PROJECT_NAME, "php", "nosonar-profile");
    SonarScanner build = SonarScanner.create()
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_NAME)
      .setProjectVersion("1")
      .setSourceEncoding("UTF-8")
      .setSourceDirs(".")
      .setProjectDir(PROJECT_DIR);

    Tests.executeBuildWithExpectedWarnings(orchestrator, build);
  }

  @Before
  public void setUp() {
    issueClient = orchestrator.getServer().wsClient().issueClient();
  }

  @Test
  public void test() {
    assertThat(countIssues("php:S1116")).isEqualTo(1);
    assertThat(countIssues("php:NoSonar")).isEqualTo(2);
  }

  private static int countIssues(String issueKey) {
    return issueClient.find(IssueQuery.create().componentRoots(PROJECT_KEY).severities("INFO").rules(issueKey)).list().size();
  }

}
