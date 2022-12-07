import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.*;

public class Parser {

    public static void main(String[] args) {

        if (args.length<2){
            System.out.println("Give logfile path and graph output path as argument!");
            return;
        }

        String fileName = args[0];
        String graphOutput = args[1];


        ArrayList<ParserTuple> temp;
        try {
            temp = textReading(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        System.out.println(temp);
        Graph<String,RelationshipEdge> g = getGraph(temp);
        exportGraph(g,graphOutput);


//        System.out.println((g.containsVertex("2213mozStorage")));
        if(args.length==5){
            String startVertex = args[2];
//            String startVertex = "2213firefox";
            String endVertex = args[3];
            String smallGraphFile = args[4];
//            String endVertex = "/proc/21912/oom_score_adj";
            RelationshipEdge ee = g.getEdge(startVertex,endVertex);
            Graph res = backTrack(g,ee);
            exportGraph(res,smallGraphFile);
        }

    }

    public static Graph backTrack(Graph<String, RelationshipEdge> graph, RelationshipEdge edge){

        String source = graph.getEdgeSource(edge);
//        System.out.println(source);
//        Set<RelationshipEdge> te = graph.incomingEdgesOf(source);
        Graph<String, RelationshipEdge> newGraph = new DefaultDirectedGraph<>(RelationshipEdge.class);

        newGraph.addVertex(graph.getEdgeSource(edge));
        newGraph.addVertex(graph.getEdgeTarget(edge));
        newGraph.addEdge(graph.getEdgeSource(edge),graph.getEdgeTarget(edge),new RelationshipEdge(edge.label));
        HashSet<String> visited = new HashSet<>();
        helper(source,edge.label.getFirst(),edge.label.getSecond(),graph,newGraph,visited);

        return newGraph;

    }

    public static void helper(String node, double startTime, double endTime, Graph<String, RelationshipEdge> graph, Graph<String, RelationshipEdge> newGraph, HashSet<String> visited){
        if(visited.contains(node))
            return;

        visited.add(node);
//        System.out.println("Node"+node);
        Set<RelationshipEdge> incomingEdges = graph.incomingEdgesOf(node);
//        System.out.println(incomingEdges);

        if(incomingEdges.size()==0)
            return;

        for (RelationshipEdge edge:incomingEdges){
//            System.out.println("Processing "+edge.label);
            if(edge.label.getFirst()<endTime){
//                System.out.println("Adding "+edge.label);

                String temp = graph.getEdgeSource(edge);
//                System.out.println(temp);
                newGraph.addVertex(temp);
                newGraph.addEdge(temp,node,new RelationshipEdge(edge.label));
                helper(temp,startTime,edge.label.getSecond(),graph,newGraph,visited);
            }
        }
    }


    public static Graph getGraph(ArrayList<ParserTuple> tuples){
        Graph<String, RelationshipEdge> graph = new DefaultDirectedGraph<>(RelationshipEdge.class);

//        Pair
        HashMap<String, Pair<Double,Double>> map = new HashMap<>();

        for (ParserTuple tuple: tuples){
            String subject = tuple.process.pid+tuple.process.processName;
            String object = tuple.object.resolvedString;

            if(object.isEmpty() || subject.isEmpty())
                continue;

            if(tuple.type.equals("read") || tuple.type.equals("write") || tuple.type.equals("writev") || tuple.type.equals("sendto") || tuple.type.equals("sendmsg") || tuple.type.equals("readv") || tuple.type.equals("recvmsg") || tuple.type.equals("recvfrom")){


                if(tuple.type.equals("write") || tuple.type.equals("writev") || tuple.type.equals("sendto") || tuple.type.equals("sendmsg")){
                    String key = subject+"#"+object+"#"+"write";
                    if(map.containsKey(key)){
                        map.get(key).setSecond(tuple.time);
                        graph.getEdge(subject,object).setLabel(map.get(key));
                    }else{
                        map.put(key,new Pair<>(tuple.time,tuple.time));
                        graph.addVertex(object);
                        graph.addVertex(subject);
                        graph.addEdge(subject,object,new RelationshipEdge(map.get(key)));
                    }

                }else{
                    String key = subject+"#"+object+"#"+"read";
                    if(map.containsKey(key)){
                        map.get(key).setSecond(tuple.time);
                        graph.getEdge(object,subject).setLabel(map.get(key));
                    }else{
                        map.put(key,new Pair<>(tuple.time,tuple.time));
                        graph.addVertex(object);
                        graph.addVertex(subject);
                        graph.addEdge(object,subject,new RelationshipEdge(map.get(key)));
                    }
                }
            }
        }

//        System.out.println(map);

        return graph;
    }

    public static void exportGraph(Graph<String,RelationshipEdge> graph, String fileName){

        DOTExporter<String, RelationshipEdge> exporter =
                new DOTExporter<>();
        Writer writer = new StringWriter();
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        exporter.setEdgeAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        exporter.exportGraph(graph, writer);
//        System.out.println(writer.toString());
        try {
            fileWrinting(fileName,writer.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void fileWrinting(String fileName, String toWrite) throws IOException {
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
            System.out.println("Creating new file");
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        bufferedOutputStream.write(toWrite.getBytes());

        bufferedOutputStream.close();
        outputStream.close();
    }

    static ArrayList<ParserTuple> textReading(String filePath) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("File bot found");
            return null;
        }

        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line= reader.readLine();

        ArrayList<ParserTuple> list = new ArrayList<>();

//        for(String yo:temp)
//            System.out.println(yo);
        while ( line!=null){
//            System.out.println("Reading "+ line);
            try{
                String[] temp = line.split(" ");
                String operation = temp[6];
                String time = temp[1];
                String process = temp[3];
                String pidString = temp[4];
                String fd = temp[8];
                String arrow = temp[5];

                String chk  = fd.substring(0,2);
                if (!chk.equals("fd")){
                    line = reader.readLine();
                    continue;
                }

                String fdMod = fd.substring(3,fd.length()-1);

//                System.out.println(arrow);
                int idx = fdMod.indexOf("(");
                String fdnum = fdMod.substring(0,idx);
                String object = fdMod.substring(idx+1);

                int start = object.indexOf("<");
                int end = object.indexOf(">");

                String fdType = object.substring(start+1,end);
                String res = object.substring(end+1);

                pidString = pidString.substring(1,pidString.length()-1);
                long pid = Long.valueOf(pidString);

                Process p = new Process(process,pid);
                FDObject fdd = new FDObject(Integer.valueOf(fdnum),fdType,res);

                ParserTuple tuple = new ParserTuple(p,operation,fdd,Double.valueOf(time));
                list.add(tuple);

                line = reader.readLine();
            }catch (Exception e){
                line = reader.readLine();
            }


        }

        return list;
    }
}
