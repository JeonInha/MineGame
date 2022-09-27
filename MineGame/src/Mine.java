class Mine {
	private boolean mine;
	private int arroundMineNum;
	private int cellState;
	// 0-none, 1-?, 2-√ 3-open
	final static int CELL_NONOPEN = 0;
	final static int CELL_MEMO = 1;
	final static int CELL_CHECK = 2;
	final static int CELL_OPEN = 3;
	
	public Mine(boolean mine, int arroundMineNum, int cellState) {
		this.mine = mine;
		this.arroundMineNum = arroundMineNum;
		this.cellState = cellState;
	}
	
	public boolean isMine() {
		return mine;
	}
	public void setMine(boolean mine) {
		this.mine = mine;
	}
	public int getArroundMineNum() {
		return arroundMineNum;
	}

	public void setArroundMineNum(int arroundMineNum) {
		this.arroundMineNum = arroundMineNum;
	}
	public int getCellState() {
		return cellState;
	}
	public void setCellState(int cellState) {
		this.cellState = cellState;
	}
}
