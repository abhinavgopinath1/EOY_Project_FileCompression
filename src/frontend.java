import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class frontend extends JPanel implements ActionListener {

    private JButton codeButton;
    private JButton decodeButton;
    private JButton cancelButton;

    private File file;
    private List<File> files;
    private JLabel labelFile;

    private JProgressBar jProgressBar;


    public frontend(){

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returningValue = jFileChooser.showOpenDialog(frontend.this);

        if(returningValue == jFileChooser.APPROVE_OPTION){

            file = jFileChooser.getSelectedFile();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createGUI();
                }
            });

        } else {

            System.exit(0);

        }

    }

    private void createGUI(){

        JLabel directoryLabel = new JLabel(file.getAbsolutePath());
        JPanel infoPanel  = new JPanel();
        JFrame jFrame = new JFrame("Huffman Encoder/Decoder");

        labelFile = new JLabel(" ");
        jProgressBar = new JProgressBar();

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.add(directoryLabel);
        infoPanel.add(labelFile);
        infoPanel.add(jProgressBar);

        JPanel labelButtons = new JPanel();

        labelButtons.setLayout(new BoxLayout(labelButtons, BoxLayout.LINE_AXIS));
        labelButtons.setBorder(BorderFactory.createEmptyBorder(0,10, 10,10));
        labelButtons.add(Box.createHorizontalGlue());
        labelButtons.add(Box.createRigidArea(new Dimension(10,0)));

        codeButton = new JButton("EnCode");
        decodeButton = new JButton("DeCode");
        cancelButton = new JButton("Cancel");

        codeButton.addActionListener(this);
        decodeButton.addActionListener(this);
        cancelButton.addActionListener(this);

        labelButtons.add(codeButton);
        labelButtons.add(decodeButton);
        labelButtons.add(cancelButton);

        Container containerPanel = jFrame.getContentPane();
        containerPanel.add(infoPanel, BorderLayout.CENTER);
        containerPanel.add(labelButtons, BorderLayout.CENTER);

        jFrame.pack();
        jFrame.setVisible(true);

    }


    private List<File> makingFileList(File fileFolder, List<File> files){

        if(fileFolder.isDirectory()){

            File[] filesArray = fileFolder.listFiles();

            for(File file: filesArray){

                if(fileFolder.isDirectory()){
                    makingFileList(file,files);
                } else {
                    files.add(file);
                }

            }
            return files;

        } else {

            files.add(fileFolder);
            return files;

        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == codeButton){
            jProgressBar.setIndeterminate(true);
            files = makingFileList(file, new LinkedList<File>());
            labelFile.setText(files.size() + "files");
            jProgressBar.setMaximum(files.size());
            jProgressBar.setValue(0);
            codeButton.setEnabled(false);
            //Thread t = new Thread(new HuffmanCentralProcessor(files, filesLabel, progressBar));
            //t.start();
        }

        if(e.getSource() == decodeButton){
            jProgressBar.setIndeterminate(true);
            files = makingFileList(file, new LinkedList<File>());
            labelFile.setText(files.size() + "files");
            jProgressBar.setMaximum(files.size());
            jProgressBar.setValue(0);
            decodeButton.setEnabled(false);
            //Thread t = new Thread(new HuffmanCentralProcessor(files, filesLabel, progressBar));
            //t.start();
        }

        if(e.getSource() == cancelButton){

            System.exit(0);

        }



    }
}
