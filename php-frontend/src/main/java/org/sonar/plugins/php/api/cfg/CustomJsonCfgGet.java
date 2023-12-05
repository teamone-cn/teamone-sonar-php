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
package org.sonar.plugins.php.api.cfg;
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;


public class CustomJsonCfgGet {

  // json 中定义项目规则的key
  private static final String JSON_RULE_KEY = "ruleKey";

  // json 中定义项目规则内容
  private static final String JSON_RULE_CONTENT = "ruleContent";

  // json中定义项目规则是否激活
  private static final String JSON_RULE_ACTIVE = "ruleActive";

  private static final String REMOTE_IP = "192.168.60.25";
  private static final int REMOTE_PORT = 22;
  private static final String REMOTE_USERNAME = "sonarqube";
  private static final String REMOTE_PASSWORD = "su2JTbV#pb!BeKsH";

  private static final String GITLAB_ID_RSA="-----BEGIN RSA PRIVATE KEY-----\n" +
    "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\n" +
    "-----END RSA PRIVATE KEY-----\n";

  private static final String REMOTE_CMD = "cat /home/sonarqube/config.json";

  private static String cfgJson ;


  public static void main(String[] args) {
    System.out.println(getCustomJson());
  }

  public static String getCustomJsonString() {
    Connection conn = new Connection(REMOTE_IP, REMOTE_PORT);
    StringBuilder sb = new StringBuilder();
    //连接
    try {
      conn.connect();
      boolean isAuthed = conn.authenticateWithPublicKey(REMOTE_USERNAME,GITLAB_ID_RSA.toCharArray(),REMOTE_PASSWORD);

//      boolean isAuthed = conn.authenticateWithPassword(REMOTE_USERNAME, REMOTE_PASSWORD);
      if (isAuthed == false) throw new IOException("Authentication failed.");
      Session session = conn.openSession();
      session.execCommand(REMOTE_CMD);

      InputStream stdout = new StreamGobbler(session.getStdout());

      BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

      while (true)
      {
        String line = br.readLine();
        if (line == null)
          break;
//        System.out.println(line.trim());
        sb.append(line);
      }
    }catch (Exception e){
      e.printStackTrace();
    }

    conn.close();
    return sb.toString();
  }


  /**
   * 通过json字符串，获取到用于sonar-php的配置
   *
   * @param json
   * @return
   */
  public static HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> getCustomJson() {
//    String json = "{\"tunefabjp\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mktime\",\"split\"],\"ruleActive\":\"N\"},{\"ruleKey\":\"S2000\",\"ruleContent\":[\"xxxx\"],\"ruleActive\":\"Y\"}],\"tunefabde\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"create_function\"],\"ruleActive\":\"Y\"},{\"ruleKey\":\"S2000\",\"ruleContent\":[],\"ruleActive\":\"N\"}],\"wp-data\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mcrypt_encrypt\"],\"ruleActive\":\"Y\"}],\"wp-order\":[{\"ruleKey\":\"S2001\",\"ruleContent\":[\"mcrypt_decrypt\",\"mcrypt_encrypt\"],\"ruleActive\":\"Y\"}]}";

   if (!StringUtils.isNotBlank(cfgJson)){
     cfgJson = getCustomJsonString();
   }

    // 构建映射关系，项目内容
    HashSet<String> contents;

    // 构建映射关系，对应的顺序为：项目规则启用标志 -- 项目内容
    HashMap<String, HashSet<String>> activeContentMap;

    // 构建映射关系，对应的顺序为：项目规则key --- 项目规则启用标志 -- 项目内容
    HashMap<String, HashMap<String, HashSet<String>>> ruleKeyActiveContentMap;

    // 构建映射关系，对应的顺序为：项目名称 -- 项目规则key --- 项目规则启用标志 -- 项目内容
    HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> projectRuleMap = new HashMap<>();

    // 获取到jsonObject
    JSONObject jsonObject = JSONObject.parseObject(cfgJson);

    // 获取最外层的key
    Iterator<String> keySet = jsonObject.keySet().iterator();

    while (keySet.hasNext()) {
      ruleKeyActiveContentMap = new HashMap<>();

      // 对应项目的项目名称，具体为：
      // 1、主题项目的 /wp-content/themes/xxxx 的具体主题的名称
      // 2、插件项目的 xxxx.php 的具体插件的名称
      // 3、内部项目的 /xxxx 的具体项目的空目录的名称
      String projectName = keySet.next();

      // 获取到每个项目的具体的配置
      JSONArray jsonArray = jsonObject.getJSONArray(projectName);

      for (int i = 0; i < jsonArray.size(); i++) {
        activeContentMap = new HashMap<>();

        contents = new HashSet<>();
        JSONObject projectRule = jsonArray.getJSONObject(i);
        // 对应项目规则下的key，可以对于到 sonar-php 的key，也是页面上有展示的 key
        String projectRuleKey = projectRule.getString(JSON_RULE_KEY);
        // 对应项目规则下具体的内容，例如可以进行 包含这些字段，不包含这些字段，这里就是用来定义这些字段的地方
        JSONArray projectRuleContents = projectRule.getJSONArray(JSON_RULE_CONTENT);
        // 对应项目规则是否启用，Y 为启用，N 为未启用
        String projectActiveFlag = projectRule.getString(JSON_RULE_ACTIVE);

        for (int j = 0; j < projectRuleContents.size(); j++) {
          // 获取到每个规则具体内容
          String projectRuleContent = projectRuleContents.getString(j);
          contents.add(projectRuleContent);
        }
        activeContentMap.put(projectActiveFlag, contents);
        ruleKeyActiveContentMap.put(projectRuleKey, activeContentMap);
      }
      projectRuleMap.put(projectName, ruleKeyActiveContentMap);
    }

    return projectRuleMap;
  }
}


