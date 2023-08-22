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
package org.sonar.php.checks.security;

import com.google.common.collect.ImmutableSet;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.FunctionUsageCheck;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;

@Rule(key = "S4818")
public class SocketUsageCheck extends FunctionUsageCheck {

  private static final String MESSAGE = "Make sure that sockets are used safely here.";

  private static final ImmutableSet<String> FUNCTION_NAMES = ImmutableSet.of(
    "socket_create",
    "socket_create_listen",
    "socket_addrinfo_bind",
    "socket_addrinfo_connect",
    "socket_create_pair",
    "fsockopen",
    "pfsockopen",
    "stream_socket_server",
    "stream_socket_client",
    "stream_socket_pair");

  @Override
  protected ImmutableSet<String> functionNames() {
    return FUNCTION_NAMES;
  }

  @Override
  protected void createIssue(FunctionCallTree tree) {
    context().newIssue(this, tree, MESSAGE);
  }

}
