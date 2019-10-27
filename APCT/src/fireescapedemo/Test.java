package fireescapedemo;

import java.io.*;

public class Test {
    public static void mainMethod() throws IOException {
        String filename = "C:\\Users\\Niklas Henderson\\Documents\\full.map";
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        int count = 0;
        try {
            while (true) {
                count++;
                try {
                    Object obj = in.readObject();
                    System.out.println("obj #" + count + " is a: " + obj.getClass());
                    System.out.println(obj + ".toString(): " + obj);
                } catch (ClassNotFoundException e) {
                    System.out.println("can't read obj #" + count + ": " + e);
                }
            }
        } catch (EOFException e) {
            // unfortunately ObjectInputStream doesn't have a good way to detect the end of the stream
            // so just ignore this exception - it's expected when there are no more objects
        } finally {
            in.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Test.mainMethod();
    }
}
