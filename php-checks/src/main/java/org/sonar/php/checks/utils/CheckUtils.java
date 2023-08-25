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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.php.tree.impl.PHPTree;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.FunctionDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.FunctionTree;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.NamespaceNameTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.LiteralTree;
import org.sonar.plugins.php.api.tree.expression.MemberAccessTree;
import org.sonar.plugins.php.api.tree.expression.NameIdentifierTree;
import org.sonar.plugins.php.api.tree.expression.ParenthesisedExpressionTree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.tree.lexical.SyntaxTrivia;
import org.sonar.plugins.php.api.tree.statement.ForStatementTree;
import org.sonar.plugins.php.api.visitors.PhpFile;

public final class CheckUtils {

  private static final String CUSTOM_RULE_ACTIVE_FLAG_N = "N";

  private static final String CUSTOM_RULE_ACTIVE_FLAG_Y = "Y";

  private static final String CUSTOM_RULE_FIRST_SPLIT = "/";

  private static final String CUSTOM_RULE_SECOND_SPLIT = "\\.";

  private static final Kind[] FUNCTION_KINDS_ARRAY = {
    Kind.METHOD_DECLARATION,
    Kind.FUNCTION_DECLARATION,
    Kind.FUNCTION_EXPRESSION,
    Kind.ARROW_FUNCTION_EXPRESSION};

  public static final ImmutableList<Kind> FUNCTION_KINDS = ImmutableList.copyOf(FUNCTION_KINDS_ARRAY);

  public static final ImmutableMap<String, String> SUPERGLOBALS_BY_OLD_NAME = ImmutableMap.<String, String>builder()
    .put("$HTTP_SERVER_VARS", "$_SERVER")
    .put("$HTTP_GET_VARS", "$_GET")
    .put("$HTTP_POST_VARS", "$_POST")
    .put("$HTTP_POST_FILES", "$_FILES")
    .put("$HTTP_SESSION_VARS", "$_SESSION")
    .put("$HTTP_ENV_VARS", "$_ENV")
    .put("$HTTP_COOKIE_VARS", "$_COOKIE").build();

  public static final ImmutableSet<String> SUPERGLOBALS = ImmutableSet.of(
    "$GLOBALS", "$_SERVER", "$_GET", "$_POST", "$_FILES", "$_COOKIE", "$_SESSION", "$_REQUEST", "$_ENV");

  private CheckUtils() {
  }

  public static boolean isFunction(Tree tree) {
    return tree.is(FUNCTION_KINDS_ARRAY);
  }

  /**
   * Returns function or method's name, or "expression" if the given node is a function expression.
   *
   * @param functionDec FUNCTION_DECLARATION, METHOD_DECLARATION or FUNCTION_EXPRESSION
   * @return name of function or "expression" if function expression
   */
  public static String getFunctionName(FunctionTree functionDec) {
    if (functionDec.is(Kind.FUNCTION_DECLARATION)) {
      return "\"" + ((FunctionDeclarationTree) functionDec).name().text() + "\"";
    } else if (functionDec.is(Kind.METHOD_DECLARATION)) {
      return "\"" + ((MethodDeclarationTree) functionDec).name().text() + "\"";
    }
    return "expression";
  }

  /**
   * @return Returns function or static method's name, like "f" or "A::f". Warning, use case insensitive comparison of the result.
   */
  @Nullable
  public static String getFunctionName(FunctionCallTree functionCall) {
    return nameOf(functionCall.callee());
  }

  /**
   * @return Returns function or static method's lower case name, like "f" or "a::f".
   */
  @Nullable
  public static String getLowerCaseFunctionName(FunctionCallTree functionCall) {
    String name = getFunctionName(functionCall);
    return name != null ? name.toLowerCase(Locale.ROOT) : null;
  }

