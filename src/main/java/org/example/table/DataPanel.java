    package org.example.table;

    import javax.swing.*;

    public class DataPanel {
        private JTextField fieldName;
        private JCheckBox flagCheckBox;
        private JTextField fieldX;
        private JTextField fieldY;
        private JTextField fieldZ;
        private JPanel panel;


        public JPanel getPanel() {
            return panel;
        }

        public void setData(Data data) {
            fieldName.setText(data.getName());
            fieldX.setText(data.getX() + "");
            fieldY.setText(data.getY() + "");
            fieldZ.setText(data.getZ() + " ");
            flagCheckBox.setSelected(data.isFlag());
        }
    }
