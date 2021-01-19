import java.awt.*;
import javax.swing.*;

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
