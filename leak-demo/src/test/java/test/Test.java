package test;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * 描述:
 *
 * @author hz.lei
 * @date 2018年04月20日 上午11:39
 */
public class Test {

    public static void main(String[] args) throws UnknownHostException {
        String ipStr = "192.168.1.121";
        String targetIpStr = "192.168.1.120";
        int ip = transferIp(ipStr);
        int targetIp = transferIp(targetIpStr);

        boolean b = matchMask(targetIp, ip, 32);

        System.out.println(b);


    }

    public static boolean matchMask(int targetIp, int serverIp, int mask) {
        int maskIp = (0xFFFFFFFF << (32 - mask));
        return (serverIp & maskIp) == (targetIp & maskIp);
    }

    public static int transferIp(String ipStr) throws UnknownHostException {
        byte[] address = Inet4Address.getByName(ipStr).getAddress();

        return ((address[0] & 0xff) << 24) | ((address[1] & 0xff) << 16)
                | ((address[2] & 0xff) << 8) | (address[3] & 0xff);
    }
}
