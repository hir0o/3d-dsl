import java.awt.*;
import java.util.*;

class Data {
  // データ
  ArrayList<FlagPoint3D> alist = new ArrayList<FlagPoint3D>(); // 3次元座標列を格納する配列
  Canvas canvas = null; // データを表示するCanvasオブジェクト（の名前）
  double s_r = 0, s_ang = 0, s_y = 0;
  double ang = 0;
  double x, z;
  final double RAD = Math.PI/180.0; //度からラジアンへの変換定数
  boolean draw_flag = true;

  void makeData(double r, double ang, double y, boolean is_draw, boolean sw_draw) {
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
    x =  s_r*Math.sin(s_ang*RAD);
    z =  s_r*Math.cos(s_ang*RAD);

    alist.add(new FlagPoint3D(x, s_y, z, is_draw, sw_draw));
  }

  void addCanvas(Canvas arg) { // MyCanvasオブジェクトを登録
    canvas = arg;
  }

  void notifyToCanvas() { // Canvasオブジェクトにデータの準備完了（あるいは更新完了）を通知
    canvas.update(this); // 自分自身を引数として渡す（thisはDataオブジェクト自身を表す）
  }

  // alistからFlagPoint3Dオブジェクトを順に取出し，2D画面座標へ変換し線で結んで図形を表示するメソッド（関数）
  void drawData(double alpha, double beta, int px, int py, Graphics g) {
    double x2d = 0, y2d = 0;// 2D画面座標
    int _dx = 0, _dy = 0, dx = 0, dy = 0;// 線で結ぶ点の2D座標．(_dx,_dy)が一つ前の点，(dx,dy)が現在の点
    int i = 0; // 何回目のループか数えるための変数
    int N = alist.size();// alistの要素数を得る
    g.setColor(Color.cyan);// 描画色をシアンにセット
    for (FlagPoint3D p : alist) {
      if(p.sw_draw) // sw_drawがあったら、フラグを反転する
        draw_flag = !draw_flag;

      x2d = p.get_2Dx(alpha, beta, px, py);// 画面上の座標を求める
      y2d = p.get_2Dy(alpha, beta, px, py);
      dx = (int) Math.rint(x2d);// x2dの値を四捨五入してdxに代入
      dy = (int) Math.rint(y2d);// y2dの値を四捨五入してdyに代入

      if (i++ > 0) {// 2回目以降のループだったら1つ前の点との間に線を引く
        if (p.is_draw && draw_flag) {
          g.drawLine(_dx, _dy, dx, dy);
        }
      }
      _dx = dx;// 1つ前の座標を更新
      _dy = dy;
    }
  }
}
