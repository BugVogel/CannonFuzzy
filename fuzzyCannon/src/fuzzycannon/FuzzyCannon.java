
package fuzzycannon;

import controller.Controller;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;


public class FuzzyCannon {

 
    public static void main(String[] args) {

        
        Controller controller = new Controller("rules.fcl");
        double angulo = 20.15;
        double velocidade = 30;
        DecimalFormat df = new DecimalFormat("#0.00");
        
        
        
        
        for(int i=0; i< 10; i++){
            
            double[] saida =controller.simulacaoDoCanhao(angulo, velocidade);
            System.out.println("--------------------------------------------- Iteração:" + i);
            System.out.println("Valor do angulo: "+angulo);
            System.out.println("Valor da velocidade: "+velocidade);
            System.out.println("X Alcançado: "+saida[0]);
            System.out.println("Y alcançado: "+saida[1]);
            System.out.println("Atingiu alvo: "+saida[2]);

            double[] diferenca= controller.calculaDXDY(saida);

            System.out.println("Diferença DX: "+diferenca[0]);
            System.out.println("Diferença DY: "+diferenca[1]);


            controller.fuzzifyDXDY(diferenca[0], diferenca[1]);
            controller.avaliarFuzzy();
            double DAngle = controller.getDefuzifierAngle();
            double DSpeed = controller.getDefuzifierSpeed();
            
        
            //System.out.println(DSpeed);
            //System.out.println(DAngle);
            //DSpeed = Double.valueOf(df.format(DSpeed).replaceAll(",", ".")).doubleValue();
            //DAngle = Double.valueOf(df.format(DAngle).replaceAll(",", ".")).doubleValue();

            System.out.println("Valor a ser ajustado no angulo: "+ DAngle);
            System.out.println("Valor a ser ajustado na velocidade: "+DSpeed);


            

            angulo = angulo + DAngle;
            velocidade = velocidade + DSpeed;

            System.out.println("-------------------------------------------------------");
        
        }
      
    }
    
}
