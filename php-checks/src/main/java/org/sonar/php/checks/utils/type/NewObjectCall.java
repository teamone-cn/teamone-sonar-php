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
package org.sonar.php.checks.utils.type;

import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.declaration.NamespaceNameTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.NewExpressionTree;

public class NewObjectCall extends TreeKindPredicate<NewExpressionTree> {

  private final String type;

  public NewObjectCall(String type) {
    super(Tree.Kind.NEW_EXPRESSION);
    this.type = type;
  }

  @Override
  protected boolean matches(TreeValues possibleValues, NewExpressionTree tree) {
    ExpressionTree expression = tree.expression();
    if (expression.is(Tree.Kind.FUNCTION_CALL)) {
      ExpressionTree callee = ((FunctionCallTree) expression).callee();
      return callee.is(Tree.Kind.NAMESPACE_NAME) && type.equalsIgnoreCase(((NamespaceNameTree) callee).qualifiedName());
    }
    return false;
  }

}
