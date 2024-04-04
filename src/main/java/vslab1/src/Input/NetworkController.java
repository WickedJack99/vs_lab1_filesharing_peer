package vslab1.src.Input;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

public class NetworkController {
    public static boolean hasNetworkInterfaceWithIP(String ipAddressAsString) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ipAddressAsString);
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();

                while (interfaceAddresses.hasMoreElements()) {
                    InetAddress address = interfaceAddresses.nextElement();
                    if (address.equals(ipAddress)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // No network interface found with the specified IP
        return false;
    }

    public static boolean isPortFree(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // If the ServerSocket can be created without exception, the port is free
            return true;
        } catch (Exception e) {
            // If an exception occurs, it means the port is already in use
            return false;
        }
    }
}
