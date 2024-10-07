import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument; 

public class  MyFrame1 extends JFrame implements ActionListener {

    JButton button;

    MyFrame1() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        button = new JButton("Select PDF File");
        button.addActionListener(this);

        this.add(button);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setCurrentDirectory(new File(".")); // sets current directory

            int response = fileChooser.showOpenDialog(null); // select file to open

            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println("Selected file: " + selectedFile);

                // Check if the selected file is a PDF
                if (selectedFile.getName().endsWith(".pdf")) {
                    // Convert the PDF to Word
                    convertPdfToWord(selectedFile);
                } else {
                    System.out.println("Please select a valid PDF file.");
                }
            }
        }
    }

    // Method to convert PDF to Word
    public void convertPdfToWord(File pdfFile) {
        try {
            // Load the PDF document
            PDDocument document = PDDocument.load(pdfFile);

            // Define the output Word file path
            String outputFilePath = pdfFile.getAbsolutePath().replace(".pdf", ".docx");

            // Create a new Word document
            XWPFDocument wordDocument = new XWPFDocument();

            // Extract text from each page of the PDF and write to the Word document
            document.getPages().forEach(page -> {
                try {
                    String pageText = new org.apache.pdfbox.text.PDFTextStripper().getText(document);
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    paragraph.createRun().setText(pageText);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Save the Word document
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                wordDocument.write(out);
            }

            // Close the documents
            wordDocument.close();
            document.close();

            System.out.println("Conversion successful! Word file saved at: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyFrame1();
    }
}
