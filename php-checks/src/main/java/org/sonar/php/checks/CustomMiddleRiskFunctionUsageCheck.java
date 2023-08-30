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

import com.google.common.collect.ImmutableSet;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.FunctionUsageCheck;
import org.sonar.plugins.php.api.tree.declaration.NamespaceNameTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;

import java.util.Locale;

@Rule(key = CustomMiddleRiskFunctionUsageCheck.KEY)
public class CustomMiddleRiskFunctionUsageCheck extends FunctionUsageCheck {

  public static final String KEY = "S10001";
  // 自定义提示说明
  private static final String MESSAGE_CUSTOM = "不要使用这个过期函数 \"%s()\".";

  private static final ImmutableSet<String> SEARCHING_STRING_FUNCTIONS = ImmutableSet.of(
    "");



  @Override
  protected ImmutableSet<String> functionNames() {
    return ImmutableSet.<String>builder()
      .addAll(SEARCHING_STRING_FUNCTIONS)
      //todo 如下面添加的代码，这里需要添加自定义的方法，才会生效
      //.addAll(CheckUtils.getRulesContents(fileNames, CustomHighRiskFunctionUsageCheck.KEY, customCfg))
      .build();
  }

  @Override
  protected void createIssue(FunctionCallTree tree) {
    String customFunctionName = ((NamespaceNameTree) tree.callee()).name().toString();

    if (null != SEARCHING_STRING_FUNCTIONS && SEARCHING_STRING_FUNCTIONS.contains(customFunctionName.toLowerCase(Locale.ROOT))) {
      context().newIssue(this, tree.callee(), String.format(MESSAGE_CUSTOM, customFunctionName));

    }
  }


  }

