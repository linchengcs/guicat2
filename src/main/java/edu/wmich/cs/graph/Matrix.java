package edu.wmich.cs.graph;

import java.io.*;
import java.util.*;

/**
 * Created by rick on 8/12/16.
 */
public class Matrix {
    int nrow = -1;
    int ncol = -1;
    List<List<Integer>> data;

    public Matrix() {
        data = new ArrayList<List<Integer>>();
    }

    public List<List<Integer>> getData() {
        return data;
    }

    public int value(int i, int j) {
        assert i < nrow && j < ncol : "array access overflow";
        return data.get(i).get(j);
    }

    public int nrow() {
        return nrow;
    }

    public int ncol() {
        return ncol;
    }

    public void readIn(InputStream is) {
        String line = "";
        String cvsSplitBy = ", "; //must be exactly a comma and a space

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            nrow = 0;
            while ((line = br.readLine()) != null) {

                 String[] lineElements = line.split(cvsSplitBy);

                // check matrix shape
                if (ncol == -1) {
                    ncol = lineElements.length;
                } else if (ncol != lineElements.length) {
                    throw new Exception("your matrix has unequal columns");
                }

                Integer[] lineInts = new Integer[ncol];
                for(int i = 0; i < ncol; i++)
                    lineInts[i] = Integer.parseInt(lineElements[i]);

                data.add(Arrays.asList(lineInts));
                nrow++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Olog.log.info(toString());
    }

    public void readInString(String s) {
        try {
            readIn(new ByteArrayInputStream(s.getBytes("UTF8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String ret = "";
        for (List<Integer> row: data ) {
            for (int ele : row) {
                ret += ele + ", ";
            }
            ret = ret.substring(0, ret.length()-2) + "\r\n";
        }
        return ret;
    }
}
