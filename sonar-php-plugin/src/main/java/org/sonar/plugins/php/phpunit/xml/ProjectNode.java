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
package org.sonar.plugins.php.phpunit.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProjectNode represent the analyzed project in the PhpUnit coverage report file.
 */
public class ProjectNode {

  /**
   * The project files.
   */
  private List<FileNode> files = new ArrayList<>();

  /**
   * The project files.
   */
  private List<PackageNode> packages = new ArrayList<>();

  /**
   * The project name.
   */
  private String name;

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the files.
   *
   * @return the files
   */
  public List<FileNode> getFiles() {
    return files;
  }

  /**
   * Gets the packages.
   *
   * @return the packages
   */
  public List<PackageNode> getPackages() {
    return packages;
  }
}
