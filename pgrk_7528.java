// Jacob Yanicak
// CS610
// PrP
// pgrk_7528.java

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class page_7528{

    public double auth;
    public double prevAuth;
    public int id;
    public int outgoingEdges;

    public page_7528(int id, double auth) {
        this.id = id;
        this.auth = auth;
        this.prevAuth = auth;
        this.outgoingEdges = 0;
        //System.out.println("created page with id,auth : " + id + " , " + auth);
    }

    @Override
    public String toString() {
        return String.format("vertex : " + this.id + " - auth : " + this.auth + " - outedge : " + this.outgoingEdges);
    }
}

class pgrk_7528 {

    // notes on arguments & function call
    // >> java pgrk iterations initialvalue inputgraph.txt
    // @param iterations :
    //      0 = errorRate = 10 ^ -5
    //      positve = # of iterations for algorithm
    //      negative = errorRate calculated by 10 ^ value
    // @param initialvalue :
    //      0 = 0
    //      1 = 1
    //      -1 = 1 / N
    //      -2 = 1 / sqrt(N)
    // If input graph has N > 10 vertices (1st num on 1st line) :
    //      iterations = 0
    //      initialvalue = -1
    //      Each vetice gets its own output line

    public static void main(String[] args) {

        int iterations = 0;
        int initialvalue = 0;
        String filename = "";
        File adjListFile = null;
        double errorRate = 0.0;
        double startVals = 0.0;
        BufferedReader reader;
        String line = "";
        int numVertices = 0;
        int numEdges = 0;
        int fromVert;
        int toVert;
        String optionBreak;
        int iter;
        String printer;

        if (args.length != 3) {
            System.out.println("Incorrect number of args used! Only 3 allowed!");
            System.exit(0);
        } else {
            iterations = Integer.parseInt(args[0]);
            initialvalue = Integer.parseInt(args[1]);
            filename = args[2];
            try {
                adjListFile = new File(filename);
            } catch (Exception e) {
                System.out.println("Unable to load file, error : " + e);
                System.exit(0);
            }
        }

        //print("iterations : " + iterations);
        //print("initialvalue : " + initialvalue);
        //print("filename : " + filename);

        try {
    		reader = new BufferedReader(new FileReader(filename)); //"/Users/pankaj/Downloads/myfile.txt"
    	    line = reader.readLine();
    		reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        String[] splitted = line.trim().split("\\s+");
        numVertices = Integer.parseInt(splitted[0]);

        //print("numVertices : " + numVertices);

        if (numVertices > 10) {
            iterations = -5;
            initialvalue = -1;
        }

        if (initialvalue == 0) {
            startVals = 0;
        } else if (initialvalue == 1) {
            startVals = 1;
        } else if (initialvalue == -1) {
            startVals = 1.0 / numVertices;
        } else if (initialvalue == -2) {
            startVals = 1.0 / Math.sqrt(numVertices);
        }

        if (iterations < 1) {
            errorRate = Math.pow(10, iterations);
        }

        //print("Starting values = " + startVals);

        page_7528[] pageArray = new page_7528[numVertices];
        for (int j = 0; j < numVertices; j++) {
            page_7528 node = new page_7528(j, startVals);
            pageArray[j] = node;
        }

        LinkedList[] adjList = new LinkedList[numVertices];
        for (int i = 0; i < numVertices; i++) {
            LinkedList<page_7528> pageList = new LinkedList<page_7528>();
            adjList[i] = pageList;
        }

        try {
    		reader = new BufferedReader(new FileReader(filename)); //"/Users/pankaj/Downloads/myfile.txt"
            line = reader.readLine(); // skipping top line
            line = reader.readLine();
            while (line != null) {

                String[] verts = line.trim().split("\\s+");
                fromVert = Integer.parseInt(verts[0]);
                toVert = Integer.parseInt(verts[1]);

                adjList[fromVert].add(pageArray[toVert]);
                pageArray[fromVert].outgoingEdges += 1;

                line = reader.readLine();
            }
    		reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            print("Exception : " + e);
        }

        // for (int i = 0; i < numVertices; i++) {
        //     print(pageArray[i].toString());
        // }

        if (numVertices > 10) {
            optionBreak = "\n";
        } else {
            optionBreak = "";
            System.out.print("Base  :  0 :");
            printVerts(numVertices, pageArray, optionBreak);
        }

        if (iterations > 0) {
            for (iter = 1; iter <= iterations; iter++) {
                calculateRanks(adjList, pageArray, numVertices);
                printer = iter < 10 ? " " + Integer.toString(iter) : Integer.toString(iter);
                if (numVertices > 10) {
                    if (iter == iterations) {
                        System.out.print("Iter  : " + printer + " :");
                        printVerts(numVertices, pageArray, optionBreak);
                    }
                } else {
                    System.out.print("Iter  : " + printer + " :");
                    printVerts(numVertices, pageArray, optionBreak);
                }
                updatePrevAuths(numVertices, pageArray);
            }
        } else {
            iter = 1;
            while (true) {
                calculateRanks(adjList, pageArray, numVertices);
                printer = iter < 10 ? " " + Integer.toString(iter) : Integer.toString(iter);
                if (numVertices <= 10) {
                    System.out.print("Iter  : " + printer + " :");
                    printVerts(numVertices, pageArray, optionBreak);
                }
                iter++;
                if (checkErrors(errorRate, numVertices, pageArray)) {
                    if (numVertices > 10) {
                        System.out.print("Iter  : " + printer + " :");
                        printVerts(numVertices, pageArray, optionBreak);
                    }
                    break;
                }
                updatePrevAuths(numVertices, pageArray);
            }
        }
    } // end of main


    // func to calculate PageRank for each page
    public static void calculateRanks(LinkedList[] adjList, page_7528[] pageArray, int numVertices) {

        double sum = 0.0;
        LinkedList<page_7528> curList;

        for (int a = 0; a < numVertices; a++) {

            sum = 0.0;
            for (int b = 0; b < numVertices; b++) {

                if (a == b) {
                    continue;
                }
                curList = adjList[b];
                for (int c = 0; c < curList.size(); c++) {
                    if (curList.get(c).id == a) {
                        sum += pageArray[b].prevAuth / pageArray[b].outgoingEdges;
                        break;
                    }
                }
            }
            pageArray[a].auth = ((1-.85)/numVertices) + (.85) * sum;
        }
    }

    // func to check if errorRate is met for EVERY page update
    public static Boolean checkErrors(double errorRate, int numVertices, page_7528[] pageArray) {

        for (int i = 0; i < numVertices; i++) {
            if ( Math.abs( pageArray[i].auth - pageArray[i].prevAuth ) > errorRate) {
                return false;
            }
        }
        return true;
    }

    // func to just print vertices and their PageRanks
    public static void printVerts(int numVertices, page_7528[] pageArray, String optionBreak) {
        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%sP[%d]=%.7f ",  optionBreak, i, pageArray[i].auth);
        }
        System.out.printf("\n");
    }

    // func to equalize all prevAuth & auth attribute values for each page
    public static void updatePrevAuths(int numVertices, page_7528[] pageArray) {
        for (int i = 0; i < numVertices; i++) {
            pageArray[i].prevAuth = pageArray[i].auth;
        }
    }

    // hax
    public static void print(String arg) {
        System.out.println(arg);
    }
}
