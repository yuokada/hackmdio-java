package io.github.yuokada.quarkus.model;

// {
//  "id": "qVIt9eQfTtShptZWUrNcXw",
//  "title": "Remove gatling-picatinny and Run another test",
//  "tags": [
//    "gatling",
//    "td"
//  ],
//  "createdAt": 1723685238684,
//  "titleUpdatedAt": 1723692520478,
//  "tagsUpdatedAt": 1723692520477,
//  "publishType": "view",
//  "publishedAt": null,
//  "permalink": null,
//  "publishLink": "https://hackmd.io/@uokada/HJJYtRqc0",
//  "shortId": "HJJYtRqc0",
//  "content": "# Remove gatling-picatinny and Run another test\n###### tags: `gatling`, `td`\n\n## OSS\n\n[Tinkoff/gatling\\-picatinny: Library with a bunch of usefull functions that extend Gatling DSL and make your performance better](https://github.com/Tinkoff/gatling-picatinny)\n\n## Pull request to remove it\n\nhttps://github.com/treasure-data/presto-sql-checker/pull/416/files\n\n\n```diff\ndiff --git a/build.sbt b/build.sbt\nindex 16ded07..7b94d54 100644\n--- a/build.sbt\n+++ b/build.sbt\n@@ -7,7 +7,7 @@ val TD_MAVEN_SNAPSHOT_REPO       = \"snapshot\" at \"https://treasuredata.jfrog.io/\n val TD_MAVEN_REPO_LOCAL          = \"release-local\" at \"https://treasuredata.jfrog.io/treasuredata/libs-release-local\"\n val TD_MAVEN_SNAPSHOT_REPO_LOCAL = \"snapshot-local\" at \"https://treasuredata.jfrog.io/treasuredata/libs-snapshot-local\"\n \n-name                       := \"presto-sql-checker\"\n+name := \"presto-sql-checker\"\n \n val SCALA_VERSION = \"3.3.3\"\n (ThisBuild / scalaVersion) := SCALA_VERSION\n@@ -19,7 +19,7 @@ val TRINO_VERSION = \"423\"\n // See https://treasure-data.atlassian.net/browse/PTD-3152\n val HIVE_VERSION     = \"4.0.0-PTD-0.0.1\"\n val AIRFRAME_VERSION = \"24.5.0\"\n-val GATLING_VERSION  = \"3.11.2\"\n+val GATLING_VERSION  = \"3.11.5\"\n \n val artifactoryCredential = Credentials(\n   \"Artifactory Realm\",\n@@ -140,8 +140,6 @@ lazy val gatling = (project in file(\"gatling\"))\n       \"org.wvlet.airframe\"      %% \"airframe-sql\"              % AIRFRAME_VERSION,\n       \"io.gatling.highcharts\"    % \"gatling-charts-highcharts\" % GATLING_VERSION,\n       \"io.gatling\"               % \"gatling-test-framework\"    % GATLING_VERSION,\n-      // TODO: gatling-picatinny has been archived. We should consider stop using it\n-      \"ru.tinkoff\"              %% \"gatling-picatinny\"         % \"0.14.0\" cross(CrossVersion.for3Use2_13),\n       \"io.trino\"                 % \"trino-jdbc\"                % TRINO_VERSION,\n       \"org.apache.logging.log4j\" % \"log4j-slf4j-impl\"          % \"2.23.1\"\n     ),\n@@ -160,19 +158,19 @@ lazy val server = Project(id = \"server\", base = file(\"server\"))\n       \"org.wvlet.airframe\" %% \"airframe-metrics\"    % AIRFRAME_VERSION,\n       \"org.wvlet.airframe\" %% \"airframe-http-netty\" % AIRFRAME_VERSION,\n       \"org.wvlet.airframe\" %% \"airframe-fluentd\"    % AIRFRAME_VERSION,\n-      \"io.trino\"           % \"trino-parser\"         % TRINO_VERSION excludeAll (\n+      \"io.trino\"            % \"trino-parser\"        % TRINO_VERSION excludeAll (\n         ExclusionRule(organization = \"com.google.guava\", name = \"guava\")\n       ),\n       \"org.apache.logging.log4j\" % \"log4j-slf4j-impl\" % \"2.23.1\",\n-      \"org.apache.hive\" % \"hive-parser\" % HIVE_VERSION excludeAll (vulnerableDeps:_*),\n+      \"org.apache.hive\"          % \"hive-parser\"      % HIVE_VERSION excludeAll (vulnerableDeps: _*),\n       // Use non-vulnerable Hadoop version\n-      \"org.apache.hadoop\" % \"hadoop-common\" % \"3.3.2-PTD-0.3\" excludeAll (vulnerableDeps:_*),\n+      \"org.apache.hadoop\" % \"hadoop-common\" % \"3.3.2-PTD-0.3\" excludeAll (vulnerableDeps: _*),\n       // For vulnerable dependencies\n-      \"org.xerial.snappy\" % \"snappy-java\" % \"1.1.10.5\",\n-      \"com.treasuredata.client\" % \"td-client\" % \"1.1.0\",\n-      \"org.codehaus.jettison\" % \"jettison\" % \"1.5.4\",\n-      \"org.apache.commons\" % \"commons-compress\" % \"1.26.0\",\n-      \"org.apache.commons\" % \"commons-configuration2\" % \"2.10.1\"\n+      \"org.xerial.snappy\"       % \"snappy-java\"            % \"1.1.10.5\",\n+      \"com.treasuredata.client\" % \"td-client\"              % \"1.1.0\",\n+      \"org.codehaus.jettison\"   % \"jettison\"               % \"1.5.4\",\n+      \"org.apache.commons\"      % \"commons-compress\"       % \"1.26.0\",\n+      \"org.apache.commons\"      % \"commons-configuration2\" % \"2.10.1\"\n     )\n   ).dependsOn(core)\n \n@@ -184,7 +182,7 @@ val vulnerableDeps: Seq[ExclusionRule] = Seq(\n   ExclusionRule(organization = \"org.apache.hadoop\", name = \"hadoop-auth\"),\n   // log4j 1.2.17 has security vulnerabilities\n   ExclusionRule(organization = \"log4j\", name = \"log4j\"),\n-  ExclusionRule(organization = \"org.slf4j\",    name = \"slf4j-log4j12\"),\n+  ExclusionRule(organization = \"org.slf4j\", name = \"slf4j-log4j12\"),\n   ExclusionRule(organization = \"org.pentaho\", name = \"pentaho-aggdesigner-algorithm\"),\n   // Hive2 -> calcite -> avatica dependency wrongly embeds com.fasterxml.databind.JsonMappingException class,\n   // and it causes NoSuchMethodError when initializing jackson-databind-scala module.\ndiff --git a/gatling/src/it/scala/com/treasuredata/presto/sqlchecker/gatling/ValidatorSimulation.scala b/gatling/src/it/scala/com/treasuredata/presto/sqlchecker/gatling/ValidatorSimulation.scala\nindex 26b4c24..8f05646 100644\n--- a/gatling/src/it/scala/com/treasuredata/presto/sqlchecker/gatling/ValidatorSimulation.scala\n+++ b/gatling/src/it/scala/com/treasuredata/presto/sqlchecker/gatling/ValidatorSimulation.scala\n@@ -1,27 +1,15 @@\n package com.treasuredata.presto.sqlchecker.gatling\n \n-import io.gatling.core.Predef._\n-import io.gatling.core.config.GatlingConfiguration\n+import com.typesafe.config.{Config, ConfigFactory}\n+import io.gatling.core.Predef.*\n import io.gatling.core.feeder.{FeederBuilderBase, FileBasedFeederBuilder}\n import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}\n-import io.gatling.http.Predef._\n+import io.gatling.http.Predef.*\n import io.gatling.jdbc.Predef.jdbcFeeder\n-import ru.tinkoff.gatling.config.SimulationConfig.{baseUrl, getIntParam, getStringParam}\n import wvlet.airframe.codec.PrimitiveCodec\n \n import java.sql.SQLException\n-import scala.concurrent.duration._\n-\n-case class BenchConfig(\n-    url: String,\n-    durationSec: Int = 60\n-)\n-\n-object BenchConfig {\n-  def apply(config: GatlingConfiguration): BenchConfig = {\n-    BenchConfig(url = baseUrl)\n-  }\n-}\n+import scala.concurrent.duration.*\n \n object FeederUtil {\n   private def readApiToken(): String = {\n@@ -57,10 +45,10 @@ object FeederUtil {\n       }.random.circular\n   }\n \n-  def DynamicTDQueryFeeder(): FeederBuilderBase[Any] = {\n+  def DynamicTDQueryFeeder(config: Config): FeederBuilderBase[Any] = {\n     val apiToken = readApiToken()\n-    val jdbcUrl  = getStringParam(\"jdbcUrl\")\n-    val query    = getStringParam(\"queryForFeeder\")\n+    val jdbcUrl  = config.getString(\"jdbcUrl\")\n+    val query    = config.getString(\"queryForFeeder\")\n \n     val codec = PrimitiveCodec.StringCodec\n     jdbcFeeder(jdbcUrl, apiToken, \"\", query)\n@@ -73,17 +61,17 @@ object FeederUtil {\n }\n \n class ValidatorSimulation extends Simulation {\n-  private val conf       = BenchConfig(configuration)\n-  private val activeUser = getIntParam(\"activeUser\")\n+  private val config    = ConfigFactory.load(\"simulation.conf\")\n+  private val activeUser = config.getInt(\"activeUser\")\n   private val httpProtocol = http\n-    .baseUrl(conf.url)\n+    .baseUrl(config.getString(\"baseUrl\"))\n     .acceptHeader(\"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\")\n     .contentTypeHeader(\"application/json\")\n     .userAgentHeader(\"gatling in presto-sql-checker\")\n \n   private val feeder =\n     try {\n-      FeederUtil.DynamicTDQueryFeeder()\n+      FeederUtil.DynamicTDQueryFeeder(config)\n     } catch {\n       case e: SQLException => {\n         if (e.getMessage.contains(\"returned HTTP 401\")) {\n```\n\n\n## ChatGPT\n\nhttps://chatgpt.com/share/da6e367c-6590-44e3-9346-55c1fef8137c\n\n\n```yaml\n      - run:\n          name: Wait until port 8080 is in LISTEN state\n          command: |\n            for i in {1..60}; do\n              if netstat -an | grep -q 'LISTEN.*8080'; then\n                echo \"Port 8080 is listening.\"\n                break\n              fi\n              echo \"Waiting for port 8080 to be in LISTEN state...\"\n              sleep 2\n            done\n```",
//  "lastChangedAt": 1723704200160,
//  "lastChangeUser": {
//    "name": "カントク",
//    "userPath": "uokada",
//    "photo": "https://pbs.twimg.com/profile_images/1615717189/reonald_normal.jpg",
//    "biography": null
//  },
//  "userPath": "uokada",
//  "teamPath": null,
//  "readPermission": "owner",
//  "writePermission": "owner"
//}

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;

