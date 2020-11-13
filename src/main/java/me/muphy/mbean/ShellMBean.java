package me.muphy.mbean;

import java.io.IOException;

public interface ShellMBean {
    public String runCmd(String cmd) throws IOException, InterruptedException;
}
