import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.io.FileReader;
import java.util.LinkedList;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

public class Graph
{
	public static int INFINITY = 999;
	public static int MIN = -1;

	class Edge
	{
		public Vertex dest;
		public int cost;
		public Edge (Vertex d,int c) {
			dest = d;
			cost = c;
		}
	}

	class Vertex
	{
		public String name;
		public List<Edge> adj;
		public int dist;
		public Vertex prev;
		public int scratch;

		public Vertex (String nm) {
			name = nm;
			adj = new LinkedList<Edge>();
			scratch = 0;
			reset();
		}

		public void reset() {
			dist = Graph.INFINITY;
			prev = null;
		}
	}

	class Path implements Comparable<Path>
	{
		public Vertex dest;
		public int cost;

		public Path (Vertex d,int c) {
			dest = d;
			cost = c;
		}

		public int compareTo (Path rhs) {
			int otherCost = rhs.cost;
			return cost < otherCost ? -1: cost > otherCost ? 1 : 0;
		}
	}

	public Vertex getVertex (String nm) {
		Vertex v = vertexMap.get(nm);
		if(v == null){
			v = new Vertex(nm);
			vertexMap.put(nm,v);
		}
		return v;
	}

	public void addEdge(String sourceName, String destName) {
		Vertex v = getVertex (sourceName);
		Vertex w = getVertex (destName);
		for(Edge e : v.adj) {
			if(e.dest == w) {
				e.cost += 1;
				return;
			}
		}
        v.adj.add(new Edge(w , 1));
	}

	public void clearAll() {
		for(Vertex v : vertexMap.values()){
			v.reset();
		}
	}

	public void findShortestPath(String startname) {
		PriorityQueue<Path> pq = new PriorityQueue<Path>();
		Vertex start = vertexMap.get(startname);
		if(start == null) {
			throw new NoSuchElementException("Start vertex not found");
		}
		clearAll();
		pq.add(new Path(start, 0));
		start.dist = 0;
		int visited = 0;
		while (!pq.isEmpty() && visited < vertexMap.size()) {
			Path verc = pq.remove();
			Vertex v = verc.dest;
			if(v.scratch != 0) {
				continue;
			}

			v.scratch = 1;
			visited++;

			for(Edge e : v.adj) {
				Vertex w = e.dest;
				int cvw = e.cost;

				if(w.dist > v.dist + cvw) {
					w.dist = v.dist + cvw;
					w.prev = v;
					pq.add( new Path(w,w.dist));
				}
			}
		}
	}

	private void getPath(Vertex dest,List<String> path) {
		path.add(dest.name);
		if (dest.prev != null) {
			getPath(dest.prev,path);
		}
		//System.out.print(dest.name + " ");
	}

	public List<String> randomWalk(Vertex in) {
		int next = 1;
		List<Edge> visit = new LinkedList<Edge>();
		List<String> path = new LinkedList<String>();
		Vertex prev;
		Scanner get = new Scanner(System.in);
		do {
			prev = in;
			in = walk(prev,visit);
			if(in != null){
				path.add(prev.name + "->" + in.name);
			}
			else {
				break;
			}
			next = get.nextInt();
		}while(next == 1);
		return path;
	}

	public Vertex walk(Vertex in, List<Edge> visit) {
		int i = getIndex(in);
		if(i == -1) {
			return null;
		}
		else {
			Edge e = in.adj.get(i);
			if(visit.contains(e)) {
				return null;
			}
			visit.add(e);
			return e.dest;
		}
	}

	public List<String> getBridgeWords(String vname,String wname,List<String> bridgewords)  {
		Vertex v = getVertex(vname);
		Vertex w = getVertex(wname);
        if(v == null || w == null) {
            return bridgewords;
        }
		for(Edge e : v.adj) {
            for(Edge t : e.dest.adj) {
                if(t.dest == w) {
                	bridgewords.add(e.dest.name);
                    //System.out.print(e.dest.name + " ");
                }
            }
		}
		return bridgewords;
	}

	public String getNewText(String line) {
        StringTokenizer st = new StringTokenizer(line);
        int count = st.countTokens() , i = 1;
        String source, dest = st.nextToken(), newtxt = "";
        List<String> words = new LinkedList<String>();
        while(i < count) {
            source = dest;
            dest = st.nextToken();
            words = getBridgeWords(source,dest,words);
            if(words.size() != 0) {
				newtxt = newtxt + " " + source + " " + words.get(words.size()-1);
			}
			else {
            	newtxt = newtxt + " " + source;
			}
			//System.out.println(newtxt);
            i++;
			words = new LinkedList<String>();
        }
        newtxt = newtxt + " " + dest;
        return newtxt;
	}

