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

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.php.api.tree.ScriptTree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.tree.lexical.SyntaxTrivia;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Teamone 自有代码扫描判断
 * 注释首个出现的字符串需要是自定义的字符串
 */
@Rule(key = CustomCommentsFormatCheck.KEY)
public class CustomCommentsFormatCheck extends PHPVisitorCheck {

  public static final String KEY = "S10005";
  private static final String MESSAGE = "注释首行应包含企业全称/缩写";

  public static final String DEFAULT_LEGAL_COMMENT_PATTERN = "Teamone";

  // 以下是用于拼接成最终正则表达式的常量
  // 第一种，以 // 开头，忽略空行，开头一定要有指定的字符串出现
  public static final String DEFAULT_COMMENT_PATTERN_FIRST_LEFT = "^\\/\\/\\s*";

  public static final String DEFAULT_COMMENT_PATTERN_FIRST_RIGHT = ".*$";

  // 第二种，以 # 开头，忽略空行，开头一定要有指定的字符串出现
  public static final String DEFAULT_COMMENT_PATTERN_SECOND_LEFT = "^\\#\\s*";

  public static final String DEFAULT_COMMENT_PATTERN_SECOND_RIGHT = ".*$";

  // 第三种和第四种都一样，以 /* 开头 或者 以 /** 开头，忽略空行、换行及*号，开头一定要有指定的字符串出现
  public static final String DEFAULT_COMMENT_PATTERN_THIRD_LEFT = "\\/\\*(\\*|\\s)*";

  public static final String DEFAULT_COMMENT_PATTERN_THIRD_RIGHT = "[\\s\\S]*\\*\\/$";

  @RuleProperty(
    key = "regex",
    defaultValue = DEFAULT_LEGAL_COMMENT_PATTERN)
  String regex = DEFAULT_LEGAL_COMMENT_PATTERN;

  private Pattern patternFirst;
  private Pattern patternSecond;
  private Pattern patternThird;
  private List<Pattern> patterns = new ArrayList<>();

  @Override
  public void init() {
    super.init();

    String[] split = regex.split(",");
    if (split.length > 0) {
      for (String pf : split) {
        patternFirst = Pattern.compile(DEFAULT_COMMENT_PATTERN_FIRST_LEFT + pf.trim() + DEFAULT_COMMENT_PATTERN_FIRST_RIGHT);
        patternSecond = Pattern.compile(DEFAULT_COMMENT_PATTERN_SECOND_LEFT + pf.trim() + DEFAULT_COMMENT_PATTERN_SECOND_RIGHT);
        patternThird = Pattern.compile(DEFAULT_COMMENT_PATTERN_THIRD_LEFT + pf.trim() + DEFAULT_COMMENT_PATTERN_THIRD_RIGHT);

        patterns.add(patternFirst);
        patterns.add(patternSecond);
        patterns.add(patternThird);
      }
    }
  }

  @Override
  public void visitScript(ScriptTree tree) {
    super.visitScript(tree);
  }

  @Override
  public void visitToken(SyntaxToken token) {
    int matchFlag;
    for (SyntaxTrivia trivia : token.trivias()) {
      String comment = trivia.text();

      matchFlag = 0;

      for (Pattern p : patterns) {
        if (p.matcher(comment).matches()) {
          matchFlag++;
        }
      }

      if (matchFlag == 0) {
        context().newIssue(this, trivia, MESSAGE);
      }
    }
  }
}
