package org.example.dll;

import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProcessUtil {

    interface IProcessUtil {
        void kill(int processId, boolean async);
        List<Integer> process(String searchTerm);
    }

    static class WindowsProcessUtil implements IProcessUtil {

        @Override
        public void kill(int processId, boolean async) {
            String cmd = "taskkill /F /T /PID " + processId;
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                if(!async) {
                    int rest = process.waitFor();
                    if(rest == 0) {
                        System.out.printf("Killed %d%n", processId);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public List<Integer> process(String searchTerm) {
            try {
                Process process = Runtime.getRuntime().exec("wmic process where \"Name like '%java%' And CommandLine like '%" + searchTerm + "%'\"  get processid /format:list");
                int result = process.waitFor();
                if (result == 0) {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    List<Integer> processList = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        int index = line.indexOf('=');
                        if (index >= 0) {
                            String candidate = line.substring(index + 1);
                            try {
                                processList.add(Integer.parseInt(candidate));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return processList;
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }
    }

    static class LinuxProcessUtil implements IProcessUtil {

        @Override
        public void kill(int processId, boolean async) {

        }

        @Override
        public List<Integer> process(String searchTerm) {
            //kill $(ps aux | grep "$search_terms" | grep -v 'grep' | awk '{print $2}')
            return null;
        }
    }


    private static WindowsProcessUtil windowsProcessUtil = new WindowsProcessUtil();
    private static LinuxProcessUtil linuxProcessUtil = new LinuxProcessUtil();

    public static void kill(int processId) {
        kill(processId, false);
    }

    public static void kill(int processId, boolean async) {
        if(SystemUtils.IS_OS_WINDOWS) {
            windowsProcessUtil.kill(processId, async);
        }
    }

    public static List<Integer> process(String search) {
        if(SystemUtils.IS_OS_WINDOWS) {
            return windowsProcessUtil.process(search);
        }
        else {
            return linuxProcessUtil.process(search);
        }

    }

    public static void main(String[] args) {
        Optional<Integer> process = ProcessUtil.process("Gateway-Launcher").stream().findFirst();
        if(process.isPresent()) {
            ProcessUtil.kill(process.get());
        }
    }
}
