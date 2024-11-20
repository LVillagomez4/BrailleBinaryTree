import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class stores braille characters in a binary tree. 
 * @author Catie Baker and YOUR NAME HERE
 *
 */
public BrailleTree(String filename) {
        root = new BrailleNode("");
        try (Scanner scanner = new Scanner(new File(filename))) {
            int brailleDots = scanner.nextInt();
            scanner.nextLine(); // move to next line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ", 2);
                if (parts.length == 2) {
                    add(parts[0], parts[1]);
                }
            }
            add("000000", " "); // Add space mapping
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        }
    }

    public void add(String binary, String text) {
        root = add(binary, text, root);
    }

    private BrailleNode add(String binary, String text, BrailleNode curr) {
        if (binary.isEmpty()) {
            curr.setText(text);
            return curr;
        }
        if (curr == null) curr = new BrailleNode("");

        if (binary.charAt(0) == '0') {
            curr.setLeft(add(binary.substring(1), text, curr.getLeft()));
        } else {
            curr.setRight(add(binary.substring(1), text, curr.getRight()));
        }
        return curr;
    }

    public String getText(String binary) {
        return getText(binary, root);
    }

    private String getText(String binary, BrailleNode curr) {
        if (curr == null) return "";
        if (binary.isEmpty()) return curr.getText();

        if (binary.charAt(0) == '0') {
            return getText(binary.substring(1), curr.getLeft());
        } else {
            return getText(binary.substring(1), curr.getRight());
        }
    }

    public String getBraille(String text) {
        return getBraille(text, root, "");
    }

    private String getBraille(String text, BrailleNode curr, String path) {
        if (curr == null) return "";
        if (curr.getText().equals(text)) return path;

        String leftPath = getBraille(text, curr.getLeft(), path + "0");
        if (!leftPath.isEmpty()) return leftPath;

        return getBraille(text, curr.getRight(), path + "1");
    }

    public void translateFile(String infile, String outfile) {
        try (Scanner scanner = new Scanner(new File(infile));
             FileWriter writer = new FileWriter(outfile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringBuilder translatedLine = new StringBuilder();
                
                for (int i = 0; i < line.length(); i += 6) {  // Assuming 6-bit Braille encoding
                    String brailleChar = line.substring(i, Math.min(i + 6, line.length()));
                    translatedLine.append(getText(brailleChar));
                }
                
                writer.write(translatedLine.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    private class BrailleNode {
        private String text;
        private BrailleNode left;
        private BrailleNode right;

        public BrailleNode(String data) {
            this.text = data;
            this.left = null;
            this.right = null;
        }

        public String getText() { return text; }
        public void setText(String data) { this.text = data; }
        public BrailleNode getLeft() { return left; }
        public void setLeft(BrailleNode left) { this.left = left; }
        public BrailleNode getRight() { return right; }
        public void setRight(BrailleNode right) { this.right = right; }
    }
}
