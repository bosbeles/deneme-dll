package org.example.dll;

import com.bsbls.home.gui.test.GuiTester;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import javax.swing.*;

public class Main {

    public interface CLibrary extends Library {
        CLibrary INSTANCE = Native.load("MathLibrary", CLibrary.class);

        Pointer leak(long len);
        void unleak(Pointer ptr);

        void fibonacci_init(long a, long b);


        boolean fibonacci_next();

        // Get the current value in the sequence.
        long fibonacci_current();

        // Get the position of the current value in the sequence.
        int fibonacci_index();

    }

    public static void main(String[] args) {
        final CLibrary instance = CLibrary.INSTANCE;


        GuiTester.test(f-> {
            JPanel panel = new JPanel();
            JButton button = new JButton("Leak Me!");
            button.addActionListener(e->{
                int len = 1000000;
                Pointer leakP = instance.leak(len);
                int[] intArray = leakP.getIntArray(0, len);
                System.out.println(intArray.length);
                instance.unleak(leakP);
                System.out.println(intArray.length);

            });
            panel.add(button);

            return panel;
        });

    }
}
