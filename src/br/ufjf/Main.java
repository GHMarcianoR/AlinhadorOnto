/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zumbi
 */
public class Main {   
    
    
    public static void main(String [] args) throws IOException 
    {       
      BufferedWriter bw;
      List<Cluster> listCluster;
      HashMap<Cluster, List<Entidade> > ancorados;
      IndirectMatching indM;
      leParametros();
       try
       {
           bw = new BufferedWriter(new FileWriter("Log",true));
          
           bw.write("Inicio\n");
           bw.write(getDate()+" "+getTime()+"\n");
           bw.flush();
         
           Pre_processa pre_proc = new Pre_processa(args[0],args[1]);
          
           
           pre_proc.iniciaPreProcessamento();
           listCluster = pre_proc.getListCluster();
           ancorados = pre_proc.getAncorados();
           
           indM = new IndirectMatching(listCluster, ancorados);
           
           bw.write("Termino\n");
           bw.write(getDate()+" "+getTime()+"\n");
           bw.flush();
        }
      catch(Exception e)
      {
          System.err.println("Erro: "+e.getMessage()+" "+Arrays.toString(e.getStackTrace()));
      }
   
   }
    public static  String getTime()
     {
		 DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
		 Date date = new Date(); 
		 return dateFormat.format(date);
	 }
    public static String getDate()
	 { 
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		 Date date = new Date(); 		 
		 return dateFormat.format(date);
	 }
    public static void leParametros()
    {

        try 
        {
             BufferedReader br = new BufferedReader(new FileReader("parametros.txt"));
             List<String> l = new ArrayList<>();
        
            while(br.ready())
                l.add(br.readLine());
        
            Globais.val_seleciona_candidatos = Double.parseDouble(l.get(0));
            Globais.val_redirects = Double.parseDouble(l.get(1));
            Globais.indirect_categories_prop = Double.parseDouble(l.get(2));
        } catch (IOException | NumberFormatException ex) {
           
            System.err.println("Erro ao tentar ler o arquivo de parametros: "+ex.getMessage());
            System.exit(1);
        }
        
    }
    
}
