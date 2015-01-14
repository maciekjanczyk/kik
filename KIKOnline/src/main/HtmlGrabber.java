import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by macias on 12.12.14.
 */

@Component
public class HtmlGrabber {
    public String toString(String fname) {
        String ret = "";
        try {
            FileInputStream fstream = new FileInputStream(fname);
            Scanner scanner = new Scanner(fstream);
            while(scanner.hasNext())
                ret += scanner.nextLine();
            scanner.close();
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
