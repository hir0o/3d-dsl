import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class RobotDSL {
  public static void main(String[] args) {
    Path path = Paths.get("text.txt");// 読み込むファイルを指定
    List<String> allLines = null;// ファイルの内容を行ごどに格納するリスト
    String delim = " ¥t,.¥n"; // 単語の区切り記号
    String allText = "";// 全行を連結した文字列（初期値は長さ0の文字列""）
    try {
      allLines = Files.readAllLines(path, StandardCharsets.UTF_8);// 全行を一括読み込み
    } catch (IOException e) {
    }

    for (String s : allLines) // 行末の改行も付け加えて全行連結
      allText += (s + "¥n");

    Context ct = new Context(allText, delim);// Contextオブジェクトの作成
    Prog prog = new Prog();// パーサオブジェクトを作る

    prog.parse(ct);// sに構文解析を指示（パーサメソッドを実行させる）
  }
}

// （1）Prog → Com_list [EOF]
// （2）Com_list → [Rep_com Prim_com] Com_list?
// （3）Rep_com → repeat { Com_list } [0-9]+
// （4）Prim_com → walk sw? Fnum Fnum Fnum
// | move Fnum Fnum Fnum
// （5）Fnum → [+-]?[0-9]+(.[0-9]+)?

// (1)Prog -> Com_list
class Prog {
  private Com_List clist;

  public void parse(Context ct) {
    clist = new Com_List(); // いきなり作って
    clist.parse(ct);// 構文解析を指示
  }

  void exe() {
    // エグゼキュータ
  }
}

// (2)Com_list → [Rep_com Prim_com] Com_list?
class Com_List {
  private Rep_com rcom;
  private Prim_com pcom;
  private Com_List clist;

  public void parse(Context ct) {
    if (ct.match("walk") || ct.match("move")) { // pcomだった場合
      pcom = new Prim_com();
      pcom.parse(ct);
    } else if (ct.match("repeat")) { // repeatだった場合
      rcom = new Rep_com();
      rcom.parse(ct);
    } else { // それ以外だった場合
      System.out.println("無効な構文です: " + ct.currentToken());
      System.exit(0); // 終了
    }
    ct.toNext(); // トークンを一つ進める
    if (ct.match("walk") || ct.match("move") || ct.match("repeat")) {
      clist = new Com_List();
      clist.parse(ct);
    }
  }

  void exe() {
    // エグゼキュータ
  }
}

// (3) Rep_com → repeat { Com_list } [0-9]+
class Rep_com {
  int inum = 0; // 繰り返し回数
  private Com_List clist;

  public void parse(Context ct) {
    // パーサ
  }

  void exe() {
    // エグゼキュータ
  }
}

// (4) Prim_com → walk sw? Fnum Fnum Fnum | move Fnum Fnum Fnum
class Prim_com {
  String op;
  Fnum fnum1, fnum2, fnum3; // 各命令の引数

  public void parse(Context ct) {
    op = ct.currentToken(); // 命令の種類（"walk"，”move”）をop に記録
    if (ct.match("walk")) {
      ct.toNext();
      if (ct.match("sw")) {
        // TODO: swの処理を考える
      } else {
        // 数字を記録
        fnum1 = new Fnum();
        fnum1.parse(ct);
        fnum2 = new Fnum();
        fnum2.parse(ct);
        fnum3 = new Fnum();
        fnum3.parse(ct);
      }
    }
  }

  void exe() {
    // エグゼキュータ
    // op に記録されている命令の種類に応じて処理を行う
  }
}

// (5) Fnum → [+-]?[0-9]+(.[0-9]+)?
class Fnum {
  private double val = 0; // 数値を格納

  public void parse(Context ct) {
    if (ct.currentToken() != null && ct.currentToken().matches("[+-]?[0-9]+(.[0-9]+)?")) {
      val = Double.parseDouble(ct.currentToken());
      ct.toNext(); // 次のトークンに進む
    } else {
      System.out.println("Num: 数字ではありません: " + ct.currentToken());
      System.exit(0); // 終了
    }
  }

  public double exe() {
    return val;
  }
}
