import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.*;

public class Displayed extends JFrame {
	private Random random = new Random();
	private int mineNum = 10;
	private int x = 9;
	private int y = 9;
	private Mine[][] field = new Mine[x][y];
	// 가로 9, 세로 9, 지뢰 10개 (12.3%)
	private int[] mineX = new int[mineNum];
	private int[] mineY = new int[mineNum];
	// 지뢰의 x, y 좌표

	////////////////////////////////////////////////
	////// 나만의 기능을 가진 버튼을 만들자........//////////
	////////////////////////////////////////////////

	private void setMineXY() { // 지뢰 10개 생성
		for (int i = 0; i < mineNum; i++) {
			mineX[i] = (random.nextInt(x));
			mineY[i] = (random.nextInt(y));
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

	private void TestMineXY() {
		for (int i = 0; i < 9; i++) {
			mineX[8] = i;
			mineY[8] = i;
		}
		mineX[9] = 0;
		mineY[9] = 1;

		// mineX {0, 1, 2, 3, 4, 5, 6, 7, 8, 0}
		// mineY {0, 1, 2, 3, 4, 5, 6, 7, 8, 1}
	}

	private String cellStateToBLabel(Mine a) {
		if (a.getCellState() == Mine.CELL_NONOPEN)
			return " ";
		else if (a.getCellState() == Mine.CELL_MEMO)
			return "?";
		else if (a.getCellState() == Mine.CELL_CHECK)
			return "√";
		else if (a.getCellState() == Mine.CELL_OPEN)
			return Integer.toString(a.getArroundMineNum());
		else
			return null;
	}

	Displayed() {

		setMineXY(); // 지뢰 생성
//		TestMineXY(); // 테스트지뢰
		setMineinField(); // 지뢰 심기
		setArroundMineNuminField(); // 근처의 지뢰 숫자 심기

		JPanel pnl = new JPanel(); // 전체 상자
		JPanel fieldBtns = new JPanel(); // 지뢰 필드
		JPanel top = new JPanel(); // top 상자
		BoxLayout pnlBox = new BoxLayout(pnl, BoxLayout.Y_AXIS); // top이랑 지뢰필드 세로로 쌓기
		BoxLayout topBox = new BoxLayout(top, BoxLayout.Y_AXIS);

		pnl.setLayout(pnlBox);
		top.setLayout(topBox);

		pnl.add(top);
		pnl.add(fieldBtns);

		add(pnl);

		////////////////////////
		Toolkit kit = Toolkit.getDefaultToolkit();

		URL winUrl = Displayed.class.getClassLoader().getResource("img/win.png");
		URL loseUrl = Displayed.class.getClassLoader().getResource("img/lose.png");
		URL playingUrl = Displayed.class.getClassLoader().getResource("img/playing.png");
		URL bombUrl = Displayed.class.getClassLoader().getResource("img/bomb.png");
		ImageIcon win = new ImageIcon(kit.getImage(winUrl));
		ImageIcon lose = new ImageIcon(kit.getImage(loseUrl));
		ImageIcon playing = new ImageIcon(kit.getImage(playingUrl));
		ImageIcon bomb = new ImageIcon(kit.getImage(bombUrl));

		////////////////////////
		// top 파트
		JLabel explainRole1 = new JLabel("클릭: 칸 열기");
		JLabel explainRole2 = new JLabel("우클릭: 지뢰 체크");
		JLabel explainRole3 = new JLabel("우클릭 여러번: 물음표(메모)");
		JLabel explainRole4 = new JLabel("모든 지뢰를 \"체크\"하면 승리");
		JLabel winlose = new JLabel(playing);

		top.add(explainRole1);
		top.add(explainRole2);
		top.add(explainRole3);
		top.add(winlose);

		// 지뢰와 버튼을 연결한 맵 제작
		Map<Mine, JButton> mineBtn = new HashMap<Mine, JButton>();

		///////////////////////// 지뢰 버튼 액션
		MouseListener mineBtnAct = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JButton btn = (JButton) e.getSource();

				for (Entry<Mine, JButton> entry : mineBtn.entrySet()) { // 엔트리 사용해서 전체를 관리
					if (entry.getValue().equals(e.getSource())) {
						Mine mine = entry.getKey();

						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
							// 한번 우클릭
							mine.setCellState(Mine.CELL_CHECK);

						} else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() >= 2) {
							// 두번 우클릭
							mine.setCellState(Mine.CELL_MEMO);
						} else if (SwingUtilities.isLeftMouseButton(e)) {// 지뢰칸의 셀 상황 먼저 변화시키고
							// 지뢰칸 클릭
							mine.setCellState(Mine.CELL_OPEN);
							btn.setEnabled(false); // 선택된 버튼 비활성화
						}

						btn.setText(cellStateToBLabel(mine)); // 셀 상황 출력
						winlose.setIcon(playing);

						// 0 밟았을 때 주위 여는 로직
						if (mine.getCellState() == 3 && mine.getArroundMineNum() == 0) {

							int co = 0;
							int ro = 0;
							for (int i = 0; i < y; i++) {
								for (int j = 0; j < x; j++) {
									if (field[i][j].equals(mine)) { // 좌표 추출
										co = i;
										ro = j;
										break;
									}
								}
							}

							for (int i = -1; i < 2; i++) {
								for (int j = -1; j < 2; j++) {
									try {
										mineBtn.get(field[i + co][j + ro]).doClick(); // 자동 클릭해줌. 좀 느리긴한디
									} catch (ArrayIndexOutOfBoundsException exception) {
										// try-catch로 범위를 넘는 인덱스를 예외처리
									}
								}
							}
						}

						///// 패배 로직
						if (mine.isMine() && mine.getCellState() == Mine.CELL_OPEN) {
							for (int i = 0; i < mineNum; i++) {
								Mine minecell = field[mineY[i]][mineX[i]];
								mineBtn.get(minecell).setIcon(bomb);
								mineBtn.get(minecell).setEnabled(true);
								mineBtn.get(minecell).setBackground(Color.red);

							}
							winlose.setIcon(lose);
						}

						///// 승리 로직
						int resultCount = 0;
						for (int i = 0; i < mineNum; i++) {
							if (field[mineY[i]][mineX[i]].isMine() && field[mineY[i]][mineX[i]].getCellState() == Mine.CELL_CHECK)
								resultCount++;
						}
						if (resultCount == mineNum) {
							winlose.setIcon(win);
						}
					}
				}
			}
		};

		//////////////////////////////////////

		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				mineBtn.put(field[i][j], new JButton());
				fieldBtns.add(mineBtn.get(field[i][j]));
				mineBtn.get(field[i][j]).addMouseListener(mineBtnAct);
			}
		}

		GridLayout grid = new GridLayout(x, y);
		fieldBtns.setLayout(grid);

		setSize(500, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}