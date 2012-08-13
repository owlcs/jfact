package conformance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class JUnitRunnerFromFile extends JUnitRunner {
    public static String readFile(File f) {
        StringBuilder b = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f)));
            String l = r.readLine();
            while (l != null) {
                b.append(l);
                b.append('\n');
                l = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return b.toString();
    }

    public JUnitRunnerFromFile(File premise, File consequence, String testId,
            TestClasses t, String description) {
        super(readFile(premise), readFile(consequence), testId, t, description);
    }
}
