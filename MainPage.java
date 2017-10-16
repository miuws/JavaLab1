// Change in b1
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;

public class MainPage extends JFrame
{
    TextMaker textMaker = new TextMaker();
    // 内容面板
    private Container pane =getContentPane();

    // 菜单项
    private JMenu menu0=new JMenu("文件");
    private JMenuItem menu0_1=new JMenuItem("打开");
    private JMenuItem menu0_2=new JMenuItem("退出");
    private JMenu menu1=new JMenu("功能");
    private JMenuItem menu1_1=new JMenuItem("1. 展示有向图");
    private JMenuItem menu1_2=new JMenuItem("2. 查询桥接词");
    private JMenuItem menu1_3=new JMenuItem("3. 添加新文本");
    private JMenuItem menu1_4=new JMenuItem("4. 生成新文本");
    private JMenuItem menu1_5=new JMenuItem("5. 计算最短路径");
    private JMenuItem menu1_6=new JMenuItem("6. 随机游走");
    private JMenu menu2=new JMenu("帮助");
    private JMenuItem menu2_1=new JMenuItem("使用帮助");
    private JMenuItem menu2_2=new JMenuItem("关于作者");

    // 放置有向图的面板
    private JPanel directedGraphPanel = new JPanel();

    // 查询功能使用
    private JDialog queryFrame = new JDialog();
    private JTextField queryWord1= new JTextField(25);
    private JTextField queryWord2= new JTextField(25);
    private JButton querySubmit = new JButton("确认");

    // 添加新文本使用
    private JDialog addFrame = new JDialog();
    private JTextArea addWord = new JTextArea(6,40);
    private JButton addSubmit = new JButton("确认");

    // 添加桥接词使用
    private JDialog addTextFrame = new JDialog();
    private JTextArea addTextWord = new JTextArea(6,40);
    private JButton addTextSubmit = new JButton("确认");

    // 查询最短路使用
    private JDialog findPathFrame = new JDialog();
    private JTextField findPathWord1= new JTextField(25);
    private JTextField findPathWord2= new JTextField(25);
    private JButton findPathSubmit= new JButton("确认");

    // 随机游走使用
    private JMenuItem walkStartMenu = new JMenuItem("开始");
    private JMenuItem walkEndMenu = new JMenuItem("停止");
    String randomWalkPath;

    // 展示结果信息使用
    private JFrame infoBoard = new JFrame();

    // 展示文本使用
    private JPanel TextInfoPanel=new JPanel();

