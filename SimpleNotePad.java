// Author       :   Alex Kourkoumelis
// Date         :   6/11/2019
// Title        :   SimpleNotepad
// Description  :   Creates a simple notepad app that has basic features:
//              :   New File, Open File, Open Recent Files, Save File, Print File,
//              :   Undo, Copy, Paste, Replace

package notepad;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
public class SimpleNotePad extends JFrame implements ActionListener, MenuListener {
    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu editMenu = new JMenu("Edit");
    private JTextPane textPane = new JTextPane();
    private JMenuItem newFileButton = new JMenuItem("New File");
    private JMenuItem openFileButton = new JMenuItem("Open File");
    private JMenu openRecentFile = new JMenu("Open Recent Files");
    private JMenuItem saveFileButton = new JMenuItem("Save File");
    private JMenuItem printFileButton = new JMenuItem("Print File");
    private JMenuItem undoButton = new JMenuItem("Undo");
    private JMenuItem copyButton = new JMenuItem("Copy");
    private JMenuItem pasteButton = new JMenuItem("Paste");
    private JMenuItem replaceButton = new JMenuItem("Replace");
    private Stack<String> recentFiles = new Stack<>();
    public SimpleNotePad() {
        setTitle("A Simple Notepad Tool");
        fileMenu.add(newFileButton);
        fileMenu.addSeparator();
        fileMenu.add(openFileButton);
        fileMenu.addSeparator();
        fileMenu.add(openRecentFile);
        fileMenu.addSeparator();
        fileMenu.add(saveFileButton);
        fileMenu.addSeparator();
        fileMenu.add(printFileButton);
        editMenu.add(undoButton);
        editMenu.add(copyButton);
        editMenu.add(pasteButton);
        editMenu.add(replaceButton);
        newFileButton.addActionListener(this);
        newFileButton.setActionCommand("new");
        openFileButton.addActionListener(this);
        openFileButton.setActionCommand("open");
        openRecentFile.addMenuListener(this);
        openRecentFile.setActionCommand("recent");
        saveFileButton.addActionListener(this);
        saveFileButton.setActionCommand("save");
        printFileButton.addActionListener(this);
        printFileButton.setActionCommand("print");
        copyButton.addActionListener(this);

        copyButton.setActionCommand("copy");
        pasteButton.addActionListener(this);
        pasteButton.setActionCommand("paste");
        undoButton.addActionListener(this);
        undoButton.setActionCommand("undo");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
        add(new JScrollPane(textPane));
        setPreferredSize(new Dimension(600,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }
    public static void main(String[] args) {
        SimpleNotePad app = new SimpleNotePad();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("new")) {
            newFile();
        }else if(e.getActionCommand().equals("save")) {
            saveFile();
        }else if(e.getActionCommand().equals("print")) {
            printFile();
        }else if(e.getActionCommand().equals("copy")) {
            copy();
        }else if(e.getActionCommand().equals("paste")) {
            paste();
        }else if(e.getActionCommand().equals("undo")) {
            undo();
        }else if(e.getActionCommand().equals("open")) {
            open();
        }else if(recentFiles.contains(e.getActionCommand())) {
            String filePath = e.getActionCommand();
            open(filePath);
        }
    }
    public void open() {
        JFileChooser openFileFC = new JFileChooser();
        int returnValOpen = openFileFC.showOpenDialog(null);
        if (returnValOpen == JFileChooser.APPROVE_OPTION) {
            String filePath = openFileFC.getSelectedFile().getAbsolutePath();
            open(filePath);
        }
    }

    public void open(String filePath) {
        String line = "";
        try {
            Scanner textReader = new Scanner(new FileReader(filePath));
            pushRecentFiles(filePath); //filePath is stored in Stack

            while (textReader.hasNext()) {
                line += textReader.nextLine() + '\n';
            }

            textPane.setText(line);
        }
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open");
        }
    }

    public void pushRecentFiles(String filePath)
    {
        if (recentFiles.contains(filePath)) {
            recentFiles.remove(filePath);
        }

        recentFiles.push(filePath);
    }

    // Sets the text pane to "" to emulate creating a new file.
    private void newFile() {
        textPane.setText("");
    }

    // saves the file
    private void saveFile() {
        File fileToWrite = null;
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            fileToWrite = fc.getSelectedFile();
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileToWrite));
            out.println(textPane.getText());
            JOptionPane.showMessageDialog(null, "File is saved successfully...");
            out.close();
        } catch (IOException ex) {
        }
    }

    // prints the file
    private void printFile() {
        try{
            PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Sample Command Pattern");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    if (pageNum>0)
                        return Printable.NO_SUCH_PAGE;
                    pg.drawString(textPane.getText(), 500, 500);
                    paint(pg);
                    return Printable.PAGE_EXISTS;
                }
            });

            if (pjob.printDialog() == false)
                return;
            pjob.print();
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(null,
                    "Printer error" + pe, "Printing error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // copies the text
    private void copy() {
        textPane.copy();
    }

    // pastes the copied text
    private void paste() {
        StyledDocument doc = textPane.getStyledDocument();
        Position position = doc.getEndPosition();
        System.out.println("offset"+position.getOffset());
        textPane.paste();
    }

    // undo most recent action
    private void undo() {

    }

    @Override
    public void menuSelected(MenuEvent e) {
        generateRecentFiles();
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        openRecentFile.removeAll();
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        // leave blank
    }

    public void generateRecentFiles()
    {
        Stack<String> duplicatedStack = generateDuplicateStack();
        while (!duplicatedStack.isEmpty()) {
            String filePath = duplicatedStack.pop();
            JMenuItem menuItem = new JMenuItem(filePath);
            openRecentFile.add(menuItem);
            menuItem.addActionListener(this);
            menuItem.setActionCommand(filePath);
            menuItem.setVisible(true);
        }
    }

    public Stack<String> generateDuplicateStack() {
        Stack<String> tempStack = new Stack<>();
        Stack<String> duplicateStack = new Stack<>();
        int count = 0;
        while (!recentFiles.isEmpty() && count < 5) {
            String poppedValue = recentFiles.pop();
            tempStack.push(poppedValue);
            count++;
        }

        while (!tempStack.isEmpty()) {
            String poppedValue = tempStack.pop();
            duplicateStack.push(poppedValue);
            recentFiles.push(poppedValue);
        }

        return duplicateStack;
    }
}