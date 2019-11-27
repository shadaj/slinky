publishMavenStyle in ThisBuild := true

pomIncludeRepository in ThisBuild := { _ => false }

publishArtifact in Test in ThisBuild := false

publishTo in ThisBuild := sonatypePublishToBundle.value

pomExtra in ThisBuild :=
  <url>https://github.com/shadaj/slinky</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/shadaj/slinky.git</url>
      <connection>https://github.com/shadaj/slinky.git</connection>
    </scm>
    <developers>
      <developer>
        <id>shadaj</id>
        <name>Shadaj Laddad</name>
        <url>http://shadaj.me</url>
      </developer>
    </developers>
