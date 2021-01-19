public class FlagPoint3D extends Point3D {
  double x, y, z;
  boolean is_draw, sw_draw;
  FlagPoint3D(double _x, double _y, double _z, boolean _is_draw, boolean _sw_draw) {
    super(_x, _y, _z);
    is_draw = _is_draw;
    sw_draw = _sw_draw;
  }
}
