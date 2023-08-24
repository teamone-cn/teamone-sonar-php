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
package org.sonar.plugins.php.api.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import org.sonar.php.tree.visitors.PHPCheckContext;
import org.sonar.plugins.php.api.cfg.CustomJsonCfgGet;
import org.sonar.plugins.php.api.symbols.SymbolTable;
import org.sonar.plugins.php.api.tree.CompilationUnitTree;
import org.sonar.plugins.php.api.tree.Tree.Kind;

public abstract class PHPSubscriptionCheck extends PHPTreeSubscriber implements PHPCheck {

  private CheckContext context;

  protected HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> customCfg;

  // 所有文件（带目录）的名称
  protected static HashSet<String> fileNames;

  @Override
  public abstract List<Kind> nodesToVisit();

  @Override
  public CheckContext context() {
    return context;
  }

  @Override
  public void init() {
    // 初始化配置
    this.customCfg= CustomJsonCfgGet.getCustomJson();
  }

  @Override
  public final List<PhpIssue> analyze(PhpFile file, CompilationUnitTree tree) {
    return analyze(new PHPCheckContext(file, tree, null));
  }

  @Override
  public List<PhpIssue> analyze(PhpFile file, CompilationUnitTree tree, SymbolTable symbolTable) {
    return analyze(new PHPCheckContext(file, tree, null, symbolTable));
  }

  @Override
  public final List<PhpIssue> analyze(CheckContext context) {
    this.context = context;
    scanTree(context.tree());
    return context().getIssues();
  }

  /**
   * 自定义方法，从 PHPSensor 获取到所有文件的名称
   * @param fileNameSet
   */
  public static void setFileNameSet(HashSet<String> fileNameSet){
    fileNames=fileNameSet;
  }
}
