/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zumbi
 */
public class Main {   
    
    
    public static void main(String [] args) throws IOException 
    {       
      BufferedWriter bw = null;
      List<Cluster> listCluster;
      HashMap<Cluster, List<Entidade> > ancorados;
      IndirectMatching indM;
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
           System.out.println("Quantidade ancorados: "+ancorados.size());
           System.out.flush();
           
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
    
}
