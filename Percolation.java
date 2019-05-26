public class Percolation {
    private static final int SLOT_CLOSED = -4;
    private static final int VIRTUAL_TOP = 0;
    private static final int VIRTUAL_BOTTOM = 1;
    private int mRow;
    private int mColumn;
    private int[] id;
    private int numberOfOpenSites = 0;
    private int[] size;

    private int[] virtualArray;
    private boolean vTIsSet = false;
    private boolean vBIsSet = false;
    private boolean isTopSlotOpened = false;
    private boolean isBottomSlotOpened = false;

    public Percolation(int n) {
        if (n < 0 || n == 0) {
            throw new java.lang.IllegalArgumentException("n has to be a positive integer");
        }
        else {
            mRow = n;
            mColumn = n;
            id = new int[n * n];
            size = new int[n * n];
            for (int i = 0; i < id.length; i++) {
                id[i] = SLOT_CLOSED;
                size[i] = 1;
            }
            virtualArray = new int[] { 0, 1 };
        }
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > mRow || col < 1 || col > mColumn) {
            throw new IllegalArgumentException(
                    "row or column number is outside the prescribed range");
        }
        int positionId = determineThePosition(row, col);
        if (!isOpen(row, col)) {
            id[positionId] = positionId; // opens up a site
            numberOfOpenSites++;
            // connects the opened site to surrounding opened sites using union find
            uniteWithAdjacentSlots(positionId);
            if (positionId < mColumn) {
                isTopSlotOpened = true;
                unionToVirtualTop(positionId);
            }
            if (positionId >= ((mRow - 1) * mColumn) && positionId < (mRow
                    * mColumn)) {
                isBottomSlotOpened = true;
                unionToVirtualBottom(positionId);
            }
        }
        //}
    }


    private int determineThePosition(int row, int col) {
        return ((row - 1) * mColumn) + (col - 1);
    }

    private void uniteWithAdjacentSlots(int positionId) {
        uniteWithRight(positionId);
        uniteWithLeft(positionId);
        uniteWithTop(positionId);
        uniteWithBottom(positionId);
    }

    private void uniteWithRight(int positionId) {
        int rightSlot = positionId + 1;
        if ((positionId % mColumn) != (mColumn - 1) && pIsOpen(rightSlot))
            union(positionId, rightSlot);
    }

    private void uniteWithLeft(int positionId) {
        int leftSlot = positionId - 1;
        if ((positionId % mColumn) != 0 && pIsOpen(leftSlot))
            union(positionId, leftSlot);
    }

    private void uniteWithTop(int positionId) {
        int topSlot = positionId - mColumn;
        if (positionId >= (mColumn) && pIsOpen(topSlot))
            union(positionId, topSlot);
    }

    private void uniteWithBottom(int positionId) {
        int bottomSlot = positionId + mColumn;
        if (positionId < (mColumn * (mRow - 1)) && pIsOpen(bottomSlot))
            union(bottomSlot, positionId);
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > mRow || col < 1 || col > mColumn) {
            throw new IllegalArgumentException(
                    "row or column number is outside the prescribed range");
        }
        int positionId = determineThePosition(row, col);
        return id[positionId] != SLOT_CLOSED;

    }

    private boolean pIsOpen(int positionId) {
        if (positionId < 0 || positionId > (id.length - 1)) {
            throw new IllegalArgumentException(
                    "row or column number is outside the prescribed range");
        }
        return id[positionId] != SLOT_CLOSED;
    }

    // is site (row, col) full? its like this is where we check if this empty site is connected to an empty site on the first row.
    public boolean isFull(int row, int col) {
        if (row < 1 || row > mRow || col < 1 || col > mColumn) {
            throw new IllegalArgumentException(
                    "row or column number is outside the prescribed range");
        }
        int positionId = determineThePosition(row, col);
        if (connected(virtualArray[VIRTUAL_TOP], positionId)) return true;
        return false;
    }

    // number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?//check for connection of the top row and the last row
    public boolean percolates() {
        if (isTopSlotOpened && isBottomSlotOpened) {
            if (connected(virtualArray[VIRTUAL_TOP], virtualArray[VIRTUAL_BOTTOM]))
                return true;
        }
        return false;
    }

    private void union(int p, int q) {
        int proot = root(p);
        int qroot = root(q);
        if (proot == qroot) return;
        if (size[proot] > size[qroot]) {
            id[qroot] = proot;
            size[proot] += size[qroot];
        }
        else {
            id[proot] = qroot;
            size[qroot] += size[proot];
        }
    }

    private int root(int index) {
        int root = index;
        while (root != id[root]) {
            id[root] = id[id[root]];
            root = id[root];
        }
        return root;
    }

    private boolean connected(int p, int q) {
        if (id[p] == SLOT_CLOSED || id[q] == SLOT_CLOSED) return false;
        return root(p) == root(q);
    }

    // set the root of virtual to the root of p
    private void unionToVirtualTop(int p) {
        if (!vTIsSet) {
            virtualArray[VIRTUAL_TOP] = p;
            vTIsSet = true;
        }
        else {
            id[root(p)] = root(virtualArray[VIRTUAL_TOP]);
        }
    }

    private void unionToVirtualBottom(int p) {
        if (!vBIsSet) {
            virtualArray[VIRTUAL_BOTTOM] = p;
            vBIsSet = true;
        }
        else {
            id[root(p)] = root(virtualArray[VIRTUAL_BOTTOM]);
        }
    }
}
