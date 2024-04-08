/**
 * @author Aaron Moser
 */
package vslab1.src.Input;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;

import vslab1.src.FileReaderWriter.FileReaderWriter;
import vslab1.src.Peers.EOnlineState;
import vslab1.src.Peers.Peer;

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
            System.err.println("Port: " + port + " is closed or not reachable on ip: " + ipAddress);
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        return socket;
    }

    /**
     * Asks user to enter ip and port until those are valid and applicable and creates a DatagramSocket with given values.
     * @return a DatagramSocket with ip and port specified by user input.
     */
    public static DatagramSocket getSocket(Scanner inputScanner) {
        DatagramSocket datagramSocket = null;
        boolean receivedValidIP = false;
        boolean receivedValidAndFreePort = false;
        String localIpAddress = null;
        int localPort = -1;
        // User has to enter a valid ip.
        while (!receivedValidIP) {
            System.out.println("Enter ip:");
            localIpAddress = inputScanner.nextLine();
            receivedValidIP = NetworkController.hasNetworkInterfaceWithIP(localIpAddress);
            if (!receivedValidIP) {
                System.err.println("Error: IP is not valid for this system. Please re-enter.");
            }
        }
        // User has to enter a valid port.
        while (!receivedValidAndFreePort) {
            System.out.println("Enter port:");
            String localPortAsString = inputScanner.nextLine();
            localPort = Integer.valueOf(localPortAsString);
            datagramSocket = NetworkController.tryBindToPort(localIpAddress, localPort);
            if (datagramSocket == null) {
                System.err.println("Error: Port is not available for this ip. Please re-enter.");
            } else {
                receivedValidAndFreePort = true;
            }
        }
        FileReaderWriter.updatePeer(new Peer(localIpAddress, localPort, null, EOnlineState.Online));
        return datagramSocket;
    }
}
