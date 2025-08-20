# HackMD CLI in Java 開発計画

## プロジェクト目標

Go言語で実装されたHackMD APIクライアントCLIを、JavaおよびQuarkusフレームワークを用いて再実装します。Quarkusの持つ強力なDI機能、RESTクライアント、Picocliによるコマンドラインアプリケーション構築の仕組みを活用し、メンテナンス性が高く、ネイティブ実行ファイルにもビルド可能なアプリケーションを目指します。

---

## 開発計画

#### ステップ1: 依存関係の追加

`pom.xml`に必要なライブラリを追加します。

1.  **Quarkus REST Client:** HackMD APIと通信するために使用します。
    *   `quarkus-rest-client-jackson`
2.  **Quarkus Picocli:** 高機能なコマンドラインインターフェースを構築するために使用します。
    *   `quarkus-picocli`

#### ステップ2: 設定の構成

`src/main/resources/application.properties` に、アプリケーションの基本設定を追加します。

*   HackMD APIのベースURL
*   APIトークン（環境変数や `.env` ファイルから読み込むのが望ましい）

```properties
# application.properties
hackmd.api.url=https://api.hackmd.io/v1
hackmd.api.token=${HACKMD_API_TOKEN}
```

#### ステップ3: APIクライアントの作成

Quarkus REST Clientを使い、HackMD APIのエンドポイントを定義するJavaインターフェースを作成します。

*   `src/main/java/io/github/yuokada/quarkus/HackMdApi.java`
    *   ノートをリストする `getNotes()` メソッド
    *   新しいノートを作成する `createNote()` メソッド
    *   APIトークンをヘッダーに付与するための `@HeaderParam` アノテーション

#### ステップ4: データモデルの作成

APIから返されるJSONデータをマッピングするためのJavaクラス（POJOまたはRecord）を作成します。

*   `src/main/java/io/github/yuokada/quarkus/model/Note.java`
    *   `id`, `title`, `content`, `tags` などのフィールドを持つ

#### ステップ5: コマンドの実装

`picocli` を利用して、CLIのコマンドを実装します。既存の `GreetingCommand.java` は削除または修正します。

1.  **メインコマンド (`HackmdCommand.java`):**
    *   すべてのサブコマンド（`list`, `create`など）を束ねる親コマンド。

2.  **一覧表示コマンド (`ListCommand.java`):**
    *   `/notes` エンドポイントを呼び出し、ノートの一覧を整形して表示します。

3.  **新規作成コマンド (`CreateCommand.java`):**
    *   `/notes` エンドポイントにPOSTリクエストを送信し、新しいノートを作成します。引数でタイトルや内容を受け取れるようにします。

#### ステップ6: サービスレイヤーの導入

コマンドクラスからAPIクライアントを直接呼び出すのではなく、ビジネスロジックをカプセル化するサービスクラスを導入します。

*   `src/main/java/io/github/yuokada/quarkus/HackMdService.java`
    *   `@ApplicationScoped` アノテーションを付与します。
    *   APIクライアントをインジェクト（`@Inject`）します。
    *   各コマンドから呼び出されるメソッド（`listNotes()`, `createNewNote()`など）を実装します。

#### ステップ7: ビルドとテスト

*   `mvn quarkus:dev` で開発モードを起動し、動作確認を行います。
*   `mvn package` で実行可能なJARファイルをビルドします。
*   （オプション）`mvn package -Pnative` でネイティブ実行ファイルをビルドし、高速な起動を確認します。
