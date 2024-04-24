ThisBuild / publishMavenStyle := true

ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / Test / publishArtifact := false

ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / pomExtra :=
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

Global / useGpgPinentry := true

Global / pgpSigningKey := Some("slinky-publishing-bot")