  public static Set<String> lowerCaseSet(String... names) {
    return Arrays.stream(names).map(name -> name.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
  }

  /**
   * @return Returns the name of a tree.
   */
  @Nullable
  public static String nameOf(Tree tree) {
    if (tree.is(Tree.Kind.NAMESPACE_NAME)) {
      return ((NamespaceNameTree) tree).qualifiedName();
    } else if (tree.is(Tree.Kind.NAME_IDENTIFIER)) {
      return ((NameIdentifierTree) tree).text();
    } else if (tree.is(Tree.Kind.CLASS_MEMBER_ACCESS)) {
      MemberAccessTree memberAccess = (MemberAccessTree) tree;
      String className = nameOf(memberAccess.object());
      String memberName = nameOf(memberAccess.member());
      if (className != null && memberName != null) {
        return className + "::" + memberName;
      }
    }
    return null;
  }

  /**
   * Return whether the method is overriding a parent method or not.
   *
   * @param declaration METHOD_DECLARATION
   * @return true if method has tag "@inheritdoc" in it's doc comment.
   */
  public static boolean isOverriding(MethodDeclarationTree declaration) {
    for (SyntaxTrivia comment : ((PHPTree) declaration).getFirstToken().trivias()) {
      if (StringUtils.containsIgnoreCase(comment.text(), "@inheritdoc")) {
        return true;
      }
    }
    return false;
  }

  public static boolean isExitExpression(FunctionCallTree functionCallTree) {
    String callee = functionCallTree.callee().toString();
    return "die".equalsIgnoreCase(callee) || "exit".equalsIgnoreCase(callee);
  }

  public static boolean hasModifier(List<SyntaxToken> modifiers, String toFind) {
    for (SyntaxToken modifier : modifiers) {
      if (modifier.text().equalsIgnoreCase(toFind)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isClosingTag(SyntaxToken token) {
    if (token.is(Kind.INLINE_HTML_TOKEN)) {
      String text = token.text().trim();
      return "?>".equals(text) || "%>".equals(text);
    }
    return false;
  }

  public static Stream<String> lines(PhpFile file) {
    return new BufferedReader(new StringReader(file.contents())).lines();
  }

  public static ExpressionTree skipParenthesis(ExpressionTree expr) {
    if (expr.is(Tree.Kind.PARENTHESISED_EXPRESSION)) {
      return skipParenthesis(((ParenthesisedExpressionTree) expr).expression());
    }
    return expr;
  }

  @Nullable
  public static ExpressionTree getForCondition(ForStatementTree tree) {
    if (tree.condition().isEmpty()) {
      return null;
    }
    // in a loop, all conditions are evaluated but only the last one is used as the result
    return tree.condition().get(tree.condition().size() - 1);
  }


  public static String trimQuotes(String value) {
    if (value.length() > 1 && (value.startsWith("'") || value.startsWith("\""))) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }

  public static String trimQuotes(LiteralTree literalTree) {
    if (literalTree.is(Kind.REGULAR_STRING_LITERAL)) {
      String value = literalTree.value();
      return value.substring(1, value.length() - 1);
    }
    throw new IllegalArgumentException("Cannot trim quotes from non-string literal");
  }

  /**
   * <a href="http://php.net/manual/en/language.types.boolean.php">PHP boolean</a>
   *
   * @param tree
   * @return true if {@code tree} represents false boolean value
   */
  public static boolean isFalseValue(ExpressionTree tree) {
    if (tree.is(Tree.Kind.BOOLEAN_LITERAL, Kind.NUMERIC_LITERAL)) {
      String value = ((LiteralTree) tree).value();
      return value.equalsIgnoreCase("false")
        || value.equals("0")
        || value.equals("0.0");
    }
    if (tree.is(Kind.REGULAR_STRING_LITERAL)) {
      String value = trimQuotes(((LiteralTree) tree).value());
      return value.isEmpty() || value.equals("0");
    }
    return tree.is(Kind.NULL_LITERAL);
  }

  /**
   * @see #isFalseValue(ExpressionTree)
   */
  public static boolean isTrueValue(ExpressionTree tree) {
    return tree.is(Kind.BOOLEAN_LITERAL, Kind.NUMERIC_LITERAL, Kind.REGULAR_STRING_LITERAL, Kind.NULL_LITERAL)
      && !isFalseValue(tree);
  }

  public static boolean isStringLiteralWithValue(@Nullable Tree tree, String s) {
    return tree != null && tree.is(Kind.REGULAR_STRING_LITERAL) && s.equalsIgnoreCase(trimQuotes((LiteralTree) tree));
  }

  public static boolean isNullOrEmptyString(ExpressionTree tree) {
    if (tree.is(Kind.NULL_LITERAL)) {
      return true;
    }
    if (tree.is(Tree.Kind.REGULAR_STRING_LITERAL)) {
      String value = CheckUtils.trimQuotes(((LiteralTree) tree).value());
      return value.trim().isEmpty();
    }
    return false;
  }

  public static HashSet<String> getRulesContents(HashSet<String> fileNames, String key, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> cfg) {
    // 先拆解每次获取到的fileName的目录和文件到集合中
    // 类似 /wp-content/themes/tunefabjp/xxxx.php
    // 拆解后变为 {"wp-content","themes","tunefabjp","xxxx"}
    ArrayList<String> fileNameList = new ArrayList<>();
    for (String fileName : fileNames) {
      if (StringUtils.isNotBlank(fileName)) {
        for (String fileSplitName : fileName.split(CUSTOM_RULE_FIRST_SPLIT)) {
          if (StringUtils.isNotBlank(fileSplitName)) {
            fileNameList.addAll(Arrays.asList(fileSplitName.split(CUSTOM_RULE_SECOND_SPLIT)));
          }
        }
      }
    }

    // 遍历最外层的项目名称，获取到对应的HashMap配置
    String projectName = "";
    Iterator<String> cfgIterator = cfg.keySet().iterator();
    while (cfgIterator.hasNext()) {
      String curProjectName = cfgIterator.next();
      if (fileNameList.contains(curProjectName)) {
        projectName = curProjectName;
      }
    }

    HashSet<String> contents = null;
    HashMap<String, HashSet<String>> activeJudge = null;

    // 逐层遍历获取到最后的装有实际配置的 HashSet 并返回
    if (StringUtils.isNotBlank(projectName)) {
      activeJudge = cfg.get(projectName).get(key);
      if (null != activeJudge) {
        Iterator<String> iterator = activeJudge.keySet().iterator();
        while (iterator.hasNext()) {
          if (iterator.next().equals(CUSTOM_RULE_ACTIVE_FLAG_N)) {
            return new HashSet<>();
          }
        }
      }
    }
    if (null != activeJudge) {
      contents = activeJudge.get(CUSTOM_RULE_ACTIVE_FLAG_Y);
    }

    return contents;
  }


//  public static void main(String[] args) {
//
//    HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> stringHashMapHashMap = new HashMap<>();
//    HashMap<String, HashMap<String, HashSet<String>>> stringHashMapHashMap1 = new HashMap<>();
//    HashMap<String, HashSet<String>> stringHashSetHashMap = new HashMap<>();
//    HashSet<String> strings = new HashSet<>();
//    strings.add("mcrypt_decrypt");
//    strings.add("mcrypt_encrypt");
//    stringHashSetHashMap.put("Y", strings);
//    stringHashMapHashMap1.put("S2001", stringHashSetHashMap);
//    stringHashMapHashMap.put("tunefabjp", stringHashMapHashMap1);
//
//    HashSet<String> fileNames = new HashSet<>();
//    fileNames.add("/wp-content/ccc/tunefabjp/aaa.php");
//    fileNames.add("/wp-content/ccc/tunefabjp/bbb.php");
//
//
////    System.out.println(judgeRulesBelongs(fileName,stringHashMapHashMap));
//    System.out.println(getRulesContents(fileNames, "S2001", stringHashMapHashMap));
//
//  }


}
