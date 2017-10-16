// Graph class
import java.util.ArrayList;

public class Graph
{
    // 随机游走使用
    private int randomWalkStart;
    private int randomWalkLast;

    // 图中点数
    private int pointNumber;

    // 动态存储节点
    private ArrayList<ArrayList<Edge>> memory = new ArrayList<>();

    // 待染色的节点
    private ArrayList<Integer> toColorPoints = new ArrayList<>();

    Graph()
    {
        pointNumber=0;
        memory.add(new ArrayList<>());
    }

    // 添加一个节点
    public void addPoint()
    {
        memory.add(new ArrayList<>());
        pointNumber++;
    }

    // 添加一条边
    public void addEdge(int from,int to)
    {
        for(int i=0;i<memory.get(from).size();i++)
        {
            if(memory.get(from).get(i).getTo()==to)
            {
                int w=memory.get(from).get(i).getValue();
                memory.get(from).get(i).setValue(w+1);
                return;
            }
        }
        memory.get(from).add(new Edge(from,to,1));
    }
    // 用迪杰斯特拉算法求最短路
    private int[] dijkstra(int from)
    {
        int[] dist = new int[pointNumber];
        boolean[] vis = new boolean[pointNumber];
        int[] pre = new int[pointNumber];
        final int INF=0x3f3f3f3f;
        for(int i=0;i<pointNumber;i++)
        {
            dist[i]=INF;
            vis[i]=false;
            pre[i]=-1;
        }
        dist[from]=0;
        pre[from]=-1;
        for(int t=0;t<pointNumber;t++)
        {
            int minDist=INF;
            int u=-1;
            for(int i=0;i<pointNumber;i++)
                if(!vis[i] && dist[i]<minDist)
                {
                    minDist=dist[i];
                    u=i;
                }
            if(u==-1) break;
            vis[u]=true;
            for(int i = 0; i< memory.get(u).size(); i++)
            {
                int v=memory.get(u).get(i).getTo();
                int w=memory.get(u).get(i).getValue();
                if(dist[u]+w<dist[v])
                {
                    dist[v]=dist[u]+w;
                    pre[v]=u;
                }
            }
        }
        return pre;
    }
    private ArrayList<Integer> getPath(int pre[],int from,int to,boolean color)
    {
        ArrayList<Integer> ret=new ArrayList<Integer>();
        if(pre[to]==-1) return ret;
        int x=to;
        while(pre[x]!=-1)
        {
            toColorPoints.add(x);
            ret.add(x);
            x=pre[x];
        }
        ret.add(from);
        toColorPoints.add(from);
        for(int i=ret.size()-1;i>0;i--)
        {
            int u=ret.get(i);
            int v=ret.get(i-1);
            for(int j=0;j<memory.get(u).size();j++)
                if(memory.get(u).get(j).getTo()==v)
                {
                    memory.get(u).get(j).setColor("red");
                    break;
                }
        }
        return ret;
    }
    // 找到最短路，并返回
    public ArrayList<Integer> findShortestPath(int from,int to)
    {
        clearColor();
        return getPath(dijkstra(from),from,to,true);
    }
    // 找到全部最短路，并返回
    public ArrayList<ArrayList<Integer>> findAllShortestPath(int from)
    {
        ArrayList<ArrayList<Integer>> ret=new ArrayList<>();
        int pre[]=dijkstra(from);
        for(int i=0;i<pointNumber;i++)
            if(i!=from)
            {
                ret.add(getPath(pre,from,i,false));
                ret.get(ret.size()-1).add(i);
            }
        return ret;
    }