/**
 * 詳細な HackMD ノートのレスポンス。
 *
 * <p>example/note-detail.json を元に、HackMD API のレスポンス構造を表現する。</p>
 */
public record NoteDetailResponse(
    String id,
    String title,
    List<String> tags,
    @JsonProperty("createdAt")
    @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
    Instant createdAt,
    @JsonProperty("titleUpdatedAt")
    @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
    Instant titleUpdatedAt,
    @JsonProperty("tagsUpdatedAt")
    @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
    Instant tagsUpdatedAt,
    String publishType,
    @JsonProperty("publishedAt")
    @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
    Instant publishedAt,
    String permalink,
    String publishLink,
    String shortId,
    String content,
    @JsonProperty("lastChangedAt")
    @JsonDeserialize(using = EpochMillisInstantDeserializer.class)
    Instant lastChangedAt,
    @JsonProperty("lastChangeUser")
    LastChangeUser lastChangeUser,
    String userPath,
    String teamPath,
    String readPermission,
    String writePermission
) {

    public record LastChangeUser(
        String name,
        String userPath,
        String photo,
        String biography
    ) {

    }

    public Note toNote() {
        return new Note(
            this.id,
            this.title,
            this.content,
            this.tags,
            this.publishedAt,
            this.shortId,
            this.lastChangedAt
        );
    }
}
