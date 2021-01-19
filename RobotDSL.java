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
    // TODO: エグゼキュータ
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
          System.out.println("Rep_com2: 数字を期待する " + ct.currentToken());
          System.exit(0);
        }
      } else {
        System.out.println("閉じカッコがありません。 " + ct.currentToken());
        System.exit(0);
      }
    } else {
      System.out.println("Rep_com1: 無効な構文です " + ct.currentToken());
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

  public void parse(Context ct) {
    op = ct.currentToken(); // 命令の種類（"walk"，”move”）をop に記録
    if (ct.match("walk")) { // walkだった場合(線を引く)
      ct.toNext();
      if (ct.match("sw")) {
        // TODO: swの処理を考える
        ct.toNext();
      } else {
        // 数字を記録
        fnum1 = new Fnum();
        fnum1.parse(ct);
        fnum2 = new Fnum();
        fnum2.parse(ct);
        fnum3 = new Fnum();
        fnum3.parse(ct);
      }
    } else if (ct.match("move")) { // moveだった場合(移動のみを行う。)
      ct.toNext();
      fnum1 = new Fnum();
      fnum1.parse(ct);
      fnum2 = new Fnum();
      fnum2.parse(ct);
      fnum3 = new Fnum();
      fnum3.parse(ct);
    }
  }

  void exe(Data data) {
    // エグゼキュータ
    // op に記録されている命令の種類に応じて処理を行う
    // TODO: 移動する座標を作成?
    if(op.equals("walk")) {
      data.makeData(fnum1.exe(), fnum2.exe(), fnum3.exe());
      // System.out.println(fnum1.exe() + " " + fnum2.exe() + " " + fnum3.exe());
    } else if(op.equals("move")) {
      data.makeData(fnum1.exe(), fnum2.exe(), fnum3.exe());
      // System.out.println(fnum1.exe() + " " + fnum2.exe() + " " + fnum3.exe());
    }
  }
}

// * (5) Fnum → [+-]?[0-9]+(.[0-9]+)?
class Fnum {
  private double val = 0; // 数値を格納

  public void parse(Context ct) {
    if (ct.currentToken() != null && ct.currentToken().matches("[+-]?[0-9]+(.[0-9]+)?")) {
      val = Double.parseDouble(ct.currentToken());
      // System.out.println(ct.currentToken());
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

//  - - - - - - - 以下描写用のClass - - - - - - - -

class Canvas extends JPanel {
  // データ
  Data data = null; // データオブジェクト（表示するデータを持つオブジェクト）の名前
  double alpha = 30; // X軸回り回転角（α）
  double beta = 30; // Y軸回り回転角（β）
  int doX, doY; // マウスの移動距離を測る

  // 関数（メソッド）群
  public void paintComponent(Graphics g) {
    super.paintComponent(g); // 親クラスのpaintメソッドを呼出す
    setBackground(Color.black); // 背景色を黒にセット

    data.drawData(alpha, beta, getWidth() / 2, getHeight() / 2, g); // データオブジェクトに表示を依頼
    // getWith(),getHeight()はそれぞれ，そのときのmcの横，縦のピクセル数を返す
  }

  void update(Data arg) { // データの準備完了時や，データの変化時に呼ばれる（引数はデータオブジェクト自身（の先頭アドレス））
    data = arg; // dataにデータオブジェクトの先頭アドレスを代入
    repaint(); // 表示を更新
  }
}

class Data {
  // データ
  ArrayList<Point3D> alist = new ArrayList<Point3D>(); // 3次元座標列を格納する配列
  Canvas canvas = null; // データを表示するCanvasオブジェクト（の名前）
  double s_r = 0, s_ang = 0, s_y = 0;
  double ang = 0;
  double x, z;
  final double rad = Math.PI/180.0; //度からラジアンへの変換定数

  void makeData(double r, double ang, double y) {
    // 変数を保存
    s_r += r;     // 半径
    s_ang += ang; // 角度
    s_y += y;     // 高さ

    // 360を超えないようにする
    if(s_ang > 360)
      s_ang -= 360;
    if(s_ang < -360)
      s_ang += 360;

    // x,z座標の計算
    x =  s_r*Math.sin(s_ang*rad); // ! そもそもここが怪しい？
    z =  s_r*Math.cos(s_ang*rad);
    System.out.printf("s_ang: %f, s_r: %f \n", s_ang,s_r);
    System.out.printf("x: %f, y: %f, z: %f \n", x,s_y,z);
    alist.add(new Point3D(x, s_y, z));
  }

  void addCanvas(Canvas arg) { // MyCanvasオブジェクトを登録
    canvas = arg;
  }

  void notifyToCanvas() { // Canvasオブジェクトにデータの準備完了（あるいは更新完了）を通知
    canvas.update(this); // 自分自身を引数として渡す（thisはDataオブジェクト自身を表す）
  }

  // alistからPoint3Dオブジェクトを順に取出し，2D画面座標へ変換し線で結んで図形を表示するメソッド（関数）
  void drawData(double alpha, double beta, int px, int py, Graphics g) {
    double x2d = 0, y2d = 0;// 2D画面座標
    int _dx = 0, _dy = 0, dx = 0, dy = 0;// 線で結ぶ点の2D座標．(_dx,_dy)が一つ前の点，(dx,dy)が現在の点
    int i = 0; // 何回目のループか数えるための変数
    int N = alist.size();// alistの要素数を得る
    g.setColor(Color.cyan);// 描画色をシアンにセット
    for (Point3D p : alist) {

      x2d = p.get_2Dx(alpha, beta, px, py);// 画面上の座標を求める
      y2d = p.get_2Dy(alpha, beta, px, py);
      dx = (int) Math.rint(x2d);// x2dの値を四捨五入してdxに代入
      dy = (int) Math.rint(y2d);// y2dの値を四捨五入してdyに代入
      if (i++ > 0) {// 2回目以降のループだったら1つ前の点との間に線を引く
        g.drawLine(_dx, _dy, dx, dy);
      }
      _dx = dx;// 1つ前の座標を更新
      _dy = dy;
    }
  }
}
