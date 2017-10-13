import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TextMaker
{
    private Graph graph = new Graph();
    GraphViz graphViz=new GraphViz();
    private HashMap<String,Integer> indexDict = new HashMap<>();
    private ArrayList<String> wordDict = new ArrayList<>();
    private String originText= "";
    private String handledText= "";

    private int getWordIndex(String word)   // 返回单词的下标，不存在则返回-1
    {
        return indexDict.getOrDefault(word, -1);
    }

    private String getWord(int index)       // 给定下标，返回单词
    {
        return wordDict.get(index);
    }

    private void addWord(String word)       // 向图中加入新词
    {
        indexDict.put(word,wordDict.size());
        wordDict.add(word);
        graph.addPoint();
    }

    private boolean isPunc(char c)
    {
        if(c==',') return true;
        if(c=='.') return true;
        if(c=='?') return true;
        if(c=='!') return true;
        if(c==':') return true;
        if(c==';') return true;
        if(c=='(') return true;
        if(c==')') return true;
        if(c=='[') return true;
        if(c==']') return true;
        if(c=='{') return true;
        if(c=='}') return true;
        return false;
    }
    private String preHandleLine(String line) // 去掉行中不合法字符
    {
        StringBuilder ret= new StringBuilder();
        for(int i=0;i<line.length();i++)
        {
            if (!(line.charAt(i)>=65&&line.charAt(i)<=90 || line.charAt(i)>=97&&line.charAt(i)<=122
                    || isPunc(line.charAt(i)) || line.charAt(i)==' '))
                ret.append(' ');
            else ret.append(line.charAt(i));
        }
        return ret.toString();
    }

    private String[] getWordFromLine(String line)   // 把行中的单词抽出来
    {
        StringBuilder ret= new StringBuilder();
        for(int i=0;i<line.length();i++)
        {
            if (isPunc(line.charAt(i)) || line.charAt(i)==' ')
            {
                if(ret.length()!=0 && ret.charAt(ret.length()-1)!=' ')
                    ret.append(' ');
            }
            else ret.append(line.charAt(i));
        }
        return ret.toString().split(" ");
    }

    private void solveLine(String line)         // 处理一行文本，把一行中的单词都加入图中
    {
        if(!Objects.equals(originText, "")) originText+="\n";
        originText+=line;
        line=preHandleLine(line);
        if(!Objects.equals(handledText, "")) handledText+="\n";
        handledText+=line;

        String words[] = getWordFromLine(line);
        if(words.length>=2)
        {
            if(getWordIndex(words[0])==-1)
                addWord(words[0]);
            for(int i=0;i<words.length-1;i++)
            {
                int u=getWordIndex(words[i]);
                if(getWordIndex(words[i+1])==-1) addWord(words[i+1]);
                int v=getWordIndex(words[i+1]);
                graph.addEdge(u,v);
            }
        }
    }

    public void addText(String text)            // 加入一段文本
    {
        StringBuilder line= new StringBuilder();
        for(int i=0;i<text.length();i++)
        {
            if('\n' == text.charAt(i))
            {
                solveLine(line.toString());
                line = new StringBuilder();
            }
            else {
                line.append(text.charAt(i));
            }
        }
        if(line.length()!=0) solveLine(line.toString());
    }

    // 读入文本，并加入图中
    public void readText(String address)
    {
        File file = new File(address);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null)
                solveLine(line);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // 展示图
    public void exhibitGraph()
    {
        final String dotFileName = "DotGraph";
        StringBuilder graphString= new StringBuilder();
        graphString.append("graph [ratio=0.75];");
        ArrayList<Integer> toColorPoints=graph.getToColorPoints();
        String colorOfWords[] = new String[wordDict.size()];
        for(int i=0;i<colorOfWords.length;i++)
            colorOfWords[i]="black";
        if(toColorPoints.size()!=0)
        {
            for (Integer toColorPoint : toColorPoints) {
                colorOfWords[toColorPoint]="red";
            }
        }
        for(int i=0;i<colorOfWords.length;i++)
        {
            if(Objects.equals(colorOfWords[i], "red"))
                graphString.append(getWord(i)).append("[style=").append("filled").append(", color=").append(colorOfWords[i]).append("];\n");
            else
                graphString.append(getWord(i)).append("[style=").append("solid").append(", color=").append(colorOfWords[i]).append("];\n");
        }
        ArrayList<Edge> edges = graph.getAllEdges();
        for (Edge edge : edges) {
            int u = edge.getFrom();
            int v = edge.getTo();
            int w = edge.getValue();
            String color=edge.getColor();
            graphString.append(getWord(u)).append("->").append(getWord(v));
            graphString.append("[");
            graphString.append("label=").append(w);
            graphString.append(", color=").append(color);
            graphString.append(']').append(";\n");
        }
        graphViz.createDotGraph(graphString.toString(), dotFileName);
    }

    // 给一组下标，返回单词们
    private ArrayList<String> int2stringArrayList(ArrayList<Integer> s)
    {
        ArrayList<String> ret=new ArrayList<>();
        for(int i=0;i<s.size();i++)
            ret.add(getWord(s.get(i)));
        return ret;
    }

    // 返回原始文本
    public String getOriginText()
    {
        return originText;
    }

    // 返回处理后的文本
    public String getHandledText()
    {
        return handledText;
    }

    // 返回桥接词
    public ArrayList<String> getBridgeWords(String from, String to)
    {
        if(getWordIndex(from)==-1 || getWordIndex(to)==-1) return null;
        ArrayList<String> ret=int2stringArrayList(graph.getWays(getWordIndex(from),getWordIndex(to)));
        exhibitGraph();
        return ret;
    }

    // 找到最短路径
    public ArrayList<ArrayList<String>> findAllShortestPath(String from)
    {
        if(getWordIndex(from)==-1) return null;
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tmp =graph.findAllShortestPath(getWordIndex(from));
        for (ArrayList<Integer> aTmp : tmp) {
            ret.add(int2stringArrayList(aTmp));
        }
        return ret;
    }
    public ArrayList<String> findShortestPath(String from,String to)
    {
        if(getWordIndex(from)==-1 || getWordIndex(to)==-1) return null;
        ArrayList<String> ret=int2stringArrayList(graph.findShortestPath(getWordIndex(from),getWordIndex(to)));
        exhibitGraph();
        return ret;
    }

    // 根据文本和桥接词生成新文本
    public String getNewTextFromBridge(String line)
    {
        line=preHandleLine(line);
        String words[]=getWordFromLine(line);
        StringBuilder ret= new StringBuilder();
        for(int i=0;i<words.length-1;i++)
        {
            ArrayList<String> bridgeWords=getBridgeWords(words[i],words[i+1]);
            ret.append(words[i]).append(" ");
            if(!(bridgeWords==null || bridgeWords.size()==0))
                ret.append(bridgeWords.get(0)).append(" ");
        }
        ret.append(words[words.length-1]);
        return ret.toString();
    }

    public void randomWalk()   // 启动随机游走
    {
        graph.randomWalk();
    }

    public String randomWalkOnce()   // 随机游走一步
    {
        Edge e = graph.randomWalkStep();
        exhibitGraph();
        if(e.getFrom()==-1) return "";
        else return getWord(e.getFrom())+"->"+getWord(e.getTo());
    }
    public void clearColor()
    {
        graph.clearColor();
    }

    TextMaker() {}

    public static void main(String[] args)
    {
        TextMaker textMaker = new TextMaker();
        textMaker.readText("C:\\Users\\ghzha\\IdeaProjects\\JavaLab1\\test.txt");
        textMaker.addText("Hello????baby...\nThis is ghzhang's testing!");
        System.out.println("--------------------------------");
        System.out.println(textMaker.getOriginText());
        System.out.println("--------------------------------");
        System.out.println(textMaker.getHandledText());
        System.out.println("--------------------------------");
        textMaker.exhibitGraph();
        System.out.println(textMaker.getNewTextFromBridge("This ghzhang testing"));
        System.out.println("--------------------------------");
        textMaker.randomWalk();
        System.out.println(textMaker.randomWalkOnce());
        System.out.println(textMaker.randomWalkOnce());
        System.out.println(textMaker.randomWalkOnce());
        System.out.println(textMaker.randomWalkOnce());
        System.out.println("--------------------------------");
        ArrayList<String> tmp;
        tmp=textMaker.findShortestPath("This","test");
        for(int i=tmp.size()-1;i>=0;i--)
        {
            System.out.printf(tmp.get(i));
            if(i!=0) System.out.printf("->");
        }
    }
}
//111