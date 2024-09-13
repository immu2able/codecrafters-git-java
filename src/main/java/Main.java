import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.InflaterInputStream;
import java.io.FileInputStream;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.

    // Uncomment this block to pass the first stage
    //
    final String command = args[0];
    
    switch (command) {
      case "init" -> {
        final File root = new File(".git");
        new File(root, "objects").mkdirs();
        new File(root, "refs").mkdirs();
        final File head = new File(root, "HEAD");
    
        try {
          head.createNewFile();
          Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
          //System.out.println("Initialized git directory");
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      case "cat-file" -> {
        final String type = args[1];
        final String sha = args[2];

        switch (type) {
          case "-p" -> {
            try {
              final File bFile = new File(".git/objects/" + sha.substring(0, 2), sha.substring(2));
              final String blob = new BufferedReader (new InputStreamReader(new InflaterInputStream(new FileInputStream(bFile)))).readLine();
              String content = bFile.subString(blob.indexOf("\0")+1);
              System.out.println(content);

            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          }

          default -> System.out.println("Unknown type: " + type);
          }


      }

      default -> System.out.println("Unknown command: " + command);
    }
  }
}
