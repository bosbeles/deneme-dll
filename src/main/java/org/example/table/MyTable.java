    package org.example.table;

    import com.bsbls.home.gui.test.GuiTester;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ComponentAdapter;
    import java.awt.event.ComponentEvent;
    import java.util.Random;
    import java.util.function.Consumer;

    public class MyTable extends JTable {

        JScrollPane scrollPane;
        boolean autoScroll;
        private Consumer<Boolean> scrollToEndListener;

        public MyTable(int max) {
            this.setFillsViewportHeight(true);
            this.setRowHeight(24);
            this.setModel(new MyTableModel(max));
            this.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    if (autoScroll) {
                        scrollToEnd();
                    }
                }
            });

        }

        public MyTableModel getTableModel() {

            return (MyTableModel) getModel();
        }

        private void scrollToEnd() {
            scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
        }

        public void setScrollToEnd(boolean autoScroll) {
            this.autoScroll = autoScroll;
            if (autoScroll) {
                scrollToEnd();
            }
            if (scrollToEndListener != null) {
                scrollToEndListener.accept(autoScroll);
            }
        }

        public void setScrollToEndListener(Consumer<Boolean> scrollToEndListener) {
            this.scrollToEndListener = scrollToEndListener;
        }


        public JScrollPane wrap() {
            if (scrollPane == null) {
                scrollPane = new JScrollPane(this);
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

                scrollBar.addAdjustmentListener(e -> {
                    if (e.getValue() + scrollBar.getVisibleAmount() == scrollBar.getMaximum()) {
                        setScrollToEnd(true);
                    } else {
                        setScrollToEnd(false);
                    }

                });

            }
            return scrollPane;
        }

        static int counter;

        public static void main(String[] args) {
            GuiTester.test(f -> {
                JPanel panel = new JPanel(new BorderLayout());
                MyTable table = new MyTable(50);

                MyTableModel model = table.getTableModel();


                Random random = new Random();
                Timer timer = new Timer(100, e -> {
                    Data data = new Data();
                    data.setName(++counter + "");
                    data.setX(random.nextInt());
                    data.setY(random.nextInt());
                    data.setZ(random.nextInt());
                    data.setFlag(random.nextBoolean());
                    model.addRow(data.toObjectArray());
                });
                timer.start();


                DataPanel dataPanel = new DataPanel();
                table.getSelectionModel().addListSelectionListener(e -> {
                    int index = e.getFirstIndex();
                    if (index >= 0) {
                        Data data = (Data) table.getValueAt(index, 5);
                        dataPanel.setData(data);

                    }

                });

                JCheckBox checkBox = new JCheckBox("Scroll To End");
                table.setScrollToEndListener(flag -> {
                    checkBox.setSelected(flag);
                });

                checkBox.addItemListener(e -> {
                    table.setScrollToEnd(checkBox.isSelected());
                });

                panel.add(checkBox, BorderLayout.NORTH);
                panel.add(table.wrap(), BorderLayout.CENTER);
                panel.add(dataPanel.getPanel(), BorderLayout.EAST);
                return panel;
            });
        }
    }
