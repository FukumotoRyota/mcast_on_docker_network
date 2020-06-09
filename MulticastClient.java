import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

class Sender extends Thread {
  private final int toPort;
  private final String mcastAddrName;

  public Sender(int toPort, String mcastAddress) {
    this.toPort = toPort;
    this.mcastAddrName = mcastAddress;
  }

  public void run() {
    Scanner scan = null;
    MulticastSocket socket = null;
    try {
      InetAddress mcastAddress = InetAddress.getByName(mcastAddrName);
      scan = new Scanner(System.in);
      socket = new MulticastSocket();

      String message;
      while ( (message = scan.nextLine()).length() > 0 ) {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, mcastAddress, toPort);
        socket.send(packet);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (scan != null) scan.close();
      if (socket != null) socket.close();
    }
  }
}

class Receiver extends Thread {
  private final int fromPort;
  private final String mcastAddrName;
  private static final int PACKET_SIZE = 1024;

  public Receiver(int fromPort, String mcastAddrName) {
    this.fromPort = fromPort;
    this.mcastAddrName = mcastAddrName;
  }

  public void run() {
    MulticastSocket socket = null;
    byte[] buf = new byte[PACKET_SIZE];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);

    try {
      socket = new MulticastSocket(fromPort);
      InetAddress mcastAddress = InetAddress.getByName(mcastAddrName);
      socket.joinGroup(mcastAddress);

      while (true) {
        socket.receive(packet);
        String message = new String(buf, 0, packet.getLength());
        System.out.println(packet.getSocketAddress() + " : " + message);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socket != null) {
        socket.close();
      }
    }
  }
}

public class MulticastClient {
  public static final int PORT = 3000;
  public static final String MCAST_ADDR = "224.0.1.1";

  public static void main(String args[]) {
    Receiver receiver = new Receiver(PORT,  MCAST_ADDR);
    Sender sender = new Sender(PORT,  MCAST_ADDR);
    receiver.start();
    sender.start();
  }
}