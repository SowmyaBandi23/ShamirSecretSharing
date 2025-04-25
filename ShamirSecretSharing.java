import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class ShamirSecretSharing {

    public static Map<Integer, Long> parseJsonRoots(String json) {
        Map<Integer, Long> points = new LinkedHashMap<>();

        Pattern p = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{[^}]\"base\"\\s:\\s*\"(\\d+)\",\\s*\"value\"\\s*:\\s*\"([a-zA-Z0-9]+)\"");
        Matcher m = p.matcher(json);

        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int base = Integer.parseInt(m.group(2));
            String value = m.group(3);

            long y = Long.parseLong(value, base);
            points.put(x, y);
        }

        return points;
    }

    public static long lagrangeInterpolation(Map<Integer, Long> points, int k) {
        List<Integer> xList = new ArrayList<>(points.keySet()).subList(0, k);
        List<Long> yList = new ArrayList<>(points.values()).subList(0, k);

        long result = 0;
        for (int i = 0; i < k; i++) {
            double term = yList.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term *= (0.0 - xList.get(j)) * 1.0 / (xList.get(i) - xList.get(j));
                }
            }
            result += Math.round(term);
        }
        return result;
    }

    public static void processTestcase(String filepath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filepath)));
        Pattern keyPattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
        Matcher keyMatcher = keyPattern.matcher(json);
        int k = keyMatcher.find() ? Integer.parseInt(keyMatcher.group(1)) : 0;

        Map<Integer, Long> points = parseJsonRoots(json);
        long constant = lagrangeInterpolation(points, k);
        System.out.println("Secret (constant term) from " + filepath + ": " + constant);
    }

    public static void main(String[] args) throws IOException {
        processTestcase("testcase1.json");
        processTestcase("testcase2.json");
    }
}