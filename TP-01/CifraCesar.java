import java.io.File;
import java.io.RandomAccessFile;

class CifraCesar {

    public static void criptografar(int id){ //metodo para criptografar senhas e inseri-las novamente no registro, no arquivo
        
        try{

            RandomAccessFile raf = new RandomAccessFile("arquivo.txt", "r");
            File f = new File("arquivo.txt");

            long pos = Main.findRegistro(f, id);    //função para encontrar o registro desejado para ter sua senha criptografada

            Conta conta = new Conta();      // conta para armazenar os dados do registro

            raf.seek(pos);
            conta.idConta = id;             // armazena todos os dados de um registro para reinserir o mesmo depois
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

            String newSenha = cifraCesar(conta.senha);  //chama o metodo de ciframento de cesar para receber a nova senha, criptografada

            Main.atualizarSenha(newSenha, pos, conta);  //atualiza o registro no arquivo principal

            raf.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static String cifraCesar(String str){    //metodo de ciframento de cesar, que criptografa a senha antiga

        String newStr = "";                         //string que receberá a nova senha

        for(int i=0; i<str.length(); i++){          //for que passa por cada char da senha antiga para criptografar
            newStr += (char)(str.charAt(i) + 3);
        }

        return newStr;
    }

    public static String desCifraCesar(String str){ //metodo contrário ao de ciframento de cesar, descriptografa a senha

        String newStr = "";

        for(int i=0; i<str.length(); i++){          //for que percorre todos os chars da senha criptografada para transformá-la na senha antiga novamente
            newStr += (char)(str.charAt(i) - 3);
        }

        return newStr;
    }

    public static void descriptografar(int id){   //método chamado para descriptografar um registro com uma senha previamente criptografada

        try{

            RandomAccessFile raf = new RandomAccessFile("arquivo.txt", "r");
            File f = new File("arquivo.txt");

            long pos = Main.findRegistro(f, id);//achar o registro desejado

            Conta conta = new Conta();

            raf.seek(pos);
            conta.idConta = id;                 // armazena todos os dados de um registro para reinserir o mesmo depois
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

            String newSenha = desCifraCesar(conta.senha);//recebe a senha antiga, sem criptografia

            Main.atualizarSenha(newSenha, pos, conta);      //re insere o registro com a senha antiga, sem criptografia

            raf.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    
    }
}
