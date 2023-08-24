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
package org.sonar.php.checks.utils;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.NamespaceNameTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.visitors.PHPCheck;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

public abstract class FunctionUsageCheck extends PHPVisitorCheck {

  private Set lowerCaseFunctionNames;

  protected HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> customCfg;

  protected abstract ImmutableSet<String> functionNames();

  // 所有文件（带目录）的名称
  protected static HashSet<String> fileNames;

  @Override
  public void init() {
    super.init();
    this.customCfg = super.customCfg;
    this.fileNames = super.fileNames;

    lowerCaseFunctionNames = functionNames().stream()
      .map(name -> name.toLowerCase(Locale.ROOT))
      .collect(Collectors.toSet());
  }

  protected abstract void createIssue(FunctionCallTree tree);

  @Override
  public void visitFunctionCall(FunctionCallTree tree) {
    if (isForbiddenFunction(tree.callee())) {
      createIssue(tree);
    }

    super.visitFunctionCall(tree);
  }

  private boolean isForbiddenFunction(ExpressionTree callee) {
    return callee.is(Kind.NAMESPACE_NAME) &&
      lowerCaseFunctionNames.contains(((NamespaceNameTree) callee).qualifiedName().toLowerCase(Locale.ROOT));
  }

}
