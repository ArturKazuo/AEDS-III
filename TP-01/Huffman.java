import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

class Huffman {

    public static PriorityQueue<No> nosP = new PriorityQueue<No>(new MyComparator()); //priority queue para armazenar as frequencia dos caracteres para construir a arvore posteriormente
    public static No raiz = new No();             //a raiz da arvore é criada


    static public void compress(File f){    //realiza a compressão do arquivo original em formato e algoritmo de huffman

        System.out.println("Compressão por Huffman começando...");

        try {
            RandomAccessFile raf = new RandomAccessFile(f, "rw");

            getFrequencia(f);  // pega a frequencia de cada carectere e continua a metodo de compressão

            raf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        
    }

    static public void getFrequencia(File f){ //metodo que pega a frequencia de cada caractere e armazena  eles em um no de uma arvore huffman
        No no = new No();
        String wholeFile = "";  //string para armazena um registro do arquivo original
        int n;                  //n = numero de emails de um registro
        char c;                 //caractere que esta sendo procurado
        char[] elem = new char[300];    //array de caracteres para armazenar em uma arvore
        int[] freq = new int[300];      //array de frequencias de caracteres
        boolean bool = false;           //usado para checar se o caractere procurado foi encontrado ou não

        for(int i=0; i<elem.length;i++){//inicio todos os elementos como ";" para que seja facil identificar quando o array de elementos parou de ser preenchido
            elem[i] = ';';
        }
        
        try {
            RandomAccessFile raf2 = new RandomAccessFile(f, "r");
 
            wholeFile += Integer.toString(raf2.readInt()); //lê o ultimo id a ser adicionado

            while(raf2.length() != raf2.getFilePointer()){      //enquanto não foi o final do arquivo, o while continuará
                wholeFile += Integer.toString(raf2.readInt());  //lê todos os elementos de um registro e armazena em uma string
                wholeFile += "=";                               // "=" é usado para determinar onde termina um campo e começa outro
                wholeFile += String.valueOf(raf2.readByte());
                wholeFile += "=";
                wholeFile += Integer.toString(raf2.readInt());
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                n = raf2.readInt();
                wholeFile += Integer.toString(n);
                wholeFile += "=";
                for(int i=0; i<n; i++){
                    wholeFile += raf2.readUTF();
                    wholeFile += "=";
                }
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += Float.toString(raf2.readFloat());
                wholeFile += "=";
                wholeFile += Integer.toString(raf2.readInt());
                wholeFile += "=";

                for(int i=0; i<wholeFile.length(); i++){//for que passa por todos os caracteres da wholeFile para procurar na priority queue e armazená-lo ou incrementar sua frequencia
                    c = wholeFile.charAt(i);
                    for(int j=0; j<elem.length; j++){   //for que compara todos os elementos do array de caracteres ao caractere desejado
                        if(c == elem[j]){               //caso seja true, o bool é true, para indicar que apenas a frequencia do caractere foi incrementada
                            freq[j]++;
                            bool = true;
                            break;
                        }
                    }
                    if(bool == false){                  //caso o caractere não tenha sido encontrado, ele é armazenado no array de chars 
                        for(int j=0; j<elem.length; j++){
                            if(elem[j] == ';'){         //esse caractere indica as posições que ainda n foram ocupadas
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
            for(k=0; elem[k] != ';'; k++);  //for para contar quantos elementos existem no array de caracteres

            for(int i=0; i<k; i++){         //for que passa todos os elementos do array de caracteres e armazena o No em uma priority queue para que a arvore de huffman seja montada
                no = new No();
                no.elem = elem[i];
                no.freq = freq[i];
                no.esq = null;
                no.dir = null;

                nosP.add(no);
            }

            while(nosP.size() > 1){         //enquanto a priority queue ainda tiver elementos, o while continua
                No x = nosP.peek();         //pega os dois menores nos
                nosP.poll();
                No y = nosP.peek();
                nosP.poll();
                No huff = new No();

                huff.freq = x.freq + y.freq;//armazena a soma da frequencia dos dois nos em um novo no

                huff.esq = x;
                huff.dir = y;

                raiz = huff;

                nosP.add(huff);             //insere o novo no na priority queue
            }

            binaryCode(raiz, "");        //chama o metodo para descobrir o codigo binario de todos os caracteres

            raf2.seek(0);

            RandomAccessFile raf3 = new RandomAccessFile("arquivoHuffmanCompressao.txt", "rw");
            wholeFile = "";

            raf2.readInt();                 //le o ultimo id a ser adicionado

            while(raf2.getFilePointer() < raf2.length()){       //enquando !EOF continua
                wholeFile += Integer.toString(raf2.readInt());  //le os caracteres novamente
                wholeFile += "=";
                wholeFile += String.valueOf(raf2.readByte());
                wholeFile += "=";
                wholeFile += Integer.toString(raf2.readInt());
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                n = raf2.readInt();
                wholeFile += Integer.toString(n);
                wholeFile += "=";
                for(int i=0; i<n; i++){
                    wholeFile += raf2.readUTF();
                    wholeFile += "=";
                }
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += raf2.readUTF();
                wholeFile += "=";
                wholeFile += Float.toString(raf2.readFloat());
                wholeFile += "=";
                wholeFile += Integer.toString(raf2.readInt());
                wholeFile += "=";

                for(int i=0; i<wholeFile.length(); i++){                    //procura o codigo binario do caractere e insere ele no arquivo de huffman
                    String bin = checkCharacter(raiz, wholeFile.charAt(i));
                    raf3.writeUTF(bin);
                }
            }

            raf3.writeUTF("EOF");

            System.out.println("Compressão por Huffman finalizada");

            raf2.close();
            raf3.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void binaryCode(No raiz, String s){ // método que define qual é o codigo binario de cada elemento

        if (raiz.esq == null && raiz.dir == null) { //caso o ponteiro para filho seja null, chegou ao fim

            raiz.bin = s;                           // s = código binario de um elemento
 
            return;
        }

        binaryCode(raiz.esq, s + "0"); //percorre a arvore inteira
        binaryCode(raiz.dir, s + "1");
    }

    public static String checkCharacter(No raiz, char c){
        String bin = "";

        if (raiz.esq == null && raiz.dir == null) { //caso o ponteiro para filho seja null, chegou ao fim

            if(raiz.elem == c){
                bin = raiz.bin;                           // s = código binario de um elemento
 
                return bin;
            }
            else{
                return bin;
            }
        }

        bin = checkCharacter(raiz.esq, c); //percorre a arvore inteira
        if(bin == ""){                     //caso o binário encontrado
            bin = checkCharacter(raiz.dir, c); 
        }

        return bin;
    }

    static public void decompress(){ //método de descompressão por huffman

        int n;

        System.out.println("Descompressão por huffman começando...");

        try {
            RandomAccessFile rafH = new RandomAccessFile("arquivoHuffmanCompressao.txt", "rw");
            RandomAccessFile raf = new RandomAccessFile("arquivo.txt", "rw");
            RandomAccessFile rafHD = new RandomAccessFile("arquivoHuffmanDescompressao", "rw");   
            String str;         
            int posH = 0;

            rafHD.writeInt(raf.readInt());

            while(true){
                str = buildNewStr(rafH);
                if(str.compareTo("EOF") == 0){
                    break;
                }
                rafHD.writeInt(Integer.parseInt(str));                
                rafHD.writeByte( (byte) (Integer.parseInt( buildNewStr(rafH) )) );
                rafHD.writeInt(Integer.parseInt(buildNewStr(rafH)));
                rafHD.writeUTF(buildNewStr(rafH));
                rafHD.writeUTF(buildNewStr(rafH));
                rafHD.writeUTF(buildNewStr(rafH));
                n = Integer.parseInt(buildNewStr(rafH));
                rafHD.writeInt(n);
                for(int i=0; i<n; i++){
                    rafHD.writeUTF(buildNewStr(rafH));
                }
                rafHD.writeUTF(buildNewStr(rafH));
                rafHD.writeUTF(buildNewStr(rafH));
                rafHD.writeFloat(Float.valueOf(buildNewStr(rafH)));
                rafHD.writeInt(Integer.parseInt(buildNewStr(rafH)));
            }

            System.out.println("Descompressão por huffman finalizada com sucesso!");

            rafH.close();
            rafHD.close();
            raf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static char checkBinary(No raiz, String bin, int pos){
        char c = '~';

        if (raiz.esq == null && raiz.dir == null) {      //caso o ponteiro para filho seja null, chegou ao fim

            if(raiz.bin == bin){
                c = raiz.elem;                           // c = caractere correspondente ao codigo binario
 
                return c;
            }
            else{
                return c;
            }
        }

        if(bin.charAt(pos) == '0'){
            c = checkBinary(raiz.esq, bin, ++pos); //percorre a arvore inteira
        }
        else{
            c = checkBinary(raiz.dir, bin, ++pos); 
        }

        return c;
    }

    public static String buildNewStr(RandomAccessFile rafH){

        String newStr = ""; 
        String s;
        char c;
        boolean bool = true;
        try {

            for( ; bool ; ){
                s = rafH.readUTF();
                System.out.println("s: "+s);
                c = checkBinary(raiz, s, 0);
                System.out.println("c: "+c);
                if(c != '='){
                    newStr += c;
                }
                else if(c == 'E'){
                    newStr = "EOF";
                    bool = false;
                }
                else{
                    bool = false;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return newStr;
    }
}

class No {
    public char elem;   //caractere para ser codificado em huffman
    public int freq;    //frequencia em que esse caractere aparece
    public No esq, dir; //ponteiro para dois outros nos para fazer a construção da arvore de huffman
    public String bin;  //codigo cinario do caractere (é armazenado apenas quando a arvore ja está montada)

    public No(){
        elem = ';';
        freq = 0;
        esq = null;
        dir = null;
        bin = null;
    }
}

class MyComparator implements Comparator<No> { //comparator para comparar qual das frequencias é maior entre dois objetos
    
    @Override
    public int compare(No x, No y){ //compara as frequencias ao inserir em uma priority queue
        return x.freq > y.freq ? 1 : -1;
    }
}