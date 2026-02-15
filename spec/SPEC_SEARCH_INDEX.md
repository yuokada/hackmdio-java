## 仕様: search / index サブコマンド

### 目的
- HackMD上の自分のノートを同期し、ローカル（Couchbase Lite）に保存する。
- 保存済みノートの本文を検索できるようにする。
- search と index を別サブコマンドとして提供する。

### サブコマンド構成
- index: HackMDからノートを取得・同期し、Couchbase Liteに保存する。
- search: Couchbase Liteに保存された本文を検索する（APIは呼ばない）。

### index コマンド仕様
- 目的: ローカル（Couchbase Lite）にノートを同期・更新する。
- 処理フロー:
  - FTS インデックスを作成する（index 実行時に作成）。
  - list API でノート一覧を取得する。
  - 各ノートについて Couchbase Lite 上のドキュメントを確認する。
    - 未保存: get API で本文を取得し保存する。
    - 既存: updatedAt と downloadedAt を比較し、更新がある場合のみ再取得する。
- 保存方式（Couchbase Lite）:
  - 保存単位: ノート1件 = ドキュメント1件。
  - ドキュメントID: note.id（または shortId）。
  - 保存フィールド例:
    - id / shortId / title / content / tags
    - updatedAt（HackMDの最終更新日時）
    - downloadedAt（ローカル保存日時）
- 出力: 同期件数、更新件数、スキップ件数などのサマリを表示する。
- 進捗表示: 可能であれば、全件数に対する処理済み件数の進捗率を表示する。

### search コマンド仕様
- 目的: ローカル保存済みのノート本文を検索する。
- 入力: hackmd search <QUERY>
- 検索対象: Couchbase Lite の content フィールド。
- 検索方式: FTS を使用する（FTS インデックスを作成して検索する）。
- 出力: 表形式 or --json オプションで結果を出力する。

### 未決事項
- HackMD API の最終更新日時フィールド名。
- Couchbase Lite の DB 名・保存場所・初期化方法。