    // 启动随机游走
    public void randomWalk()
    {
        clearColor();
        randomWalkStart=(int)(Math.random()*pointNumber-0.1);
        toColorPoints.add(randomWalkStart);
        randomWalkLast=-1;
        for(int i=0;i<pointNumber;i++)
            for(int j=0;j<memory.get(i).size();j++)
                memory.get(i).get(j).setVis(false);
    }
    // 找出随机游走可选的边
    private ArrayList<Integer> getToSelect(ArrayList<Edge> s)
    {
        ArrayList<Integer> ret = new ArrayList<>();
        for(int i=0;i<s.size();i++)
            if(!s.get(i).getVis())
                ret.add(i);
        return ret;
    }
    // 随机游走一步
    public Edge randomWalkStep()
    {
        ArrayList<Integer> toSelect= getToSelect(memory.get(randomWalkStart));
        if(toSelect.size()==0) return new Edge(-1,-1,-1);
        if(randomWalkLast!=-1)
        {
            for(int i=0;i<memory.get(randomWalkLast).size();i++)
            {
                if(memory.get(randomWalkLast).get(i).getTo()==randomWalkStart)
                {
                    memory.get(randomWalkLast).get(i).setColor("red");
                    break;
                }
            }
        }
        int from=randomWalkStart;
        int to=toSelect.get((int)(Math.random()*toSelect.size()-0.1));
        memory.get(randomWalkStart).get(to).setVis(true);
        memory.get(randomWalkStart).get(to).setColor("green");
        to=memory.get(randomWalkStart).get(to).getTo();
        toColorPoints.add(to);
        randomWalkLast=randomWalkStart;
        randomWalkStart=to;
        return new Edge(from,to,-1);
    }

    // 找到所有桥接词并返回
    public ArrayList<Integer> getWays(int from,int to)
    {
        ArrayList<Integer> ret = new ArrayList<>();
        for(int i=0;i<memory.get(from).size();i++)
        {
            int v=memory.get(from).get(i).getTo();
            for(int j=0;j<memory.get(v).size();j++)
            {
                int x=memory.get(v).get(j).getTo();
                if(x==to)
                {
                    ret.add(v);
                    break;
                }

            }
        }
        clearColor();
        toColorPoints.addAll(ret);
        return ret;
    }

    // 返回所有边
    public ArrayList<Edge> getAllEdges()
    {
        ArrayList<Edge> ret=new ArrayList<>();
        for (ArrayList<Edge> aMemory : memory)
            ret.addAll(aMemory);
        return ret;
    }

    // 清除所有颜色
    public void clearColor()
    {
        toColorPoints.clear();
        for (ArrayList<Edge> aMemory : memory)
            for (Edge anAMemory : aMemory) anAMemory.setColor("black");
    }

    // 返回需要涂色的点
    public ArrayList<Integer> getToColorPoints()
    {
        return toColorPoints;
    }

    public static void main(String[] args)
    {
        Graph g=new Graph();
        g.addPoint();
        g.addPoint();
        g.addPoint();
        g.addPoint();
        g.addEdge(0,1);
        g.addEdge(0,2);
        g.addEdge(1,2);
        g.addEdge(1,2);
        g.addEdge(1,2);
        g.addEdge(1,3);
        g.addEdge(0,3);
        g.addEdge(0,3);
        g.addEdge(0,3);
        g.addEdge(0,3);
        g.addEdge(0,3);

        ArrayList<Integer> tmpInt;
        ArrayList<Edge> tmpEdge;

        // 测试从0到3的路径
        tmpInt=g.getWays(0,3);
        for (Integer aTmpInt : tmpInt) System.out.println(aTmpInt);
        System.out.printf("----------------------\n");

        // 测试所有边
        tmpEdge=g.getAllEdges();
        for (Edge aTmpEdge : tmpEdge) System.out.printf("%d -> %d\n", aTmpEdge.getFrom(), aTmpEdge.getTo());
        System.out.printf("----------------------\n");

        // 测试最短路
        tmpInt=g.findShortestPath(0,3);
        for (Integer aTmpInt : tmpInt) System.out.println(aTmpInt);
        System.out.printf("----------------------\n");

        // 测试随机游走
        g.randomWalk();
        Edge tmp=g.randomWalkStep();
        System.out.printf("%d -> %d\n",tmp.getFrom(),tmp.getTo());
        tmp=g.randomWalkStep();
        System.out.printf("%d -> %d\n",tmp.getFrom(),tmp.getTo());
        tmp=g.randomWalkStep();
        System.out.printf("%d -> %d\n",tmp.getFrom(),tmp.getTo());
        tmp=g.randomWalkStep();
        System.out.printf("%d -> %d\n",tmp.getFrom(),tmp.getTo());
        System.out.printf("----------------------\n");
    }
}
