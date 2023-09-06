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
  private static final String MESSAGE_CUSTOM = "(Teamone) \"%s()\" 方法已删除，禁止使用";

  private static final ImmutableSet<String> SEARCHING_STRING_FUNCTIONS = ImmutableSet.of(
    "call_user_method","call_user_method_array","define_syslog_variables","session_register","session_unregister",
    "session_is_registered","mcrypt_generic_end","ereg","eregi","eregi_replace","set_magic_quotes_runtime",
    "set_socket_blocking","split","spliti","sql_regcase","import_request_variables","ereg_replace","mcrypt_ecb",
    "mcrypt_cbc","mcrypt_cfb","mcrypt_ofb","mcrypt_get_key_size","mcrypt_get_block_size","mcrypt_get_cipher_name",
    "mcrypt_create_iv","mcrypt_list_algorithms","mcrypt_list_modes","mcrypt_get_iv_size","mcrypt_encrypt",
    "mcrypt_decrypt","mcrypt_module_open","mcrypt_generic_init","mcrypt_generic","mdecrypt_generic",
    "mcrypt_generic_end","mcrypt_generic_deinit","mcrypt_enc_self_test","mcrypt_enc_is_block_algorithm_mode",
    "mcrypt_enc_is_block_algorithm","mcrypt_enc_is_block_mode","mcrypt_enc_get_block_size","mcrypt_enc_get_key_size",
    "mcrypt_enc_get_supported_key_sizes","mcrypt_enc_get_iv_size","mcrypt_enc_get_algorithms_name",
    "mcrypt_enc_get_modes_name","mcrypt_module_self_test","mcrypt_module_is_block_algorithm_mode",
    "mcrypt_module_is_block_algorithm","mcrypt_module_is_block_mode","mcrypt_module_get_algo_block_size",
    "mcrypt_module_get_algo_key_size","mcrypt_module_get_supported_key_sizes","mcrypt_module_close","mysql_connect",
    "mysql_pconnect","mysql_close","mysql_select_db","mysql_query","mysql_unbuffered_query","mysql_db_query",
    "mysql_list_dbs","mysql_list_tables","mysql_list_fields","mysql_list_processes","mysql_error","mysql_errno",
    "mysql_affected_rows","mysql_insert_id","mysql_result","mysql_num_rows","mysql_num_fields","mysql_fetch_row",
    "mysql_fetch_array","mysql_fetch_assoc","mysql_fetch_object","mysql_data_seek","mysql_fetch_lengths",
    "mysql_fetch_field","mysql_field_seek","mysql_free_result","mysql_field_name","mysql_field_table",
    "mysql_field_len","mysql_field_type","mysql_field_flags","mysql_escape_string","mysql_real_escape_string",
    "mysql_stat","mysql_thread_id","mysql_client_encoding","mysql_ping","mysql_get_client_info",
    "mysql_get_host_info","mysql_get_proto_info","mysql_get_server_info","mysql_info","mysql_set_charset",
    "mysql","mysql_fieldname","mysql_fieldtable","mysql_fieldlen","mysql_fieldtype","mysql_fieldflags",
    "mysql_selectdb","mysql_freeresult","mysql_numfields","mysql_numrows","mysql_listdbs","mysql_listtables",
    "mysql_listfields","mysql_db_name","mysql_dbname","mysql_tablename","mysql_table_name");


  @Override
  protected ImmutableSet<String> functionNames() {
    return ImmutableSet.<String>builder()
      // 这里需要添加自定义的方法，才会生效
      .addAll(SEARCHING_STRING_FUNCTIONS)
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

