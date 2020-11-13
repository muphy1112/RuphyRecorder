package me.muphy.mbean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Shell implements ShellMBean {
    @Override
    public String runCmd(String cmd) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String stdout_data = "";
        String strtmp;
        while ((strtmp = stdInput.readLine()) != null) {
            stdout_data += strtmp + "\n";
        }
        while ((strtmp = stdError.readLine()) != null) {
            stdout_data += strtmp + "\n";
        }
        process.waitFor();
        try {
            stdError.close();
            stdInput.close();
        } catch (IOException e) {

        }
        return stdout_data;
    }

    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName shell = new ObjectName("ShellMBean:name=Shell");
        mBeanServer.registerMBean(new Shell(), shell);
        Registry registry = LocateRegistry.createRegistry(9010);
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi");
        JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, null, mBeanServer);
        jmxConnectorServer.start();
        System.out.println("JMXConnectorServer is running");
    }
}