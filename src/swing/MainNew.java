package swing;

import papago.PapagoAPI;
import papago.PapagoLogin;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainNew extends JFrame implements ActionListener, ListSelectionListener {

    //파일 이름, 프로퍼티
    private static int selectTextLength = 0;
    private static JList<String> modFileJList;
    private static JList<String> langReadJList;
    private static JButton translateButton;
    private static JScrollPane langReadJListScoll;
    private static JMenuBar jMenuBar;
    private static JMenu jMenu;
    private static JMenuItem jMenuItemOri;
    public static boolean addOriginal = true;
    public static String MC_FOLDER = (System.getenv("APPDATA") + "/.minecraft");

    public MainNew() {
        new MinecraftJar().loadMC();
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
                if (jMenuItemOri.getText().equals("원래 내용도 같이 넣기 : O")) {
                    jMenuItemOri.setText("원래 내용도 같이 넣기 : X");
                    addOriginal = false;
                } else {
                    jMenuItemOri.setText("원래 내용도 같이 넣기 : O");
                    addOriginal = true;
                }
            }
        });

        jMenu.add(jMenuItemOri);
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        JPanel panel = new JPanel();

        modFileJList = new JList<String>();
        modFileJList.addListSelectionListener(this);
        JScrollPane modFileJListScroll = new JScrollPane(modFileJList);
        panel.add(modFileJListScroll);

        langReadJList = new JList<String>();
        langReadJList.addListSelectionListener(this);
        langReadJListScoll = new JScrollPane(langReadJList);
        langReadJListScoll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(langReadJListScoll);
        add(panel);

        translateButton = new JButton("선택한 문장만 번역");
        translateButton.addActionListener(this);
        add(translateButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new PapagoLogin();
            fileLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fileLoad() throws IOException {
        File modFolder = new File(MC_FOLDER + "/mods");
        Vector<String> modFileList = new Vector<>();

        for (File jarFile : modFolder.listFiles()) {
            if (jarFile.isFile()) {
                ModLangMap.createModLang(jarFile);
                modFileList.add(jarFile.getName());
            }
        }
        modFileJList.setListData(modFileList);
    }

    public static int MAX_LENGTH = 1000;
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == translateButton) {
                if (selectTextLength > 0) {
                    String checkMessage = selectTextLength > MAX_LENGTH ? "파파고는 하루에 만글자까지만 번역할 수 있습니다. 할 수 있는 것만 할까요? 현재 글자 수:" + selectTextLength : "번역할 글자 수는 " + selectTextLength + "자입니다. 번역할까요?(이미 번역된 문장 제외)";
                    int option = JOptionPane.showConfirmDialog(this, checkMessage, "번역", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        List<String> selectText = langReadJList.getSelectedValuesList();
                        Properties properties = getSelectModLang().getKoProperties();
                        int translateLength = 0;
                        for (int i = 0; i < selectText.size(); i++) {
                            translateLength += selectText.size();
                            if (translateLength < MAX_LENGTH) {
                                String key = getSelectModLang().findValueKey(selectText.get(i));
                                System.out.println(key + " - " + selectText.get(i) + " - "+properties.getProperty(key));
                                if (selectText.get(i).equalsIgnoreCase(properties.getProperty(key))) {
                                    String translate = PapagoAPI.main(this, selectText.get(i));
                                    properties.put(key, translate);
                                }
                            } else
                                break;
                        }
                        langReadJList.clearSelection();
                        getSelectModLang().propertiesSave();
                        openFile(this);
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public static void openFile(Component component) {
        int i = JOptionPane.showConfirmDialog(component, "번역이 끝났습니다. 파일을 열까요?", "번역", JOptionPane.YES_NO_OPTION);
        if (i == JOptionPane.YES_OPTION) {
            try {
                File file = new File(MC_FOLDER+"/resourcepacks/local/assets/" + getSelectModLang().getModID() + "/lang/ko_KR.lang");
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();// 메모장 열기 위한 코드
                    Runtime.getRuntime().exec(cmd);
                } else {
                    Desktop.getDesktop().edit(file);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String getSelectFileName() {
        return modFileJList.getSelectedValue();
    }

    public static ModLang getSelectModLang() {
        return ModLangMap.get(getSelectFileName());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == modFileJList) {
            Properties properties = ModLangMap.get(getSelectFileName()).getEnProperties();
            String[] str = new String[properties.values().size()];
            langReadJList.setListData(properties.values().toArray(str));
        }
        if (e.getSource() == langReadJList) {
            selectTextLength = 0;
            setTitle("번역할 글자 수 : " + selectTextLength);

            ModLang modLang = getSelectModLang();
            for (String str : langReadJList.getSelectedValuesList()) {
                if (modLang.isTranslate(modLang.findValueKey(str))) {
                    selectTextLength += str.length();
                    setTitle("번역할 글자 수 : " + selectTextLength);
                }
            }
        }
    }
}
