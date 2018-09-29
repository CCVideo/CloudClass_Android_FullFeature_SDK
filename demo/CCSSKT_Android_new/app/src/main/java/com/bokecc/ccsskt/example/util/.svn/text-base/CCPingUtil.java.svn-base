package com.bokecc.ccsskt.example.util;

import android.text.TextUtils;
import android.util.Log;

import com.bokecc.ccsskt.example.entity.PingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class CCPingUtil {

    private static final String TAG = CCPingUtil.class.getSimpleName();

    private CCPingUtil() {
        throw new UnsupportedOperationException();
    }

    public static PingResult pingJava(InetAddress ia, int timeOutMillis) {
        float useTime;
        PingResult pingResult = new PingResult();
        try {
            long startTime = System.currentTimeMillis();
            final boolean reached = ia.isReachable(timeOutMillis);
            if (reached) {
                useTime = System.currentTimeMillis() - startTime;
                pingResult.setUseTime(useTime);
            }
        } catch (Exception ignored) {
        }
        return pingResult;
    }

    public static PingResult pingNative(InetAddress host, int count) {
        StringBuilder echo = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        String address = host.getHostAddress();
        String pingCommand = "ping";
        if (!TextUtils.isEmpty(address)) {
            if (IPTools.isIPv6Address(address)) {
                // If we detect this is a ipv6 address, change the to the ping6 binary
                pingCommand = "ping6";
            } else if (!IPTools.isIPv4Address(address)) {
                // Address doesn't look to be ipv4 or ipv6, but we could be mistaken
                Log.w("AndroidNetworkTools", "Could not identify " + address + " as ipv4 or ipv6, assuming ipv4");
            }
        } else {
            // Not sure if getHostAddress ever returns null, but if it does, use the hostname as a fallback
            throw new NullPointerException("address is null");
        }
        float useTime;
        PingResult pingResult = new PingResult();
        BufferedReader buffer = null;
        Process proc = null;
        try {
            pingCommand += (" -c " + count + " " + address);
            Log.e(TAG, "pingNative: start [ " + pingCommand + " ]");
            ProcessBuilder builder = new ProcessBuilder("/system/bin/ping", "-c 5", address).redirectErrorStream(false);
            proc = builder.start();
//            proc = runtime.exec(pingCommand);
            int status = proc.waitFor();
            Log.e(TAG, "pingNative: status [ " + status + " ]");
            if (status == 0) {
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                buffer = new BufferedReader(reader);
                String line;
                while ((line = buffer.readLine()) != null) {
                    echo.append(line).append("\n");
                }
                useTime = parsePingResult(echo.toString());
                pingResult.setUseTime(useTime);
                Log.e(TAG, "pingNative: time [ " + useTime + " ]");
            }  else {
                Log.e(TAG, "failed pingNative: " + status);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception pingNative: " + e.getMessage());
        } finally {
            if (proc != null) {
                proc.destroy();
            }
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {
                }
            }
        }
        return pingResult;
    }

    /**
     * PING www.a.shifen.com (119.75.213.61): 56 data bytes
     64 bytes from 119.75.213.61: icmp_seq=0 ttl=56 time=3.495 ms
     64 bytes from 119.75.213.61: icmp_seq=1 ttl=56 time=5.059 ms
     64 bytes from 119.75.213.61: icmp_seq=2 ttl=56 time=4.194 ms
     64 bytes from 119.75.213.61: icmp_seq=3 ttl=56 time=3.376 ms
     64 bytes from 119.75.213.61: icmp_seq=4 ttl=56 time=4.468 ms

     --- www.a.shifen.com ping statistics ---
     5 packets transmitted, 5 packets received, 0.0% packet loss
     round-trip min/avg/max/stddev = 3.376/4.118/5.059/0.625 ms
     * @param data
     * @return
     */
    private static float parsePingResult(String data) {
        if (TextUtils.isEmpty(data)) {
            return -1f;
        } else {
            Log.e(TAG, "parsePingResult: [ " + data + " ]");
            if (data.contains("0% packet loss")) {
                int start = data.indexOf("/mdev = ");
                int end = data.indexOf(" ms\n", start);
                if (start == -1 || end == -1) {
                    // TODO: We failed at parsing, maybe we should fix ;)
                    Log.e(TAG, "parsePingResult: error - " + data);
                    return -1f;
                } else {
                    data = data.substring(start + 8, end);
                    String stats[] = data.split("/");
                    return Float.parseFloat(stats[1]);
                }
            } else if (data.contains("100% packet loss")) {
                Log.e(TAG, "parsePingResult: 100% packet loss");
                return -1f;
            } else if (data.contains("% packet loss")) {
                Log.e(TAG, "parsePingResult: partial packet loss");
                // FIXME: 2017/9/3 maybe Calculation
                return -1f;
            } else if (data.contains("unknown host")) {
                Log.e(TAG, "parsePingResult: unknown host");
                return -1f;
            } else {
                Log.e(TAG, "parsePingResult: unknown error in getPingStats");
                return -1f;
            }
        }
    }

}
