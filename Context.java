import	java.util.*;
import	java.util.regex.*;

// * 字句解析を行うクラス
public class Context {
	private StringTokenizer st;//文字列を単語に区切るオブジェクト
	private String token; //現在のトークンを格納する変数
	public Context(String text, String delim) { //コンストラクタ
		st = new StringTokenizer(text, delim);//行を単語に分割
		toNext(); //最初のトークンを読みtokenへ格納
	}
	//次のトークンを探し，見つかれば文字列としてtokenへ格納するメソッド
	public void toNext() {
		token = st.hasMoreTokens() ? st.nextToken() : null;
	}
	//現在のトークンを返すメソッド
	public String currentToken() {
		return token;
	}
	//現在のトークンと引数文字列（正規表現可）が等しければtrueを返し，
	//等しくないか，現在のトークンがnullの場合falseを返すメソッド
	public boolean match(String s) {
		return (token != null) ? token.matches(s) : false;
	}
}
