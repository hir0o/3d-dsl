public class RobotDSL {

}

// （1）Prog → Com_list [EOF]
// （2）Com_list → [Rep_com Prim_com] Com_list?
// （3）Rep_com → repeat { Com_list } [0-9]+
// （4）Prim_com → walk sw? Fnum Fnum Fnum
// | move Fnum Fnum Fnum
// （5）Fnum → [+-]?[0-9]+(.[0-9]+)?

//(1)Prog -> Com_list
class Prog {
  private Com_List clist;

  public void parse(Context ct) {
    //パーサ
  }
  void exe() {
    //エグゼキュータ
  }
}

//(2)Com_list → [Rep_com Prim_com] Com_list?
class Com_List {
  private Rep_com rcom;
  private Prim_com pcom;
  private Com_List clist;

  public void parse(Context ct) {
  // パーサ
  }
  void exe() {
  //エグゼキュータ
  }
}

//(3) Rep_com → repeat { Com_list } [0-9]+
class Rep_com {
  int inum = 0; //繰り返し回数
  private Com_List clist;
  public void parse(Context ct) {
  //パーサ
  }
  void exe() {
  //エグゼキュータ
  }
}

//(4) Prim_com → walk sw? Fnum Fnum Fnum | move Fnum Fnum Fnum
class Prim_com {
  String op;
  double fnum1, fnum2, fnum3; //各命令の引数
  public void parse(Context ct) {
  // パーサ
  }
  void exe() {
  //エグゼキュータ
  //op に記録されている命令の種類に応じて処理を行う
  }
}


//(5) Fnum → [+-]?[0-9]+(.[0-9]+)?
