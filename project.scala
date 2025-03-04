// 上記のGraalVM関連の設定を一時的にコメントアウト
// //> using packaging.graalvmArgs --static
// //> using packaging.graalvmArgs --no-fallback
//> using toolkit default
//> using dep "com.softwaremill.sttp.client4::core:4.0.0-M20"
//> using dep "com.lihaoyi::upickle:4.0.2"
//> using dep "com.google.apis:google-api-services-calendar:v3-rev20230707-2.0.0"
//> using dep "com.google.oauth-client:google-oauth-client-jetty:1.34.1"
//> using dep "com.google.auth:google-auth-library-oauth2-http:1.19.0"
// 一時的にjvmを使用
//> using jvm 11
//> using packaging.packageType "assembly"
//> using mainClass "main"
