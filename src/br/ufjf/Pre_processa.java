/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class Pre_processa {
    private final int tamanhoi, tamanhoj;
    private final double [][] matrix;   
    private IniClusters st;
    private HashMap<Integer,List<Integer> > candidatos;
    private List<Cluster> listClusters; 
    private List<Entidade> ancLabels;
    private List<Entidade> ancRedirects;
    private BufferedWriter ancLabelTxt;
    public Pre_processa(String ont1, String ont2) throws IOException
    {
        System.out.println("pre processamento em andamento...");
        st  = new IniClusters(ont1,ont2);
        tamanhoi = st.getSchem().size();
        tamanhoj = st.getSchem2().size();
        matrix =  new double [tamanhoi][tamanhoj];
        listClusters= new ArrayList<> ();
        ancRedirects = new ArrayList<>();
        ancLabels =  new ArrayList<>();
       
    }    
    private double distancia_(SchemaGraph sg, SchemaGraph sg1)
    {
        double dividendo, divisor;
        dividendo = 0;
        divisor = 0;
        boolean v = false;      
        for( Entidade s : sg.getListEntidade())   
                 for(Entidade s1 : sg1.getListEntidade())
                 {
                       if(s1.getNome().contains(s.getNome()) 
                               || s.getNome().contains(s1.getNome()))
                                   dividendo++;
                       for(String spro : s.getPropriedades())
                           for(String spro1 : s1.getPropriedades())
                           {
                               if(spro.contains(spro1) ||
                                       spro1.contains(spro))
                                       dividendo++;
                           }                     
                 }
        divisor =  Math.sqrt( sg.getListEntidade().size() * sg1.getListEntidade().size());               
      
        return  divisor ==  0 ?  0 : (dividendo/divisor);
    }
    
   public HashMap<Cluster, List<Entidade> > getAncorados() throws IOException
    {
        HashMap<Cluster, List<Entidade>> ancorados = new HashMap<> ();
        List<Entidade> list = new ArrayList<> ();
        HashSet<String> hs = new HashSet<>();
        BufferedWriter anc = new BufferedWriter(new FileWriter("Ancorados2.txt"));      
        for(Cluster c : listClusters)
        {
            if(c.getRoot().getListRedirects() != null ||
                    c.getRoot().getCategorias() != null)
                   hs.add(c.getRoot().getNome());
            for(Entidade e : c.getListEntidades())
            {
                if(e.getListRedirects() != null ||
                           e.getCategorias() != null)
                    list.add(e);
                hs.add(e.getNome());
            }
            if(!list.isEmpty())
                ancorados.put(c, list);
        }
        for(String s : hs)
            anc.write(s+"\n");
        anc.flush();
        anc.close();
        return ancorados.isEmpty() ? null : ancorados ;
    }
   private void cria_csv() throws IOException
   {
        BufferedWriter csv = new BufferedWriter(new FileWriter("categorias.csv"));
        for(Cluster c : listClusters)
        {
            if(c.getRoot().getCategorias() != null && !c.getRoot().getCategorias().isEmpty())
            {
                csv.write(c.getRoot().getURI());
                for(String s : c.getRoot().getCategorias())
                    csv.write(";"+s);
                csv.write("\n");
            }
            for(Entidade e : c.getListEntidades())
                if(e.getCategorias() != null && !e.getCategorias().isEmpty())
                {
                    csv.write(e.getURI());
                    for(String s : e.getCategorias())
                        csv.write(";"+s);
                    csv.write("\n");
                }
        }
        csv.flush();
        csv.close();
        
   }
   private void conta() throws IOException
   {      
       BufferedWriter bw = new BufferedWriter(new FileWriter("Ancorados.txt")); 
       Set<String> hs = new HashSet<>();
       
       for(Cluster c: listClusters)
       {
           if(c.getRoot().getRscLabel() != null)
                hs.add(c.getRoot().getRscLabel()); 
           for(Entidade e : c.getListEntidades())
              if(e.getRscLabel() != null)
                   hs.add(e.getRscLabel());
       }
       for(String s : hs)
           bw.write(s+"\n");
                  

   }
    public List<Cluster> getListCluster()
    {
           return listClusters;
    }

    public void iniciaPreProcessamento() throws IOException
    {     
        
        System.out.println("Calculando Distancias para Clusterizacao");
        calculaDistancias(st.getSchem(), st.getSchem2());  
        System.out.println("Clusterizacao em andamento");
        selecionarCandidatos();
        System.out.println("Realizando Uniao dos Clusters");
        unir();
        AncoragemMemoria anc = new AncoragemMemoria(listClusters);   
        conta();
        cria_csv();
       
    }
    public void imprimiCluster() throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter("Clusters"));
        for(Cluster c: listClusters)
        {
            bw.write(c.getRoot().getNome()+"\n");
            for(Entidade e : c.getListEntidades())
            {
               bw.write("\t"+e.getNome()+"\n");
            }
            bw.flush();
        }
    }

    private void calculaDistancias(Collection<SchemaGraph> c, Collection<SchemaGraph> c2)
    {
        int i = 0; 
        for(SchemaGraph s : c)
        {
            int j= 0;
            for(SchemaGraph p : c2)
            {
                matrix[i][j] = distancia_(s, p);
                j++;
            }
            i++;
        }
    }
   private void selecionarCandidatos()
    {
        candidatos = new HashMap< >();

        for(int i = 0; i<tamanhoi; i++)
        {
            List<Integer> listaCandidatos =  new ArrayList<>();
            for(int j = 0; j<tamanhoj; j++)
            {
                if(matrix[i][j] > Globais.val_seleciona_candidatos)
                    listaCandidatos.add(j);
            }
            if(!listaCandidatos.isEmpty())
                candidatos.put(i, listaCandidatos);
        }
        System.out.println("Tamanho Canditados: "+candidatos.size());
    }
    private void unir()
    {
        
        Iterator<Entry<Integer,List<Integer>>> it = candidatos.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Integer,List<Integer>> ob = (Map.Entry<Integer,List<Integer>>) it.next();
            int iClust1 = ob.getKey();
            int iAux = 0;
            Cluster clust = new Cluster();
            for(SchemaGraph s : st.getSchem())
            {
                if(iAux == iClust1 )
                {
                    clust.setRoot(s.getRoot());
                    for(Entidade e: s.getListEntidade())
                               clust.add(e);                 
                   
                } 
                iAux++;
            }
            
            for(Integer aux : ob.getValue())
            {
                int i = 0;
                for(SchemaGraph s : st.getSchem2())
                  {
                      if(i == aux)
                      {
                          clust.add(s.getRoot());
                          for(Entidade e : s.getListEntidade()) 
                                 clust.add(e);                                         
                      }
                      i++;
                  }
            }
               
            
            if(!clust.vazio())
                listClusters.add(clust);
        }
    }



   
    
}
