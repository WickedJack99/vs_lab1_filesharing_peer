package vslab1.src.Input;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
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

    public static DatagramSocket tryBindToPort(String ipAddress, int port) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(null);
            // Allow socket to be bound even if address was recently in use
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(ipAddress, port));
        } catch (Exception e) {
            System.out.println("Port: " + port + " is closed or not reachable on ip: " + ipAddress);
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        return socket;
    }
}