    // 功能按钮监听
    private class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JButton button=(JButton) e.getSource();
            if(button==querySubmit)         // 查询桥接词
            {
                ArrayList<String> bridgeWords= textMaker.getBridgeWords(queryWord1.getText(),queryWord2.getText());
                StringBuilder bridge= new StringBuilder();
                if(bridgeWords==null) bridge.append("所查询的单词不存在！");
                else if(bridgeWords.size()==0) bridge.append("所查询的单词之间无桥接词！");
                else for (String bridgeWord : bridgeWords) bridge.append(bridgeWord).append("\n");
                showInformation(bridge.toString());
                showDirectedGraph();
            }
            else if(button==addTextSubmit)
            {
                showInformation(textMaker.getNewTextFromBridge(addTextWord.getText()));
            }
            else if(button==addSubmit)      // 添加新文本
            {
                textMaker.addText(addWord.getText());
                textMaker.exhibitGraph();
                showDirectedGraph();
                setTextBoard(textMaker.getHandledText(),textMaker.getOriginText());
            }
            else if(button==findPathSubmit) // 查找最短路
            {
                // 查找全部最短路
                if(Objects.equals(findPathWord2.getText(), ""))
                {
                    ArrayList<ArrayList<String>> allShortestPathStrings=textMaker.findAllShortestPath(findPathWord1.getText());
                    StringBuilder allShortestPath= new StringBuilder();
                    if(allShortestPathStrings==null) allShortestPath.append("所查询的单词不存在！");
                    else
                    {
                        for(int i=0;i<allShortestPathStrings.size();i++)
                        {
                            for(int j=allShortestPathStrings.get(i).size()-1;j>=0;j--)
                            {
                                if(j==allShortestPathStrings.get(i).size()-1)
                                {
                                    allShortestPath.append(allShortestPathStrings.get(i).get(j)).append(": ");
                                    if(allShortestPathStrings.get(i).size()==1) allShortestPath.append("两单词间无路径!\n");
                                    continue;
                                }
                                allShortestPath.append(allShortestPathStrings.get(i).get(j));
                                if(j==0) allShortestPath.append("\n");
                                else allShortestPath.append("->");
                            }
                        }
                    }
                    showInformation(allShortestPath.toString());
                }
                // 查找一条最短路
                else
                {
                    ArrayList<String> shortestPathStrings= textMaker.findShortestPath(findPathWord1.getText(),findPathWord2.getText());
                    StringBuilder shortestPath= new StringBuilder();
                    if(shortestPathStrings==null) shortestPath.append("所查询的单词不存在！");
                    else if(shortestPathStrings.size()==0) shortestPath.append("所查询的单词之间无路径！");
                    else
                    {
                        for(int i=shortestPathStrings.size()-1;i>=0;i--)
                        {
                            shortestPath.append(shortestPathStrings.get(i));
                            if(i==0) shortestPath.append("\n");
                            else shortestPath.append("->");
                        }
                    }
                    showDirectedGraph();
                    showInformation(shortestPath.toString());
                }
            }

        }
    }

    // 菜单事件监听
    private class MenuListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JMenuItem menuSource=(JMenuItem)e.getSource();

            if(menuSource==menu0_1)             // 打开文件
            {
                JFileChooser fileOpener=new JFileChooser();
                fileOpener.setFileSelectionMode(JFileChooser.FILES_ONLY );
                fileOpener.showOpenDialog(new JLabel());
                File file=fileOpener.getSelectedFile();
                if(file!=null && file.isFile())
                {
                    textMaker.readText(file.getAbsolutePath());
                    textMaker.exhibitGraph();
                    showDirectedGraph();
                    setTextBoard(textMaker.getHandledText(),textMaker.getOriginText());
                }
            }
            else if (menuSource==menu1_1)       // 展示有向图
            {
                textMaker.clearColor();
                textMaker.exhibitGraph();
                showDirectedGraph();
            }
            else if (menuSource==menu1_2)       // 查询桥接词
            {
                setQueryFrame();
            }
            else if (menuSource==menu1_3)       // 添加新文本
            {
                setAddFrame();
            }
            else if (menuSource==menu1_4)       // 添加新文本
            {
                setAddTextFrame();
            }
            else if (menuSource==menu1_5)       // 查找最短路
            {
                setFindPathFrame();
            }
            else if (menuSource==menu1_6)       // 随机游走
            {
                JMenuBar menu = new JMenuBar();
                showDirectedGraph();
                menu.add(walkStartMenu);
                menu.add(walkEndMenu);
                setJMenuBar(menu);
                repaint();
                setVisible(true);
            }
            else if(menuSource==menu2_1)        // 帮助
            {
                String help="本次作业按照作业要求实现了如下功能:\n" +
                        "1.展示有向图\n2.查询桥接词\n3.添加新文本\n4.生成新文本\n5.查询最短路\n6.随机游走\n" +
                        "读入的文本在左侧文本框中显示，修改后有向图会随之更新。\n" +
                        "随机游走时走过的路用红色标记，当前走的路用绿色标记。\n"+
                        "最短路在图中用红色点和红色边标出。\n"+
                        "查询到的桥接词用红色在图中标出。\n"+
                        "生成的有向图以DotGraph.jpg保存在程序当前目录下。\n"+
                        "随机游走路径以randomWalkPath.txt的形式保存在程序当前目录下。\n" ;
                showInformation(help,"使用帮助");
            }
            else if(menuSource==menu2_2)        // 关于
            {
                String about="GUI:张冠华 1150310323\n图:王姗 1150310302\n";
                showInformation(about,"关于作者");
            }
            else if(menuSource==menu0_2)        // 退出
                System.exit(0);
            else if(menuSource==walkStartMenu)  // 随机游走开始
            {
                if(Objects.equals(walkStartMenu.getText(), "开始"))
                {
                    textMaker.randomWalk();
                    randomWalkPath="";
                }
                String edge=textMaker.randomWalkOnce();
                if(Objects.equals(edge, "")) randomWalkPath+="无可游走的节点!\n";
                else randomWalkPath+=(edge+"\n");
                setTextBoard(textMaker.getHandledText(),randomWalkPath);
                walkStartMenu.setText("继续");
                showDirectedGraph();
            }
            else if(menuSource==walkEndMenu)    // 随机游走结束
            {
                textMaker.clearColor();
                setTextBoard(textMaker.getHandledText(),textMaker.getOriginText());
                walkStartMenu.setText("开始");
                writeByFileOutputStream("randomWalkPath.txt",randomWalkPath);
                setMenu();
                repaint();
                setVisible(true);
            }
        }
    }
    public void writeByFileOutputStream(String address,String toWrite)
    {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(address);
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = toWrite.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 添加监听器
    private void setListener()
    {
        MenuListener MenuHandler = new MenuListener();
        menu0_1.addActionListener(MenuHandler);
        menu0_2.addActionListener(MenuHandler);
        menu1_1.addActionListener(MenuHandler);
        menu1_2.addActionListener(MenuHandler);
        menu1_3.addActionListener(MenuHandler);
        menu1_4.addActionListener(MenuHandler);
        menu1_5.addActionListener(MenuHandler);
        menu1_6.addActionListener(MenuHandler);
        menu2_1.addActionListener(MenuHandler);
        menu2_2.addActionListener(MenuHandler);
        walkStartMenu.addActionListener(MenuHandler);
        walkEndMenu.addActionListener(MenuHandler);

        ButtonListener ButtonHandler = new ButtonListener();
        querySubmit.addActionListener(ButtonHandler);
        addTextSubmit.addActionListener(ButtonHandler);
        addSubmit.addActionListener(ButtonHandler);
        findPathSubmit.addActionListener(ButtonHandler);
    }

    // 设置菜单
    private void setMenu()
    {
        JMenuBar menu = new JMenuBar();
        menu.add(menu0);
        menu.add(menu1);
        menu.add(menu2);
        menu0.add(menu0_1);
        menu0.add(menu0_2);
        menu1.add(menu1_1);
        menu1.add(menu1_2);
        menu1.add(menu1_3);
        menu1.add(menu1_4);
        menu1.add(menu1_5);
        menu1.add(menu1_6);
        menu2.add(menu2_1);
        menu2.add(menu2_2);
        setJMenuBar(menu);
    }

    // 展示有向图
    private void showDirectedGraph()
    {
        directedGraphPanel.removeAll();
        directedGraphPanel.setBounds((int)(0.28*getWidth()),(int)(0.01*getHeight()),(int)(0.7*getWidth()),(int)(0.9*getHeight()));

        JLabel directedGraphPicture = new JLabel();
        ImageIcon directedGraphIcon = new ImageIcon("DotGraph.jpg");
        directedGraphPicture.setSize(directedGraphPanel.getWidth(),directedGraphPanel.getHeight());
        double percent=min(min(1.0*directedGraphPicture.getWidth()/directedGraphIcon.getIconWidth(),
                1.0*directedGraphPicture.getHeight()/directedGraphIcon.getIconHeight()),1.0);
        directedGraphIcon.setImage(directedGraphIcon.getImage().getScaledInstance(
                (int)(percent*directedGraphIcon.getIconWidth()), (int)(percent*directedGraphIcon.getIconHeight()),
                Image.SCALE_DEFAULT));
        directedGraphPicture.setIcon(directedGraphIcon);
        directedGraphPicture.setHorizontalAlignment(SwingConstants.CENTER);

        directedGraphPanel.add(directedGraphPicture);
        directedGraphPanel.setLayout(null);
        pane.add(directedGraphPanel);
        repaint();
    }

    // 查询添加桥接词界面
    private void setQueryFrame()
    {
        Container queryFramePane = queryFrame.getContentPane();
        queryFrame.setTitle("请输入要查询的桥接词");
        queryFrame.setSize(400,180);
        JLabel hint1=new JLabel("开始词：");
        JLabel hint2=new JLabel("结束词");
        hint1.setBounds(20,20,80,30);
        queryWord1.setBounds(120,20,100,30);
        hint2.setBounds(20,80,80,30);
        queryWord2.setBounds(120,80,100,30);
        querySubmit.setBounds(250,40,100,40);
        queryFramePane.add(hint1);
        queryFramePane.add(queryWord1);
        queryFramePane.add(hint2);
        queryFramePane.add(queryWord2);
        queryFramePane.add(querySubmit);
        queryFrame.setLayout(null);
        queryFrame.setLocationRelativeTo(null);
        queryFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        queryFrame.setVisible(true);
    }

    // 展示生成新文本界面
    private void setAddTextFrame()
    {
        Container addTextFramePane = addTextFrame.getContentPane();
        addTextFrame.setTitle("请输入要生成的文本");
        addTextFrame.setSize(400,180);

        JScrollPane addTextScrollPane=new JScrollPane();
        addTextScrollPane.setBounds(20,20,200,100);
        TextInfoPanel.add(addTextScrollPane,BorderLayout.CENTER);

        addTextWord.setLineWrap(true);                  // 自动换行
        addTextWord.setWrapStyleWord(true);             // 换行不断字
        addTextScrollPane.setViewportView(addTextWord);
        addTextFramePane.add(addTextScrollPane);

        addTextSubmit.setBounds(250,40,100,40);
        addTextFramePane.add(addTextSubmit);
        addTextFrame.setLayout(null);
        addTextFrame.setLocationRelativeTo(null);
        addTextFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        addTextFrame.setVisible(true);
    }

    // 展示添加新文本界面
    private void setAddFrame()
    {
        Container addFramePane = addFrame.getContentPane();
        addFrame.setTitle("请输入要添加的新文本");
        addFrame.setSize(400,180);

        JScrollPane addScrollPane=new JScrollPane();
        addScrollPane.setBounds(20,20,200,100);
        TextInfoPanel.add(addScrollPane,BorderLayout.CENTER);

        addWord.setLineWrap(true);                  // 自动换行
        addWord.setWrapStyleWord(true);             // 换行不断字
        addScrollPane.setViewportView(addWord);
        addFramePane.add(addScrollPane);

        addSubmit.setBounds(250,40,100,40);
        addFramePane.add(addSubmit);
        addFrame.setLayout(null);
        addFrame.setLocationRelativeTo(null);
        addFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        addFrame.setVisible(true);
    }

    // 展示查询最短路界面
    private void setFindPathFrame()
    {
        Container findPathFramePane = findPathFrame.getContentPane();
        findPathFrame.setTitle("请输入要查询的最短路的起点和终点词");
        findPathFrame.setSize(400,180);
        JLabel hint1=new JLabel("起点词：");
        JLabel hint2=new JLabel("终点词");
        hint1.setBounds(20,20,80,30);
        findPathWord1.setBounds(120,20,100,30);
        hint2.setBounds(20,80,80,30);
        findPathWord2.setBounds(120,80,100,30);
        findPathSubmit.setBounds(250,40,100,40);
        findPathFramePane.add(hint1);
        findPathFramePane.add(findPathWord1);
        findPathFramePane.add(hint2);
        findPathFramePane.add(findPathWord2);
        findPathFramePane.add(findPathSubmit);
        findPathFrame.setLayout(null);
        findPathFrame.setLocationRelativeTo(null);
        findPathFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        findPathFrame.setVisible(true);
    }

    // 设置左侧的展示文本框
    private void setTextBoard(String text1,String text2)
    {
        TextInfoPanel.removeAll();
        TextInfoPanel.setBounds((int)(0.01*getWidth()),(int)(0.01*getHeight()),(int)(0.27*getWidth()),(int)(0.9*getHeight()));
        TextInfoPanel.setLayout(null);
        add(TextInfoPanel);

        JScrollPane textBoard1=new JScrollPane();
        textBoard1.setBounds(0,0,TextInfoPanel.getWidth(),TextInfoPanel.getHeight()/2);
        TextInfoPanel.add(textBoard1,BorderLayout.CENTER);
        JTextArea textArea1=new JTextArea(text1);
        textArea1.setEditable(false);
        textBoard1.setViewportView(textArea1);

        JScrollPane textBoard2=new JScrollPane();
        textBoard2.setBounds(0,TextInfoPanel.getHeight()/2,TextInfoPanel.getWidth(),TextInfoPanel.getHeight()/2);
        TextInfoPanel.add(textBoard2,BorderLayout.CENTER);
        JTextArea textArea2=new JTextArea(text2);
        textArea2.setEditable(false);
        textBoard2.setViewportView(textArea2);

        repaint();
        setVisible(true);
    }

    // 展示结果信息
    private void showInformation(String infoToShow)
    {
        showInformation(infoToShow,"查询结果为：");
    }
    private void showInformation(String infoToShow, String title)
    {
        JPanel contentPane=new JPanel();
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout(0,0));
        infoBoard.setContentPane(contentPane);
        JScrollPane scrollPane=new JScrollPane();
        contentPane.add(scrollPane,BorderLayout.CENTER);
        JTextArea textArea=new JTextArea(infoToShow);
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        infoBoard.setTitle(title);
        infoBoard.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        infoBoard.setLocationRelativeTo(null);
        infoBoard.setSize(400, 300);
        infoBoard.setDefaultCloseOperation(HIDE_ON_CLOSE);
        infoBoard.setVisible(true);
    }

    // 初始化
    private void initMainPage()
    {
        setTitle("软件工程第一次作业");
        setLayout(null);
        setMenu();
        setListener();
        setSize(1000,500);
        setVisible(true);
        setLocationRelativeTo(null);                // 居中显示
        setExtendedState(JFrame.MAXIMIZED_BOTH);    // 初始最大化
        setTextBoard("处理后的文本","读入的文本");
    }

    private MainPage()
    {
        initMainPage();
    }

    public static void main(String[] args)
    {
        new MainPage();
    }

}
