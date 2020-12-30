
class Point3D {
	double x, y, z;
	Point3D(double _x, double _y, double _z) {//コンストラクタ
		x = _x; y = _y; z = _z;
	}
	double get_2Dx(double alpha, double beta, int px, int py) {//正射影変換により画面上のX座標を求める
		final double D2Rad = Math.PI/180.0; //度からラジアンへの変換定数（finalをつけると，値の変更のできない定数となる）
		return px + x*Math.cos(beta*D2Rad) + z*Math.sin(beta*D2Rad);
	}
	double get_2Dy(double alpha, double beta, int px, int py) {//正射影変換により画面上のY座標を求める
		final double D2Rad = Math.PI/180.0; //度からラジアンへの変換定数
		double y1, zt = -x*Math.sin(beta*D2Rad) + z*Math.cos(beta*D2Rad);
		y1 = y*Math.cos(alpha*D2Rad) - zt*Math.sin(alpha*D2Rad);
		return py-y1;
	}
}
