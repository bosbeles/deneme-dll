    package org.example.table;

    import javax.swing.table.DefaultTableModel;

    public class MyTableModel extends DefaultTableModel {

        private int max = -1;

        public MyTableModel() {
            this(-1);

        }

        public MyTableModel(int max) {
            super(new Object[]{
                    "Name", "Flag", "X", "Y", "Z", "Data"
            }, 0);
            this.max = max;
        }


        @Override
        public void addRow(Object[] rowData) {
            if (getRowCount() == max) {
                super.removeRow(0);
            }
            super.addRow(rowData);
        }
    }
