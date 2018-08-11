
package fuzzycannon;

import controller.Controller;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import org.jfree.chart.ChartPanel;


public class FuzzyCannon {

 
    public static void main(String[] args) {

        
        Controller controller = new Controller("rules.fcl");
        double angulo = 30; //inicialize sempre com um ângulo entre 30 e 60
        double velocidade = 10; //minimo de 5
        DecimalFormat df = new DecimalFormat("#0.00");
        int i = 0;
        double[] saida = new double[3];
        ArrayList<double[]> dadosGrafico = new ArrayList<double[]>();
       

        
        
        //for(int i=0; i< 10; i++){
        while(saida[2] != 1.0 && i <15000){
            
            saida =controller.simulacaoDoCanhao(angulo, velocidade);
            System.out.println("--------------------------------------------- Iteração:" + (1+i));
            i = i + 1;
            System.out.println("Valor do angulo: "+angulo);
            System.out.println("Valor da velocidade: "+velocidade);
            System.out.println("X Alcançado / X real: "+saida[0]+" / "+controller.getXalvo());
            System.out.println("Y alcançado / Y real: "+saida[1]+" / "+controller.getYalvo());
            if(saida[2] == 1.0){
                System.out.println("Atingiu o Alvo: SIM!");
                System.out.println("Coordenadas de colisão: X: "+controller.ultimoXt+"  Y: "+saida[1]);
                
                double[] diferenca= controller.calculaDXDY(saida);
                controller.fuzzifyDXDY(diferenca[0], diferenca[1]);
                controller.avaliarFuzzy();
                double DAngle = controller.getDefuzifierAngle();
                double DSpeed = controller.getDefuzifierSpeed();
                double[] dado = new double[4]; //DX,DY, AjusteX e AjusteY da iteração            
                dado[0] = diferenca[0];
                dado[1] = diferenca[1];
                dado[2] = DAngle;
                dado[3] = DSpeed;

                dadosGrafico.add(dado); 
                
                
            }
            else{
                System.out.println("Atingiu o Alvo: não...");
                double[] diferenca= controller.calculaDXDY(saida);

                System.out.println("Diferença DX: "+diferenca[0]);
                System.out.println("Diferença DY: "+diferenca[1]);


                controller.fuzzifyDXDY(diferenca[0], diferenca[1]);
                controller.avaliarFuzzy();
                double DAngle = controller.getDefuzifierAngle();
                double DSpeed = controller.getDefuzifierSpeed();

                double[] dado = new double[4]; //DX,DY, AjusteX e AjusteY da iteração
                dado[0] = diferenca[0];
                dado[1] = diferenca[1];
                dado[2] = DAngle;
                dado[3] = DSpeed;

                dadosGrafico.add(dado);
                
                
                //System.out.println(DSpeed);
                //System.out.println(DAngle);
                //DSpeed = Double.valueOf(df.format(DSpeed).replaceAll(",", ".")).doubleValue();
                //DAngle = Double.valueOf(df.format(DAngle).replaceAll(",", ".")).doubleValue();

                System.out.println("Valor a ser ajustado no angulo: "+ DAngle);
                System.out.println("Valor a ser ajustado na velocidade: "+DSpeed);



                //Não deixa angulo e velocidade sairem dos limites
                if(angulo+DAngle <= 90.0 && angulo+DAngle >= 1.0){
                        angulo = angulo + DAngle;
                }
                else if(DAngle > 0){
                    angulo = 90.0;
                }
                else{
                    angulo = 1;
                }
                if(velocidade+DSpeed <= 30.0 && velocidade+DSpeed > 0.0){
                    velocidade = velocidade + DSpeed;
                }
                else if(DSpeed > 0){
                    velocidade = 30.0;
                }
                else{
                    velocidade = 1;
                }
            }



            System.out.println("-------------------------------------------------------");
        
        }
        
        //Plotar o gráfico
        ChartPanel painel = controller.criarGrafico(dadosGrafico);
        
        Grafico grafico = new Grafico();
        grafico.setVisible(true);
        grafico.setPainel(painel);
        
        
        
      
    }
    
}
