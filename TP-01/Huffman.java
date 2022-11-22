import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

class Huffman {

    static public void compress(File f){    //realiza a compressão do arquivo original em formato e algoritmo de huffman
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            RandomAccessFile rafH = new RandomAccessFile("arquivo_huffman", "rw");

            PriorityQueue<No> nos = getFrequencia(f);
            No no = new No();
            No menorUm, menorSeg;

            for(int i=0; i<nos.size(); i++){

                
            }

            raf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        
    }

    static public PriorityQueue<No> getFrequencia(File f){
        No no = new No();
        PriorityQueue<No> nosP = new PriorityQueue<No>(new MyComparator());
        String wholeFile = "";
        int n;
        char c;
        char[] elem = new char[300];
        int[] freq = new int[300];
        boolean bool = false;

        for(int i=0; i<elem.length;i++){
            elem[i] = ';';
        }
        
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
 
            wholeFile += Integer.toString(raf.readInt());

            while(raf.length() != raf.getFilePointer()){
                wholeFile += Integer.toString(raf.readInt());
                wholeFile += raf.readByte();
                wholeFile += Integer.toString(raf.readInt());
                wholeFile += raf.readUTF();
                wholeFile += raf.readUTF();
                wholeFile += raf.readUTF();
                n = raf.readInt();
                wholeFile += Integer.toString(n);;
                for(int i=0; i<n; i++){
                    wholeFile += raf.readUTF();
                }
                wholeFile += raf.readUTF();
                wholeFile += raf.readUTF();
                wholeFile += Float.toString(raf.readFloat());
                wholeFile += Integer.toString(raf.readInt());

                for(int i=0; i<wholeFile.length(); i++){
                    c = wholeFile.charAt(i);
                    for(int j=0; j<elem.length; j++){
                        if(c == elem[j]){
                            freq[j]++;
                            bool = true;
                            break;
                        }
                    }
                    if(bool == false){
                        for(int j=0; j<elem.length; j++){
                            if(elem[j] == ';'){
                                elem[j] = c;
                                freq[j] = 1;
                                break;
                            }
                        }
                    }
                    bool = false;
                }
                wholeFile = "";
            }

            int k;
            for(k=0; elem[k] != ';'; k++);

            for(int i=0; i<k; i++){
                no = new No();
                no.elem = elem[i];
                no.freq = freq[i];
                no.esq = null;
                no.dir = null;

                nosP.add(no);
            }

            System.out.println("*************************");

            while(nosP.size() > 0){
                No noP = nosP.peek();
                //System.out.println("elem: "+noP.elem);
                System.out.println("freq: "+noP.freq);
                nosP.poll();
            }

            raf.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return nosP;
    }

    static public void decompress(File f){

    }
}

class No {
    public char elem;
    public int freq;
    public No esq, dir;
}

class MyComparator implements Comparator<No> {
    
    @Override
    public int compare(No x, No y){
        return x.freq < x.freq ? 1 : -1;
    }
}