import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import java.awt.Font;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    private static File cache;
    private static String os = System.getProperty("os.name");
    private static boolean enabled;
    private static String path;
    private static JFrame frame;
    private static JButton toggle;
    
    public static void main(String[] args)
    {
        if (os.toLowerCase().indexOf("nix") != -1) cache = new File(".cacheRobloxSteamVRDisable.txt");
        else cache = new File("cacheRobloxSteamVRDisable.txt");
        if (cache.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(cache));
                String cacheFetch = reader.readLine();
                verifyDirectory(cacheFetch);
            } catch (IOException e) {
                verifyDirectory("C:/Program Files (x86)/Steam/steamapps/common/SteamVR");
            }
        } else verifyDirectory("C:/Program Files (x86)/Steam/steamapps/common/SteamVR");
    }
    public static void verifyDirectory(String testPath)
    {
        File testFile = new File(testPath);
        File verificationFile = new File(testPath + "/bin/vrclient.dll"); //A random file unique to SteamVR's directory
        String properPath;
        if (testFile.exists() && verificationFile.exists()) {
            if (!(testFile.getName().equals("SteamVR") || testFile.getName().equals("SteamVR_robloxDisabled"))) {
                path = testPath.substring(0,testPath.indexOf(testFile.getName())).concat("SteamVR");
                testFile.renameTo(new File(path));
            } else path = testPath;
            writeCache(path);
            if (testFile.getName().equals("SteamVR")) enabled = true;
            else enabled = false;
            normalGUI();
        } else triggerPathSelection();
    }
    public static void writeCache(String path)
    {
        if (cache.exists()) cache.delete();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cache.getAbsolutePath()));
            writer.write(path);
            writer.flush();
            writer.close();
            if (os.toLowerCase().indexOf("nix") == -1) {
                Path cachePath = Paths.get(cache.getAbsolutePath());
                Files.setAttribute(cachePath,"dos:hidden",true,LinkOption.NOFOLLOW_LINKS);
            }
        } catch (IOException e) {
            triggerPathSelection();
        }
    }
    public static void initializeFrame(int sizeX, int sizeY)
    {
        if (frame != null) frame.dispose();
        frame = new JFrame();
            frame.setTitle("Roblox SteamVR Disable");
            frame.setSize(sizeX,sizeY);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLayout(new BorderLayout());
        Image icon = new ImageIcon("icon.png").getImage();
            frame.setIconImage(icon);
    }
    public static void triggerPathSelection()
    {
        initializeFrame(500,200);
        JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel label = new JLabel("SteamVR Directory:");
        JPanel directoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField textField = new JTextField("C:/Program Files (x86)/Steam/steamapps/common/SteamVR");
        JButton browse = new JButton("Browse");
            browse.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int option = fileChooser.showOpenDialog(frame);
                    if (option == JFileChooser.APPROVE_OPTION){
                       File selectedFile = fileChooser.getSelectedFile();
                       verifyDirectory(selectedFile.getAbsolutePath());
                    }
                }
            }
            );
            directoryPanel.add(textField);
            directoryPanel.add(browse);
            panel.add(Box.createVerticalGlue());
            panel.add(label);
            panel.add(directoryPanel);
            
            frame.add(panel);
            frame.setVisible(true);
    }
    public static void normalGUI()
    {
        initializeFrame(500,300);
        JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel description = new JLabel("For when you don't want to unplug your vr headset to play flatscreen Roblox");
            description.setFont(new Font("Arial",Font.PLAIN,12));
            description.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel functionLabel = new JLabel("Auto-launch SteamVR with Roblox:");
            functionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggle = new JButton();
            toggle.setFont(new Font("Arial",Font.BOLD,20));
            toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
            updateButton();
            toggle.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String renamedPath;
                    if (enabled) renamedPath = path.concat("_robloxDisabled");
                    else renamedPath = path.substring(0,path.length()-("_robloxDisabled".length()));
                    enabled = !enabled;
                    File current = new File(path);
                    File renamed = new File(renamedPath);
                    current.renameTo(renamed);
                    path = renamedPath;
                    writeCache(path);
                    updateButton();
                }
            }
            );
            panel.add(Box.createVerticalGlue());
            panel.add(description);
            panel.add(Box.createRigidArea(new Dimension(0,70)));
            panel.add(functionLabel);
            panel.add(Box.createRigidArea(new Dimension(0,10)));
            panel.add(toggle);
            panel.add(Box.createVerticalGlue());
            
        frame.add(panel,BorderLayout.CENTER);
        frame.setVisible(true);
    }
    public static void updateButton()
    {
        if (enabled) {
            toggle.setText("Enabled");
            toggle.setBackground(new Color(0,255,0));
        } else {
            toggle.setText("Disabled");
            toggle.setBackground(new Color(255,0,0));
        }
    }
}