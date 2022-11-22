import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;


public class Main {

    static Scanner sc = new Scanner(System.in);
    static String PATH = "arquivo.txt";
    public static void main(String args[]){
        Conta conta;
        int resp;

        File f = new File(PATH);
        File fHash = new File("arquivo_hash.txt");
        HashingExtendido hash = new HashingExtendido();

        try{
            f.createNewFile();//cria o arquivo para armazenar os registros
            fHash.createNewFile();//arquivo de hash
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        int id = getLastId(f);//recebe o ultimo id do arquivo

        do{
            System.out.println("\n/-----------------------------------------------/");
            System.out.println("Menu:");
            System.out.println("digite 1 para criar uma conta bancaria");
            System.out.println("digite 2 para realizar uma transferencia");
            System.out.println("digite 3 para procurar e ler um registro");
            System.out.println("digite 4 para atualizar um registro");
            System.out.println("digite 5 para deletar um registro");
            System.out.println("digite 6 para ordenar o arquivo");
            System.out.println("digite 7 para procurar por um registro no arquivo de hashing");
            System.out.println("digite 8 para procurar o nome ou a cidade na lista invertida");
            resp = sc.nextInt();

            switch(resp){
                
                case 1: // primeiro caso, criação e inserção de uma conta 

                    conta = new Conta();//cria um objeto conta para armazenar os dados e depois inseri-los no arquivo

                    System.out.println("Digite o seu nome:");
                    sc.nextLine();
                    conta.nomePessoa = sc.nextLine();

                    conta.cpf = conta.checkCPF(true);

                    conta.nomeUsuario = conta.checkNomeUsuario(true, f);

                    System.out.println("Digite a senha:");
                    conta.senha = sc.nextLine();

                    System.out.println("Digite a cidade:");
                    conta.cidade = sc.nextLine();

                    System.out.println("Digite o numero de emails vinculados a conta:");
                    conta.numEmails = sc.nextInt();

                    conta.email = new String[conta.numEmails];

                    sc.nextLine();
                    for(int i=0; i<conta.numEmails; i++){
                        System.out.println("Digite o email:");
                        conta.email[i] = sc.nextLine();
                    }

                    System.out.println("Digite o valor do saldo da conta: ");
                    conta.saldoConta = sc.nextFloat();

                    try{
                        RandomAccessFile raf = new RandomAccessFile(f, "rw");//raf para inserir os dados
                        int totalSize, somaEmails = 0;

                        for(int i=0; i<conta.numEmails; i++){
                            somaEmails += conta.email[i].length();
                        }

                        totalSize = 2 + (conta.nomePessoa.length()) + 2 + 11 + 2 + (conta.cidade.length()) + 4 + (conta.numEmails*2) + somaEmails + 
                        2 + (conta.nomeUsuario.length()) + 2 + (conta.senha.length()) + 4 + 4; //tamanho total do registro a ser inserido

                        id++;

                        setLastId(f, id);                       //armazena o ultimo id no arquivo

                        raf.seek(f.length());

                        raf.writeInt(id);                       //inserção do id do registro
                        raf.writeByte(conta.lapide);            //inserção da lapide
                        raf.writeInt(totalSize);
                        raf.writeUTF(conta.nomePessoa);
                        raf.writeUTF(conta.cpf);                //inserção do cpf, por ter tamanho fixo nao precisa de byte de tamanho
                        raf.writeUTF(conta.cidade);
                        raf.writeInt(conta.numEmails);          //inserção do numero de emails que existem no registro
                        for(int i=0; i<conta.numEmails; i++){   //inserção de cada email com seu respectivo tamanho
                            raf.writeUTF(conta.email[i]);
                        }
                        raf.writeUTF(conta.nomeUsuario);
                        raf.writeUTF(conta.senha);
                        raf.writeFloat(conta.saldoConta);           //inserção do tamanho do saldo da conta
                        raf.writeInt(conta.transferenciasRealizadas);//tranferecias realizadas

                        raf.close();

                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }

                    break;
                
                case 2: // segundo caso, realizar uma transferencia para uma conta

                    System.out.println("Digite o id da sua conta: ");
                    int idFazer = sc.nextInt();

                    System.out.println("Digite o id da conta que recebera a transferencia: ");
                    int idRecebe = sc.nextInt();

                    System.out.println("Digite o valor a ser transferido: ");
                    float valor = sc.nextFloat();

                    transaction(idFazer, idRecebe, valor, f);

                    break;

                case 3: // terceiro caso, leitura de um registro        
                
                    System.out.println("Digite o id do registro que deseja visualizar: ");
                    int idRead = sc.nextInt();
                    printRegistro(idRead, f);

                    break;

                case 4:

                    System.out.println("Digite o id do registro que deseja editar: ");
                    int idEdit = sc.nextInt();

                    atualizarRegistro(f, idEdit);

                    break;

                case 5: // Quinto caso, deletar um registro

                    System.out.println("Digite o id do registro que deseja deletar: ");
                    int idDelete = sc.nextInt();
                    deleteRegistro(idDelete, f);

                    break;

                case 6:

                    intercalacao(f);

                    break;

                case 7:

                    hash.insertTudo(f, fHash);
                
                    System.out.println("Digite o id do registro que deseja visualizar: ");
                    int idReadHash = sc.nextInt();

                    long posH = hash.search(fHash, idReadHash);//recebe a posição da procura no arquivo de hashing

                    try {
                        RandomAccessFile raf2 = new RandomAccessFile(f, "r");

                        raf2.seek(posH);//vai até a posição recebida
                        raf2.readInt();
                        if(raf2.readByte() == 1){//caso o registro tenha sido apagado
                            System.out.println("ERRO! O registro desejado foi apagado");
                        }
                        else{//imprime os dados do registro
                            System.out.println("");
                            System.out.println("/************** Registro **************/");
                            raf2.readInt();
                            System.out.println("nome da pessoa: " + raf2.readUTF());
                            System.out.println("cpf: " + raf2.readUTF());
                            System.out.println("cidade: " + raf2.readUTF());
                            int n = raf2.readInt();
                            System.out.println("numero de emails: " + n);
                            for(int j=0; j<n; j++){
                                System.out.println("email " + (j+1) + ": " + raf2.readUTF()); 
                            }
                            System.out.println("nome do usuario: " + raf2.readUTF());
                            System.out.println("senha: " + raf2.readUTF());
                            System.out.println("saldo: " + raf2.readFloat());
                            System.out.println("tranferencia: " + raf2.readInt());
                            System.out.println("/*********** Fim do Registro ***********/");
                            System.out.println("");
                        }

                        raf2.close();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case 8: 
                    int op;
                    System.out.println("O que voce quer procurar? \n (1) Nome \n (2) Cidade:");
                    op = sc.nextInt();
                    if(op ==1){
                        String nome;
                        System.out.println("Qual nome a procurar?");
                        nome = sc.nextLine();
                        listaInvertida.buscaLista(nome, "listaInvertida/listaInvertidoNome.txt");
                    } else if(op == 2){
                        String cidade;
                        System.out.println("Qual nome a procurar?");
                        cidade = sc.nextLine();
                        listaInvertida.buscaLista(cidade, "listaInvertida/listaInvertidaCidade.txt");
                    } else{
                        System.out.println("Numero inválido");
                    }
                    String cidadeP = sc.nextLine();

                    //long lista1 = listaInvertida
                    break;

                    case 9:

                        System.out.println("Compressão por Huffman começando...");

                        Huffman.compress(f);

                        break;
            }

        }while(true);

    }

    public static int getLastId(File f){//acessa o arquivo e pega o ultimo id que foi armazenado
        int lastId = 0;

        try{
            RandomAccessFile raf4 = new RandomAccessFile(f, "r");

            raf4.seek(0);
            lastId = raf4.readInt();

            raf4.close();

        }catch(Exception e){
            lastId = 0;
        }

        return lastId;
    }

    public static void setLastId(File f, int id){//acessa o arquivo e insere o ultimo id que foi armazenado

        try{

            RandomAccessFile raf3 = new RandomAccessFile(f, "rw");

            raf3.seek(0);
            raf3.writeInt(id);

            raf3.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void printRegistro(int idRead, File f){//acha um id e mostra os dados na tela

        int n, id;//quantidade de emails no registro e id de um registro
        boolean checkApagado = false;

        try{

            RandomAccessFile raf = new RandomAccessFile(f, "r");

            int lastId = raf.readInt();

            while(true){            // while que recebe o id do registro do arquivo e compara com o id desejado
                id = raf.readInt(); // se o id for diferente (if) o raf lê todos os dados do registro mas não armazena, apenas para passar para posicionar seu ponteiro no inicio do proximo registro
                if(id != idRead){   
                    raf.readByte();
                    raf.readInt();
                    raf.readUTF();
                    raf.readUTF();
                    raf.readUTF();
                    n = raf.readInt();
                    for(int i=0; i<n; i++){
                        raf.readUTF();
                    }
                    raf.readUTF();
                    raf.readUTF();
                    raf.readFloat();
                    raf.readInt();
                }
                else{               // se o id for igual (else), apenas break para ler os dados do registro encontrado
                    System.out.println("");
                    if(raf.readByte() == 1){//se o registro ja foi deletado, esse if fará com que os dados não sejam exibidos
                        raf.readInt();
                        raf.readUTF();
                        raf.readUTF();
                        raf.readUTF();
                        n = raf.readInt();
                        for(int i=0; i<n; i++){
                            raf.readUTF();
                        }
                        raf.readUTF();
                        raf.readUTF();
                        raf.readFloat();
                        raf.readInt();
                        checkApagado = true;
                    }
                    else{//imprime os dados do registro encontrado
                        System.out.println("/************** Registro **************/");
                        raf.readInt();
                        System.out.println("nome da pessoa: " + raf.readUTF());
                        System.out.println("cpf: " + raf.readUTF());
                        System.out.println("cidade: " + raf.readUTF());
                        n = raf.readInt();
                        System.out.println("numero de emails: " + n);
                        for(int j=0; j<n; j++){
                            System.out.println("email " + (j+1) + ": " + raf.readUTF()); 
                        }
                        System.out.println("nome do usuario: " + raf.readUTF());
                        System.out.println("senha: " + raf.readUTF());
                        System.out.println("saldo: " + raf.readFloat());
                        System.out.println("tranferencia: " + raf.readInt());
                        System.out.println("/*********** Fim do Registro ***********/");
                        System.out.println("");
                        break;
                    }    
                }
            }

            raf.close();

        }catch(Exception e){

            if(checkApagado == true){//caso tenha sido apagado
                System.out.println("ERRO! O registro desejado foi apagado");
            }
            else{//caso não exista no arquivo
                System.out.println("ERRO! O registro nao foi encontrado");//o registro não existe no arquivo        
            }
        }
    }

    public static void deleteRegistro(int idDelete, File f){//encontra um registro e deleta ele, marcando sua lapide
        
        int id; // id de um registro

        try{

            RandomAccessFile raf2 = new RandomAccessFile(f, "rw");

            int skip;//bytes para dar skip, para ler o proximo
            int skipSoma = 0;//soma de todos os bytes para a raf 
            int n;

            raf2.readInt();

            while(true){
                id = raf2.readInt();
                if(id != idDelete){// se o id for diferente (if) o raf lê todos os dados do registro mas não armazena, apenas para passar para posicionar seu ponteiro no inicio do proximo registro
                    raf2.readByte();
                    skip = raf2.readInt();
                    raf2.readUTF();
                    raf2.readUTF();
                    raf2.readUTF();
                    n = raf2.readInt();
                    for(int i=0; i<n; i++){
                        raf2.readUTF();
                    }
                    raf2.readUTF();
                    raf2.readUTF();
                    raf2.readFloat();
                    raf2.readInt();

                }
                else{//caso encontre o registro, ele sera marcado como deletado

                    raf2.write(1);

                    raf2.close();

                    break;
                }
            }

            raf2.close();

        }catch(Exception e){
            System.out.println("ERRO! O registro nao foi encontrado");//o registro não existe no arquivo
        }
    }

    public static void transaction(int idFazer, int idRecebe, float valor, File f){//metodo para realizar a transação entre duas contas

        long skipSomaFazer = 0, skipSomaRecebe = 0; //posições para serem acessadas depois, para realizar a atualização dos floats de saldo e do int da quantidade de tranferencias
        int transferencias; //quantidade de tranferencias
        float saldoFazer = 0, saldoRecebe = 0; //saldo das contas que fazem a transferencia e recebem a mesma, respectivamente

        try{
            int n;//numero de emails do registro

            RandomAccessFile raf = new RandomAccessFile(f, "rw");

            long fazerPos = findRegistro(f, idFazer);//recebe a posição do registro desejado

            if(fazerPos != 0){
                raf.seek(fazerPos);//posiciona o ponteiro do raf na posição do registro desejado

                raf.readInt();//lê os dados até o ponteiro chegar no float do saldo
                raf.readUTF();
                raf.readUTF();
                raf.readUTF();
                n = raf.readInt();
                for(int i=0; i<n; i++){     
                    raf.readUTF();
                }
                raf.readUTF();
                raf.readUTF();
                skipSomaFazer = raf.getFilePointer();//pega a posição do ponteiro
                saldoFazer = raf.readFloat();//armazena o saldo do registro
                transferencias = raf.readInt();//armazena a quantidade de transferencias

                if(saldoFazer < valor){
                    System.out.println("ERRO! O saldo da conta que realizara a transacao e insuficiente.");
                }
                else{
                    transferencias++;//incrementa as tranferencias
                    saldoFazer = saldoFazer - valor;//calcula o valor final da transação

                    raf.seek(skipSomaFazer);//posiciona o ponteiro para realizar a alteração dos valores
                    raf.writeFloat(saldoFazer);//atualiza o saldo da conta
                    raf.writeInt(transferencias);//incrementa a quantidade de tranferencias realizadas pela conta

                    long recebePos = findRegistro(f, idRecebe);//recebe a posição do registro desejado

                    if(recebePos != 0){
                        raf.seek(recebePos);//posiciona o ponteiro do raf na posição do registro desejado

                        raf.readInt();//lê os dados até o ponteiro chegar no float do saldo
                        raf.readUTF();
                        raf.readUTF();
                        raf.readUTF();
                        n = raf.readInt();
                        for(int i=0; i<n; i++){
                            raf.readUTF();
                        }
                        raf.readUTF();
                        raf.readUTF();
                        skipSomaRecebe = raf.getFilePointer();//pega a posição do ponteiro
                        saldoRecebe = raf.readFloat();//armazena o saldo do registro

                        saldoRecebe = saldoRecebe + valor;//calcula o valor final da transação

                        raf.seek(skipSomaRecebe);//posiciona o ponteiro para realizar a alteração dos valores
                        raf.writeFloat(saldoRecebe);//atualiza o saldo da conta
                    }

                }
            }

            raf.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void intercalacao(File f){ //realiza a intercalação balanceada comum dos registros do arquivo

        try{
            RandomAccessFile raf = new RandomAccessFile(f, "r");

            RandomAccessFile[] rafTmp = new RandomAccessFile[4];

            File[] tmp = new File[4];
            Conta[] contas = new Conta[5];

            for(int i=0; i<4; i++){
                tmp[i] = new File("arquivo_tmp_" + (i+1) + ".txt");
            }

            for(int i=0; i<4; i++){
                tmp[i].createNewFile();
            }

            for(int j=0; j<4; j++){
                rafTmp[j] = new RandomAccessFile(tmp[j], "rw");
            }

            int lastId = raf.readInt();
            long pos = raf.getFilePointer();
            int contasCount = 0;//conta o numero de contas existente no array de contas

            for(int j=0; contasCount < 5;){ //primeiro for, realiza a partição dos elementos do arquivo original
                
                contasCount = 0;

                for(int i=0; i<5; i++){ //aponta todos os objetos conta, do array de contas, para null
                    contas[i] = null;   //desse modo, null não será lido pelo programa, e nenhum resquicio de outras contas sobrará para o proximo loop de for
                }

                for(int i=0; i<5 && pos < f.length(); i++){ //preenche um objeto do array de contas com as informações de um registro
                    contas[i].idConta = raf.readInt();
                    contas[i].lapide = raf.readByte();
                    contas[i].tamanho = raf.readInt();
                    contas[i].nomePessoa = raf.readUTF();
                    contas[i].cpf = raf.readUTF();
                    contas[i].cidade = raf.readUTF();
                    contas[i].numEmails = raf.readInt();
                    contas[i].email = new String[contas[i].numEmails];
                    for(int a=0; a<contas[i].numEmails; a++){
                        contas[i].email[a] = raf.readUTF();
                    }
                    contas[i].nomeUsuario = raf.readUTF();
                    contas[i].senha = raf.readUTF();
                    contas[i].saldoConta = raf.readFloat();
                    contas[i].transferenciasRealizadas = raf.readInt();

                    contasCount++;

                    pos = raf.getFilePointer();
                }

                contas = ordenacao(contas,contasCount);//envia o array de conta para ser ordenado pelo metodo

                for(int a=0; a<5; a++){ //insere todos os objetos do array em um arquivo temporario
                    rafTmp[j].write(contas[a].toByteArray());
                }

                if(j == 0){//alterna entre os arquivos temporarios
                    j = 1;
                }
                else{
                    j = 0;
                }
            }

            int insideLoopCount = 0;

            contasCount = 0;

            for(int k=1; insideLoopCount>1; k++){ //segundo for, realiza a intercalação das partições dos registros

                insideLoopCount = 0;

                contas = new Conta[k*2*5]; //aloca o espaço na memoria para k*5*2 contas, para realizar a intercalação

                int j=0;

                contasCount = k*2*5;

                for(int z=(j+2)%4; contasCount == k*2*5;){//primeiro loop de dentro (do segundo loop)
                    contasCount = 0; //zera a contagem de quantas contas tem no array toda vez que o for para inserí-las recomeça
    
                    for(int i=0; i<5*2*k; i++){
                        contas[i] = null;
                    }

                    int i=0;
    
                    for(; i<5*k && pos < tmp[j].length(); i++){ //le os dados de um arquivo temporaria
                        contas[i].idConta = rafTmp[j].readInt();
                        contas[i].lapide = rafTmp[j].readByte();
                        contas[i].tamanho = rafTmp[j].readInt();
                        contas[i].nomePessoa = rafTmp[j].readUTF();
                        contas[i].cpf = rafTmp[j].readUTF();
                        contas[i].cidade = rafTmp[j].readUTF();
                        contas[i].numEmails = rafTmp[j].readInt();
                        contas[i].email = new String[contas[i].numEmails];
                        for(int a=0; a<contas[i].numEmails; a++){
                            contas[i].email[a] = rafTmp[j].readUTF();
                        }
                        contas[i].nomeUsuario = rafTmp[j].readUTF();
                        contas[i].senha = rafTmp[j].readUTF();
                        contas[i].saldoConta = rafTmp[j].readFloat();
                        contas[i].transferenciasRealizadas = rafTmp[j].readInt();
    
                        contasCount++;
    
                        pos = rafTmp[j].getFilePointer();
                    }

                    for(; i<5*k*2 && pos < tmp[j+1].length(); i++){//le os dados de outro arquivo temporario
                        contas[i].idConta = rafTmp[j+1].readInt();
                        contas[i].lapide = rafTmp[j+1].readByte();
                        contas[i].tamanho = rafTmp[j+1].readInt();
                        contas[i].nomePessoa = rafTmp[j+1].readUTF();
                        contas[i].cpf = rafTmp[j+1].readUTF();
                        contas[i].cidade = rafTmp[j+1].readUTF();
                        contas[i].numEmails = rafTmp[j+1].readInt();
                        contas[i].email = new String[contas[i].numEmails];
                        for(int a=0; a<contas[i].numEmails; a++){
                            contas[i].email[a] = rafTmp[j+1].readUTF();
                        }
                        contas[i].nomeUsuario = rafTmp[j+1].readUTF();
                        contas[i].senha = rafTmp[j+1].readUTF();
                        contas[i].saldoConta = rafTmp[j+1].readFloat();
                        contas[i].transferenciasRealizadas = rafTmp[j+1].readInt();
    
                        contasCount++;
    
                        pos = rafTmp[j+1].getFilePointer();
                    }
    
                    contas = ordenacao(contas,contasCount);
    
                    for(int a=0; a<k*5*2; a++){
                        rafTmp[z].write(contas[a].toByteArray()); //armazena os registros ordenados em seus respectivos arquivos temporarios
                    }

                    if(z == (j+2)%4){ //alterna entre os arquivos temporarios para armazenar os regsitros ordenados
                        z = (j+2)%4 + 1;
                    }
                    else{
                        z = (j+2)%4;
                    }

                    insideLoopCount++;
                }

                rafTmp[j].seek(0);
                rafTmp[j+1].seek(0);

                if(j == 0){ //alterna entre os arquivos temporarios para ler
                    j = 2;
                }
                else{
                    j = 0;
                }
            }

            raf.close();

            for(int i=0; i<4; i++){
                rafTmp[i].close();
            }

        }catch(Exception e){
        }

    }

    public static Conta[] ordenacao(Conta[] contas, int m){ //ordena o vetor de contas 

        Conta sub;

        for(int i=0; i<m; i++){

            for(int j=m;j>0;j--){
                if(contas[j-1].idConta > contas[j].idConta){
                    sub = contas[j];
                    contas[j] = contas[j-1];
                    contas[j-1] = sub;
                }
            }

        }

        sub = null;

        return contas;
    }

    public static void atualizarRegistro(File f, int idEdit){ //metodo para atualizar os campos de um registro

        boolean bool = true;//boolean para checar se o usuario deseja sair do programa ou nao

        try {
            RandomAccessFile raf = new RandomAccessFile(f, "rw");

            long pos = findRegistro(f, idEdit);

            raf.seek(pos);

            Conta conta = new Conta();

            conta.idConta = idEdit; //armazena todos os dados de um registro em um objeto conta para realizar a reinserção no final 
            conta.lapide = 0;
            conta.tamanho = raf.readInt();
            conta.nomePessoa = raf.readUTF();
            conta.cpf = raf.readUTF();
            conta.cidade = raf.readUTF();
            conta.numEmails = raf.readInt();
            conta.email = new String[conta.numEmails];
            for(int i=0; i<conta.numEmails; i++){
                conta.email[i] = raf.readUTF();
            }
            conta.nomeUsuario = raf.readUTF();
            conta.senha = raf.readUTF();
            conta.saldoConta = raf.readFloat();
            conta.transferenciasRealizadas = raf.readInt();
            
            do{
                System.out.println("O que deseja editar?");//menu para saber qual campo o usuario deseja editar
                System.out.println("1 - Nome da pessoa");
                System.out.println("2 - cpf");
                System.out.println("3 - Cidade");
                System.out.println("4 - quantidade de emails");
                System.out.println("5 - email");
                System.out.println("6 - nome do usuario");
                System.out.println("7 - senha");
                System.out.println("8 - sair");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1://atualiza o nome da pessoa
                        
                        System.out.println("Digite o novo nome:");
                        String newName = sc.nextLine();

                        raf.seek(pos);raf.readInt();//percorre parte do registro até chegar na posição desejada

                        long changePos= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newName.length() == conta.nomePessoa.length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePos);
                            raf.writeUTF(newName);
                        }
                        else{                                              //caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            raf.seek(f.length());
                            raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(newName);                        //armazena todos os dados novamente, porem no final do arquivo
                            raf.writeUTF(conta.cpf);
                            raf.writeUTF(conta.cidade);
                            raf.writeInt(conta.numEmails);
                            for(int i=0; i<conta.numEmails; i++){
                                raf.writeUTF(conta.email[i]);
                            }
                            raf.writeUTF(conta.nomeUsuario);
                            raf.writeUTF(conta.senha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;
                
                    case 2://atualiza o cpf 

                        System.out.println("Digite o novo cpf:");
                        String newCpf = sc.nextLine();

                        raf.seek(pos);raf.readInt();raf.readUTF();//percorre parte do registro até chegar na posição desejada

                        long changePosCpf= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newCpf.length() == conta.cpf.length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePosCpf);
                            raf.writeUTF(newCpf);
                        }
                        else{//caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            raf.seek(f.length());
                            raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(conta.nomePessoa);
                            raf.writeUTF(newCpf);
                            raf.writeUTF(conta.cidade);
                            raf.writeInt(conta.numEmails);
                            for(int i=0; i<conta.numEmails; i++){
                                raf.writeUTF(conta.email[i]);
                            }
                            raf.writeUTF(conta.nomeUsuario);
                            raf.writeUTF(conta.senha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;

                    case 3://atualiza a cidade

                        System.out.println("Digite a nova cidade:");
                        String newCidade = sc.nextLine();

                        raf.seek(pos);raf.readInt();raf.readUTF();raf.readUTF();//percorre parte do registro até chegar na posição desejada

                        long changePosCidade= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newCidade.length() == conta.cidade.length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePosCidade);
                            raf.writeUTF(newCidade);
                        }
                        else{//caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            raf.seek(f.length());raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(conta.nomePessoa);
                            raf.writeUTF(conta.cpf);
                            raf.writeUTF(newCidade);
                            raf.writeInt(conta.numEmails);
                            for(int i=0; i<conta.numEmails; i++){
                                raf.writeUTF(conta.email[i]);
                            }
                            raf.writeUTF(conta.nomeUsuario);
                            raf.writeUTF(conta.senha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;

                    case 4://atualiza a quantidade de emails da conta

                        System.out.println("Digite a nova quantidade de emails:");
                        int newNumEmail = sc.nextInt();
                        sc.nextLine();

                        raf.seek(pos);raf.readInt();raf.readUTF();raf.readUTF();raf.readUTF();//percorre parte do registro até chegar na posição desejada

                        long changePosNumEmail= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        raf.seek(pos - 1);
                        raf.writeByte(1);

                        raf.seek(f.length());raf.writeInt(conta.idConta);
                        raf.writeByte(0);
                        raf.writeInt(conta.tamanho);
                        raf.writeUTF(conta.nomePessoa);
                        raf.writeUTF(conta.cpf);
                        raf.writeUTF(conta.cidade);
                        raf.writeInt(newNumEmail);
                        String[] newEmails = new String[newNumEmail];
                        for(int j=0; j<conta.numEmails; j++){
                            newEmails[j] = conta.email[j];
                        }
                        for(int i=0; i<newNumEmail; i++){
                            if(i<conta.numEmails){
                                raf.writeUTF(conta.email[i]);
                            }
                            else{
                                System.out.println("Digite um novo email:");
                                raf.writeUTF(sc.nextLine());
                            }
                        }
                        raf.writeUTF(conta.nomeUsuario);
                        raf.writeUTF(conta.senha);
                        raf.writeFloat(conta.saldoConta);
                        raf.writeInt(conta.transferenciasRealizadas);

                        break;

                    case 5://atualiza um email especifico

                        System.out.println("Digite o número do email que deseja editar:");
                        int newEmailNumber = sc.nextInt();
                        sc.nextLine();

                        System.out.println("Digite o novo email:");
                        String newEmail = sc.nextLine();

                        raf.seek(pos);raf.readInt();raf.readUTF();raf.readUTF();raf.readUTF();raf.readInt();//percorre parte do registro até chegar na posição desejada

                        int i=0;

                        for(; i<newEmailNumber - 1;i++){
                            raf.readUTF();
                        }

                        long changePosEmail= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newEmail.length() == conta.email[i+1].length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePosEmail);
                            raf.writeUTF(newEmail);
                        }
                        else{//caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            conta.email[i+1] = newEmail;
                            raf.seek(f.length());raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(conta.nomePessoa);
                            raf.writeUTF(conta.cpf);
                            raf.writeUTF(conta.cidade);
                            raf.writeInt(conta.numEmails);
                            for(int j=0; j<conta.numEmails; j++){
                                raf.writeUTF(conta.email[j]);
                            }
                            raf.writeUTF(conta.nomeUsuario);
                            raf.writeUTF(conta.senha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;

                    case 6://atualiza o nome do usuario

                        System.out.println("Digite a novo nome do usuario:");
                        String newNomeUsuario = conta.checkNomeUsuario(true, f);

                        raf.seek(pos);raf.readInt();raf.readUTF();raf.readUTF();raf.readUTF();raf.readInt();//percorre parte do registro até chegar na posição desejada

                        for(int j=0; j<conta.numEmails; j++){
                            raf.readUTF();
                        }

                        long changePosNomeUsuario= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newNomeUsuario.length() == conta.nomeUsuario.length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePosNomeUsuario);
                            raf.writeUTF(newNomeUsuario);
                        }
                        else{//caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            raf.seek(f.length());raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(conta.nomePessoa);
                            raf.writeUTF(conta.cpf);
                            raf.writeUTF(conta.cidade);
                            raf.writeInt(conta.numEmails);
                            for(int j=0; j<conta.numEmails; j++){
                                raf.writeUTF(conta.email[j]);
                            }
                            raf.writeUTF(newNomeUsuario);
                            raf.writeUTF(conta.senha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;

                    case 7:// atualiza a senha

                        System.out.println("Digite a nova senha:");
                        String newSenha = sc.nextLine();

                        raf.seek(pos);raf.readInt();raf.readUTF();raf.readUTF();raf.readUTF();raf.readInt();//percorre parte do registro até chegar na posição desejada

                        for(int j=0; j<conta.numEmails; j++){
                            raf.readUTF();
                        }
                        raf.readUTF();

                        long changePosSenha= raf.getFilePointer();//armazena a posição do ponteiro antes da string para mudar o nome

                        if(newSenha.length() == conta.senha.length()){//caso a string seja de tamanho igual a que ja havia sido armazenada
                            raf.seek(changePosSenha);
                            raf.writeUTF(newSenha);
                        }
                        else{//caso a string seja maior ou menor do que a que estava armazenada
                            raf.seek(pos - 1);
                            raf.writeByte(1);
                            raf.seek(f.length());raf.writeInt(conta.idConta);
                            raf.writeByte(0);
                            raf.writeInt(conta.tamanho);
                            raf.writeUTF(conta.nomePessoa);
                            raf.writeUTF(conta.cpf);
                            raf.writeUTF(conta.cidade);
                            raf.writeInt(conta.numEmails);
                            for(int j=0; j<conta.numEmails; j++){
                                raf.writeUTF(conta.email[j]);
                            }
                            raf.writeUTF(conta.nomeUsuario);
                            raf.writeUTF(newSenha);
                            raf.writeFloat(conta.saldoConta);
                            raf.writeInt(conta.transferenciasRealizadas);
                        }

                        break;

                    case 8:
                        bool = false;
                        break;
                }

            }while(bool);

            raf.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public static long findRegistro(File f, int idFind){//metodo que retorna a posição do registro com o id desejado
        long pos=0;
        boolean checkApagado = false;

        int id, n;//id = id que é lido para ser comparado com o id desejado, n = numero de emails de um registro

        try{
            RandomAccessFile raf2 = new RandomAccessFile(f, "rw");

            raf2.readInt();//lê o int com o ultimo id inserido

            while(true){            // while que recebe o id do registro do arquivo e compara com o id desejado
                id = raf2.readInt(); // se o id for diferente (if) o raf2 lê todos os dados do registro mas não armazena, apenas para passar para posicionar seu ponteiro no inicio do proximo registro
                if(id != idFind){   
                    raf2.readByte();
                    raf2.readInt();
                    raf2.readUTF();
                    raf2.readUTF();
                    raf2.readUTF();
                    n = raf2.readInt();
                    for(int i=0; i<n; i++){
                        raf2.readUTF();
                    }
                    raf2.readUTF();
                    raf2.readUTF();
                    raf2.readFloat();
                    raf2.readInt();
                }
                else{               // se o id for igual (else), checa se o registro foi deletado ou não e retorna a posição do mesmo
                    if(raf2.readByte() == 1){//1 significa que o byte foi apagado
                        raf2.readInt();
                        raf2.readUTF();
                        raf2.readUTF();
                        raf2.readUTF();
                        n = raf2.readInt();
                        for(int i=0; i<n; i++){
                            raf2.readUTF();
                        }
                        raf2.readUTF();
                        raf2.readUTF();
                        raf2.readFloat();
                        raf2.readInt();
                        checkApagado = true;
                    }
                    else{
                        pos = raf2.getFilePointer();
                        break; 
                    }
                }
            }    

            raf2.close();
        }catch(Exception e){

            if(checkApagado == true){//se o registro tiver sido apagado, imprime a mensagem que foi apagado
                System.out.println("ERRO! O registro foi apagado");
            }
            else{//se não tiver sido apagado, é porque ele nao existe no arquivo
                System.out.println("ERRO! O registro nao foi encontrado");//o registro não existe no arquivo
            }
        }

        return pos;
    }
}

class Conta { //classe de conta, ou de registro

    Scanner sc = new Scanner(System.in);

    int idConta, transferenciasRealizadas, numEmails, tamanho;/*idConta = id da conta, transferenciasRealizadas = quantidade de transferencias
    numEmails = quantidade de emails da conta, tamanho = tamanho do arquivo*/
    float saldoConta;//saldo da conta
    String nomePessoa, nomeUsuario, senha, cidade, cpf;/* nomePessoa = nome da pessoa, nomeUsuario = nome do usuario, senha = senha, 
    cidade = cidade, cpf = cpf */
    String[] email;//array de emails
    byte lapide;

    Conta(){//construtor que inicia a lapide como nao marcada e a quantidade de transferencias como 0 
        transferenciasRealizadas = 0;
        lapide = 0;
    }

    public String checkCPF(boolean sizeCheck){ //checa se o cpf tem exatamente 11 digitos
        String cpf;

        if(sizeCheck == false){
            System.out.println("ERRO! O CPF deve ter exatamente 11 caracteres de tamanho");
        }

        System.out.println("Digite seu CPF:");
        cpf = sc.nextLine();

        if(cpf.length() != 11){
            checkCPF(false);
        }
        
        return cpf;
    }

    public String checkNomeUsuario(boolean checkUnique, File f){//checa se o nome do usuario é unico
        String nomeUsuario;

        if(checkUnique == false){
            System.out.println("ERRO! O nome do usuario deve ser unico para cada conta.");
        }
        checkUnique = true;

        System.out.println("Digite o nome do usuario:");
        nomeUsuario = sc.nextLine();

        try {
            RandomAccessFile raf2 = new RandomAccessFile(f, "r");
            int n;
            long length = f.length();

            raf2.readInt();

            while(true){
                raf2.readInt();//percorre todos os campos do registro ate cehgar no nome
                raf2.readByte();
                raf2.readInt();
                raf2.readUTF();
                raf2.readUTF();
                raf2.readUTF();
                n = raf2.readInt();
                for(int i=0; i<n; i++){
                    raf2.readUTF();
                }
                String nomeUsuarioCheck = raf2.readUTF();
                raf2.readUTF();
                raf2.readFloat();
                raf2.readInt();

                if(nomeUsuarioCheck.equals(nomeUsuario)){//checa se o nome desejado é igual ao nome que ja existe
                    checkUnique = false;
                    checkNomeUsuario(checkUnique, f);//recursivo até que o usuario insira um nome unico
                }
                else if(raf2.getFilePointer() >= length){//caso tenha chegado no fim do arquivo
                    break;
                }
            }

            raf2.close();

        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }

        return nomeUsuario;
    }

    public byte[] toByteArray() throws IOException{ //transforma o objeto em um array de bytes para facilitar a inserção.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idConta);
        dos.writeByte(lapide);
        dos.writeInt(tamanho);
        dos.writeUTF(nomePessoa);
        dos.writeUTF(cpf);
        dos.writeUTF(cidade);
        dos.writeInt(numEmails);
        for(int i=0; i<numEmails; i++){
            dos.writeUTF(email[i]);
        }
        dos.writeUTF(nomeUsuario);
        dos.writeUTF(senha);
        dos.writeFloat(saldoConta);
        dos.writeInt(transferenciasRealizadas);

        return baos.toByteArray();
    }
}

class HashingExtendido {//classe de hashing extendido

    Bucket bu;//cada hashing tem um bucket para armazenar os dados
    int p;//p é a profundidade global

    HashingExtendido(){//construtor
        bu = new Bucket();
        p = 2;//profundidade inicial é 2
    }

    public int h(int id){//função de hashing
        int  num=0, pow = 1;

        for(int k=0; k<p; k++){
            pow = pow * 2;
        }

        num = id % pow;

        return num;
    }

    public void insertTudo(File f, File fHash){//inserir todos os dados do arquivo original no arquivo de hashing

        try{
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            RandomAccessFile rafHash = new RandomAccessFile(fHash, "rw");

            long pos=0;
            int[] idB = new int[raf.readInt()];//o ultimo id inserido, é o numero de ids que existem
            long[] posB = new long[idB.length];//o numero de posições desses ids é o mesmo
            int i=0;//contador para o for

            for( ; pos<f.length(); i++){
                posB[i] = raf.getFilePointer();//a posição do inicio do registro é armazenada
                idB[i] = raf.readInt();//o id tambem é armazenado
                if(raf.readByte() == 1){
                    i--;
                }
                raf.readInt();
                raf.readUTF();
                raf.readUTF();
                raf.readUTF();
                int n = raf.readInt();
                for(int j=0; j<n; j++){
                    raf.readUTF();
                }
                raf.readUTF();
                raf.readUTF();
                raf.readFloat();
                raf.readInt();
                pos = raf.getFilePointer();//checa se o final do arquivo foi alcançado
            }

            int pow = 1;

            for(int j=0; j<p; j++){//calcula 2 elevado a p
                pow = pow * 2;
            }

            for(int j=0; j<pow; j++){
                for(int k=0; k<4; k++){
                    rafHash.writeInt(0);//insere todos os elementos dos buckets com 0
                    rafHash.writeLong(-1);//e -1 
                }
            }

            raf.close();
            rafHash.close();

            for(int j=0; j<i; j++){
                insertBucket(idB[j], posB[j], fHash);//inserir no bucket
            }
        }catch(Exception e){
        }

    }

    public void insertBucket(int idB, long posB, File fHash){//insere id e posição do registro no arquivo de hashing
        if(!(bu.insert(idB, posB, h(idB), fHash))){//caso seja falso, ou seja, não foi possivel inserir, a profundidade global é aumentada
            p++;
            bu.insert(idB, posB, h(idB), fHash);//a inserção é tentada novamente
        }
    }

    public long search(File fHash, int idFind){//metodo para procurar um id no arquivo de hashing e retorna a posição do registro no arquivo original

        int idH=0;//id que sera usado para comparar com o id desejado
        long pos=0;//posição a ser retornada

        try{

            RandomAccessFile raf = new RandomAccessFile(fHash, "r");

            int idHash = h(idFind);//id apos a função de hash

            raf.seek(48*idHash);//vai para a posição do arquivo em que o idHash se encontra

            for(int i=0; i<4; i++){
                idH = raf.readInt();//id a ser comparado
                if(idH == idFind){
                    pos = raf.readLong();//le a posição do id no arquivo original
                    break;
                }
                else{
                    raf.readLong();
                }
            }

            raf.close();

        }catch(Exception e){
            System.out.println("ERRO! O registro não existe no arquivo de indices de hashing");
        }

        return pos;//retorna a posição do registro no arquivo orginial

    }

}

class listaInvertida{

    private static RandomAccessFile arq;
    private final String ListaInveritidaNome = "listaInvertida/listaInvertidaNome.db";
    private final String ListaInveritidaCidade = "listaInvertida/listaInvertidaCidade.db";
    

    //Função para a construtução de parametros e verificação de eistencia de arquivo.
    public listaInvertida(){
        try{
            boolean exist_Cidade = (new File(ListaInveritidaCidade)).exists();
            boolean exist_Nome = (new File(ListaInveritidaNome)).exists();
            if (exist_Cidade == true && exist_Nome == true){

            } else
                try {
                    RandomAccessFile arq = new RandomAccessFile(ListaInveritidaCidade, "rw");
                    RandomAccessFile arqN = new RandomAccessFile(ListaInveritidaNome, "rw");
                    arq.close();
                    arqN.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }             
    }

    //Função para verificar palavra dentro da lista
    public boolean verificaPalavra(String palavra, String file){
        try {
            arq = new RandomAccessFile(file, "r");

            String palavranoArq = "";

            arq.seek(0);
            while(arq.getFilePointer() < arq.length()){
                palavranoArq = arq.readUTF();
                arq.readInt();
                arq.readUTF();
                arq.readUTF();
                arq.readUTF();
                int n = arq.readInt();
                for(int j=0; j<n; j++){
                    arq.readUTF();
                }
                arq.readUTF();
                arq.readUTF();
                arq.readFloat();
                arq.readInt();
                if(palavra.compareTo(palavranoArq) == 0) {
                    return true;
                }
            }
          
        } catch(Exception e){
           System.out.println("Não foi encotrado a palvra no arquivo.");
        }
        return false;
    }

    public static int contarPalavras(String nome){
        int qntPalavras = 0;
        for(int i=0; i < nome.length(); i++){
            if(nome.charAt(i) == ' '){
                qntPalavras++;
            }
        }
        return qntPalavras;
    }
    //Função para procurar a posicao no arquivo que esta livre para inserir o indice
    public long posIndiceLivre(String palavra, String arquivo) {
        try {
            arq = new RandomAccessFile(arquivo, "rw");

            arq.seek(0);
            long pos = arq.getFilePointer();
            String palavra_arq;

            while(arq.getFilePointer() < arq.length()) {
                palavra_arq = arq.readUTF();

                if(palavra.compareTo(palavra_arq) == 0) {
                    // Pega a posição antes de ler para se caso o valor seja == a -1 retornar a posicao correta livre
                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    int n = arq.readInt();
                    for(int j=0; j<n; j++){
                        if(arq.readByte() == -1){
                            return pos;
                        }
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readByte() == -1) {
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if (arq.readLong() == -1) {
                        arq.seek(pos);                  // vai para a ultima posicao livre registrada
                        arq.writeLong(arq.length());    // escreve a ultima posicao do arquivo como se fosse um ponteiro para a continuacao do array
                        arq.seek(arq.length());         // vai para a ultima posica
                        return arq.getFilePointer();    // retorna a ultima posicao do arquivo para criar o objeto apontado
                    }
                } else {
                    arq.readByte();
                    arq.readByte();
                    arq.readByte();
                    arq.readByte();
                    arq.readByte();
                    arq.readLong();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    //Cria a lista invertida do nome e cidade do time, quando ele existe faz um ponteiro para a proxima localizacao da continuacao, pois ele so tem tamanho 5. Caso ele nao exista ele cria um normal de tamanho 5 na ultima posicao do arquivo
    public void createArqLista(String nome, byte id, String arquivo) {
        String palavras[] = new String[contarPalavras(nome)];
        palavras = nome.split(" ");

        try {
            arq = new RandomAccessFile(arquivo, "rw");

            for(int i = 0; i < palavras.length; i++) {
                if(verificaPalavra(palavras[i], arquivo) == true) {
                    long posAvaliable = posIndiceLivre(palavras[i], arquivo);

                    if(posAvaliable != arq.length()) {
                        arq.seek(posAvaliable);
                        arq.writeByte(id);
                    } else {
                        // Temos que criar a mesma palavra novamente com o id desejado na frente
                        arq.seek(posAvaliable);
                        arq.writeUTF(palavras[i]);
                        arq.writeByte(id);
                        arq.writeByte(-1);
                        arq.writeByte(-1);
                        arq.writeByte(-1);
                        int n = arq.readInt();
                    for(int j=0; j<n; j++){
                        arq.writeByte(-1);
                    }
                        arq.writeByte(-1);
                        arq.writeLong(-1);
                    }
                } else {
                    arq.seek(arq.length());
                    arq.writeUTF(palavras[i]);
                    arq.writeByte(id);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeLong(-1); // endereço
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Função apenas para msotrar a lista invertida
    public void showListaInvertida() {
        try {
            arq = new RandomAccessFile(ListaInveritidaNome, "rw");

            while(arq.getFilePointer() < arq.length()) {
                System.out.println(arq.readUTF());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readLong());
                System.out.println("-----------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Remove todos os relacionadas com id no arquivo
    public void DeleteAllIdForList(byte id, String arquivo) {
        try {
            arq = new RandomAccessFile(arquivo, "rw");

            long pos;

            while(arq.getFilePointer() < arq.length()) {
                arq.readUTF(); // ler a palavra 
                
                pos = arq.getFilePointer();
                if(arq.readByte() == id) {
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if (arq.readByte() == id) {
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if (arq.readByte() == id) {
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if (arq.readByte() == id) {
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if (arq.readByte() == id) {
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                arq.readLong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Essa funcao tem a funcao de deletar os indices do arqui e atualizar conforme as mudancas no arquivo original
    public void updateLista(String palavra, byte id, String arquivo, boolean isDelete) {
        try {
            // Pega a posicao que precisa ser deletada e deleta do arquivo
            DeleteAllIdForList(id, arquivo);

            if(isDelete == false) {
                createArqLista(palavra, id, arquivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Funcao necessaria para confirmar se o id esta presente na lista
    static public boolean idExistsInArray(ArrayList<Byte> ids, byte id) {
        for(Byte idList : ids) {
            if(idList == id) {
                return true;
            } else {
                break;
            }
        }
        return false;
    }
    //Função "Main" que recebe os parametros, busa e printa.
    public static void buscaLista(String palavra, String file){
        String palavras[] = new String[contarPalavras(palavra)];
        palavras = palavra.split(" ");
        try {
            arq = new RandomAccessFile(file, "rw");
            String palavra_arq;
            byte id;
            long pos;
            ArrayList<Byte> ids = new ArrayList<>();

            // for para buscar por cada palavra no arquivo
            for(int i = 0; i < palavras.length; i++) {
                // while para navegar dentro do arquivo tentando buscar a palavra
                arq.seek(0);
                while(arq.getFilePointer() < arq.length()) {
                    palavra_arq = arq.readUTF();
                    
                    if(palavras[i].compareTo(palavra_arq) == 0) {
                        pos = arq.getFilePointer();
                        if(arq.readByte() != -1) {
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids, id) == false) {
                                ids.add(id);
                            }
                        }

                        pos = arq.getFilePointer();
                        if (arq.readByte() != -1) {
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids, id) == false) {
                                ids.add(id);
                            }
                        }

                        pos = arq.getFilePointer();
                        if (arq.readByte() != -1) {
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids, id) == false) {
                                ids.add(id);
                            }
                        }

                        pos = arq.getFilePointer();
                        if (arq.readByte() != -1) {
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids, id) == false) {
                                ids.add(id);
                            }
                        }

                        pos = arq.getFilePointer();
                        if (arq.readByte() != -1) {
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids, id) == false) {
                                ids.add(id);
                            }
                        }

                        pos = arq.getFilePointer();
                        if (arq.readLong() != -1) {
                            arq.seek(pos);
                        }
                    } else {
                        arq.readByte();
                        arq.readByte();
                        arq.readByte();
                        arq.readByte();
                        arq.readByte();
                        arq.readLong();
                    }
                }
            }

            System.out.println("Os ID`s relacionados a palavra digitada foi: ");
            System.out.println(ids);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Bucket {
    int[] id = new int[4];   //ids de um bucket
    long[] pos = new long[4];//posições dos registros de um bucket

    Bucket(){ //construtor para inicializar cada id e pos como 0 e -1 respectivamente, pois nenhum id será 0 e nenhuma posição será -1
        for(int i=0; i<4; i++){
            id[i] = 0;
            pos[i] = -1;
        }
    }

    public boolean insert(int id, long pos, int buNumber, File fH){ //insere um id no arquivo de hashing

        boolean bool = false;//bool para checar se a inserção foi feita ou não

        try{
            RandomAccessFile raf4 = new RandomAccessFile(fH, "rw");

            raf4.seek(0);

            for(int i=0; i<buNumber*4; i++){//cada bucket tem 4 ids e 4 posições, o for lê bucket por bucket
                raf4.readInt();
                raf4.readLong();
            }

            long posF = raf4.getFilePointer();//armazena a posição para procurar se ha espaço no bucket desejado

            for(int i=0; i<4; i++){ //preenche todos os 4 ids e posições com os dados encontrados no arquivo
                this.id[i] = raf4.readInt();
                this.pos[i] = raf4.readLong();
            }

            for(int i=0; i<4; i++){
                if(this.id[i] == 0){//insere os dados recebidos como parametro caso ainda haja espaço no bucket
                    this.id[i] = id;
                    this.pos[i] = pos;
                    bool = true;
                    break;
                }
            }

            raf4.seek(posF);

            for(int i=0; i<4; i++){//insere os dados novos no arquivo
                raf4.writeInt(this.id[i]);
                raf4.writeLong(this.pos[i]);
            }

            raf4.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return bool;
    }
}
