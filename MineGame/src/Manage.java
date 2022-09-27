import java.util.*;

public class Manage {
	
	private Scanner sc = new Scanner(System.in);
	private Random random = new Random();
	private int mineNum = 10;
	private int x = 9;
	private int y = 9;
	private Mine[][] field = new Mine[x][y];
	// 가로 9, 세로 9, 지뢰 10개 (12.3%)

	private int[] mineX = new int[mineNum];
	private int[] mineY = new int[mineNum];
	// 지뢰의 x, y 좌표

	public Manage() { // 생성자로 실행
		manage();
	}

	private void setMineXY() { // 지뢰 10개 생성
		for (int i = 0; i < mineNum; i++) {
			mineX[i] = (random.nextInt(9));
			mineY[i] = (random.nextInt(9));
			for (int j = 0; j < i; j++) { // 중복방지
				if (mineX[i] == mineX[j] && mineY[i] == mineY[j]) {
					i--;
				}
			}
		}
	}

	private void countArroundMineNum(int a, int b) { // 주변 지뢰 숫자 세기
		int count = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				try {
					if (field[i + a][j + b].isMine()) {
						count++;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// try-catch로 범위를 넘는 인덱스를 예외처리
				}
			}
		}
		field[a][b].setArroundMineNum(count);
	}

	private void reset() { // 필드 초기화
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				field[i][j] = new Mine(false, 0, 0);
			}
		}
	}

	private void setMineinField() { // 필드에 지뢰 심기
		reset();
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				for (int k = 0; k < mineNum; k++) {
					if (i == mineY[k] && j == mineX[k])
						field[i][j].setMine(true);
				}
			}
		}
	}

	private void setArroundMineNuminField() { // 필드에 주변 지뢰 숫자 심기
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				countArroundMineNum(i, j);
			}
		}
	}

	private void printField() { // 필드 출력
		System.out.println("-------------------------------------");
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				if (field[i][j].isMine() && field[i][j].getCellState() == 3) { // 지뢰를 열면 그 칸 지뢰로 표시
					System.out.print("| ■ ");
					continue;
				}
				if (field[i][j].getCellState() == 0) { // 셀 상태에 따라 표시
					System.out.print("|   "); // 안 연 칸
				} else if (field[i][j].getCellState() == 1) {
					System.out.print("| ? "); // 물음표 메모
				} else if (field[i][j].getCellState() == 2) {
					System.out.print("| √ "); // 지뢰 표시
				} else if (field[i][j].getCellState() == 3) { // 칸 열기
					System.out.printf("| %d ", field[i][j].getArroundMineNum());
				}

			}
			System.out.printf("|  %d \n", i + 1);
			System.out.println("-------------------------------------");
		}
		System.out.println("  1   2   3   4   5   6   7   8   9 ");
	}
	
	public void TestMineXY() {
		
		for (int i = 0; i < 9; i++) {
			mineX[i]=i;
			mineY[i]=i;
		}
		mineX[9]=0;
		mineY[9]=1;
		
		//mineX {0, 1, 2, 3, 4, 5, 6, 7,  8, 0}
		//mineY {0, 1, 2, 3, 4, 5, 6, 7,  8, 1}
	}

	public void manage() {
		setMineXY(); // 지뢰 생성
//		TestMineXY(); // 테스트지뢰
		setMineinField(); // 지뢰 심기
		setArroundMineNuminField(); // 근처의 지뢰 숫자 심기

		System.out.println("===============지뢰 찾기===============");
		System.out.println("===============게임 시작===============");

		while (true) { // 아래부터의 로직이 반복됨
			printField(); // 필드 출력
			System.out.println();
			System.out.println("1: 메모\n2: 찾은 지뢰 표시\n3.칸 열기");
			int select = sc.nextInt();
			if (select > 3 || select < 1) {
				System.out.println("잘못된 입력");
				continue;
			}
			System.out.print("열고자 하는 칸 번호 입력(세로 / 가로): "); // 오픈칸 입력받기
			try {
				int column = sc.nextInt() - 1;
				int row = sc.nextInt() - 1;

				if (select == 1) {
					field[column][row].setCellState(1);
				} else if (select == 2) {
					field[column][row].setCellState(2);
				} else if (select == 3) {
					field[column][row].setCellState(3);
				}

				/////////////// 지뢰 밟았을 때의 로직//////////////////
				if (field[column][row].getCellState() == 3 && field[column][row].isMine()) {
					System.out.println("!!BANG!!");
					for (int i = 0; i < y; i++) {
						for (int j = 0; j < x; j++) {
							if (field[i][j].isMine())
								field[i][j].setCellState(3);
						}
					}
					printField();
					return;
				}

				////////////// 0일때의 로직///////////////////

				if (field[column][row].getCellState() == 3
						&& field[column][row].getArroundMineNum() == 0) {
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							try {
								field[i+column][j+row].setCellState(3);
							} catch (ArrayIndexOutOfBoundsException e) {
								// try-catch로 범위를 넘는 인덱스를 예외처리
							}
						}
					}
				}

				///////////////////// 승리 로직////////////////////////
				int resultCount = 0;
				for (int i = 0; i < mineNum; i++) {
					if (field[mineY[i]][mineX[i]].isMine() && field[mineY[i]][mineX[i]].getCellState() == 2)
						resultCount++;
				}
				if (resultCount == mineNum) {
					printField();
					System.out.println("=====================================");
					System.out.println("=====================================");
					System.out.println("=================승리!================");
					System.out.println("=====================================");
					System.out.println("=====================================");
					return;
				}
			} catch (Exception e) { // 입력 오류 캐치
				System.out.println("잘못된 입력");
			}
		}
	}

}
