package swing;

import papago.PapagoAPI;
import papago.PapagoLogin;
import ruo.io.EnumMCFolder;
import ruo.io.FileUtil;

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
    private static JButton translateButton, translateAllButton;
    private static JScrollPane langReadJListScoll;
    private static JMenuBar jMenuBar;
    private static JMenu jMenu;
    private static JMenuItem jMenuItemOri, jMenuItemRP,jMenuItemRPOpen;
    public static final MinecraftJar MC_LANG = new MinecraftJar();
    public MainNew() {

        MC_LANG.loadMCProperties();

        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("설정");
        jMenuItemOri = new JMenuItem("원래 내용도 같이 넣기 : O");
//        jMenuItemOri.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (jMenuItemOri.getText().equals("원래 내용도 같이 넣기 : O")) {
//                    jMenuItemOri.setText("원래 내용도 같이 넣기 : X");
//                    addOriginal = false;
//                } else {
//                    jMenuItemOri.setText("원래 내용도 같이 넣기 : O");
//                    addOriginal = true;
//                }
//            }
//        });
        jMenuItemRP = new JMenuItem("리소스팩 만들기");
        jMenuItemRP.addActionListener(this);
        jMenuItemRPOpen = new JMenuItem("리소스팩 폴더 열기");
        jMenuItemRPOpen.addActionListener(this);
//        jMenu.add(jMenuItemOri);
        jMenu.add(jMenuItemRP);
        jMenu.add(jMenuItemRPOpen);
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        JPanel panel = new JPanel();
        JPanel buttonPanel = new JPanel();
        modFileJList = new JList<String>();
        modFileJList.addListSelectionListener(this);
        JScrollPane modFileJListScroll = new JScrollPane(modFileJList);
        panel.add(modFileJListScroll);

        langReadJList = new JList<String>();
        langReadJList.addListSelectionListener(this);
        langReadJList.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXX");
        langReadJListScoll = new JScrollPane(langReadJList);

        add(panel);
        add(langReadJListScoll);

        translateButton = new JButton("선택한 것만 번역");
        translateButton.addActionListener(this);
        buttonPanel.add(translateButton);

        translateAllButton = new JButton("모두 번역");
        translateAllButton.addActionListener(this);
        buttonPanel.add(translateAllButton);
        add(buttonPanel);
        setVisible(true);
        try {
            fileLoad(new File(FileUtil.getMinecraftFolder(EnumMCFolder.MODS)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new PapagoLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fileLoad(File modFolder) throws IOException {
        Vector<String> modFileList = new Vector<>();
        for (File jarFile : modFolder.listFiles()) {
            if (jarFile.isFile()) {
                ModLangMap.createModLang(jarFile);
                modFileList.add(jarFile.getName());
            }
        }
        modFileJList.setListData(modFileList);
    }

    public static int MAX_LENGTH = 10000;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getSource() == jMenuItemRPOpen){
                FileUtil.openFolder(new File(FileUtil.getMinecraftFolder(EnumMCFolder.RP)));
            }
            if (e.getSource() == jMenuItemRP) {
                showResourcePackDialog(this);
            }
            if (e.getSource() == translateAllButton) {
                updateSelectTextLength(true);

                if (selectTextLength > 0) {
                    if (showCheckMessage() == JOptionPane.YES_OPTION) {
                        Properties enProperties = getSelectModLang().getEnProperties();
                        Properties koProperties = getSelectModLang().getKoProperties();
                        for (Object obj : enProperties.keySet()) {
                            String key = (String) obj;
                            if (!getSelectModLang().isTranslate(key)) {
                                String translate = PapagoAPI.main(this, enProperties.getProperty(key));
                                if (translate == null)
                                    break;
                                koProperties.put(key, translate);
                                System.out.println(key + " - " + enProperties.getProperty(key) + " - " + koProperties.getProperty(key));
                            }
                        }
                        langReadJList.clearSelection();
                        getSelectModLang().propertiesSave();
                        showResourcePackDialog(this);
                    }
                }
            }
            if (e.getSource() == translateButton) {
                if (selectTextLength > 0) {
                    if (showCheckMessage() == JOptionPane.YES_OPTION) {
                        List<String> selectText = langReadJList.getSelectedValuesList();
                        Properties koProperties = getSelectModLang().getKoProperties();
                        int translateLength = 0;
                        for (int i = 0; i < selectText.size(); i++) {
                            translateLength += selectText.size();
                            if (translateLength < MAX_LENGTH) {
                                String key = getSelectModLang().findValueKey(selectText.get(i));
                                if (selectText.get(i).equalsIgnoreCase(koProperties.getProperty(key))) {
                                    String translate = PapagoAPI.main(this, selectText.get(i));
                                    if (translate == null)
                                        break;
                                    koProperties.put(key, translate);
                                    System.out.println(key + " - " + selectText.get(i) + " - " + koProperties.getProperty(key));
                                }
                            } else
                                break;
                        }
                        langReadJList.clearSelection();
                        getSelectModLang().propertiesSave();
                        showResourcePackDialog(this);
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void updateSelectTextLength(boolean all) {
        ModLang modLang = getSelectModLang();
        selectTextLength = 0;
        Iterable iterable = all ? modLang.getEnProperties().keySet() : langReadJList.getSelectedValuesList();

        for (Object obj : iterable) {
            String key = all ? (String) obj : getSelectModLang().findValueKey((String) obj);
            String engText = modLang.getEnProperties().getProperty(key);
            System.out.println("key "+key + " - "+engText + " - "+modLang.isTranslate(key));
            if (!modLang.isTranslate(key)) {
                selectTextLength += engText.length();
            }
        }
    }

    public int showCheckMessage() {
        String warningMessage = "파파고는 하루에 만글자까지만 번역할 수 있습니다. 할 수 있는 것만 할까요? 현재 글자 수:" + selectTextLength;
        String translateMessage = "번역할 글자 수는 " + selectTextLength + "자입니다. 번역할까요?(이미 번역된 문장 제외)";
        int option = JOptionPane.showConfirmDialog(this, selectTextLength > MAX_LENGTH ? warningMessage : translateMessage, "번역", JOptionPane.YES_NO_OPTION);
        return option;
    }

    public void showResourcePackDialog(Component component) throws IOException {
        int i = JOptionPane.showConfirmDialog(component, "번역이 끝났습니다. 리소스팩을 만들까요? (만들어진 리소스팩은 마인크래프트 폴더 \\resourcepacks에 저장됩니다)", "번역", JOptionPane.YES_NO_OPTION);
        if (i == JOptionPane.YES_OPTION) {
            FileUtil.copyFolder("./번역", FileUtil.getMinecraftFolder(EnumMCFolder.RP, "번역 리소스팩"));
        }
    }

    public String getSelectFileName() {
        return modFileJList.getSelectedValue();
    }

    public ModLang getSelectModLang() {
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
            updateSelectTextLength(false);
        }
    }
}
