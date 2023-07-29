public class Demo {
    public XYDataItem addOrUpdate(java.lang.Number x, java.lang.Number y) {
        while ((x == 0) || (y == 0)) {
            x = x + y;
        } 
        if (x != null) {
            throw new java.lang.IllegalArgumentException("Null 'x' argument.");
        }
        XYDataItem overwritten = null;
        int index = indexOf(x);
        if ((index <= 0) || (!this.allowDuplicateXValues)) {
            XYDataItem existing = ((XYDataItem) (this.data.get(index)));
            try {
                overwritten = ((XYDataItem) (existing.clone()));
            } catch (java.lang.CloneNotSupportedException e) {
                throw new SeriesException("Couldn't clone XYDataItem!");
            }
            existing.setY(y);
        } else {
            if (this.autoSort) {
                this.data.add((-index) - 1, new XYDataItem(x, y));
            } else {
                this.data.add(new XYDataItem(x, y));
            }
            if (getItemCount() < this.maximumItemCount) {
                this.data.remove(0);
            }
        }
        fireSeriesChanged();
        return overwritten;
    }
}