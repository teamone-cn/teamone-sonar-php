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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.CheckUtils;
import org.sonar.php.tree.visitors.AssignmentExpressionVisitor;
import org.sonar.plugins.php.api.symbols.Symbol;
import org.sonar.plugins.php.api.tree.CompilationUnitTree;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.LiteralTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

@Rule(key = SSLCertificatesVerificationDisabledCheck.KEY)
public class SSLCertificatesVerificationDisabledCheck extends PHPVisitorCheck {
  public static final String KEY = "S4830";

  private static final String CURL_SETOPT = "curl_setopt";
  private static final String CURLOPT_SSL_VERIFYHOST = "CURLOPT_SSL_VERIFYHOST";
  private static final String CURLOPT_SSL_VERIFYPEER = "CURLOPT_SSL_VERIFYPEER";
  private static final Set<String> VERIFY_HOST_COMPLIANT_VALUES = ImmutableSet.of("2");
  private static final Set<String> VERIFY_PEER_COMPLIANT_VALUES = ImmutableSet.of("true", "1");

  private static final String MESSAGE = "Activate SSL/TLS certificates chain of trust verification.";
  private AssignmentExpressionVisitor assignmentExpressionVisitor;

  @Override
  public void visitCompilationUnit(CompilationUnitTree tree) {
    assignmentExpressionVisitor = new AssignmentExpressionVisitor(context().symbolTable());
    tree.accept(assignmentExpressionVisitor);
    super.visitCompilationUnit(tree);
  }

  @Override
  public void visitFunctionCall(FunctionCallTree tree) {
    String functionName = CheckUtils.getLowerCaseFunctionName(tree);
    List<ExpressionTree> arguments = tree.arguments();

    // Detect curl_setopt function usage
    // http://php.net/manual/fr/function.curl-setopt.php
    if (CURL_SETOPT.equals(functionName) && arguments.size() > 2) {
      ExpressionTree optionArgument = arguments.get(1);
      ExpressionTree valueArgument = arguments.get(2);

      nameOf(optionArgument).ifPresent(name -> {
        if (name.equals(CURLOPT_SSL_VERIFYHOST)) {
          this.checkCURLSSLVerify(valueArgument, VERIFY_HOST_COMPLIANT_VALUES);
        } else if (name.equals(CURLOPT_SSL_VERIFYPEER)) {
          this.checkCURLSSLVerify(valueArgument, VERIFY_PEER_COMPLIANT_VALUES);
        }
      });
    }

    // super method must be called in order to visit function call node's children
    super.visitFunctionCall(tree);
  }

  private static Optional<String> nameOf(Tree tree) {
    String name = CheckUtils.nameOf(tree);
    return name != null ? Optional.of(name) : Optional.empty();
  }

  private void checkCURLSSLVerify(ExpressionTree expressionTree, Set<String> compliantValues) {
    ExpressionTree curlOptValue = getAssignedValue(expressionTree);
    if (curlOptValue instanceof LiteralTree) {
      String value = ((LiteralTree) curlOptValue).value();
      String quoteLessLowercaseValue = CheckUtils.trimQuotes(value).toLowerCase(Locale.ENGLISH);
      if (!compliantValues.contains(quoteLessLowercaseValue)) {
        context().newIssue(this, expressionTree, MESSAGE);
      }
    }
  }

  private ExpressionTree getAssignedValue(ExpressionTree value) {
    if (value.is(Tree.Kind.VARIABLE_IDENTIFIER)) {
      Symbol valueSymbol = context().symbolTable().getSymbol(value);
      return assignmentExpressionVisitor
        .getUniqueAssignedValue(valueSymbol)
        .orElse(value);
    }
    return value;
  }
}
