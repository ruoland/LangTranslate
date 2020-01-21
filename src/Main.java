import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Main extends JFrame implements ActionListener, ListSelectionListener {

    public static HashMap<String, OriginalProperties> originalProprerties = new HashMap<>();
    private static HashMap<String, TranslateProperties> translateProprerties = new HashMap();
    private static int selectTextLength = 0;
    private static JList<String> modFileJList;
    private static JList<String> langReadJList;
    private static JButton translateButton;
    private static JScrollPane langReadJListScoll;
    private static JMenuBar jMenuBar;
    private static JMenu jMenu;
    private static JMenuItem jMenuItemOri;
    public static boolean addOriginal = true;

    public static void main(String[] args) {
        try {
            fileLoad();
            new PapagoLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Main() {
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("설정");
        jMenuItemOri = new JMenuItem("원래 내용도 같이 넣기 : O");
        jMenuItemOri.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jMenuItemOri.getText().equals("원래 내용도 같이 넣기 : O")) {
                    jMenuItemOri.setText("원래 내용도 같이 넣기 : X");
                    addOriginal = false;
                }else {
                    jMenuItemOri.setText("원래 내용도 같이 넣기 : O");
                    addOriginal = true;
                }
            }
        });
        jMenu.add(jMenuItemOri);
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        JPanel panel = new JPanel();

        String[] originalKeySet = new String[originalProprerties.keySet().size()];
        modFileJList = new JList<String>(originalProprerties.keySet().toArray(originalKeySet));
        modFileJList.addListSelectionListener(this);
        JScrollPane modFileJListScroll = new JScrollPane(modFileJList);
        panel.add(modFileJListScroll);

        langReadJList = new JList<String>();
        langReadJList.addListSelectionListener(this);
        langReadJListScoll = new JScrollPane(langReadJList);
        langReadJListScoll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(langReadJListScoll);
        add(panel);

        translateButton = new JButton("선택한 문장 번역");
        translateButton.addActionListener(this);
        add(translateButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == translateButton) {
            int option = JOptionPane.showConfirmDialog(this, "번역할 글자 수는 " + selectTextLength + "자입니다. 번역할까요?(이미 번역된 문장 제외)", "번역", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                if(selectTextLength != 0) {
                    List<String> selectText = langReadJList.getSelectedValuesList();
                    TranslateProperties properties = translateProprerties.get(getSelectFileName());
                    for (int i = 0; i < selectText.size(); i++) {
                        String key = LangKey.getKey(selectText.get(i));
                        String translate = PapagoAPI.main(this, selectText.get(i));
                        properties.put(key, translate);
                    }
                    properties.store();
                }
                int i = JOptionPane.showConfirmDialog(this, "번역이 끝났습니다. 파일을 열까요?", "번역", JOptionPane.YES_NO_OPTION);
                if(i == JOptionPane.YES_OPTION){
                    try {
                        File file = new File("./" + getSelectFileName() + "/lang/ko_KR.lang");
                        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                            String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
                            Runtime.getRuntime().exec(cmd);
                        }
                        else {
                            Desktop.getDesktop().edit(file);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

    }

    public static OriginalProperties getOriginalProperties() {
        return originalProprerties.get(getSelectFileName());
    }

    public static TranslateProperties getTranslateProperties() {
        return translateProprerties.get(getSelectFileName());
    }

    public static String getSelectFileName() {
        return modFileJList.getSelectedValue();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == modFileJList) {
            Properties properties = getOriginalProperties();
            String[] str = new String[properties.values().size()];
            langReadJList.setListData(properties.values().toArray(str));
        }
        if (e.getSource() == langReadJList) {
            selectTextLength = 0;
            for (String str : langReadJList.getSelectedValuesList()) {
                String str2 = (String) getTranslateProperties().get(LangKey.getKey(str));
                System.out.println(str+ " - "+str2);
                if (str.equals(str2)) {

                    selectTextLength += str.length();
                }
            }
        }
    }

    public static void fileLoad() {
        File modFolder = new File("./mod");
        modFolder.mkdirs();
        for (File jarFile : modFolder.listFiles()) {
            OriginalProperties originalProperties = new OriginalProperties(jarFile);
            if (!originalProperties.isEmpty()) {
                originalProprerties.put(jarFile.getName(), new OriginalProperties(jarFile));
                createLangProperties(jarFile.getName());
            }
        }
    }


    public static void createLangProperties(String fileName) {
        try {
            createLangFile(fileName);
            TranslateProperties langProperties = new TranslateProperties(fileName);
            translateProprerties.put(fileName, langProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createLangFile(String fileName) throws IOException {
        Path path = Files.createDirectories(Paths.get("./" + fileName + "/lang"));
        File langFile = new File(path.toString(), "ko_KR.lang");
        langFile.createNewFile();
        return langFile;
    }
}
