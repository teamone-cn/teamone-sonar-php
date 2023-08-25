package org.sonar.plugins.php.api.cfg;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
      boolean isAuthed = conn.authenticateWithPassword(REMOTE_USERNAME, REMOTE_PASSWORD);
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


