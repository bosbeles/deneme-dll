package org.example.table;

import com.bsbls.home.gui.test.GuiTester;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MyTable extends JTable {

    JScrollPane scrollPane;
    boolean autoScroll;
    private Consumer<Boolean> scrollToEndListener;
    private Object lastValue;
    private ScheduledFuture<?> future;

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
            scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            scrollBar.addAdjustmentListener(e -> {


                if (e.getValue() + scrollBar.getVisibleAmount() == scrollBar.getMaximum()) {
                    setScrollToEnd(true);
                } else {
                    setScrollToEnd(false);
                }

            });

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scrollPane.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (future != null) {
                        future.cancel(false);
                    }
                    future = scheduler.schedule(() -> {
                        EventQueue.invokeLater(() -> {
                            System.out.println("Yes");
                            JViewport viewport = scrollPane.getViewport();
                            Point p = viewport.getViewPosition();
                            Dimension extentSize = viewport.getExtentSize();
                            //p.translate(extentSize.width, extentSize.height);
                            int rowIndex = rowAtPoint(p);
                            if (rowIndex >= 0) {
                                lastValue = getValueAt(rowIndex, 0);
                            }
                        });
                    }, 500, TimeUnit.MILLISECONDS);

                }
            });

            scrollBar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //System.out.println(e);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //System.out.println(e);
                    lastValue = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //System.out.println(e);
                    JViewport viewport = scrollPane.getViewport();
                    Point p = viewport.getViewPosition();
                    Dimension extentSize = viewport.getExtentSize();
                    //p.translate(extentSize.width, extentSize.height);
                    int rowIndex = rowAtPoint(p);
                    if (rowIndex >= 0) {
                        lastValue = getValueAt(rowIndex, 0);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    //System.out.println(e);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //System.out.println(e);
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    //System.out.println(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    // System.out.println(e);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    //System.out.println(e);
                }
            });

            getTableModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {

                    if (lastValue != null && !autoScroll) {
                        int rowCount = getRowCount();
                        int newIndex = -1;
                        for (int i = 0; i < rowCount; i++) {
                            Object indexValue = getValueAt(i, 0);
                            if (indexValue == lastValue) {
                                newIndex = i;
                                break;
                            }
                        }

                        System.out.println(lastValue + " " + newIndex);
                        if (newIndex > 1) {
                            scrollRectToVisible(getCellRect(newIndex - 1, 0, true));
                        } else {
                            lastValue = null;
                        }

                    }

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