	private int getIndex(Vertex v) {
		Random random = new Random();
		int index;
		if(v.adj.size() == 0) {
			index = -1;
		}
		else {
			index = random.nextInt(v.adj.size());
		}
		return index;
	}

	public String getdatapath(String s) {
		return s;
	}

	public String getgraph() {
		Iterator iterator = vertexMap.keySet().iterator();
		Vertex v;
		String s = "";
		List<Edge> edgelist = new LinkedList<Edge>();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			v = vertexMap.get(key);
			for(Edge e : v.adj) {
				Vertex w = e.dest;
				if(edgelist.contains(e)) {
					continue;
				}
				if(s == "") {
					s = s + v.name + "->" + w.name;
				}
				else {
					s = s + "," + v.name + "->" + w.name;
				}
				edgelist.add(e);
			}
		}
		return s;
	}

	public Graph creatGraph(String datapath) {
		Graph g = new Graph();
		//Scanner in = new Scanner(System.in);
		int count, i, len, j;
		String source, dest;
		try {
			FileReader fin = new FileReader(datapath);
			Scanner graphFile = new Scanner(fin);
			String line;
			while(graphFile.hasNextLine()) {
				line = graphFile.nextLine();
				line = line.toLowerCase();
				len = line.length();
				for (j = 0;j<len;j++) {
					if (!(line.charAt(j)>=65&&line.charAt(j)<=90 || line.charAt(j)>=97&&line.charAt(j)<=122 || line.charAt(j)>=9&&line.charAt(j)<=13 || line.charAt(j)==' ')) {
						line = line.replace(line.charAt(j),' ');
					}
				}
				StringTokenizer st = new StringTokenizer(line);
				count = st.countTokens();
				i = 1;
				dest = st.nextToken();
				while(i < count) {
					source = dest;
					dest = st.nextToken();
					g.addEdge(source, dest);
					i++;
				}
			}
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return g;
	}


	public static void main(String[] args) {
		Graph g = new Graph();
		Scanner in = new Scanner(System.in);
		int count, i, len, j;
		String source, dest;
		try {
			//FileReader fin = new FileReader(g.getdatapath());
			FileReader fin = new FileReader("D:\\miaomiao\\作业作业\\软件工程\\LAB1\\project\\data.txt");
			Scanner graphFile = new Scanner(fin);
			String line;
			while(graphFile.hasNextLine()) {
				line = graphFile.nextLine();
				line = line.toLowerCase();
				len = line.length();
				for (j = 0;j<len;j++) {
					if (!(line.charAt(j)>=65&&line.charAt(j)<=90 || line.charAt(j)>=97&&line.charAt(j)<=122 || line.charAt(j)>=9&&line.charAt(j)<=13 || line.charAt(j)==' ')) {
						line = line.replace(line.charAt(j),' ');
					}
				}
				StringTokenizer st = new StringTokenizer(line);
				count = st.countTokens();
				i = 1;
				dest = st.nextToken();
				while(i < count) {
					source = dest;
					dest = st.nextToken();
					g.addEdge(source, dest);
					i++;
				}
			}

            //get bridge words
			/*List<String> words = new LinkedList<String>();
			g.getBridgeWords("new","and",words);
			for(int k=0;k<words.size();k++) {
				System.out.println(words.get(k));
			}*/

			//get new text
			/*line = "seek to explore new and exciting synergies";
			System.out.println(g.getNewText(line));*/

            //random walk
			/*Vertex m = g.getVertex("out");
			List<Edge> visit = new LinkedList<Edge>();
			List<String> s = g.randomWalk(m);
			for(int k=0;k<s.size();k++) {
				System.out.println(s.get(k));
			}*/

			//the shortest path
			/*List<String> path = new LinkedList<String>();
			g.findShortestPath("to");
			Vertex m = g.getVertex("life");
			g.getPath(m,path);
			for(int k=path.size()-1;k>=0;k--) {
				System.out.println(path.get(k));
			}*/

			String s = g.getgraph();
			System.out.println(s);
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}

	private Map<String ,Vertex> vertexMap = new HashMap<String,Vertex> ();
}
//111