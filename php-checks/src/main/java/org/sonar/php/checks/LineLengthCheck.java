/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2019 SonarSource SA
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
package org.sonar.php.checks;

import java.util.stream.Stream;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.php.checks.utils.CheckUtils;
import org.sonar.plugins.php.api.tree.CompilationUnitTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

@Rule(key = LineLengthCheck.KEY)
public class LineLengthCheck extends PHPVisitorCheck {

  public static final String KEY = "S103";
  private static final String MESSAGE = "Split this %s characters long line (which is greater than %s authorized).";

  public static final int DEFAULT = 120;

  @RuleProperty(
    key = "maximumLineLength",
    defaultValue = "" + DEFAULT)
  public int maximumLineLength = DEFAULT;

  @Override
  public void visitCompilationUnit(CompilationUnitTree tree) {
    Stream<String> lines = CheckUtils.lines(context().getPhpFile());

    int[] idx = {0};
    lines.forEach(line -> {
      if (line.length() > maximumLineLength) {
        String message = String.format(MESSAGE, line.length(), maximumLineLength);
        context().newLineIssue(this, idx[0] + 1, message);
      }
      idx[0]++;
    });
  }
}
