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
package org.sonar.php.parser.expression;

import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;

import static org.sonar.php.utils.Assertions.assertThat;

public class AssignmentExpressionTest {

  @Test
  public void test() {
    assertThat(PHPLexicalGrammar.ASSIGNMENT_EXPRESSION)
      .matches("$a = $b")
      .matches("$a **= $b")
      .matches("$a *= $b")
      .matches("$a /= $b")
      .matches("$a %= $b")
      .matches("$a += $b")
      .matches("$a -= $b")
      .matches("$a <<= $b")
      .matches("$a >>= $b")
      .matches("$a &= $b")
      .matches("$a ^= $b")
      .matches("$a |= $b")
      .matches("$a -= $b")
      .matches("$a -= $b")

      .matches("$a =& $b")
      .matches("$a =& new X")
      .matches("$a =& myFunction()")

      .matches("$array = [1, 2]")
      .matches("$array = [1, 2, 3, [3, 4]]")
      .matches("$a = ['one' => 1, 'two' => 2]")
      .matches("[$a, $b] = $array")
      .matches("list($a, $b) = $array")
      .matches("[$a, &$b] = $array")
      .matches("list($a, &$b) = $array")
      .matches("$array = [1, 2, 3, [3, 4]]")
      .matches("$bar = [\"bar\" => 3][\"bar\"]")

      .notMatches("[$a, &&$b] = $array")
      .notMatches("[$a, &] = $array")
      .notMatches("[] = $array")
      .notMatches("list($a, &&$b) = $array")
      .notMatches("list($a, &) = $array")

      .notMatches("$a =& $b * $c")

      .matches("$var = function () {}")
      .matches("$a = $b = 1")
      .matches("$a ??= $b")
      .matches("$a ??= myFunction()");
  }
}
