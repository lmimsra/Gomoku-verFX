/**
 * Created by mmr on 2016/06/27.
 */

public class GJudge {
	GFrame frame;

	GJudge(GFrame f) {
		frame = f;
	}
	//勝利判定　戻り値　0:黒勝　1:白勝　2:引分 3:続行
	public int Judge(int x, int y) {

		if (LineS(x, y)) return frame.Arr[x][y];          //斜め検索
		else if (LineVH(x, y)) return frame.Arr[x][y];    //縦横検索
		else if (Full()) return 2;                       //ドロー検索
		else return 3;                                  //続行
	}

	//指定の点から上下左右に検索
	public boolean LineVH(int x, int y) {

		int Criteria;   //置かれた石
		int count1 = 0, count2 = 0;
		int Lx = x, Ly = y;
		Criteria = frame.Arr[x][y];

		try {
			while (Lx >= 0) {//左へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count1++;
					Lx = Lx - 1;
					//System.out.println("move 1");  //動作チェック
				} else break;
			}
			Lx = x;
			Ly = y;
			while (Lx < frame.getMasu()) {//右へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count1++;
					Lx = Lx + 1;
					//System.out.println("move 2"); //動作チェック
				} else break;
			}

			Lx = x;
			Ly = y;
			while (Ly >= 0) {//上へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count2++;
					Ly = Ly - 1;
					//System.out.println("move 3");  //動作チェック
				} else break;
			}

			Lx = x;
			Ly = y;
			while (Ly < frame.getMasu()) {//下へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count2++;
					Ly = Ly + 1;
					//	System.out.println("move 4");   //動作チェック
				} else break;
			}


		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("上下探索にて枠外の配列を参照しています" + e.toString());
			//System.out.println("" + count1 + "," + count2);
			System.out.println("" + Lx + "," + Ly);
			//無視のためのキャッチ

		}
		count1 = count1 - 1;
		count2 = count2 - 1;
		//System.out.println("End of VH " + count1 + "," + count2);
		return count1 >= 5 || count2 >= 5;
	}

	//指定の点から斜めに検索
	public boolean LineS(int x, int y) {
		int Criteria;   //置かれた石
		int count1 = 0, count2 = 0;
		int Lx = x, Ly = y;
		Criteria = frame.Arr[x][y];
		try {
			while (Lx >= 0 && Ly >= 0) {//左上へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count1++;
					Lx = Lx - 1;
					Ly = Ly - 1;
					//System.out.println("move 1");  //動作チェック
				} else break;
			}
			Lx = x;
			Ly = y;
			while (Lx < frame.getMasu() && Ly < frame.getMasu()) {//右下へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count1++;
					Lx = Lx + 1;
					Ly = Ly + 1;
					//System.out.println("move 2");		//動作チェック
				} else break;
			}

			Lx = x;
			Ly = y;
			while (Lx < frame.getMasu() && Ly >= 0) {//右上へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count2++;
					Lx = Lx + 1;
					Ly = Ly - 1;
					//System.out.println("move 3");   //動作チェック
				} else break;
			}

			Lx = x;
			Ly = y;
			while (Lx >= 0 && Ly < frame.getMasu()) {//左下へシフト
				if (frame.Arr[Lx][Ly] == Criteria) {
					count2++;
					Lx = Lx - 1;
					Ly = Ly + 1;
					//System.out.println("move 4");  //動作チェック
				} else break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("斜め探索にて枠外の配列を参照しています" + e.toString());
			//System.out.println("" + count1 + "," + count2);
			System.out.println("" + Lx + "," + Ly);
			//無視のためのキャッチ

		}
		count1 = count1 - 1;
		count2 = count2 - 1;
		//System.out.println("End of S " + count1 + "," + count2);
		return count1 >= 5 || count2 >= 5;
	}

	//全てのマスが埋まっているか確認
	public boolean Full() {

		int CountD = 0;   //カウント変数

		//マスが全て埋まっているか
		for (int i = 0; i < frame.Arr.length; i++) {
			for (int j = 0; j < frame.Arr[i].length; j++) {
				if (frame.Arr[i][j] == frame.getDF()) CountD++;
			}

		}
		//System.out.println("------- END -------"); //動作チェック
		return CountD == 0;

	}

}