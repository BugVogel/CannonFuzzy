

package controller;

import java.awt.Dimension;
import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.defuzzifier.Defuzzifier;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


public class Controller {

    private double gravidade = 10.0;
    private double alturaBocaDoCanhao = 1.0;
    private double Xalvo = 58.0; //Coloque no máximo 60 aqui
    private double Yalvo = 5.0; //Coloque no máximo 20 aqui
    private double raioProjetil = 0.5;
    public double ultimoXt = 0.0;
    private FIS fis;
    private FunctionBlock functionBlock;
    //ALCANCE HORIZONTAL MÁXIMO: 90 metros. VELOCIDADE MÁXIMA: 30 metros por segundo.
    //ALCANCE VERTICAL MÁXIMO: 45 metros. Velocidade Máxima 30 metros.
    
    
    //seta parametros
    public Controller(double Xalvo,double Yalvo, double raioProjetil, double alturaBocaCanhao, double gravidade, String fileName){
        
        this.Xalvo = Xalvo;
        this.Yalvo = Yalvo;
        this.raioProjetil = raioProjetil;
        this.alturaBocaDoCanhao = alturaBocaCanhao;
        this.gravidade = gravidade;
        
        
          // Load from 'FCL' file
        
        fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }
        
         functionBlock  = fis.getFunctionBlock("cannon");
        
        
    }
    
    
    //Parametros default
    public Controller(String fileName){
        
        
        fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }
        
        
        functionBlock  = fis.getFunctionBlock("cannon");
        
        
    }
    
    public double calculaPosicaoDeColisaoX(double angulo, double velocidade, double Yalcancado){
        double tempo = 2*velocidade*(Math.sin(angulo*(Math.PI)/180)/gravidade);
        return 0;
        
    }
    
    public double[] calculaDXDY(double[] saidaSimulacao){
        double[] retorno = new double[2];
        retorno[0] = saidaSimulacao[0]-Xalvo;
        retorno[1] = saidaSimulacao[1]-Yalvo;
        
        return retorno;
    }
    
    //retorna ponto que passou pelo alvo para calcular a diferença ou ponto de colisão do alvo
    public double[] simulacaoDoCanhao(double A0, double V0){
        int parada = 0;
        double Vx = V0*(Math.cos(( Math.PI / 180 )*A0));
        double Vy = V0*(Math.sin(( Math.PI / 180 )*A0));
        double tempoMax = (2*Vy)/gravidade;
        double Xr = this.getAlcance(A0, V0);
        double Yr = 0.0;
        
        for(int tInt = 1; tInt<tempoMax*10; tInt = tInt+1){
            double t = (double)tInt/10.0;
            
            double xt = Vx*t;
            ultimoXt = xt;
            if(expandeBalaX(xt) && parada == 0){ //passou pelo ponto X
                parada = 1;
                double yt = alturaBocaDoCanhao + (Vy*t) - (gravidade*(t*t)/2);
                Yr = yt; //se Yr for 0, entao
                if(expandeBalaY(yt)){ //acertou o tiro!
                    double[] saida = new double[3];
                    saida[0] = Xr;
                    saida[1] = Yr;
                    saida[2] = 1; //digito verificador do acerto
                    
                    return saida;
                }
            }
        }
        double[] saida = new double[3];
        saida[0] = Xr;
        saida[1] = Yr;
        saida[2] = 0;
        
        return saida;
    }
    
    
    
    public void mostrarConjuntos(){
        
        JFuzzyChart.get().chart(functionBlock);
        
    }
    
    public void fuzzifyDXDY(double DX, double DY){
        
        fis.setVariable("DX", DX);
        fis.setVariable("DY", DY);
        
    }

    public double getXalvo() {
        return Xalvo;
    }

    public void setXalvo(double Xalvo) {
        this.Xalvo = Xalvo;
    }

    public double getYalvo() {
        return Yalvo;
    }

    public void setYalvo(double Yalvo) {
        this.Yalvo = Yalvo;
    }
    
    public void avaliarFuzzy(){
        fis.evaluate();
    }
    
    public void mostrarDefuzzifier(){
        
        Variable angleVariation = functionBlock.getVariable("variacaoDeAngulo");
        JFuzzyChart.get().chart(angleVariation, angleVariation.getDefuzzifier(), true);
        
        Variable speedVariation = functionBlock.getVariable("variacaoDeVelocidade");
        JFuzzyChart.get().chart(speedVariation, speedVariation.getDefuzzifier(), true);
        
    }
    
    public double getDefuzifierAngle(){
        
        Variable angleVariation = functionBlock.getVariable("variacaoDeAngulo");
        Defuzzifier d = angleVariation.getDefuzzifier();
  
        
        return d.defuzzify();
    }
    
    
    
    public  double getDefuzifierSpeed(){
        
        Variable speedVariation = functionBlock.getVariable("variacaoDeVelocidade");
        Defuzzifier d = speedVariation.getDefuzzifier();
        
        
        return d.defuzzify();
    }
    
    
    private CategoryDataset criarDatasetParaGrafico(ArrayList<double[]> lista){
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int i=0;
        for(double[] dado : lista){
            
            
            dataset.addValue(dado[0], "DX", ""+(i+1));
            dataset.addValue(dado[1], "DY", ""+(i+1));
            dataset.addValue(dado[2], "Ajuste de Angulo", ""+(i+1));
            dataset.addValue(dado[3], "Ajuste de velocidade", ""+(i+1));
            i++;
         
        }
        
        return dataset;
    }
    
    private JFreeChart criarGráficoDeLinha(CategoryDataset dataset){
        
        JFreeChart graficoDeLinha = ChartFactory.createLineChart("Variação Entrada/Saida do Sistema Fuzzy", "Iteração", "Entradas", dataset, PlotOrientation.VERTICAL, true, false, false);
        
        return graficoDeLinha;
    }
    
    public ChartPanel criarGrafico(ArrayList<double[]> lista){
        
        CategoryDataset dataset = this.criarDatasetParaGrafico(lista);
        JFreeChart grafico = this.criarGráficoDeLinha(dataset);
        
        ChartPanel painel = new ChartPanel(grafico); 
        
        painel.setPreferredSize(new Dimension(664,400));
        
        return painel;
    }
    
    
    
    private boolean expandeBalaY(double yt){
        double ytC = yt+raioProjetil;
        double ytB = yt-raioProjetil;
        
        if(ytC >= Yalvo && ytB <= Yalvo){
            return true;
        }
        return false;
    }
    
    private boolean expandeBalaX(double xt){
        double xtE = xt-raioProjetil;
        double xtD = xt+raioProjetil;
        //System.out.println("RaioE / Raio D / X alvo"+xtE+"  "+xtD+"  "+Xalvo+"  "+raioProjetil);
        
        if(xtE >= Xalvo || xtD >= Xalvo){
            return true;
        }
        return false;
    }
    
        
    
    private double getAlcance(double ang, double velocidade){
        
        double seno2A = this.calcSeno2A(ang);

        return (velocidade*velocidade)*seno2A/gravidade;
    }
    
    
    private double calcSeno2A(double ang){
        
        double rad = ( Math.PI / 180 ) * (2*ang);
        
        
        return Math.sin(rad);
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
