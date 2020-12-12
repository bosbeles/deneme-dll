package org.example.dll;

import com.bsbls.home.gui.test.GuiTester;
import com.bsbls.home.gui.test.GuiUtil;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main2 {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook-1");
        }));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook-2");
        }));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            GuiUtil.sleep(5000);
            scheduler.shutdown();
            System.out.println("Shutdown hook-3");
        }));


        GuiTester.test(f -> {
            JPanel panel = new JPanel();
            JButton exit = new JButton("Exit");
            JButton halt = new JButton("Halt");
            JButton schedule = new JButton("Schedule");
            panel.add(exit);
            panel.add(halt);
            panel.add(schedule);

            halt.addActionListener(e -> {
                Optional<Integer> process = ProcessUtil.process("Gateway-Launcher").stream().findFirst();
                if (process.isPresent()) {
                    System.out.println("Killing them hardly...");
                    ProcessUtil.kill(process.get());
                    System.out.println("They killed Kenny.");
                }
            });
            exit.addActionListener(e -> System.exit(1));
            schedule.addActionListener(e -> {

                scheduler.scheduleAtFixedRate(() -> {
                    System.out.println("Scheduled.");
                    GuiUtil.sleep(5000);
                }, 1, 1, TimeUnit.SECONDS);

            });

            return panel;
        });


    }
}
