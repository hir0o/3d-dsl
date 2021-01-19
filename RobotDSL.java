import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class RobotDSL {
  public static void main(String[] args) {
    Path path = Paths.get("text2.txt");// 読み込むファイルを指定
    List<String> allLines = null;// ファイルの内容を行ごどに格納するリスト
    String delim = " ,¥n"; // 単語の区切り記号
    String allText = "";// 全行を連結した文字列（初期値は長さ0の文字列""）
    try {
      allLines = Files.readAllLines(path, StandardCharsets.UTF_8);// 全行を一括読み込み
    } catch (IOException e) {
    }

    for (String s : allLines) // 行末の改行も付け加えて全行連結
      allText += (s + "¥n");

    Context ct = new Context(allText, delim);// Contextオブジェクトの作成
    Prog prog = new Prog();// パーサオブジェクトを作る

    // 描写
    Data data = new Data(); // データオブジェクトを作る
    JFrame jf = new JFrame("Canvas"); // フレームオブジェクトを作る
    Canvas mc = new Canvas(); // キャンバスオブジェクトを作る

    prog.parse(ct);// progに構文解析を指示（パーサメソッドを実行させる）
    prog.exe(data);

    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // クローズボタンで終了するように設定
    mc.setPreferredSize(new Dimension(1200, 1200)); // キャンバスのサイズを設定
    jf.getContentPane().add(mc); // フレームにキャンバスを貼り付け
    jf.pack(); // フレームの大きさを自動調整
    data.addCanvas(mc); // データオブジェクトにキャンバスオブジェクトを登録
    data.notifyToCanvas(); // データ準備完了をキャンパスオブジェクトに通知するよう，データオブジェクトに依頼
    jf.setVisible(true); // PC画面へ表示

  }
}

// * （1）Prog → Com_list [EOF]
// * （2）Com_list → [Rep_com Prim_com] Com_list?
// * （3）Rep_com → repeat { Com_list } [0-9]+
// * （4）Prim_com → walk sw? Fnum Fnum Fnum
// * | move Fnum Fnum Fnum
// * | reset
// * （5）Fnum → [+-]?[0-9]+(.[0-9]+)?

// * (1)Prog -> Com_list
class Prog {
  private Com_List clist;

  public void parse(Context ct) {
    clist = new Com_List(); // いきなり作って
    clist.parse(ct);// 構文解析を指示
  }

  void exe(Data data) {
    clist.exe(data);
  }
}

// * (2)Com_list → [Rep_com Prim_com] Com_list?
class Com_List {
  private Rep_com rcom;
  private Prim_com pcom;
  private Com_List clist;

  public void parse(Context ct) {
    if (ct.match("walk") || ct.match("move")) { // Prim_comだった場合
      pcom = new Prim_com();
      pcom.parse(ct);
    } else if (ct.match("repeat")) { // Rep_comだった場合
      rcom = new Rep_com();
      rcom.parse(ct);
    } else { // それ以外だった場合
      System.out.println("Com_List: 無効な構文です: " + ct.currentToken());
      System.exit(0);
    }

    // 後ろに語句がない場合は処理を終了
    if (ct.currentToken() == null) return;

    if (ct.match("walk") || ct.match("move") || ct.match("repeat")) {
      clist = new Com_List();
      clist.parse(ct);
    }
  }

  void exe(Data data) {
    if(pcom != null)
      pcom.exe(data);
    if(rcom != null)
      rcom.exe(data);
    if(clist != null)
      clist.exe(data);
  }
}

// * (3) Rep_com → repeat { Com_list } [0-9]+
class Rep_com {
  int inum = 0; // 繰り返し回数
  private Com_List clist;

  public void parse(Context ct) {
    ct.toNext();
    if(ct.match("\\{")) {
      ct.toNext();
      clist = new Com_List();
      clist.parse(ct);

      if(ct.match("\\}")) {
        ct.toNext();
        if (ct.currentToken() != null && ct.currentToken().matches("[+-]?[0-9]+(.[0-9]+)?")) {
          // リピート回数を保存
          inum = Integer.parseInt(ct.currentToken());
          ct.toNext();
        } else {
          System.out.println("Rep_com: 数字を期待します。 " + ct.currentToken());
          System.exit(0);
        }
      } else {
        System.out.println("Rep_com: } がありません。 " + ct.currentToken());
        System.exit(0);
      }
    } else {
      System.out.println("Rep_com: 無効な構文です。 " + ct.currentToken());
      System.exit(0);
    }
  }

  void exe(Data data) {
    for(int i = 0; i < inum; i++) {
      clist.exe(data);
    }
  }
}

// * (4) Prim_com → walk sw? Fnum Fnum Fnum | move Fnum Fnum Fnum
class Prim_com {
  String op;
  Fnum fnum1, fnum2, fnum3; // 各命令の引数
  double x, y, z; // 座標を格納
  boolean is_draw, sw_draw;
  public void parse(Context ct) {
    op = ct.currentToken(); // 命令の種類（"walk"，”move”）をop に記録
    if (ct.match("walk")) { // walkだった場合(線を引く)
      ct.toNext();
      if (ct.match("sw")) {
        sw_draw = true; // 描写フラグ
        ct.toNext();
      }
      is_draw = true;  // 描写フラグ
      // 数字を記録
      fnum1 = new Fnum();
      fnum1.parse(ct);
      fnum2 = new Fnum();
      fnum2.parse(ct);
      fnum3 = new Fnum();
      fnum3.parse(ct);
    } else if (ct.match("move")) { // moveだった場合(移動のみを行う。)
      ct.toNext();
      is_draw = false;
      fnum1 = new Fnum();
      fnum1.parse(ct);
      fnum2 = new Fnum();
      fnum2.parse(ct);
      fnum3 = new Fnum();
      fnum3.parse(ct);
    }
  }

  void exe(Data data) {
    if(op.equals("walk")) {
      data.makeData(fnum1.exe(), fnum2.exe(), fnum3.exe(), is_draw, sw_draw);
    } else if(op.equals("move")) {
      data.makeData(fnum1.exe(), fnum2.exe(), fnum3.exe(), false, false);
    }
  }
}

// * (5) Fnum → [+-]?[0-9]+(.[0-9]+)?
class Fnum {
  private double val = 0; // 数値を格納

  public void parse(Context ct) {
    if (ct.currentToken() != null && ct.currentToken().matches("[+-]?[0-9]+(.[0-9]+)?")) {
      val = Double.parseDouble(ct.currentToken());
      ct.toNext(); // 次のトークンに進む
    } else {
      System.out.println("Num: 数字ではありません: " + ct.currentToken());
      System.exit(0);
    }
  }

  public double exe() {
    return val;
  }
}
