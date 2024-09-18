import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.InflaterInputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
              String content = blob.substring(blob.indexOf("\0")+1);
              System.out.print(content);

            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          }

          default -> System.out.println("Unknown type: " + type);
          }
      }

      case "hash-object" -> {
        final String type = args[1];
        switch (type) {
          case "-w" -> {
            // Compute Hash
            final String path = args[2];
            final File file = new File(path);
            sha = hash(file);
            System.out.println(sha);

            // Write to .git/objects
            File bFile = new File(".git/objects/" + sha.substring(0, 2), sha.substring(2));
            bFile.getParentFile().mkdirs();
            bFile.createNewFile();
            Files.write(bFile.toPath(), (type + "\0" + Files.readString(file.toPath())).getBytes());

            }

          default -> System.out.println("Unknown type: " + type);

          }
        }
      }

      default -> System.out.println("Unknown command: " + command);
    }
  }
  
  public static String hash(File file) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        // Add logic here to read the file contents and update the digest
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
  }
  // Helper function to convert byte array to hex string
  private static String bytesToHex(byte[]bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }


}
